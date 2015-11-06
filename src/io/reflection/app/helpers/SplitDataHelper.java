//
//  SplitDataHelper.java
//  storedata
//
//  Created by mamin on 20 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions.Method;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.lookupitem.LookupItemService;
import io.reflection.app.service.sale.ISaleService;
import io.reflection.app.service.sale.SaleServiceProvider;

/**
 * @author mamin
 *
 */
public class SplitDataHelper {
	private static final Logger LOG = Logger.getLogger(SplitDataHelper.class.getName());

	public static final SplitDataHelper INSTANCE = new SplitDataHelper();

	private SplitDataHelper() {
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

		Date gatherTo = date;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date gatherFrom = cal.getTime(); // first day of the month to gather to

		if (gatherFrom.after(new Date())) {
			LOG.log(GaeLevel.DEBUG, String.format("Can't gather split data in the future. Returning"));
			return;
		}

		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1); // last day of the month to gather to
		Date lastDayOfMonth = cal.getTime();

		Calendar lastSalesDataDate = Calendar.getInstance();
		lastSalesDataDate.add(Calendar.DATE, lastSalesDataDate.get(Calendar.HOUR_OF_DAY) > 18 ? -1 : -2);
		lastSalesDataDate.set(Calendar.HOUR_OF_DAY, 0);
		lastSalesDataDate.set(Calendar.MINUTE, 0);
		lastSalesDataDate.set(Calendar.SECOND, 0);
		lastSalesDataDate.set(Calendar.MILLISECOND, 0);

		if (lastDayOfMonth.before(lastSalesDataDate.getTime())) {
			// the last day of that month is before the latest date of when sales data is available so we can fetch the whole month that month
			gatherTo = lastDayOfMonth;
			LOG.log(GaeLevel.DEBUG, String.format("The last date of the given month is before latest date of when sales data is available so gathering split data for the full month"));
		} else {
			// this must be the current month so we can only gather to the last sales data available date.
			gatherTo = lastSalesDataDate.getTime();
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

			HashMap<String, String> mainItemsGrouped = groupConcatValuesByKey(mainItemIdsAndCountries);

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

					String taskName = String.format("%d_%s_%s_%s_%s___%d", dataAccountId, mainItemId, country, gatherFromStr, gatherToStr, System.currentTimeMillis());

					LOG.log(GaeLevel.DEBUG, String.format("Queueing spit data gather task for data account %d, main item id %s in %s with iaps %s", dataAccountId, mainItemId, country, iapItemIds));

					QueueHelper.enqueue("gathersplitsaledata", Method.PULL,
							new SimpleEntry<String, String>("taskName", taskName),
							new SimpleEntry<String, String>("dataAccountId", String.valueOf(dataAccountId)),
							new SimpleEntry<String, String>("gatherFrom", gatherFromStr),
							new SimpleEntry<String, String>("gatherTo", gatherToStr),
							new SimpleEntry<String, String>("mainItemId", mainItemId),
							new SimpleEntry<String, String>("countryCode", country),
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

	/**
	 * @param entryList
	 * @return
	 */
	private HashMap<String, String> groupConcatValuesByKey(List<SimpleEntry<String, String>> entryList) {
		HashMap<String, String> result = new HashMap<String, String>();

		for (SimpleEntry<String, String> entry : entryList) {

		}

		return result;
	}
}
