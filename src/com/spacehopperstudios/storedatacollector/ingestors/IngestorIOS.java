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
		Map<Date, String> combined = combineDataParts(stored); // one data string for each import of the day (should be 2)

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

				if (PersistenceService.ofy().load().type(Rank.class).filter("source=", rank.source).filter("type=", rank.type).filter("date=", rank.date)
						.filter("country=", rank.country).filter("position=", rank.position).count() == 0) {
					PersistenceService.ofy().save().entity(rank);
				}
			}
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

		return success;
	}

	private Map<Date, String> combineDataParts(List<Entity> entities) {
		Map<Date, List<String>> map = new HashMap<Date, List<String>>();
		Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

		for (Entity entity : entities) {
			Date date = (Date) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATE);
			List<String> dataChunks = null;
			String data = ((Text) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATA)).getValue();

			int chunkIndex = ((Long) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_PART)).intValue();
			int totalChunks = ((Long) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_TOTALPARTS)).intValue();

			if ((dataChunks = map.get(date)) == null) {
				dataChunks = new ArrayList<String>(totalChunks);
				for (int i = 0; i < totalChunks; i++) {
					dataChunks.add("");
				}
				map.put(date, dataChunks);
				sizeMap.put(date, Integer.valueOf(totalChunks));
			}

			dataChunks.set(chunkIndex - 1, data);
		}

		Map<Date, String> combined = new HashMap<Date, String>(map.size());

		for (Date date : map.keySet()) {
			StringBuffer buffer = new StringBuffer();
			boolean complete = true;

			for (String part : (List<String>) map.get(date)) {
				if (part.length() == 0) {
					complete = false;
					break;
				}

				buffer.append(part);
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
		Filter ingestedFilter = new FilterPredicate(DataStoreDataCollector.ENTITY_COLUMN_INGESTED, FilterOperator.NOT_EQUAL, true);
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
