/**
 * 
 */
package io.reflection.app.ingestors;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.collectors.StoreCollector;
import io.reflection.app.datatypes.FeedFetch;
import io.reflection.app.datatypes.Item;
import io.reflection.app.datatypes.Rank;
import io.reflection.app.logging.GaeLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.googlecode.objectify.VoidWork;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("deprecation")
public class IngestorIOS extends StoreCollector implements Ingestor {

	private static final Logger LOG = Logger.getLogger(IngestorIOS.class.getName());

	private static final String IOS_STORE_A3 = "ios";

	@Override
	public void ingest(List<Long> itemIds) {
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
			blobify(stored, grouped, combined);
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

	}

	private void blobify(List<FeedFetch> stored, Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined) {
		for (Date date : grouped.keySet()) {
			Map<Integer, FeedFetch> group = grouped.get(date);
			boolean first = true;

			for (Integer keyInteger : group.keySet()) {
				FeedFetch entity = group.get(keyInteger);
				if (entity.totalParts == 1 && (entity.data.startsWith("/blobstore/writable:") || entity.data.startsWith("/gs/"))) {
					continue;
				}

				if (first) {
					store(combined.get(date), entity.country, entity.store, entity.type, entity.date, entity.code, true);
					first = false;
				}

				ofy().delete().entity(entity).now();
			}
		}
	}

	/**
	 * @param stored
	 * @param grouped
	 * @param combined
	 */
	private void extractItemRanks(List<FeedFetch> stored, final Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined) {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Extracting item ranks");
		}

		DataStorePersist persistor = new DataStorePersist();

		for (final Date key : combined.keySet()) {

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Parsing [%s]", key.toString()));
			}

			List<Item> items = (new ParserIOS()).parse(combined.get(key));

