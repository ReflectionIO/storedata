//
//  DevUtilServlet.java
//  storedata
//
//  Created by mamin on 7 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.spacehopperstudios.utility.StringUtils;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.helpers.SplitDataHelper;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.ingestors.IngestorIOS;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.feedfetch.IFeedFetchService;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

@SuppressWarnings("serial")
public class DevUtilServlet extends HttpServlet {
	private transient static final Logger	LOG															= Logger.getLogger(DevUtilServlet.class.getName());

	public static final String						PARAM_ACTION										= "action";

	public static final String						PARAM_DATA_ACCOUNT_IDS					= "dataaccountids";
	public static final String						PARAM_DATA_ACCOUNT_ID						= "dataaccountid";
	public static final String						PARAM_ITEM_IDS									= "itemids";
	public static final String						PARAM_DATES											= "dates";
	public static final String						PARAM_DATE_RANGE								= "daterange";

	public static final String						PARAM_COUNTRIES									= "countries";
	public static final String						PARAM_CATEGORIES								= "categories";
	public static final String						PARAM_LIST_TYPES								= "types";
	public static final String						PARAM_PLATFORMS									= "platforms";
	public static final String						PARAM_TIME											= "time";
	public static final String						PARAM_GATHER_CONDITIONS					= "conditions";
	public static final String						PARAM_GATHER_CONDITION					= "condition";

	public static final String						PARAM_GATHER_CONDITION_MISSING	= "missing";
	public static final String						PARAM_GATHER_CONDITION_EMPTY		= "empty";
	public static final String						PARAM_GATHER_CONDITION_ERROR		= "error";
	public static final String						PARAM_GATHER_CONDITION_INGEST		= "ingest";
	public static final String						PARAM_GATHER_CONDITION_RETRY		= "retry";
	public static final String						PARAM_GATHER_CONDITION_REINGEST	= "reingest";
	public static final String						PARAM_GATHER_CONDITION_REGATHER	= "regather";
	public static final String						PARAM_GATHER_CONDITION_ALL			= "all";

	public static final String						ACTION_GATHER_SALES							= "sales";
	public static final String						ACTION_GATHER_RANKS							= "ranks";
	public static final String						ACTION_INGEST_RANKS							= "rank_ingest";
	public static final String						ACTION_SUMMARISE								= "summarise";
	public static final String						ACTION_SPLIT_DATA								= "split";
	public static final String						ACTION_SPLIT_DATA_TOP_200				= "splittop200";
	public static final String						ACTION_MODEL										= "model";

	public static final String						QUEUE_SUMMARISE									= "summarise";

