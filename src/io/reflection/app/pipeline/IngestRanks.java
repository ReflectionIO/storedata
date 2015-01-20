//
//  IngestRanks.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spacehopperstudios.utility.StringUtils;

public class IngestRanks extends Job6<Void, Long, String, Long, String, Long, String> {

	private static final long serialVersionUID = 5579515120223362343L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job6#run(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Value<Void> run(Long paidFeedId, String paidRanks, Long freeFeedId, String freeRanks, Long grossingFeedId, String grossingRanks) throws Exception {
		FeedFetch paidFetch = FeedFetchServiceProvider.provide().getFeedFetch(paidFeedId);
		FeedFetch freeFetch = FeedFetchServiceProvider.provide().getFeedFetch(freeFeedId);
		FeedFetch grossingFetch = FeedFetchServiceProvider.provide().getFeedFetch(grossingFeedId);

		List<Item> paidRankList = itemsFromJson(paidRanks), freeRankList = itemsFromJson(freeRanks), grossingRankList = itemsFromJson(grossingRanks);
		Map<String, Rank> ranks = new HashMap<String, Rank>();
		Map<String, Item> items = new HashMap<String, Item>();

		int size = paidRankList == null ? 0 : paidRankList.size();
		Item item;
		Rank rank;
		for (int i = 0; i < size; i++) {
			item = paidRankList.get(i);
			rank = new Rank().category(paidFetch.category).code(paidFetch.code).country(paidFetch.country).currency(item.currency).date(paidFetch.date)
					.position(Integer.valueOf(i)).itemId(item.internalId).price(item.price).source(paidFetch.store).type(paidFetch.type);

			items.put(item.internalId, item);
			ranks.put(item.internalId, rank);
		}

		size = freeRankList == null ? 0 : freeRankList.size();
		for (int i = 0; i < size; i++) {
			item = freeRankList.get(i);
			rank = new Rank().category(freeFetch.category).code(freeFetch.code).country(freeFetch.country).currency(item.currency).date(freeFetch.date)
					.position(Integer.valueOf(i)).itemId(item.internalId).price(item.price).source(freeFetch.store).type(freeFetch.type);

			items.put(item.internalId, item);
			ranks.put(item.internalId, rank);
		}

		size = grossingRankList == null ? 0 : grossingRankList.size();
		for (int i = 0; i < size; i++) {
			item = grossingRankList.get(i);

			rank = ranks.get(item.internalId);

			if (rank == null) {
				rank = new Rank().category(grossingFetch.category).code(grossingFetch.code).country(grossingFetch.country).currency(item.currency)
						.date(grossingFetch.date).itemId(item.internalId).price(item.price).source(grossingFetch.store).type(grossingFetch.type);

				items.put(item.internalId, item);
				ranks.put(item.internalId, rank);
			}

			rank.grossingPosition(Integer.valueOf(i));
		}

		if (ranks.size() > 0) {
			RankServiceProvider.provide().addRanksBatch(ranks.values());
		}

		Collection<String> existingInternalIds = ItemServiceProvider.provide().getExistingInternalIdBatch(items.keySet());

		for (String internalId : existingInternalIds) {
			items.remove(internalId);
		}

		if (items.size() > 0) {
			ItemServiceProvider.provide().addItemsBatch(items.values());
		}

		PromisedValue<String> salesSummary = newPromise();

		// save the handle from salesDate.getHandle()

		Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);
		String key = StringUtils.join(Arrays.asList(grossingFetch.country, grossingFetch.store, modeller.getForm(freeFetch.type).toString()), ".");

		PersistentMapFactory.createObjectify().put(key, salesSummary.getHandle());

		// TODO: we need to kick off a specific calibration cycle for the country store and type

		return null;
	}

	private List<Item> itemsFromJson(String json) {
		List<Item> items = new ArrayList<Item>();
		JsonElement jsonElement = (new JsonParser()).parse(json);

		if (jsonElement != null && jsonElement.isJsonArray()) {
			JsonArray itemJsonArray = jsonElement.getAsJsonArray();

			Item item;
			Date date = DateTime.now(DateTimeZone.UTC).toDate();

			for (JsonElement current : itemJsonArray) {
				item = new Item();
				item.fromJson(current.getAsJsonObject());

				if (item.added == null) {
					item.added = date;
				}

				items.add(item);
			}
		}

		return items;
	}
}