			Map<Integer, FeedFetch> group = grouped.get(key);
			FeedFetch firstFeedFetch = group.values().iterator().next();

			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);

				item.added = key;

				// // save the item if it does not exist
				// if (ofy().load().type(Item.class).filter("externalId =", item.externalId).count() == 0) {
				// item.added = key;
				// ofy().save().entity(item).now();
				// }

				Rank rank = new Rank();
				rank.position = Integer.valueOf(i);
				rank.itemId = item.externalId;
				rank.type = firstFeedFetch.type;
				rank.source = firstFeedFetch.store;
				rank.country = firstFeedFetch.country;
				rank.date = key;
				rank.price = item.price;
				rank.currency = item.currency;
				rank.code = firstFeedFetch.code;

				// // will only save the rank if it has not been done before
				// if (ofy().cache(false).load().type(Rank.class).filter("source =", rank.source).filter("type =", rank.type).filter("date =", rank.date)
				// .filter("country =", rank.country).filter("position =", rank.position).count() == 0) {
				// ofy().save().entity(rank).now();
				//
				// if (LOG.isLoggable(GaeLevel.TRACE)) {
				// LOG.log(GaeLevel.TRACE, String.format("Saved rank [%s] for", rank.itemId));
				// }
				// }

				persistor.enqueue(item, rank);
			}

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, "Marking items as ingested");
			}

			ofy().transact(new VoidWork() {

				@Override
				public void vrun() {
					int i = 0;
					for (Integer entityKey : grouped.get(key).keySet()) {
						FeedFetch entity = grouped.get(key).get(entityKey);

						entity.ingested = Boolean.TRUE;
						ofy().save().entity(entity).now();
						i++;

						if (LOG.isLoggable(GaeLevel.TRACE)) {
							LOG.log(GaeLevel.TRACE, String.format("Marked entity [%d]", entity.id.longValue()));
						}
					}

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Marked [%d] items", i));
					}
				}

			});

		}
	}

	private Map<Date, Map<Integer, FeedFetch>> groupDataByDate(List<FeedFetch> entities) {
		Map<Date, Map<Integer, FeedFetch>> map = new HashMap<Date, Map<Integer, FeedFetch>>();
		Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Grouping entites by date");
		}

		for (FeedFetch entity : entities) {
			Date date = entity.date;
			Map<Integer, FeedFetch> dataChunks = null;
			// String data = ((Text) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATA)).getValue();

			int chunkIndex = entity.part;
			int totalChunks = entity.totalParts;

			if ((dataChunks = map.get(date)) == null) {
				dataChunks = new HashMap<Integer, FeedFetch>(totalChunks);
				map.put(date, dataChunks);
				sizeMap.put(date, Integer.valueOf(totalChunks));
			}

			dataChunks.put(Integer.valueOf(chunkIndex), entity);
		}

		List<Date> remove = new ArrayList<Date>();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Creating list of incomplete feeds");
		}

		// remove any items with incomplete sets
		int i = 0;
		for (Date key : map.keySet()) {
			Map<Integer, FeedFetch> part = map.get(key);
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
		for (Date date : remove) {
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

		Map<Date, String> combined = new HashMap<Date, String>(grouped.size());

		for (Date date : grouped.keySet()) {
			Map<Integer, FeedFetch> group = grouped.get(date);
			String data = null;
			boolean blob = false;

			if (group.size() == 1) {
				data = group.values().iterator().next().data;
				if (data.startsWith("/blobstore/writable:") || data.startsWith("/gs/")) {
					blob = true;
				}
			}

			StringBuffer buffer = new StringBuffer();

			if (blob) {
				FileService fileService = FileServiceFactory.getFileService();

				AppEngineFile file = new AppEngineFile(data);
				FileReadChannel readChannel = null;
				BufferedReader reader = null;

				try {
					readChannel = fileService.openReadChannel(file, false);
					reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
					int length;
					char[] bytes = new char[1024];
					while ((length = reader.read(bytes)) > 0) {
						buffer.append(bytes, 0, length);
					}
				} catch (IOException e) {
					LOG.log(Level.SEVERE, String.format("Error closing read channel for file [%s]", file.getFullPath()), e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							LOG.log(Level.SEVERE, "Error closing reader", e);
						}
					}

					if (readChannel != null) {
						try {
							readChannel.close();
						} catch (IOException e) {
							LOG.log(Level.SEVERE, String.format("Error closing read channel for file [%s]", file.getFullPath()), e);
						}
					}
				}

			} else {
				for (int i = 0; i < group.size(); i++) {
					FeedFetch part = group.get(Integer.valueOf(i + 1));
					data = part.data;
					buffer.append(data);
				}
			}

			combined.put(date, buffer.toString());
		}

		return combined;
	}

	private List<FeedFetch> get(List<Long> itemIds) {

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Fetching [%d] items", itemIds == null ? 0 : itemIds.size()));
		}

		List<FeedFetch> stored = new ArrayList<FeedFetch>();

		// for now ingest so that we don't kill the band width

		int i = 0;
		Map<Long, FeedFetch> items = ofy().cache(false).load().type(FeedFetch.class).ids(itemIds);
		FeedFetch row = null;
		for (Long itemId : itemIds) {
			row = items.get(itemId);
			if (!row.ingested.booleanValue()) {
				stored.add(row);
				i++;
			} else {
				stored.clear();
				i = 0;

				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("found ingested item [%d] in items marked for ingesting", itemId.longValue()));
				}

				break;
			}
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Found [%d] uningested items", i));
		}

		return stored;
	}

	private void enqueue(Queue queue, List<Long> itemIds) {
		StringBuffer buffer = new StringBuffer();
		for (Long id : itemIds) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}

			buffer.append(id.toString());
		}

		String store = IOS_STORE_A3, ids = buffer.toString();

		try {
			queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_INGEST_FORMAT, store, ids)).method(Method.GET));
		} catch (TransientFailureException ex) {

			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
			}

			// retry once
			try {
				queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_INGEST_FORMAT, store, ids)).method(Method.GET));
			} catch (TransientFailureException reEx) {
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
			Queue queue = QueueFactory.getQueue("ingest");

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