	public static final String						URL_SUMMARISE										= "/summarise";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String appEngineQueue = req.getHeader("X-AppEngine-QueueName");
		final boolean isNotQueue = appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase());

		if (isNotQueue && (req.getParameter("defer") == null || req.getParameter("defer").equals("yes"))) {
			final Queue deferredQueue = QueueFactory.getQueue("deferred");
			final String query = req.getQueryString();

			if (query != null) {
				final String dest = req.getParameter("dest");

				String url = null;
				if (dest != null && dest.trim().length() > 0) {
					url = "/" + dest + "?" + query;
				} else {
					url = "/dev/devutil?" + query;
				}

				deferredQueue.add(TaskOptions.Builder.withUrl(url).method(Method.GET));
				LOG.log(GaeLevel.DEBUG, String.format("Added task to deferred queue with url: %s", url));
			} else {
				final TaskOptions options = Builder.withUrl("/dev/devutil");

				@SuppressWarnings("rawtypes")
				final Map params = req.getParameterMap();

				for (final Object param : params.keySet()) {
					options.param((String) param, req.getParameter((String) param));
				}

				deferredQueue.add(options.method(Method.POST));
				LOG.log(GaeLevel.DEBUG, String.format("Requeueing up the task in the defered queue for the devutil servlet"));
			}

			return;
		}   // end of if not from deferred queue then re-route it to the deferred queue

		execute(req, resp);
	}

	/**
	 * @param req
	 * @param resp
	 */
	private void execute(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if (action == null) {
			String msg = "No action provided.";
			writeResponse(resp, msg);
			return;
		} else {
			action = action.toLowerCase();
		}

		LOG.log(GaeLevel.DEBUG, String.format("Performing action: %s", action));

		String msg = null;
		switch (action) {
			case ACTION_GATHER_RANKS:
				msg = gatherRanks(req, resp);
				break;
			case ACTION_INGEST_RANKS:
				msg = ingestRanks(req, resp);
				break;
			case ACTION_GATHER_SALES:
				msg = gatherSales(req, resp);
				break;
			case ACTION_SUMMARISE:
				msg = summarise(req, resp);
				break;
			case ACTION_SPLIT_DATA:
				msg = splitData(req, resp);
				break;
			case ACTION_SPLIT_DATA_TOP_200:
				msg = splitDataTop200(req, resp);
				break;
			case ACTION_MODEL:
				msg = model(req, resp);
				break;
			default:
				msg = String.format("I don't understand this action %s, please try one of the following: %s,%s,%s,%s,%s,%s,%s", action,
						ACTION_GATHER_RANKS, ACTION_INGEST_RANKS, ACTION_GATHER_SALES, ACTION_SUMMARISE, ACTION_SPLIT_DATA, ACTION_SPLIT_DATA_TOP_200, ACTION_MODEL);

				LOG.log(GaeLevel.DEBUG, msg);
				break;
		}

		writeResponse(resp, msg);
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	private String ingestRanks(HttpServletRequest req, HttpServletResponse resp) {
		ArrayList<Date> datesToProcess = getDatesToProcess(req);
		if (datesToProcess.isEmpty()) {
			String msg = "There are no dates to process. Returning without doing anything.";

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		IngestorIOS ingestor = new IngestorIOS();

		StringBuilder builder = new StringBuilder();
		appendAndReturn("Starting rank ingests", builder);
		for (Date date : datesToProcess) {
			IFeedFetchService feedFetchService = FeedFetchServiceProvider.provide();
			List<FeedFetch> feedFetches = null;
			try {
				feedFetches = feedFetchService.getDatesFeedFetches(date);
			} catch (DataAccessException e) {
				appendAndReturn(String.format("Could not load rank fetches from DB for date %s. Exception thrown %s", date.toString(), e.getMessage()), builder);
			}

			if (feedFetches == null || feedFetches.size() == 0) {
				appendAndReturn(String.format("Didn't get any fetches for %s", date), builder);
				continue;
			}

			int skipped = 0, queued = 0;

			for (FeedFetch feedFetch : feedFetches) {
				if (feedFetch.status != FeedFetchStatusType.FeedFetchStatusTypeGathered) {
					skipped++;
					continue;
				}

				queued++;
				ingestor.enqueue(QueueFactory.getQueue("ingest"), feedFetch.id.toString());
			}

			appendAndReturn(String.format("Queued: %d, skipped: %d for %s", queued, skipped, date), builder);
		}

		return builder.toString();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	private String gatherRanks(HttpServletRequest req, HttpServletResponse resp) {
		/*
		 * Load all the rank_fetch records for today. Then loop through all the countries and categories and gather the missing ones.
		 */

		IFeedFetchService feedFetchService = FeedFetchServiceProvider.provide();
		List<FeedFetch> feedFetches = null;
		try {
			feedFetches = feedFetchService.getDatesFeedFetches(new Date());
		} catch (DataAccessException e) {
			String msg = String.format("Could not load rank fetches from DB. Exception thrown %s", e.getMessage());
			LOG.log(Level.WARNING, msg, e);
			return msg;
		}

		HashMap<String, Long> rankFetchMap = new HashMap<String, Long>(feedFetches.size());

		for (FeedFetch fetch : feedFetches) {
			String key = fetch.country + "_" + fetch.category.id + "_" + fetch.type;
			Long fetchId = fetch.id;

			rankFetchMap.put(key, fetchId);
		}

		final String countriesToGather = System.getProperty("ingest.ios.countries");
		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Found countries [%s].", countriesToGather));
		}

		final List<String> splitCountries = new ArrayList<String>();
		Collections.addAll(splitCountries, countriesToGather.split(","));

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("[%d] countries to fetch data for", splitCountries.size()));
		}

		final Queue queue = QueueFactory.getQueue("gather");
		if (queue == null) {
			String msg = String.format("Could not get the gather queue");
			LOG.log(Level.WARNING, msg);
			return msg;
		}

		Long code = null;
		try {
			code = FeedFetchServiceProvider.provide().getCode();
			if (code == null) {
				String msg = "Could not generate a gather code from the DB";
				LOG.log(Level.WARNING, msg);
				return msg;
			} else {
				LOG.log(GaeLevel.DEBUG, String.format("Got code [%d] from feed fetch service", code.longValue()));
			}
		} catch (DataAccessException e) {
			String msg = "Exception while generating a gather code from the DB";
			LOG.log(Level.WARNING, msg, e);
			return msg;
		}

		String[] ALL_TYPE_PLATFORM_COMBINATIONS = new String[] {
																															CollectorIOS.TOP_FREE_APPS,
																															CollectorIOS.TOP_PAID_APPS,
																															CollectorIOS.TOP_GROSSING_APPS,
																															CollectorIOS.TOP_FREE_IPAD_APPS,
																															CollectorIOS.TOP_PAID_IPAD_APPS,
																															CollectorIOS.TOP_GROSSING_IPAD_APPS
		};

		Long overallCategoryId = getOverallCategoryId();
		List<Category> categories = null;
		try {
			categories = CategoryServiceProvider.provide().getStoreCategories(StoreServiceProvider.provide().getA3CodeStore(DataTypeHelper.IOS_STORE_A3), PagerHelper.createInfinitePager());
		} catch (DataAccessException e) {
			LOG.log(GaeLevel.DEBUG, String.format("Exception while trying to queue up all the categories. %s", e.getMessage()), e);
		}

		CollectorIOS collector = new CollectorIOS();
		StringBuilder builder = new StringBuilder();

		int skipCount = 0;
		int enqueuedCount = 0;

		for (final String countryCode : splitCountries) {

			// QUEUE UP THE OVERALL CATEGORIES IF WE DON'T ALREADY HAVE THEM
			if (overallCategoryId == null) {
				appendAndReturn("Could not get the overall category id. Skipping the overall lists.", builder);
			} else {
				for (String type : ALL_TYPE_PLATFORM_COMBINATIONS) {
					String key = countryCode + "_" + overallCategoryId + "_" + type;

					if (rankFetchMap.containsKey(key)) {
						// we have already gathered this rank list.
						skipCount++;
						continue;
					}

					collector.enqueue(queue, countryCode, type, code);
					enqueuedCount++;
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("Enqueueing gather tasks for country [%s] overall type [%s]", countryCode, type), builder));
					}
				}
			}

			if (categories == null) {
				continue;
			}

			// QUEUE UP ALL CHILD CATEGORIES NEXT.
			for (Category category : categories) {
				// skip the overall category
				if (category.parent == null || category.parent.id == null) {
					continue;
				}

				for (String type : ALL_TYPE_PLATFORM_COMBINATIONS) {
					String key = countryCode + "_" + category.id + "_" + type;
					if (rankFetchMap.containsKey(key)) {
						// we have already gathered this rank list.
						skipCount++;
						continue;
					}

					collector.enqueue(queue, countryCode, type, category.internalId, code);
					enqueuedCount++;
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("Enqueueing gather tasks for country [%s] category [%s] type [%s] ", countryCode, category.name, type), builder));
					}
				}
			}
		}

		LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("Rank gather completed. Enqueued: %d, Skipped: %d", enqueuedCount, skipCount), builder));
		return builder.toString();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	private String gatherSales(HttpServletRequest req, HttpServletResponse resp) {
		ArrayList<Date> datesToProcess = getDatesToProcess(req);
		if (datesToProcess.isEmpty()) {
			String msg = "There are no dates to process. Returning without doing anything.";

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		List<Long> dataAccountIds = getLongParameters(req, PARAM_DATA_ACCOUNT_IDS);
		if (dataAccountIds == null) {
			dataAccountIds = getLongParameters(req, PARAM_DATA_ACCOUNT_ID);
		}

		if (dataAccountIds.isEmpty()) {
			try {
				dataAccountIds = DataAccountServiceProvider.provide().getAllDataAccountIDs();
			} catch (DataAccessException e) {
				String msg = "Exception occured while trying to get data account ids with sales between dates. Check the logs.";

				LOG.log(Level.WARNING, msg, e);
				return msg;
			}
		}

		if (dataAccountIds == null || dataAccountIds.isEmpty()) {
			String msg = String.format("There are no data accounts to process. Doing nothing.");

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		ArrayList<String> conditions = getStringParameters(req, PARAM_GATHER_CONDITIONS);
		if (conditions.isEmpty()) {
			conditions = getStringParameters(req, PARAM_GATHER_CONDITION);
		}

		boolean gatherMissing = false;
		boolean gatherEmpty = false;
		boolean gatherError = false;
		boolean ingest = false;
		boolean reingest = false;
		boolean regather = false;

		for (String condition : conditions) {
			switch (condition.toLowerCase()) {
				case PARAM_GATHER_CONDITION_MISSING:
					gatherMissing = true;
					break;
				case PARAM_GATHER_CONDITION_EMPTY:
					gatherEmpty = true;
					break;
				case PARAM_GATHER_CONDITION_ERROR:
					gatherError = true;
					break;
				case PARAM_GATHER_CONDITION_RETRY:
					gatherMissing = gatherEmpty = gatherError = ingest = true;
					break;
				case PARAM_GATHER_CONDITION_INGEST:
					ingest = true;
					break;
				case PARAM_GATHER_CONDITION_REINGEST:
					reingest = true;
					break;
				case PARAM_GATHER_CONDITION_REGATHER:
					gatherMissing = gatherEmpty = gatherError = ingest = reingest = regather = true;
					break;
				case PARAM_GATHER_CONDITION_ALL:
					gatherMissing = gatherEmpty = gatherError = ingest = reingest = true;
					break;
			}
		}

		if (!(gatherMissing || gatherEmpty || gatherError || ingest || reingest || regather)) {
			String msg = "No condition given. Nothing to do. Returning";
			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		StringBuilder webResponse = new StringBuilder();

		String msg = String.format("Gathing for %d accounts. Conditions: missing: %b, empty: %b, error: %b, ingest: %b, reingest: %b, regather: %b", dataAccountIds.size(),
				gatherMissing, gatherEmpty, gatherError, ingest, reingest, regather);
		LOG.log(GaeLevel.DEBUG, msg);
		webResponse.append(msg).append("\n\n");

		for (Date date : datesToProcess) {
			for (Long dataAccountId : dataAccountIds) {
				webResponse.append(gatherForAccountOnDateWithConditions(dataAccountId, date, gatherMissing, gatherEmpty, gatherError, ingest, reingest, regather))
						.append('\n');
			}
		}

		return webResponse.toString();
	}

	/**
	 * @param dataAccountId
	 * @param date
	 * @param gatherMissing
	 * @param gatherEmpty
	 * @param gatherError
	 * @param reingest
	 * @param regather
	 *
	 * @return
	 */
	private String gatherForAccountOnDateWithConditions(Long dataAccountId, Date date, boolean gatherMissing, boolean gatherEmpty, boolean gatherError,
			boolean ingest, boolean reingest, boolean regather) {
		StringBuilder builder = new StringBuilder();
		try {

			DataAccountFetch dataAccountFetch = DataAccountFetchServiceProvider.provide().getDataAccountFetch(dataAccountId, date);

			if (regather) {
				if (dataAccountFetch != null && dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
					LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("The fetch was previously marked as ingested so setting it as an error"), builder));
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeError;
					DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
				}

				LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("Triggering a gather for %d, on %s", dataAccountId, date), builder));
				DataAccountServiceProvider.provide().triggerSingleDateDataAccountFetch(dataAccountId, date);

				return builder.toString();
			}

			// MISSING
			if (dataAccountFetch == null || dataAccountFetch.status == null) {
				if (gatherMissing) {
					LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("Fetch for %d on %s missing. Enqueuing for gather", dataAccountId, date), builder));
					DataAccountServiceProvider.provide().triggerSingleDateDataAccountFetch(dataAccountId, date);
				} else {
					LOG.log(GaeLevel.DEBUG,
							String.format("Fetch status for %d on %s is missing but condition for missing not set. Skipping", dataAccountId, date), builder);
				}

				return builder.toString();
			}

			// EMPTY
			if (dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty) {
				if (gatherEmpty) {

					LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("The fetch was previously marked as empty. Enqueuing for gather"), builder));
					DataAccountServiceProvider.provide().triggerSingleDateDataAccountFetch(dataAccountId, date);
				} else {
					LOG.log(GaeLevel.DEBUG, String.format("Fetch status for %d on %s is empty but condition for empty not set. Skipping", dataAccountId, date),
							builder);
				}

				return builder.toString();
			}

			// ERROR
			if (dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeError) {
				if (gatherError) {
					LOG.log(GaeLevel.DEBUG, appendAndReturn(String.format("The fetch was previously marked as error. Enqueuing for gather"), builder));
					DataAccountServiceProvider.provide().triggerSingleDateDataAccountFetch(dataAccountId, date);

				} else {
					LOG.log(GaeLevel.DEBUG, String.format("Fetch status for %d on %s is error but condition for error not set. Skipping", dataAccountId, date),
							builder);
				}
				return builder.toString();
			}

			// INGESTED
			if (dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
				if (reingest) {
					LOG.log(GaeLevel.DEBUG,
							appendAndReturn(String.format("The fetch was previously marked as ingested. Updating status to gathered"), builder));

					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered;
					DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
					DataAccountFetchServiceProvider.provide().triggerDataAccountFetchIngest(dataAccountFetch);

				} else {
					LOG.log(GaeLevel.DEBUG,
							String.format("Fetch status for %d on %s is ingested but condition for reingest not set. Skipping", dataAccountId, date), builder);
					return builder.toString();
				}
			}

			// GATHERED BUT NOT PREVIOUSLY INGESTED (OR SET TO GATHERED IN THE PREVIOUS BLOCK)
			if (dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
				if ((ingest || reingest)) {
					LOG.log(GaeLevel.DEBUG,
							appendAndReturn(String.format("Fetch status for %d on %s is gathered. Enqueuing for ingest", dataAccountId, date), builder));
					DataAccountFetchServiceProvider.provide().triggerDataAccountFetchIngest(dataAccountFetch);

				} else {
					LOG.log(GaeLevel.DEBUG, String.format("Fetch status for %d on %s is error but condition for error not set. Skipping", dataAccountId, date),
							builder);
				}
				return builder.toString();
			}

		} catch (DataAccessException e) {
			LOG.log(Level.WARNING, appendAndReturn(
					String.format("Exception while trying to gather for account id %d on a %s. %s", dataAccountId, date, e.getMessage()), builder), e);
		}
		return builder.toString();
	}

	/**
	 * @param req
	 * @param resp
	 */
	private String summarise(HttpServletRequest req, HttpServletResponse resp) {
		ArrayList<Date> datesToProcess = getDatesToProcess(req);
		if (datesToProcess.isEmpty()) {
			String msg = "There are no dates to process. Returning without doing anything.";

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		List<Long> dataAccountIds = getLongParameters(req, PARAM_DATA_ACCOUNT_IDS);
		if (dataAccountIds.isEmpty()) {
			try {
				dataAccountIds = DataAccountServiceProvider.provide().getAllDataAccountIDs();
			} catch (DataAccessException e) {
				String msg = "Exception occured while trying to get data account ids with sales between dates. Check the logs.";

				LOG.log(Level.WARNING, msg, e);
				return msg;
			}
		}

		if (dataAccountIds == null || dataAccountIds.isEmpty()) {
			String msg = String.format("There are no data accounts to process. Doing nothing.");

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		StringBuilder webResponse = new StringBuilder();
		SimpleDateFormat sqlDateFormat = SqlQueryHelper.getSqlDateFormat();
		for (Date date : datesToProcess) {
			for (Long dataAccountId : dataAccountIds) {
				String sqlDate = sqlDateFormat.format(date);

				String taskName = "summarise_" + dataAccountId + "_" + sqlDate + "-" + System.currentTimeMillis();

				QueueHelper.enqueue(QUEUE_SUMMARISE, URL_SUMMARISE, Method.GET, new SimpleEntry<String, String>("taskName", taskName),
						new SimpleEntry<String, String>("dataaccountid", dataAccountId.toString()), new SimpleEntry<String, String>("date", sqlDate));

				String logMsg = String.format("Enqueued summarisation for data account %d on %s with taskName %s", dataAccountId, sqlDate, taskName);
				LOG.log(GaeLevel.DEBUG, logMsg);
				webResponse.append(logMsg).append('\n');
			}
		}

		return webResponse.toString();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	private String model(HttpServletRequest req, HttpServletResponse resp) {
		// get platforms
		// get countries
		// get date range
		// get types
		StringBuilder webResponse = new StringBuilder();

		ArrayList<String> platformList = getStringParameters(req, PARAM_PLATFORMS);
		ArrayList<String> typeList = getStringParameters(req, PARAM_LIST_TYPES);
		ArrayList<String> categoryList = getStringParameters(req, PARAM_CATEGORIES);
		ArrayList<String> countryList = getStringParameters(req, PARAM_COUNTRIES);
		String time = req.getParameter(PARAM_TIME);

		if (platformList.isEmpty()) {
			platformList.add("TABLET");
			platformList.add("PHONE");

			String msg = "Platforms not specified. Including both phones and tables.";
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);
		}

		if (typeList.isEmpty()) {
			typeList.add("FREE");
			typeList.add("PAID");
			typeList.add("GROSSING");

			String msg = "List types not specified. Including Free, Paid and Grossing.";
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);
		}

		if (categoryList.isEmpty()) {
			categoryList.add("15");
			categoryList.add("24");

			String msg = "Categories not specified. Including Games (15) and Overall (24)";
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);
		}

		if (categoryList.size() == 1 && categoryList.get(0).equalsIgnoreCase("all")) {
			categoryList.clear();
			for (int i = 1; i < 25; i++) {
				categoryList.add("" + i);
			}
			String msg = "Added all categories to be modelled";
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);
		}

		if (countryList.isEmpty()) {
			String validCountries = System.getProperty("ingest.ios.countries");
			validCountries = validCountries == null ? "" : validCountries.toLowerCase();

			String msg = "Countries not specified. Including " + validCountries;
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);

			for (String validCountry : validCountries.split(",")) {
				String country = validCountry.trim();
				countryList.add(country);
			}
		}

		boolean usingDateRange = true;
		String dateRange = req.getParameter(PARAM_DATE_RANGE);
		String dates = req.getParameter(PARAM_DATES);

		if (dateRange == null || dateRange.trim().length() == 0) {
			if (dates == null || dates.trim().length() == 0) {
				String msg = "There are no dates to process. Returning without doing anything.";

				LOG.log(GaeLevel.DEBUG, msg);
				return msg;
			}
			usingDateRange = false;
		}

		String dateCondition = usingDateRange ? SqlQueryHelper.getDateRangeCondition("fetch_date", dateRange.split(","))
				: SqlQueryHelper.getDateListCondition("fetch_date", dates.split(","));
		if (dateCondition == null || dateCondition.length() == 0) {
			String msg = "Could not generate a date condition based on the input params. Using date range: " + usingDateRange + ", " + PARAM_DATE_RANGE + " = "
					+ dateRange + ", " + PARAM_DATES + " = " + dates;

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		// if there is no time param or it is = all then ignore time else if it is am then do am else do pm.
		String timeCondition = time == null ? ""
				: time.trim().equalsIgnoreCase("all") ? "" : time.trim().equalsIgnoreCase("am") ? " AND fetch_time<'13:00'" : " AND fetch_time>'21:00'";

		String selectQuery = String.format(
				"SELECT rank_fetch_id from rank_fetch where %s AND country in ('%s') AND category in (%s) and type in ('%s') and platform in ('%s') %s",
				dateCondition, StringUtils.join(countryList, "', '"), StringUtils.join(categoryList, ", "), StringUtils.join(typeList, "', '"),
				StringUtils.join(platformList, "', '"), timeCondition);

		String msg = "Running select query: " + selectQuery;
		webResponse.append(msg).append('\n');
		LOG.log(GaeLevel.DEBUG, msg);

		ArrayList<Long> rankFetchIds = new ArrayList<Long>();
		try {
			final Connection con = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());
			con.connect();
			con.executeQuery(selectQuery);

			while (con.fetchNextRow()) {
				rankFetchIds.add(con.getCurrentRowLong("rank_fetch_id"));
			}

			con.disconnect();
		} catch (NullPointerException | DataAccessException e) {
			LOG.log(Level.WARNING, "DB Exception", e);
			webResponse.append(String.format("An exception occured while running the statement: %s", e.getMessage())).append('\n');
			return webResponse.toString();
		}

		LOG.log(GaeLevel.DEBUG, String.format("Got %d rank fetch ids. Processing...", rankFetchIds.size()));

		final Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);
		for (Long rankFetchId : rankFetchIds) {
			modeller.enqueueFetchId(rankFetchId);
			webResponse.append("Modelling rank fetch id ").append(rankFetchId).append('\n');
		}

		return webResponse.toString();
	}

	/**
	 * Although this method get the split data for the whole month,
	 * it only gets it for the items that have been in the top 200 for the dates covered by the dates params.
	 * Don't call this method with the daterange param as it will queue up items multiple times for the same timeframe
	 * if they have appeared in top 200 multiple times in that date range.
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	private String splitDataTop200(HttpServletRequest req, HttpServletResponse resp) {
		StringBuilder webResponse = new StringBuilder("Splitting data for top 200 sales items\n\n");

		ArrayList<Date> datesToProcess = getDatesToProcess(req);
		if (datesToProcess.isEmpty()) {
			String msg = "There are no dates to process. Returning without doing anything.";

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		ArrayList<String> countryList = getStringParameters(req, PARAM_COUNTRIES);
		if (countryList.isEmpty()) {
			String validCountries = System.getProperty("ingest.ios.countries");
			validCountries = validCountries == null ? "" : validCountries.toLowerCase();

			String msg = "Countries not specified. Including " + validCountries;
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);

			for (String validCountry : validCountries.split(",")) {
				String country = validCountry.trim();
				countryList.add(country);
			}
		}

		for (Date date : datesToProcess) {
			for (String country : countryList) {
				try {
					HashMap<Long, ArrayList<Long>> dataAccountIdItemIdsInTop200 = SaleServiceProvider.provide().getItemSalesInTop200(date, country);

					for (Long dataAccountId : dataAccountIdItemIdsInTop200.keySet()) {
						SplitDataHelper.INSTANCE.enqueueToGatherSplitData(dataAccountId, date, countryList, dataAccountIdItemIdsInTop200.get(dataAccountId));
					}
				} catch (DataAccessException e) {
					LOG.log(Level.WARNING, String.format("Could not get and enqueue the item ids and data account ids for sales on %s for %s", date, country), e);
				}
			}
		}

		return webResponse.toString();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	private String splitData(HttpServletRequest req, HttpServletResponse resp) {
		StringBuilder webResponse = new StringBuilder("Splitting data\n\n");

		ArrayList<Date> datesToProcess = getDatesToProcess(req);
		if (datesToProcess.isEmpty()) {
			String msg = "There are no dates to process. Returning without doing anything.";

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		List<Long> dataAccountIds = getLongParameters(req, PARAM_DATA_ACCOUNT_IDS);
		if (dataAccountIds.isEmpty()) {
			try {
				dataAccountIds = SaleServiceProvider.provide().getDataAccountIdsWithSaleSummariesBetweenDates(datesToProcess.get(0),
						datesToProcess.get(datesToProcess.size() - 1));
			} catch (DataAccessException e) {
				String msg = "Exception occured while trying to get data account ids with sales between dates. Check the logs.";

				LOG.log(Level.WARNING, msg, e);
				return msg;
			}
		}

		if (dataAccountIds == null || dataAccountIds.isEmpty()) {
			String msg = String.format("There are no data accounts to process. Doing nothing.");

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		ArrayList<String> countryList = getStringParameters(req, PARAM_COUNTRIES);
		if (countryList.isEmpty()) {
			String validCountries = System.getProperty("ingest.ios.countries");
			validCountries = validCountries == null ? "" : validCountries.toLowerCase();

			String msg = "Countries not specified. Including " + validCountries;
			webResponse.append(msg).append('\n');
			LOG.log(GaeLevel.DEBUG, msg);

			for (String validCountry : validCountries.split(",")) {
				String country = validCountry.trim();
				countryList.add(country);
			}
		}

		List<Long> itemIds = getLongParameters(req, PARAM_ITEM_IDS);

		for (Date date : datesToProcess) {
			for (Long dataAccountId : dataAccountIds) {
				String msg = String.format("Requesting split data for %d on %s", dataAccountId, date);
				LOG.log(GaeLevel.DEBUG, msg);
				webResponse.append(msg).append('\n');
				SplitDataHelper.INSTANCE.enqueueToGatherSplitData(dataAccountId, date, countryList, itemIds);
			}
		}

		return webResponse.toString();
	}

	/**
	 * @return
	 */
	private Long getOverallCategoryId() {
		try {
			Category allCategory = CategoryServiceProvider.provide().getAllCategory(DataTypeHelper.getIosStore());
			if (allCategory != null) return allCategory.id;
		} catch (DataAccessException e) {
			LOG.log(GaeLevel.DEBUG, String.format("Exception getting the overall category from DB %s", e.getMessage()), e);
		}
		return null;
	}

	/**
	 * @param req
	 * @return
	 */
	private ArrayList<Date> getDatesToProcess(HttpServletRequest req) {
		SimpleDateFormat sqlDateFormat = SqlQueryHelper.getSqlDateFormat();

		ArrayList<Date> datesToProcess = new ArrayList<Date>();
		ArrayList<String> dateRangeStr = getStringParameters(req, PARAM_DATE_RANGE);

		if (!dateRangeStr.isEmpty()) {
			if (dateRangeStr.size() == 1) {
				dateRangeStr.add(sqlDateFormat.format(new Date()));
			}

			LOG.log(GaeLevel.DEBUG, String.format("Got a date range with %d elements", dateRangeStr.size()));

			ArrayList<Date> sortedDates = getSortedDatesFromStrings(dateRangeStr);

			Date from = sortedDates.get(0);
			Date to = sortedDates.get(1);
			Calendar toCal = Calendar.getInstance();
			toCal.setTime(to);

			LOG.log(GaeLevel.DEBUG, String.format("Range is %s to %s", from, to));

			Calendar cal = Calendar.getInstance();
			cal.setTime(from);
			while (cal.before(toCal)) {
				datesToProcess.add(cal.getTime());
				cal.add(Calendar.DATE, 1);
			}

			LOG.log(GaeLevel.DEBUG, String.format("Added %d dates to be processed", datesToProcess.size()));
		} else {
			ArrayList<String> datesStr = getStringParameters(req, PARAM_DATES);
			if (datesStr.isEmpty()) {
				LOG.log(GaeLevel.DEBUG, String.format("We didn't get any dates or date ranges"));
				return datesToProcess; // empty
			} else {
				LOG.log(GaeLevel.DEBUG, String.format("Date range not given but we have %d dates", datesStr.size()));
			}

			datesToProcess = getSortedDatesFromStrings(datesStr);
		}

		return datesToProcess;
	}

	/**
	 * @param dates
	 * @return
	 */
	private ArrayList<Date> getSortedDatesFromStrings(ArrayList<String> dates) {
		SimpleDateFormat sdf = SqlQueryHelper.getSqlDateFormat();

		ArrayList<Date> sortedDates = new ArrayList<Date>(dates.size());
		for (String date : dates) {
			try {
				sortedDates.add(sdf.parse(date));
			} catch (ParseException e) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid date in date list %s", date));
			}
		}

		Collections.sort(sortedDates);
		return sortedDates;
	}

	/**
	 * @param req
	 * @param paramDates
	 * @return
	 */
	private ArrayList<String> getStringParameters(HttpServletRequest req, String paramName) {
		String parameterValue = req.getParameter(paramName);
		if (parameterValue == null || parameterValue.trim().length() == 0) return new ArrayList<String>(0);

		String[] values = parameterValue.split(",");

		ArrayList<String> list = new ArrayList<String>(values == null ? 0 : values.length);

		if (values == null || values.length == 0) return list;

		for (String value : values) {
			if (value != null && value.trim().length() > 0) {
				list.add(value.trim());
			}
		}

		return list;
	}

	/**
	 * @param req
	 * @param paramName
	 * @return
	 */
	private List<Long> getLongParameters(HttpServletRequest req, String paramName) {
		String parameterValue = req.getParameter(paramName);
		if (parameterValue == null || parameterValue.trim().length() == 0) return new ArrayList<Long>(0);

		String[] values = parameterValue.split(",");

		ArrayList<Long> list = new ArrayList<Long>(values == null ? 0 : values.length);

		if (values == null || values.length == 0) return list;

		for (String value : values) {
			if (value != null && value.trim().length() > 0) {
				try {
					list.add(Long.parseLong(value));
				} catch (NumberFormatException e) {
				}
			}
		}

		return list;
	}

	/**
	 * @param msg
	 * @param builder
	 * @return
	 */
	private String appendAndReturn(String msg, StringBuilder builder) {
		builder.append(msg).append('\n');
		return msg;
	}

	/**
	 * @param resp
	 * @param msg
	 */
	private void writeResponse(HttpServletResponse resp, String msg) {
		try {
			resp.setContentType("text/html");
			PrintWriter writer = resp.getWriter();
			writer.println("<html><body><pre>");
			writer.println(msg);
			writer.println("</pre></body></html>");
			writer.flush();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not write to the servlet response stream", e);
		}

		if (msg == null) return;

		try {
			NotificationHelper.sendEmail("hello@reflection.io (Reflection)", System.getProperty("devadmin.email"), System.getProperty("devadmin.name"),
					"DevUtil Servlet Log output", msg, false);
		} catch (Exception e) {
			LOG.log(Level.WARNING, String.format("Exception while trying to send the email with the msg: \n%s", msg), e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
