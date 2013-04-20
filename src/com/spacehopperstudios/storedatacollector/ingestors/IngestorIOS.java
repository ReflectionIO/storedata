/**
 * 
 */
package com.spacehopperstudios.storedatacollector.ingestors;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.collectors.DataStoreDataCollector;
import com.spacehopperstudios.storedatacollector.datatypes.FeedFetch;
import com.spacehopperstudios.storedatacollector.datatypes.Item;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

/**
 * @author billy1380
 * 
 */
public class IngestorIOS extends DataStoreDataCollector implements Ingestor {

	private static final Logger LOG = Logger.getLogger(IngestorIOS.class);
	private int count = 20;
	private String type = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spacehopperstudios.storedatacollector.ingestors.Ingestor#ingest(java.util.Date)
	 */
	@Override
	public boolean ingest() {
		boolean success = false;
		boolean selectedType = (type != null);

		List<FeedFetch> stored = null;
		Map<Date, Map<Integer, FeedFetch>> grouped = null;
		Map<Date, String> combined = null;

		// For now we are only interested in top grossing and top paid

//		if (!selectedType || type.equals(DataCollectorIOS.TOP_FREE_APPS)) {
//			 stored = get(DataCollectorIOS.TOP_FREE_APPS);
//			 grouped = groupDataByDate(stored);
//			 combined = combineDataParts(grouped);
//			 extractItemRanks(stored, grouped, combined);
//			 blobify(stored, grouped, combined);
//		}

		if (!selectedType || type.equals(DataCollectorIOS.TOP_PAID_APPS)) {
			stored = get(DataCollectorIOS.TOP_PAID_APPS);
			grouped = groupDataByDate(stored);
			combined = combineDataParts(grouped);
			extractItemRanks(stored, grouped, combined);
			blobify(stored, grouped, combined);
		}

		if (!selectedType || type.equals(DataCollectorIOS.TOP_GROSSING_APPS)) {
			stored = get(DataCollectorIOS.TOP_GROSSING_APPS);
			grouped = groupDataByDate(stored);
			combined = combineDataParts(grouped);
			extractItemRanks(stored, grouped, combined);
			blobify(stored, grouped, combined);
		}

//		if (!selectedType || type.equals(DataCollectorIOS.TOP_FREE_IPAD_APPS)) {
//			 stored = get(DataCollectorIOS.TOP_FREE_IPAD_APPS);
//			 grouped = groupDataByDate(stored);
//			 combined = combineDataParts(grouped);
//			 extractItemRanks(stored, grouped, combined);
//			 blobify(stored, grouped, combined);
//		}

		if (!selectedType || type.equals(DataCollectorIOS.TOP_PAID_IPAD_APPS)) {
			stored = get(DataCollectorIOS.TOP_PAID_IPAD_APPS);
			grouped = groupDataByDate(stored);
			combined = combineDataParts(grouped);
			extractItemRanks(stored, grouped, combined);
			blobify(stored, grouped, combined);
		}

		if (!selectedType || type.equals(DataCollectorIOS.TOP_GROSSING_IPAD_APPS)) {
			stored = get(DataCollectorIOS.TOP_GROSSING_IPAD_APPS);
			grouped = groupDataByDate(stored);
			combined = combineDataParts(grouped);
			extractItemRanks(stored, grouped, combined);
			blobify(stored, grouped, combined);
		}

//		if (!selectedType || type.equals(DataCollectorIOS.NEW_APPS)) {
//			 stored = get(DataCollectorIOS.NEW_APPS);
//			 grouped = groupDataByDate(stored);
//			 combined = combineDataParts(grouped);
//			 extractItemRanks(stored, grouped, combined);
//			 blobify(stored, grouped, combined);
//		}

//		if (!selectedType || type.equals(DataCollectorIOS.NEW_FREE_APPS)) {
//			 stored = get(DataCollectorIOS.NEW_FREE_APPS);
//			 grouped = groupDataByDate(stored);
//			 combined = combineDataParts(grouped);
//			 extractItemRanks(stored, grouped, combined);
//			 blobify(stored, grouped, combined);
//		}

//		if (!selectedType || type.equals(DataCollectorIOS.NEW_PAID_APPS)) {
//			 stored = get(DataCollectorIOS.NEW_PAID_APPS);
//			 grouped = groupDataByDate(stored);
//			 combined = combineDataParts(grouped);
//			 extractItemRanks(stored, grouped, combined);
//			 blobify(stored, grouped, combined);
//		}

		success = true;

		return success;
	}

