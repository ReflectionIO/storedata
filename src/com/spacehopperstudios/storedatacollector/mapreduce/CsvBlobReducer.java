package com.spacehopperstudios.storedatacollector.mapreduce;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;
import com.google.gson.Gson;
import com.spacehopperstudios.storedatacollector.collectors.HttpExternalGetter;
import com.spacehopperstudios.storedatacollector.datatypes.Item;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

public class CsvBlobReducer extends Reducer<String, String, ByteBuffer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(CsvBlobReducer.class.getName());

	@Override
	public void beginShard() {
		LOG.info("beginShard()");

		StringBuffer buffer = new StringBuffer();
		buffer.append("# data for shard ");
		buffer.append(getContext().getShardNumber());
		buffer.append("#item id,date,paid possition,grossing possition,price,usesiap");
		buffer.append("\n");

		getContext().emit(ByteBuffer.wrap(buffer.toString().getBytes()));
	}

	@Override
	public void beginSlice() {
		LOG.info("beginSlice()");
	}

	@Override
	public void reduce(String key, ReducerInput<String> values) {
		Rank grossingItem = null, topItem = null;
		Rank masterItem = null;

		StringBuffer buffer = new StringBuffer();

		int i = 0;
		while (values.hasNext()) {
			i++;

			masterItem = (new Gson()).fromJson(values.next(), Rank.class);

			if (masterItem.type.contains("grossing")) {
				grossingItem = masterItem;
			} else if (masterItem.type.contains("top")) {
				topItem = masterItem;
			}
		}

		if (i > 2) {
			LOG.warning("Found more than 2 items in a single reducer input, this should not happen");
		}

		buffer.append(masterItem.itemId);
		buffer.append(",");
		buffer.append(masterItem.date);
		buffer.append(",");

		if (topItem != null) {
			buffer.append(topItem.position + 1);
		}
		buffer.append(",");

		if (grossingItem != null) {
			buffer.append(grossingItem.position + 1);
		}
		buffer.append(",");

		buffer.append(masterItem.price);

		buffer.append(",");

		try {
			if (usesIap(masterItem)) {
				buffer.append(true);
			} else {
				buffer.append(false);
			}
		} catch (Exception ex) {
			buffer.append("?");
		}

		buffer.append("\n");

		getContext().emit(ByteBuffer.wrap(buffer.toString().getBytes()));
	}

	private boolean usesIap(Rank rank) throws Exception {

		String usesIapKey = rank.itemId + ".usesIap";

		MemcacheService memCacheService = MemcacheServiceFactory.getMemcacheService();

		Boolean cachedUsesIap = (Boolean) memCacheService.get(usesIapKey);

		if (cachedUsesIap == null) {
			String rankItemIdKey = rank.itemId + ".itemId";

			String itemId = (String) memCacheService.get(rankItemIdKey);

			if (itemId == null) {
				List<Item> items = ofy().load().type(Item.class).filter("externalId =", rank.itemId).limit(1).list();

				if (items != null && items.size() > 0) {
					itemId = items.get(0).internalId;

					memCacheService.put(rankItemIdKey, itemId);
				}
			}

			if (itemId == null) {
				throw new Exception("Could not find an item for this rank which is wrong!");
			}

			String data = HttpExternalGetter.getData("https://itunes.apple.com/app/id" + itemId);

			if (data != null) {
				cachedUsesIap = data.contains("class=\"extra-list in-app-purchases") ? Boolean.TRUE : Boolean.FALSE;

				memCacheService.put(usesIapKey, cachedUsesIap);
			}

			if (cachedUsesIap == null) {
				throw new Exception("Could not find whether the app uses iap or not!");
			}
		}

		return cachedUsesIap.booleanValue();
	}

	@Override
	public void endShard() {
		LOG.info("endShard()");
	}

	@Override
	public void endSlice() {
		LOG.info("endSlice()");
	}

}
