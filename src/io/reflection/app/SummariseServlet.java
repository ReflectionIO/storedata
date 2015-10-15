package io.reflection.app;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.TaskOptions.Method;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.lookupitem.LookupItemService;
import io.reflection.app.service.sale.ISaleService;
import io.reflection.app.service.sale.SaleServiceProvider;

@SuppressWarnings("serial")
public class SummariseServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(SummariseServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine summarise queue
		if (isNotQueue = appEngineQueue == null || !"summarise".toLowerCase().equals(appEngineQueue.toLowerCase())) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.warning("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servlet is being called from [%s] queue", appEngineQueue));
			}
		}

		try{
			final String dataAccountIdStr = req.getParameter("dataaccountid");
			final String dateStr = req.getParameter("date");

			Long dataAccountId = Long.parseLong(dataAccountIdStr);
			if (dataAccountId == null || dataAccountId == 0L) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid data account id %s", dataAccountIdStr));
				return;
			}

			Date date = null;
			try {
				date = SqlQueryHelper.getSqlDateFormat().parse(dateStr);
			} catch (ParseException e1) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid date %s", dateStr), e1);
			}

			if (date == null) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid date %s", dateStr));
				return;
			}

			try {
				LOG.log(GaeLevel.DEBUG, String.format("Summarising dataAccountId %d on %s", dataAccountId, dateStr));

				SaleServiceProvider.provide().summariseSalesForDataAccountOnDate(dataAccountId, date);

				LOG.log(GaeLevel.DEBUG, String.format("Summarisation complete. Queuing up this summary for splitting sales data"));

				enqueueToGatherSplitData(dataAccountId, date);

			} catch (NumberFormatException | DataAccessException e) {
				LOG.log(Level.SEVERE, String.format("Unable to execute summarisation for dataAccountId: %s on %s", dataAccountId, date), e);
			}
		}finally{
			resp.setHeader("Cache-Control", "no-cache");
		}
	}

	/**
	 * @param dataAccountId
	 * @param date
	 */
	public void enqueueToGatherSplitData(Long dataAccountId, Date date) {
		/*
		 * Get all sale summary rows for this dataaccount for this date. For each item id, get its iap ids and then for each country the main item is in,
		 * enqueue the item for gathering the splits
		 */

		LOG.log(GaeLevel.DEBUG, String.format("Enqueuing split data gathers for data account id %d on %s", dataAccountId, date));

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		Date gatherFrom = cal.getTime();

		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1); // last day of the month

		Date gatherTo = date;
		Date lastDayOfMonth = cal.getTime();

		if (lastDayOfMonth.before(new Date())) {
			// the last day of that month is before today so we can fetch the whole month that month
			gatherTo = lastDayOfMonth;
			LOG.log(GaeLevel.DEBUG, String.format("The last date of the given month is before today for so gathering split data for the full month"));
		}

		LOG.log(GaeLevel.DEBUG, String.format("Gathering for period from %s to %s", gatherFrom, gatherTo));

		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");

		String gatherFromStr = sdf.format(gatherFrom);
		String gatherToStr = sdf.format(gatherTo);
		try {
			LookupItemService lookupService = LookupItemService.INSTANCE;

			List<LookupItem> lookupItemsForAccount = lookupService.getLookupItemsForAccount(dataAccountId);
			HashMap<String, String> parentItemsByIaps = lookupService.mapItemsByParentId(lookupItemsForAccount);

			ISaleService saleService = SaleServiceProvider.provide();

			List<SimpleEntry<String, String>> mainItemIdsAndCountries = saleService.getSoldItemIdsForAccountInDateRange(dataAccountId, gatherFrom, gatherTo);
			LOG.log(GaeLevel.DEBUG, String.format("Got %d combinations of main item id and country", mainItemIdsAndCountries.size()));

			String countriesToIngest = System.getProperty("ingest.ios.countries");
			countriesToIngest = countriesToIngest == null ? null : countriesToIngest.toLowerCase();

			LOG.log(GaeLevel.DEBUG, String.format("Countries to ingest are %s. Processing %d main item id / country combinations", countriesToIngest, mainItemIdsAndCountries.size()));

			for (SimpleEntry<String, String> entry : mainItemIdsAndCountries) {
				try {
					String country = entry.getValue();

					if (countriesToIngest != null && !countriesToIngest.contains(country.toLowerCase())) {
						continue;
					}

					String mainItemId = entry.getKey();
					String iapItemIds = parentItemsByIaps.get(mainItemId);

					if (iapItemIds == null) {
						iapItemIds = "";
					}

					LOG.log(GaeLevel.DEBUG, String.format("Queuing spit data gather task for data account %d, main item id %s in %s with iaps %s", dataAccountId, mainItemId, country, iapItemIds));

					QueueHelper.enqueue("gathersplitsaledata", Method.PULL, new SimpleEntry<String, String>("dataAccountId", String.valueOf(dataAccountId)),
							new SimpleEntry<String, String>("gatherFrom", gatherFromStr), new SimpleEntry<String, String>("gatherTo", gatherToStr),
							new SimpleEntry<String, String>("mainItemId", mainItemId), new SimpleEntry<String, String>("countryCode", country),
							new SimpleEntry<String, String>("iapItemIds", iapItemIds));

				} catch (Exception e) {
					LOG.log(Level.WARNING, String.format("Exception thrown while looping to enqueue items for split data gather: %s", entry), e);
				}
			}
		} catch (DataAccessException e) {
			LOG.log(Level.SEVERE, String.format("Exception occured while retrieving main item id for data account [%d] between [%s] and [%s]", dataAccountId,
					gatherFromStr, gatherToStr), e);
		}
	}
}
