//
//  IngestorIOS.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
// 	Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.ingestors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.archivers.ArchiverFactory;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.collectors.StoreCollector;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.GoogleCloudClientHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

/**
 * @author billy1380
 *
 */
public class IngestorIOS extends StoreCollector implements Ingestor {

	private static final Logger LOG = Logger.getLogger(IngestorIOS.class.getName());

	@Override
	public void ingest(List<Long> itemIds) throws DataAccessException {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		List<FeedFetch> stored = null;
		Map<Date, Map<Integer, FeedFetch>> grouped = null;
		Map<Date, String> combined = null;

		try {
			stored = get(itemIds);
			grouped = groupDataByDate(stored);
			combined = combineDataParts(grouped);
			extractItemRanks(stored, grouped, combined);
			// blobify(stored, grouped, combined);

		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

	}

	@SuppressWarnings("unused")
	private void blobify(List<FeedFetch> stored, Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined) throws DataAccessException {
		for (final Date date : grouped.keySet()) {
			final Map<Integer, FeedFetch> group = grouped.get(date);
			boolean first = true;

			for (final Integer keyInteger : group.keySet()) {
				final FeedFetch feedFetch = group.get(keyInteger);
				if (feedFetch.totalParts == 1 && (feedFetch.data.startsWith("/blobstore/writable:") || feedFetch.data.startsWith("/gs/"))) {
					continue;
				}

				if (first) {
					store(combined.get(date), feedFetch.country, feedFetch.store, feedFetch.type, null, feedFetch.date, feedFetch.code, true);
					first = false;
				}

				FeedFetchServiceProvider.provide().deleteFeedFetch(feedFetch);
			}
		}
	}

	/**
	 * extractItemRanks
	 *
	 * @param stored
	 * @param grouped
	 * @param combined
	 * @throws DataAccessException
	 */
	private void extractItemRanks(List<FeedFetch> stored, final Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined)
			throws DataAccessException {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Extracting item ranks");
		}

		final Collector c = CollectorFactory.getCollectorForStore(DataTypeHelper.IOS_STORE_A3);

		boolean isGrossing;
		for (final Date key : combined.keySet()) {

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Parsing [%s]", key.toString()));
			}

			final Map<Integer, FeedFetch> group = grouped.get(key);
			final Iterator<FeedFetch> iterator = group.values().iterator();
			final FeedFetch firstFeedFetch = iterator.next();

			final List<Item> items = new ParserIOS().parse(firstFeedFetch.country, firstFeedFetch.category.id, combined.get(key));

			final List<Rank> addRanks = new ArrayList<Rank>();
			final List<Rank> updateRanks = new ArrayList<Rank>();

			final List<Item> addItems = new ArrayList<Item>();

			isGrossing = c.isGrossing(firstFeedFetch.type);

			final Country country = new Country();
			country.a2Code = firstFeedFetch.country;

			final Store store = new Store();
			store.a3Code = firstFeedFetch.store;

			final Pager pager = new Pager();
			pager.start = Long.valueOf(0);
			pager.count = new Long(Long.MAX_VALUE);

			final List<Rank> foundRanks = RankServiceProvider.provide().getGatherCodeRanks(country, firstFeedFetch.category, firstFeedFetch.type,
					firstFeedFetch.code, false);

			final Map<String, Rank> lookup = indexRanks(foundRanks);
			final List<String> itemIds = new ArrayList<String>();

			Rank rank, existing;
			Item item;
			for (int i = 0; i < items.size(); i++) {
				item = items.get(i);

				existing = null;

				item.added = key;

				rank = new Rank();
				rank.code = firstFeedFetch.code;
				rank.country = firstFeedFetch.country;
				rank.currency = item.currency;
				rank.date = key;
				rank.itemId = item.internalId;

				rank.price = item.price;
				rank.source = firstFeedFetch.store;
				rank.type = firstFeedFetch.type;

				rank.category = firstFeedFetch.category;

				if ((existing = lookup.get(constructKey(rank))) != null) {
					rank = existing;
				}

				if (isGrossing) {
					rank.grossingPosition = Integer.valueOf(i + 1);
				} else {
					rank.position = Integer.valueOf(i + 1);
				}

				// PersisterBase.enqueue(item, Integer.valueOf(i + 1), item.internalId, firstFeedFetch.type, firstFeedFetch.store, firstFeedFetch.country, key,
				// item.price, item.currency, firstFeedFetch.code);

				if (existing == null) {
					addRanks.add(rank);
					addItems.add(item);
					itemIds.add(item.internalId);
				} else {
					updateRanks.add(rank);
				}
			}

			if (addRanks.size() > 0) {
				RankServiceProvider.provide().addRanksBatch(firstFeedFetch.id, addRanks);
			}

			if (updateRanks.size() > 0) {
				RankServiceProvider.provide().updateRanksBatch(updateRanks);
			}

			// we do not update items
			// if (updateItems.size() > 0) {
			// ItemServiceProvider.provide().updateItemsBatch(updateItems);
			// }

			final List<Item> foundItems = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);
			final List<Item> newItems = new ArrayList<Item>();
			final List<Item> updateItems = new ArrayList<Item>();

