package com.spacehopperstudios.storedatacollector.collectors;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.spacehopperstudios.storedatacollector.datatypes.FeedFetch;

public abstract class DataStoreDataCollector {
	
	public static final int MAX_DATA_CHUNK_LENGTH = 500000;

	private static final Logger LOG = Logger.getLogger(DataStoreDataCollector.class);


	private List<String> splitEqually(String text, int size) {
		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}

		return ret;
	}

	protected void store(String data, String countryCode, String store, String type, Date date, String code) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving Data to data store");
		}

		List<String> splitData = splitEqually(data, MAX_DATA_CHUNK_LENGTH);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Data split into [%d] chuncks", splitData.size()));
		}

		for (int i = 0; i < splitData.size(); i++) {
			FeedFetch entity = new FeedFetch();

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Data chunck [%d of %d] is [%d] characters", i, splitData.size(), splitData.get(i).length()));
			}

			entity.data = splitData.get(i);
			entity.country = countryCode;
			entity.store = store;
			entity.type = type;
			entity.date = date;
			entity.part = Integer.valueOf(i + 1);
			entity.totalParts = Integer.valueOf(splitData.size());
			entity.ingested = false;
			entity.code = code;

			ofy().save().entity(entity);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Storing entity complete [%s] [%s] [%s] at [%s]", countryCode, store, type, date.toString()));
		}
	}
}
