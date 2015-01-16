//
//  IosRankPipeline.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.ingestors.IngestorIOS;
import io.reflection.app.ingestors.ParserIOS;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spacehopperstudios.utility.JsonUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
public class IosRankPipeline {

	private static final Logger LOG = Logger.getLogger(IosRankPipeline.class.getName());

	private static final String COUNTRIES_KEY = "gather." + DataTypeHelper.IOS_STORE_A3 + ".countries";

	// private static final String KEY_FORMAT = "gather." + DataTypeHelper.IOS_STORE_A3 + ".%s";
	// private static final String KEY_CATEGORY_FEED = "gather." + DataTypeHelper.IOS_STORE_A3 + ".category.feed.url";

	public static final String TOP_FREE_APPS = "topfreeapplications";
	public static final String TOP_PAID_APPS = "toppaidapplications";
	public static final String TOP_GROSSING_APPS = "topgrossingapplications";

	public static final String TOP_FREE_IPAD_APPS = "topfreeipadapplications";
	public static final String TOP_PAID_IPAD_APPS = "toppaidipadapplications";
	public static final String TOP_GROSSING_IPAD_APPS = "topgrossingipadapplications";

	public static class GatherAll extends Job0<Integer> {

		private static final long serialVersionUID = -6950514903299207863L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job0#run()
		 */
		@Override
		public Value<Integer> run() throws Exception {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Entering...");
			}

			int count = 0;

			try {
				String countries = System.getProperty(COUNTRIES_KEY);
				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Found countries [%s].", countries));
				}

