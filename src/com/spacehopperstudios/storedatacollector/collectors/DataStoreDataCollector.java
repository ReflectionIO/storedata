package com.spacehopperstudios.storedatacollector.collectors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public abstract class DataStoreDataCollector {
	
	public static final int MAX_DATA_CHUNK_LENGTH = 500000;

	public static final String DATASTORE_ENITY_NAME = "FeedFetch";
	
	public static final String ENTITY_COLUMN_DATA = "data";
	public static final String ENTITY_COLUMN_COUNTRY = "country";
	public static final String ENTITY_COLUMN_STORE = "store";
	public static final String ENTITY_COLUMN_TYPE = "type";
	public static final String ENTITY_COLUMN_DATE = "date";
	public static final String ENTITY_COLUMN_PART = "part";
	public static final String ENTITY_COLUMN_TOTALPARTS = "totalparts";
	public static final String ENTITY_COLUMN_INGESTED = "ingested";

	private static final Logger LOG = Logger.getLogger(DataStoreDataCollector.class);


	private List<String> splitEqually(String text, int size) {
		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}

		return ret;
	}

	protected void store(String data, String countryCode, String store, String type, Date date) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving Data to data store");
		}

		List<String> splitData = splitEqually(data, MAX_DATA_CHUNK_LENGTH);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Data split into [%d] chuncks", splitData.size()));
		}

		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

		for (int i = 0; i < splitData.size(); i++) {
			Entity entity = new Entity(DATASTORE_ENITY_NAME);

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Data chunck [%d of %d] is [%d] characters", i, splitData.size(), splitData.get(i).length()));
			}

			Text dataAsTextField = new Text(splitData.get(i));
			entity.setProperty(ENTITY_COLUMN_DATA, dataAsTextField);
			entity.setProperty(ENTITY_COLUMN_COUNTRY, countryCode);
			entity.setProperty(ENTITY_COLUMN_STORE, store);
			entity.setProperty(ENTITY_COLUMN_TYPE, type);
			entity.setProperty(ENTITY_COLUMN_DATE, date);
			entity.setProperty(ENTITY_COLUMN_PART, Integer.valueOf(i + 1));
			entity.setProperty(ENTITY_COLUMN_TOTALPARTS, Integer.valueOf(splitData.size()));
			entity.setProperty(ENTITY_COLUMN_INGESTED, Boolean.FALSE);

			if (LOG.isTraceEnabled()) {
				LOG.trace(String.format("Saving entity [%s]", entity.toString()));
			}

			datastoreService.put(entity);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Storing entity complete [%s] [%s] [%s] at [%s]", countryCode, store, type, date.toString()));
		}
	}
}