			boolean found = false;
			boolean update = false;

			final Date date30DaysAgo = DateTime.now(DateTimeZone.UTC).minusDays(30).toDate();

			for (final Item addItem : addItems) {
				found = false;
				update = false;

				for (final Item foundItem : foundItems) {
					if (foundItem.internalId.equals(addItem.internalId)) {
						if (foundItem.added.before(date30DaysAgo)) {
							// if we find the item, give it the id and properties of the existing item to avoid data loss
							addItem.id = foundItem.id;
							addItem.properties = foundItem.properties;
							update = true;
							break;
						} else {
							found = true;
							break;
						}
					}
				}

				if (!found) {
					newItems.add(addItem);
				}

				if (update) {
					updateItems.add(addItem);
				}
			}

			if (newItems.size() > 0) {
				ItemServiceProvider.provide().addItemsBatch(newItems);
			}

			for (final Item updateItem : updateItems) {
				ItemServiceProvider.provide().updateItem(updateItem);
			}

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, "Marking items as ingested");
			}

			// ofy().transact(new VoidWork() {
			//
			// @Override
			// public void vrun() {
			// int i = 0;
			// for (Integer entityKey : grouped.get(key).keySet()) {
			// FeedFetch feed = grouped.get(key).get(entityKey);
			//
			// // entity.ingested = Boolean.TRUE;
			// ofy().save().entity(feed).now();
			// i++;
			//
			// if (LOG.isLoggable(GaeLevel.TRACE)) {
			// LOG.log(GaeLevel.TRACE, String.format("Marked entity [%d]", feed.id.longValue()));
			// }
			// }
			//
			// if (LOG.isLoggable(GaeLevel.DEBUG)) {
			// LOG.log(GaeLevel.DEBUG, String.format("Marked [%d] items", i));
			// }
			// }
			//
			// });

			FeedFetch fetch;
			for (int i = 0; i < group.size(); i++) {
				fetch = group.get(Integer.valueOf(i));
				fetch.status = FeedFetchStatusType.FeedFetchStatusTypeIngested;

				fetch = FeedFetchServiceProvider.provide().updateFeedFetch(fetch);

				ArchiverFactory.getItemRankArchiver().enqueueIdFeedFetch(fetch.id);

				// This code below has been disabled as we don't want to model on ingestion of rank data but rather on the ingestion of sales data

				// Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);
				// once the feed fetch status is updated model the list
				// modeller.enqueue(fetch);
			}

			// Store s = DataTypeHelper.getIosStore();
			// Category all = CategoryServiceProvider.provide().getAllCategory(s);
			//
			// // only run the model based on the "all" category, right now category data is not modelled
			// if (isGrossing) {
			// if (firstFeedFetch.category.id.longValue() == all.id.longValue()) {
			// ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3).enqueue(firstFeedFetch.country, firstFeedFetch.category,
			// firstFeedFetch.type, firstFeedFetch.code);
			// }
			//
			// CallServiceMethodServlet.enqueueGetAllRanks(firstFeedFetch.country, DataTypeHelper.IOS_STORE_A3, firstFeedFetch.category.id,
			// firstFeedFetch.type, key);
			// }

		}
	}

	/**
	 * @param ranks
	 * @return
	 */
	private Map<String, Rank> indexRanks(List<Rank> ranks) {
		final Map<String, Rank> indexed = new HashMap<String, Rank>();

		for (final Rank rank : ranks) {
			indexed.put(constructKey(rank), rank);
		}

		return indexed;
	}

	/**
	 * @param rank
	 * @return
	 */
	private String constructKey(Rank rank) {
		return rank.code + rank.source + rank.country + rank.category.id.toString() + rank.itemId;
	}

	private Map<Date, Map<Integer, FeedFetch>> groupDataByDate(List<FeedFetch> entities) {
		final Map<Date, Map<Integer, FeedFetch>> map = new HashMap<Date, Map<Integer, FeedFetch>>();
		final Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Grouping entites by date");
		}

		for (final FeedFetch entity : entities) {
			final Date date = entity.date;
			Map<Integer, FeedFetch> dataChunks = null;
			// String data = ((Text) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATA)).getValue();

			final int chunkIndex = entity.part;
			final int totalChunks = entity.totalParts;

			if ((dataChunks = map.get(date)) == null) {
				dataChunks = new HashMap<Integer, FeedFetch>(totalChunks);
				map.put(date, dataChunks);
				sizeMap.put(date, Integer.valueOf(totalChunks));
			}

			dataChunks.put(Integer.valueOf(chunkIndex), entity);
		}

		final List<Date> remove = new ArrayList<Date>();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Creating list of incomplete feeds");
		}

		// remove any items with incomplete sets
		int i = 0;
		for (final Date key : map.keySet()) {
			final Map<Integer, FeedFetch> part = map.get(key);
			if (part.size() < sizeMap.get(key).intValue()) {
				remove.add(key);
				i++;
			}
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("List contains [%d] items", i));
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "remove any items with incomplete sets");
		}

		i = 0;
		for (final Date date : remove) {
			map.remove(date);

			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, String.format("Removed item with key [%s]", date.toString()));
			}

			i++;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Removed [%d] items", i));
		}

		return map;
	}

	private Map<Date, String> combineDataParts(Map<Date, Map<Integer, FeedFetch>> grouped) {

		final Map<Date, String> combined = new HashMap<Date, String>(grouped.size());

		for (final Date date : grouped.keySet()) {
			final Map<Integer, FeedFetch> group = grouped.get(date);
			String data = null;
			boolean blob = false;

			if (group.size() == 1) {
				data = group.values().iterator().next().data;
				if (data.startsWith("/blobstore/writable:") || data.startsWith("/gs/")) {
					blob = true;
				}
			}

			final StringBuffer buffer = new StringBuffer();

			if (blob) {
				GcsService gcsService = GcsServiceFactory.createGcsService();
				SimpleEntry<String, String> bucketAndFileName = GoogleCloudClientHelper.getGCSBucketAndFileName(data);
				GcsFilename filename = new GcsFilename(bucketAndFileName.getKey(), bucketAndFileName.getValue());

				GcsInputChannel readChannel = null;
				BufferedReader reader = null;

				try {
					readChannel = gcsService.openReadChannel(filename, 0); // fileService.openReadChannel(file, false);
					reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
					int length;
					final char[] bytes = new char[1024];
					while ((length = reader.read(bytes)) > 0) {
						buffer.append(bytes, 0, length);
					}
				} catch (final IOException e) {
					LOG.log(Level.SEVERE, String.format("Error closing read channel for file [%s]", data), e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (final IOException e) {
							LOG.log(Level.SEVERE, "Error closing reader", e);
						}
					}

					if (readChannel != null) {
						readChannel.close();
					}
				}

			} else {
				for (int i = 0; i < group.size(); i++) {
					final FeedFetch part = group.get(Integer.valueOf(i + 1));
					data = part.data;
					buffer.append(data);
				}
			}

			combined.put(date, buffer.toString());
		}

		return combined;
	}

	private List<FeedFetch> get(List<Long> itemIds) throws DataAccessException {

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Fetching [%d] items", itemIds == null ? 0 : itemIds.size()));
		}

		final List<FeedFetch> stored = new ArrayList<FeedFetch>();

		// for now ingest so that we don't kill the band width

		int i = 0;
		FeedFetch row = null;
		for (final Long itemId : itemIds) {
			row = FeedFetchServiceProvider.provide().getFeedFetch(itemId);
			stored.add(row);
			i++;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Found [%d] ingestable items", i));
		}

		return stored;
	}

	private void enqueue(Queue queue, List<Long> itemIds) {

		final StringBuffer buffer = new StringBuffer();
		for (final Long id : itemIds) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}

			buffer.append(id.toString());
		}

		final String store = DataTypeHelper.IOS_STORE_A3, ids = buffer.toString();

		try {
			queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_INGEST_FORMAT, store, ids)).method(Method.GET));
		} catch (final TransientFailureException ex) {

			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
			}

			// retry once
			try {
				queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_INGEST_FORMAT, store, ids)).method(Method.GET));
			} catch (final TransientFailureException reEx) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							String.format("Retry of with parameters store [%s] ids [%s] failed while adding to queue [%s] twice", store, ids,
									queue.getQueueName()), reEx);
				}
			}
		}
	}

	@Override
	public void enqueue(List<Long> itemIds) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			final Queue queue = QueueFactory.getQueue("ingest");

			if (queue != null) {
				enqueue(queue, itemIds);
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}
}