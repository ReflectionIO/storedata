//
//  DevUtilServlet.java
//  storedata
//
//  Created by mamin on 7 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.helpers.SplitDataHelper;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

@SuppressWarnings("serial")
public class DevUtilServlet extends HttpServlet {
	private transient static final Logger LOG = Logger.getLogger(DevUtilServlet.class.getName());

	public static final String	PARAM_ACTION						= "action";
	public static final String	PARAM_DATA_ACCOUNT_IDS	= "dataaccountids";
	public static final String	PARAM_DATES							= "dates";
	public static final String	PARAM_DATE_RANGE				= "daterange";
	public static final String	PARAM_PLATFORMS					= "platforms";
	public static final String	PARAM_LIST_TYPES				= "types";
	public static final String	PARAM_CATEGORIES				= "categories";
	public static final String	PARAM_COUNTRIES					= "countries";

	public static final String	ACTION_SUMMARISE	= "summarise";
	public static final String	ACTION_SPLIT_DATA	= "split";
	public static final String	ACTION_MODEL			= "model";

	public static final String QUEUE_SUMMARISE = "summarise";

	public static final String URL_SUMMARISE = "/summarise";


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
		}     // end of if not from deferred queue then re-route it to the deferred queue

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

		String msg = null;
		switch (action) {
			case ACTION_SUMMARISE:
				msg = summarise(req, resp);
				break;
			case ACTION_SPLIT_DATA:
				msg = splitData(req, resp);
				break;
			case ACTION_MODEL:
				msg = model(req, resp);
				break;
		}

		resp.setContentType("text/plain");
		writeResponse(resp, msg);
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

				QueueHelper.enqueue(QUEUE_SUMMARISE, URL_SUMMARISE, Method.GET,
						new SimpleEntry<String, String>("taskName", taskName),
						new SimpleEntry<String, String>("dataaccountid", dataAccountId.toString()),
						new SimpleEntry<String, String>("date", sqlDate));


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

		if(dateRange==null || dateRange.trim().length()==0){
			if(dates==null || dates.trim().length()==0){
				String msg = "There are no dates to process. Returning without doing anything.";

				LOG.log(GaeLevel.DEBUG, msg);
				return msg;
			}
			usingDateRange = false;
		}

		String dateCondition = usingDateRange?SqlQueryHelper.getDateRangeCondition("fetch_date", dateRange.split(",")):SqlQueryHelper.getDateListCondition("fetch_date", dates.split(","));
		if(dateCondition==null || dateCondition.length()==0){
			String msg = "Could not generate a date condition based on the input params. Using date range: "+usingDateRange+", "+PARAM_DATE_RANGE+" = "+dateRange+", "+PARAM_DATES+" = "+dates;

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		String selectQuery = String.format("SELECT rank_fetch_id from rank_fetch where %s AND country in ('%s') AND category in (%s) and type in ('%s') and platform in ('%s') ",
				dateCondition,
				StringUtils.join(countryList, "', '"),
				StringUtils.join(categoryList, ", "),
				StringUtils.join(typeList, "', '"),
				StringUtils.join(platformList, "', '"));

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
	 * @param req
	 * @param resp
	 * @return
	 */
	private String splitData(HttpServletRequest req, HttpServletResponse resp) {
		ArrayList<Date> datesToProcess = getDatesToProcess(req);
		if (datesToProcess.isEmpty()) {
			String msg = "There are no dates to process. Returning without doing anything.";

			LOG.log(GaeLevel.DEBUG, msg);
			return msg;
		}

		List<Long> dataAccountIds = getLongParameters(req, PARAM_DATA_ACCOUNT_IDS);
		if (dataAccountIds.isEmpty()) {
			try {
				dataAccountIds = SaleServiceProvider.provide().getDataAccountIdsWithSaleSummariesBetweenDates(datesToProcess.get(0), datesToProcess.get(datesToProcess.size() - 1));
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
		for (Date date : datesToProcess) {
			for (Long dataAccountId : dataAccountIds) {
				String msg = String.format("Requesting split data for %d on %s", dataAccountId, date);
				LOG.log(GaeLevel.DEBUG, msg);
				webResponse.append(msg).append('\n');
				SplitDataHelper.INSTANCE.enqueueToGatherSplitData(dataAccountId, date);
			}
		}

		return webResponse.toString();
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
	 * @param resp
	 * @param msg
	 */
	private void writeResponse(HttpServletResponse resp, String msg) {
		try {
			resp.getWriter().println(msg);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not write to the servlet response stream", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