				List<String> splitCountries = new ArrayList<String>();
				Collections.addAll(splitCountries, countries.split(","));

				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("[%d] countries to fetch data for", splitCountries.size()));
				}

				Long code = null;

				try {
					code = FeedFetchServiceProvider.provide().getCode();

					if (code == null && LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, "Got null code");
					} else {
						LOG.log(GaeLevel.DEBUG, String.format("Got code [%d] from feed fetch service", code.longValue()));
					}

					for (String countryCode : splitCountries) {
						if (LOG.isLoggable(GaeLevel.DEBUG)) {
							LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
						}

						futureCall(new GatherCountry(), immediate(countryCode), immediate(code));

						count++;
					}
				} catch (DataAccessException dae) {
					LOG.log(GaeLevel.SEVERE, "A database error occured attempting to to get an id code for gather enqueueing", dae);
				}

			} finally {
				if (LOG.isLoggable(GaeLevel.TRACE)) {
					LOG.log(GaeLevel.TRACE, "Exiting...");
				}
			}

			return immediate(Integer.valueOf(count));
		}
	}

	public static class GatherCountry extends Job2<Void, String, Long> {

		private static final long serialVersionUID = 3208213738551499825L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<Void> run(String countryCode, Long code) throws Exception {
			FutureValue<Long> free = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), null, immediate(code));
			FutureValue<Long> paid = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), null, immediate(code));
			FutureValue<Long> grossing = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), null, immediate(code));

			final boolean ingestCountryFeeds = shouldIngest(countryCode);
			if (ingestCountryFeeds) {
				FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paid);
				FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), free);
				FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossing);

				futureCall(new IngestRanks(), paid, slimmedPaidFeed, free, slimmedFreeFeed, grossing, slimmedGrossingFeed);
			}

			free = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), null, immediate(code));
			paid = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), null, immediate(code));
			grossing = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), null, immediate(code));

			if (ingestCountryFeeds) {
				FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paid);
				FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), free);
				FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossing);

				futureCall(new IngestRanks(), paid, slimmedPaidFeed, free, slimmedFreeFeed, grossing, slimmedGrossingFeed);
			}

			// when we have gathered all the counties feeds we do the same but for each category
			try {
				Store store = DataTypeHelper.getIosStore();

				// get the parent category all which references all lower categories
				Category all = CategoryServiceProvider.provide().getAllCategory(store);

				List<Category> categories = null;

				if (all != null) {
					Pager p = new Pager();
					p.start = Pager.DEFAULT_START;
					p.sortBy = Pager.DEFAULT_SORT_BY;
					p.count = CategoryServiceProvider.provide().getParentCategoriesCount(all);

					if (p.count != null && p.count.longValue() > 0) {
						categories = CategoryServiceProvider.provide().getParentCategories(all, p);

						if (categories != null && categories.size() > 0) {
							if (LOG.isLoggable(GaeLevel.DEBUG)) {
								LOG.log(GaeLevel.DEBUG, String.format("Found [%d] categories", categories.size()));
							}

							if (LOG.isLoggable(GaeLevel.DEBUG)) {
								LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
							}

							// for each category
							for (Category category : categories) {
								futureCall(new GatherCategory(), immediate(countryCode), immediate(category.id), immediate(code));
							}
						}
					}
				}

			} catch (DataAccessException dae) {
				LOG.log(GaeLevel.SEVERE, "A database error occured attempting to enqueue category top feeds for gathering phase", dae);
			}

			return null;
		}

	}

	public static class GatherCategory extends Job3<Void, String, Long, Long> {
		private static final long serialVersionUID = -2027106533638960844L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<Void> run(String countryCode, Long categoryId, Long code) throws Exception {

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for category [%d]", categoryId.longValue()));
			}

			Category category = CategoryServiceProvider.provide().getCategory(categoryId);

			FutureValue<Long> free = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), immediate(category.internalId),
					immediate(code));
			FutureValue<Long> paid = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), immediate(category.internalId),
					immediate(code));
			FutureValue<Long> grossing = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), immediate(category.internalId),
					immediate(code));

			final boolean ingestCountryFeeds = shouldIngest(countryCode);
			if (ingestCountryFeeds) {
				FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paid);
				FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), free);
				FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossing);

				futureCall(new IngestRanks(), paid, slimmedPaidFeed, free, slimmedFreeFeed, grossing, slimmedGrossingFeed);
			}

			free = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), immediate(category.internalId), immediate(code));
			paid = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), immediate(category.internalId), immediate(code));
			grossing = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), immediate(category.internalId), immediate(code));

			if (ingestCountryFeeds) {
				FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paid);
				FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), free);
				FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossing);

				futureCall(new IngestRanks(), paid, slimmedPaidFeed, free, slimmedFreeFeed, grossing, slimmedGrossingFeed);
			}

			return null;
		}

	}

	public static class GatherFeed extends Job4<Long, String, String, Long, Long> {

		private static final long serialVersionUID = 959780630540839671L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job4#run(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<Long> run(String countryCode, String listName, Long categoryInternalId, Long code) throws Exception {
			// we only care about the first id since we no longer attempt to store the feed in the feedfetch table (and even if we did we would only need the
			// first id)

			return immediate(new CollectorIOS().collect(countryCode, listName, categoryInternalId == null ? null : Long.toString(categoryInternalId), code)
					.get(0));
		}
	}

	public static class IngestRanks extends Job6<Void, Long, String, Long, String, Long, String> {

		private static final long serialVersionUID = 5579515120223362343L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job6#run(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public Value<Void> run(Long paidFeedId, String paidRanks, Long freeFeedId, String freeRanks, Long grossingFeedId, String grossingRanks)
				throws Exception {
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

	public static class SlimFeed extends Job1<String, Long> {

		private static final long serialVersionUID = -627864366513850701L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<String> run(Long feedId) throws Exception {
			String slimmed = null;

			List<FeedFetch> stored = null;
			Map<Date, Map<Integer, FeedFetch>> grouped = null;
			Map<Date, String> combined = null;

			stored = IngestorIOS.get(Arrays.asList(feedId));
			grouped = IngestorIOS.groupDataByDate(stored);
			combined = IngestorIOS.combineDataParts(grouped);

			for (final Date key : combined.keySet()) {
				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Parsing [%s]", key.toString()));
				}

				Map<Integer, FeedFetch> group = grouped.get(key);
				Iterator<FeedFetch> iterator = group.values().iterator();
				FeedFetch firstFeedFetch = iterator.next();

				List<Item> items = (new ParserIOS()).parse(firstFeedFetch.country, firstFeedFetch.category.id, combined.get(key));

				JsonArray itemJsonArray = new JsonArray();

				for (Item item : items) {
					itemJsonArray.add(item.toJson());
				}

				slimmed = JsonUtils.cleanJson(itemJsonArray.toString());

				// we are only expecting to do this for one feed for break just in-case
				break;
			}

			return immediate(slimmed);
		}

	}

	private static boolean shouldIngest(String country) {
		boolean ingest = false;

		Collection<String> countries = IngestorFactory.getIngestorCountries(DataTypeHelper.IOS_STORE_A3);

		if (countries.contains(country)) {
			ingest = true;
		} else {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Country [" + country + "] not in list of countries to ingest.");
			}
		}

		return ingest;
	}

}
