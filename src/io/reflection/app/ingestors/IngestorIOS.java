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
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

/**
 * @author billy1380
 * 
 */
public class IngestorIOS extends AbstractIngestorIos implements Ingestor {

	private static final Logger LOG = Logger.getLogger(IngestorIOS.class.getName());

	/**
	 * extractItemRanks
	 * 
	 * @param stored
	 * @param grouped
	 * @param combined
	 * @throws DataAccessException
	 */
	@Override
	protected void extractItemRanks(List<FeedFetch> stored, final Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined)
			throws DataAccessException {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Extracting item ranks");
		}

		Collector c = CollectorFactory.getCollectorForStore(DataTypeHelper.IOS_STORE_A3);

		boolean isGrossing;
		for (final Date key : combined.keySet()) {
			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Parsing [%s]", key.toString()));
			}

			Map<Integer, FeedFetch> group = grouped.get(key);
			Iterator<FeedFetch> iterator = group.values().iterator();
			FeedFetch firstFeedFetch = iterator.next();

			List<Item> items = (new ParserIOS()).parse(firstFeedFetch.country, firstFeedFetch.category.id, combined.get(key));

			List<Rank> addRanks = new ArrayList<Rank>();
			List<Rank> updateRanks = new ArrayList<Rank>();

			List<Item> addItems = new ArrayList<Item>();

			isGrossing = c.isGrossing(firstFeedFetch.type);

			Country country = new Country();
			country.a2Code = firstFeedFetch.country;

			Store store = new Store();
			store.a3Code = firstFeedFetch.store;

			Pager pager = new Pager();
			pager.start = Long.valueOf(0);
			pager.count = new Long(Long.MAX_VALUE);

			List<Rank> foundRanks = RankServiceProvider.provide().getGatherCodeRanks(country, store, firstFeedFetch.category, firstFeedFetch.type,
					firstFeedFetch.code, pager, Boolean.TRUE);

			Map<String, Rank> lookup = indexRanks(foundRanks);
			List<String> itemIds = new ArrayList<String>();

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
				RankServiceProvider.provide().addRanksBatch(addRanks);
			}

			if (updateRanks.size() > 0) {
				RankServiceProvider.provide().updateRanksBatch(updateRanks);
			}

			// we do not update items
			// if (updateItems.size() > 0) {
			// ItemServiceProvider.provide().updateItemsBatch(updateItems);
			// }

			List<Item> foundItems = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);
			List<Item> newItems = new ArrayList<Item>();
			List<Item> updateItems = new ArrayList<Item>();

			boolean found = false;
			boolean update = false;

			Date date30DaysAgo = DateTime.now(DateTimeZone.UTC).minusDays(30).toDate();

			for (Item addItem : addItems) {
				found = false;
				update = false;

				for (Item foundItem : foundItems) {
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

			for (Item updateItem : updateItems) {
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

			Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);

			FeedFetch fetch;
			for (int i = 0; i < group.size(); i++) {
				fetch = group.get(Integer.valueOf(i));
				fetch.status = FeedFetchStatusType.FeedFetchStatusTypeIngested;
				
				fetch = FeedFetchServiceProvider.provide().updateFeedFetch(fetch);

				ArchiverFactory.getItemRankArchiver().enqueueIdFeedFetch(fetch.id);

				// once the feed fetch status is updated model the list
				modeller.enqueue(fetch);
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