	private void blobify(List<FeedFetch> stored, Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined) {
		for (Date date : grouped.keySet()) {
			Map<Integer, FeedFetch> group = grouped.get(date);
			boolean first = true;

			for (Integer keyInteger : group.keySet()) {
				FeedFetch entity = group.get(keyInteger);
				if (entity.totalParts == 1 && entity.data.startsWith("/blobstore/writable:")) {
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
	private void extractItemRanks(List<FeedFetch> stored, Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Extracting item ranks");
		}

		for (Date key : combined.keySet()) {

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Parsing [%s]", key.toString()));
			}

			List<Item> items = (new ParserIOS()).parse(combined.get(key));

			Map<Integer, FeedFetch> group = grouped.get(key);
			FeedFetch firstFeedFetch = group.values().iterator().next();

			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);

				// save the item if it does not exist
				if (ofy().load().type(Item.class).filter("externalId =", item.externalId).count() == 0) {
					item.added = key;
					ofy().save().entity(item).now();
				}

				Rank rank = new Rank();
				rank.position = i;
				rank.itemId = item.externalId;
				rank.type = firstFeedFetch.type;
				rank.source = firstFeedFetch.store;
				rank.country = firstFeedFetch.country;
				rank.date = key;
				rank.price = item.price;
				rank.currency = item.currency;
				rank.code = firstFeedFetch.code;

				// will only save the rank if it has not been done before
				if (ofy().cache(false).load().type(Rank.class).filter("source =", rank.source).filter("type =", rank.type).filter("date =", rank.date)
						.filter("country =", rank.country).filter("position =", rank.position).count() == 0) {
					ofy().save().entity(rank).now();

					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Saved rank [%s] for", rank.itemId));
					}
				}
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Marking items as ingested");
			}

			int i = 0;
			for (Integer entityKey : grouped.get(key).keySet()) {
				FeedFetch entity = grouped.get(key).get(entityKey);

				entity.ingested = true;
				ofy().save().entity(entity).now();
				i++;

				if (LOG.isTraceEnabled()) {
					LOG.trace(String.format("Marked entity [%d]", entity.id.longValue()));
				}
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Marked [%d] items", i));
			}
		}
	}

	private Map<Date, Map<Integer, FeedFetch>> groupDataByDate(List<FeedFetch> entities) {
		Map<Date, Map<Integer, FeedFetch>> map = new HashMap<Date, Map<Integer, FeedFetch>>();
		Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Grouping entites by date");
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

		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating list of incomplete feeds");
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

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("List contains [%d] items", i));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("remove any items with incomplete sets");
		}

		i = 0;
		for (Date date : remove) {
			map.remove(date);

			if (LOG.isTraceEnabled()) {
				LOG.trace(String.format("Removed item with key [%s]", date.toString()));
			}

			i++;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Removed [%d] items", i));
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
				if (data.startsWith("/blobstore/writable:")) {
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
					LOG.error("Error closing read channel for file [%s]", e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							LOG.error("Error closing reader", e);
						}
					}

					if (readChannel != null) {
						try {
							readChannel.close();
						} catch (IOException e) {
							LOG.error("Error closing read channel for file [%s]", e);
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

	private List<FeedFetch> get(String type) {

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Fetching %s", type));
		}

		List<FeedFetch> stored = new ArrayList<FeedFetch>();

		// for now ingest so that we don't kill the band width

		int i = 0;
		for (FeedFetch row : ofy().cache(false).load().type(FeedFetch.class).order("date").filter("type = ", type).filter("store =", "ios")
				.filter("ingested =", Boolean.FALSE).limit(count)) {
			stored.add(row);
			i++;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Found [%d] uningested items", i));
		}

		return stored;
	}

	@Override
	public boolean ingest(int count) {
		this.count = count;
		return ingest();
	}

	public boolean ingest(int count, String type) {
		this.type = type;
		return ingest(count);
	}

}
