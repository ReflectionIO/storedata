//
//  IosRankPipeline.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.accountdatacollectors.DataAccountCollector;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.accountdatacollectors.ITunesConnectDownloadHelper;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.ingestors.IngestorIOS;
import io.reflection.app.ingestors.ParserIOS;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccount.IDataAccountService;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.dataaccountfetch.IDataAccountFetchService;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

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
import com.google.appengine.tools.pipeline.PromisedValue;
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

	public static final String ACCOUNT_DATA_BUCKET_KEY = "account.data.bucket";

	public static class GatherAllRanks extends Job0<Integer> {

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

			final boolean ingestCountryFeeds = shouldIngestFeedFetch(countryCode);
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

			final boolean ingestCountryFeeds = shouldIngestFeedFetch(countryCode);
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

			PromisedValue<Date> salesDate = newPromise();

			// save the handle from salesDate.getHandle()

			futureCall(new CalibrateAll(), salesDate);

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

	public static final class CalibrateAll extends Job1<Void, Date> {

		private static final long serialVersionUID = -2335419676158668911L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<Void> run(Date date) throws Exception {

			return null;
		}

	}

	public static class GatherAllSales extends Job0<Integer> {

		private static final long serialVersionUID = 8112347752177694061L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job0#run()
		 */
		@Override
		public Value<Integer> run() throws Exception {
			Pager pager = new Pager().count(Long.valueOf(100));

			int count = 0;

			try {
				IDataAccountFetchService dataAccountFetchService = DataAccountFetchServiceProvider.provide();
				IDataAccountService dataAccountService = DataAccountServiceProvider.provide();

				List<DataAccount> dataAccounts = null;
				// get data accounts 100 at a time
				do {
					dataAccounts = dataAccountService.getDataAccounts(pager);

					for (DataAccount dataAccount : dataAccounts) {
						// if the account has some errors then don't bother otherwise enqueue a message to do a gather for it

						if (DataAccountFetchServiceProvider.provide().isFetchable(dataAccount) == Boolean.TRUE) {
							futureCall(new GatherDataAccountForDate(), immediate(dataAccount.id), immediate(DateTime.now().minusDays(1).toDate()));

							// go through all the failed attempts and get them too (failed attempts = less than 30 days old)
							List<DataAccountFetch> failedDataAccountFetches = dataAccountFetchService.getFailedDataAccountFetches(dataAccount,
									PagerHelper.createInfinitePager());

							for (DataAccountFetch dataAccountFetch : failedDataAccountFetches) {
								futureCall(new GatherDataAccountForDate(), immediate(dataAccount.id), immediate(dataAccountFetch.date));
							}

							count++;
						}
					}

					PagerHelper.moveForward(pager);

				} while (dataAccounts != null && dataAccounts.size() <= pager.count.intValue());
			} catch (DataAccessException dae) {
				LOG.log(GaeLevel.SEVERE, "A database error occured attempting to start sales gather process", dae);
			}

			return immediate(Integer.valueOf(count));
		}
	}

	public static class GatherDataAccountForDate extends Job2<Void, Long, Date> {

		private static final long serialVersionUID = -8706042892487009601L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<Void> run(Long dataAccountId, Date date) throws Exception {
			boolean sendNotification = false;

			try {
				DataAccount account = DataAccountServiceProvider.provide().getDataAccount(Long.valueOf(dataAccountId));

				if (account != null) {
					if (date == null) {
						date = new Date();
					}

					DataSource dataSource = DataSourceServiceProvider.provide().getDataSource(account.source.id);

					if (dataSource != null) {
						account.source = dataSource;

						DataAccountCollector collector = DataAccountCollectorFactory.getCollectorForSource(dataSource.a3Code);

						if (collector != null) {
							boolean status = collect(account, date);

							if (status && sendNotification) {
								Event event = EventServiceProvider.provide().getEvent(Long.valueOf(5));
								User user = UserServiceProvider.provide().getDataAccountOwner(account);

								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("user", user);

								String body = NotificationHelper.inflate(parameters, event.longBody);

								Notification notification = (new Notification()).from("hello@reflection.io").user(user).event(event).body(body)
										.subject(event.subject);
								Notification added = NotificationServiceProvider.provide().addNotification(notification);

								if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
									notification.type = NotificationTypeType.NotificationTypeTypeInternal;
									NotificationServiceProvider.provide().addNotification(notification);
								}
							}

						} else {
							if (LOG.isLoggable(GaeLevel.WARNING)) {
								LOG.log(GaeLevel.WARNING, "Could not find a collector for [%s]", dataSource.a3Code);
							}
						}
					} else {
						if (LOG.isLoggable(GaeLevel.WARNING)) {
							LOG.log(GaeLevel.WARNING, "Could not find a data source for id [%d]", account.source.id.longValue());
						}
					}

				}
			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE,
						String.format("Database error occured while trying to import data with accountid [%s] and date [%s]", dataAccountId, date), e);
			}

			return null;
		}

		private boolean collect(DataAccount dataAccount, Date date) throws DataAccessException {
			date = ApiHelper.removeTime(date);

			String dateParameter = ITunesConnectDownloadHelper.DATE_FORMATTER.format(date);

			if (LOG.isLoggable(GaeLevel.INFO)) {
				LOG.info(String.format("Getting data from itunes connect for data account [%s] and date [%s]", dataAccount.id == null ? dataAccount.username
						: dataAccount.id.toString(), dateParameter));
			}

			boolean success = false;
			String cloudFileName = null, error = null;
			try {
				cloudFileName = ITunesConnectDownloadHelper.getITunesSalesFile(dataAccount.username, dataAccount.password,
						ITunesConnectDownloadHelper.getVendorId(dataAccount.properties), dateParameter, System.getProperty(ACCOUNT_DATA_BUCKET_KEY),
						dataAccount.id.toString());
			} catch (Exception ex) {
				error = ex.getMessage();
			}

			DataAccountFetch dataAccountFetch = DataAccountFetchServiceProvider.provide().getDateDataAccountFetch(dataAccount, date);

			if (dataAccountFetch == null) {
				dataAccountFetch = new DataAccountFetch();

				dataAccountFetch.date = date;
				dataAccountFetch.linkedAccount = dataAccount;
			}

			if (dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
				if (error != null) {
					if (error.startsWith("There are no reports") || error.startsWith("There is no report")) {
						dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
					} else {
						dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeError;
					}

					dataAccountFetch.data = error;
				} else if (cloudFileName != null) {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered;
					dataAccountFetch.data = cloudFileName;
					success = true;
				} else {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
					success = true;
				}

				if (dataAccountFetch.id == null) {
					dataAccountFetch = DataAccountFetchServiceProvider.provide().addDataAccountFetch(dataAccountFetch);
				} else {
					dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
				}

				if (dataAccountFetch != null && dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
					futureCall(new IngestDataAccountFetch(), immediate(dataAccountFetch.id));
				}
			} else {
				LOG.warning(String.format("Gather for data account [%s] and date [%s] skipped because of status [%s]",
						dataAccount.id == null ? dataAccount.username : dataAccount.id.toString(), dateParameter, dataAccountFetch.status));
			}

			return success;
		}

	}

	public static class IngestDataAccountFetch extends Job1<Void, Long> {

		private static final long serialVersionUID = 3258834001291433964L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<Void> run(Long dataAccountFetchId) throws Exception {

			return null;
		}

	}

	private static boolean shouldIngestFeedFetch(String country) {
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
