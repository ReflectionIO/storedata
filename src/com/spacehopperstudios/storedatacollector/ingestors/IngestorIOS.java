/**
 * 
 */
package com.spacehopperstudios.storedatacollector.ingestors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.collectors.DataStoreDataCollector;
import com.spacehopperstudios.storedatacollector.datatypes.Item;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;
import com.spacehopperstudios.storedatacollector.objectify.PersistenceService;

/**
 * @author billy1380
 * 
 */
public class IngestorIOS implements Ingestor {

	private static final Logger LOG = Logger.getLogger(IngestorIOS.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spacehopperstudios.storedatacollector.ingestors.Ingestor#ingest(java.util.Date)
	 */
	@Override
	public boolean ingest() {
		boolean success = false;

		List<Entity> stored = get(DataCollectorIOS.TOP_FREE_APPS);
		Map<Date, Map<Integer, Entity>> grouped = groupDataByDate(stored);
		Map<Date, String> combined = combineDataParts(grouped);

		for (Date key : combined.keySet()) {
			List<Item> items = (new ParserIOS()).parse(combined.get(key));

			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);

				// save the item if it does not exist
				if (PersistenceService.ofy().load().type(Item.class).filter("externalId", item.externalId).count() == 0) {
					item.added = (Date) stored.get(0).getProperty(DataCollectorIOS.ENTITY_COLUMN_DATE);
					PersistenceService.ofy().save().entity(item);
				}

				Rank rank = new Rank();
				rank.position = i;
				rank.itemId = item.externalId;
				rank.type = (String) stored.get(0).getProperty(DataCollectorIOS.ENTITY_COLUMN_TYPE);
				rank.source = (String) stored.get(0).getProperty(DataCollectorIOS.ENTITY_COLUMN_STORE);
				rank.country = (String) stored.get(0).getProperty(DataCollectorIOS.ENTITY_COLUMN_COUNTRY);
				rank.date = (Date) stored.get(0).getProperty(DataCollectorIOS.ENTITY_COLUMN_DATE);
				rank.price = item.price;

				if (PersistenceService.ofy().load().type(Rank.class).filter("source=", rank.source).filter("type=", rank.type).filter("date=", rank.date)
						.filter("country=", rank.country).filter("position=", rank.position).count() == 0) {
					PersistenceService.ofy().save().entity(rank);
				}
			}
		}

		DatastoreService dataStoreService = DatastoreServiceFactory.getDatastoreService();
		for (Entity entity : stored) {
			entity.setProperty(DataCollectorIOS.ENTITY_COLUMN_INGESTED, Boolean.TRUE);
			dataStoreService.put(entity);
		}

		// stored = get(date, DataCollectorIOS.TOP_PAID_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.TOP_GROSSING_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.TOP_FREE_IPAD_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.TOP_PAID_IPAD_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.TOP_GROSSING_IPAD_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.NEW_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.NEW_FREE_APPS);
		// combined = combineDataParts(stored);
		//
		// stored = get(date, DataCollectorIOS.NEW_PAID_APPS);
		// combined = combineDataParts(stored);

		success = true;

		return success;
	}

	private Map<Date, Map<Integer, Entity>> groupDataByDate(List<Entity> entities) {
		Map<Date, Map<Integer, Entity>> map = new HashMap<Date, Map<Integer, Entity>>();
		Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

		for (Entity entity : entities) {
			Date date = (Date) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATE);
			Map<Integer, Entity> dataChunks = null;
			// String data = ((Text) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATA)).getValue();

			int chunkIndex = ((Long) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_PART)).intValue();
			int totalChunks = ((Long) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_TOTALPARTS)).intValue();

			if ((dataChunks = map.get(date)) == null) {
				dataChunks = new HashMap<Integer, Entity>(totalChunks);
				map.put(date, dataChunks);
				sizeMap.put(date, Integer.valueOf(totalChunks));
			}

			dataChunks.put(Integer.valueOf(chunkIndex), entity);
		}

		List<Date> remove = new ArrayList<Date>();
		// remove any items with incomplete sets
		for (Date key : map.keySet()) {
			Map<Integer, Entity> part = map.get(key);
			if (part.size() < sizeMap.get(key).intValue()) {
				remove.add(key);
			}
		}

		for (Date date : remove) {
			map.remove(date);
		}

		return map;
	}

	private Map<Date, String> combineDataParts(Map<Date, Map<Integer, Entity>> grouped) {

		Map<Date, String> combined = new HashMap<Date, String>(grouped.size());

		for (Date date : grouped.keySet()) {
			StringBuffer buffer = new StringBuffer();
			boolean complete = true;

			Map<Integer, Entity> group = grouped.get(date);

			for (int i = 0; i < group.size(); i++) {
				Entity part = group.get(Integer.valueOf(i + 1));
				String data = ((Text) part.getProperty(DataCollectorIOS.ENTITY_COLUMN_DATA)).getValue();
				buffer.append(data);
			}

			if (complete) {
				combined.put(date, buffer.toString());
			}
		}

		return combined;
	}

	private List<Entity> get(String type) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

		Query query = new Query(DataStoreDataCollector.DATASTORE_ENITY_NAME);

		Filter typeFilter = new FilterPredicate(DataStoreDataCollector.ENTITY_COLUMN_TYPE, FilterOperator.EQUAL, type);
		Filter storeFilter = new FilterPredicate(DataStoreDataCollector.ENTITY_COLUMN_STORE, FilterOperator.EQUAL, "ios");
		Filter ingestedFilter = new FilterPredicate(DataStoreDataCollector.ENTITY_COLUMN_INGESTED, FilterOperator.NOT_EQUAL, Boolean.TRUE);
		Filter filter = CompositeFilterOperator.and(typeFilter, storeFilter, ingestedFilter);

		query.setFilter(filter);

		PreparedQuery preparedQuery = datastoreService.prepare(query);

		List<Entity> stored = new ArrayList<Entity>();
		for (Entity row : preparedQuery.asIterable()) {
			stored.add(row);
		}

		return stored;
	}

}
