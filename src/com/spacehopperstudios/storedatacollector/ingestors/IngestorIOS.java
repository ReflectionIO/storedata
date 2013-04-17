/**
 * 
 */
package com.spacehopperstudios.storedatacollector.ingestors;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.datatypes.FeedFetch;
import com.spacehopperstudios.storedatacollector.datatypes.Item;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

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

		List<FeedFetch> stored = get(DataCollectorIOS.TOP_FREE_APPS);
		Map<Date, Map<Integer, FeedFetch>> grouped = groupDataByDate(stored);
		Map<Date, String> combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.TOP_PAID_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.TOP_GROSSING_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.TOP_FREE_IPAD_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.TOP_PAID_IPAD_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.TOP_GROSSING_IPAD_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.NEW_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.NEW_FREE_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		stored = get(DataCollectorIOS.NEW_PAID_APPS);
		grouped = groupDataByDate(stored);
		combined = combineDataParts(grouped);
		extractItemRanks(stored, grouped, combined);

		success = true;

		return success;
	}

	/**
	 * @param stored
	 * @param grouped
	 * @param combined
	 */
	private void extractItemRanks(List<FeedFetch> stored, Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined) {

		for (Date key : combined.keySet()) {
			List<Item> items = (new ParserIOS()).parse(combined.get(key));

			FeedFetch firstFeedFetch = grouped.get(key).get(0);
			
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);

				// save the item if it does not exist
				if (ofy().load().type(Item.class).filter("externalId =", item.externalId).count() == 0) {
					item.added = key;
					ofy().save().entity(item);
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

				if (ofy().load().type(Rank.class).filter("source =", rank.source).filter("type =", rank.type).filter("date =", rank.date)
						.filter("country =", rank.country).filter("position =", rank.position).count() == 0) {
					ofy().save().entity(rank);
				}
			}

			for (Integer entityKey : grouped.get(key).keySet()) {
				FeedFetch entity = grouped.get(key).get(entityKey);

				entity.ingested = true;
				ofy().save().entity(entity);
			}

		}
	}

	private Map<Date, Map<Integer, FeedFetch>> groupDataByDate(List<FeedFetch> entities) {
		Map<Date, Map<Integer, FeedFetch>> map = new HashMap<Date, Map<Integer, FeedFetch>>();
		Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

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
		
		// remove any items with incomplete sets
		for (Date key : map.keySet()) {
			Map<Integer, FeedFetch> part = map.get(key);
			if (part.size() < sizeMap.get(key).intValue()) {
				remove.add(key);
			}
		}

		for (Date date : remove) {
			map.remove(date);
		}

		return map;
	}

	private Map<Date, String> combineDataParts(Map<Date, Map<Integer, FeedFetch>> grouped) {

		Map<Date, String> combined = new HashMap<Date, String>(grouped.size());

		for (Date date : grouped.keySet()) {
			StringBuffer buffer = new StringBuffer();
			boolean complete = true;

			Map<Integer, FeedFetch> group = grouped.get(date);

			for (int i = 0; i < group.size(); i++) {
				FeedFetch part = group.get(Integer.valueOf(i + 1));
				String data = part.data;
				buffer.append(data);
			}

			if (complete) {
				combined.put(date, buffer.toString());
			}
		}

		return combined;
	}

	private List<FeedFetch> get(String type) {

		List<FeedFetch> stored = new ArrayList<FeedFetch>();
		for (FeedFetch row : ofy().load().type(FeedFetch.class).filter("type = ", type).filter("store =", "ios").filter("ingested =", Boolean.FALSE)) {
			stored.add(row);
		}

		return stored;
	}

}
