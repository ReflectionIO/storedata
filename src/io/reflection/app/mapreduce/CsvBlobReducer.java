package io.reflection.app.mapreduce;

import static com.willshex.gson.json.shared.Convert.toJsonObject;
import io.reflection.app.datatypes.Item;
import io.reflection.app.datatypes.Rank;
import io.reflection.app.service.item.ItemServiceProvider;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CsvBlobReducer extends Reducer<String, String, ByteBuffer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(CsvBlobReducer.class.getName());
	private static final String NOT_AVAILABLE = "NA";

	private String topType;
	private String grossingType;

	/**
	 * @param topType
	 * @param grossingType
	 */
	public CsvBlobReducer(String topType, String grossingType) {
		super();

		this.topType = topType;
		this.grossingType = grossingType;
	}

	@Override
	public void beginShard() {
		LOG.info("beginShard()");

		StringBuffer buffer = new StringBuffer();

		buffer.append("# data for ");
		buffer.append(topType);
		buffer.append(" and ");
		buffer.append(grossingType);
		buffer.append(" on shard id [");
		buffer.append(getContext().getShardNumber());
		buffer.append("]\n");
		buffer.append("#item id,date,top possition,grossing possition,price,usesiap");

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
		} else {
			buffer.append(NOT_AVAILABLE);
		}
		buffer.append(",");

		if (grossingItem != null) {
			buffer.append(grossingItem.position + 1);
		} else {
			buffer.append(NOT_AVAILABLE);
		}
		buffer.append(",");

		buffer.append(masterItem.price);

		buffer.append(",");

		buffer.append(iapColumn(masterItem, topItem, grossingItem));

		buffer.append("\n");

		getContext().emit(ByteBuffer.wrap(buffer.toString().getBytes()));
	}

	/**
	 * @param grossingItem
	 * @param topItem
	 * @param masterItem
	 * @return
	 */
	private String iapColumn(Rank master, Rank top, Rank grossing) {
		String iapColumn = null;
		try {
			if ((top != null && top.price == 0 && grossing != null) || // you are in the top free list and in the grossing list you are using iap
					usesIapViaLookup(master)) {
				iapColumn = "true";
			} else {
				iapColumn = "false";
			}
		} catch (Exception ex) {
			iapColumn = NOT_AVAILABLE;
		}

		return iapColumn;
	}

	private boolean usesIapViaLookup(Rank rank) throws Exception {

		String usesIapKey = rank.itemId + ".usesIap";

		MemcacheService memCacheService = MemcacheServiceFactory.getMemcacheService();

		Boolean usesIap = (Boolean) memCacheService.get(usesIapKey);

		if (usesIap == null) {

			usesIap = isRankItemIap(rank);

			// TODO: maybe enqueue request to find out

			if (usesIap == null) {
				throw new Exception("Could not find whether the app uses iap or not!");
			}

			memCacheService.put(usesIapKey, usesIap);
		}

		return usesIap.booleanValue();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private Boolean isRankItemIap(Rank rank) throws Exception {
		Boolean isRankItemIap = null;

		String rankItemKey = rank.itemId + ".item";

		MemcacheService memCacheService = MemcacheServiceFactory.getMemcacheService();

		Item item = (Item) memCacheService.get(rankItemKey);

		if (item == null) {
			item = ItemServiceProvider.provide().getExternalIdItem(rank.itemId);

			if (item == null) {
				throw new Exception("Could not find an item for this rank which is just, wrong!");
			}

			memCacheService.put(rankItemKey, item);

			JsonObject properties = toJsonObject(item.properties);

			if (properties != null) {
				JsonElement value = properties.get("usesIap");

				if (value != null) {
					isRankItemIap = Boolean.valueOf(value.getAsBoolean());
				}
			}
		}

		return isRankItemIap;
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
