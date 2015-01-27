//
//  GatherAllRanks.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import static io.reflection.app.collectors.CollectorIOS.COUNTRIES_KEY;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.DOWNLOADS_LIST_PROPERTY;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.REVENUE_LIST_PROPERTY;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.spacehopperstudios.utility.StringUtils;

public class GatherAllRanks extends Job0<Integer> {

	private static final long serialVersionUID = -6950514903299207863L;

	private static final Logger LOG = Logger.getLogger(GatherAllRanks.class.getName());

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

				PersistentMap persist = PersistentMapFactory.createObjectify();

				String revenueTabletPromiseKey, downloadsTabletPromiseKey, revenueOtherPromiseKey, downloadsOtherPromiseKey;
				PromisedValue<Map<String, Double>> revenueTabletSummaryValue, downloadsTabletSummaryValue, revenueOtherSummaryValue, downloadsOtherSummaryValue;
				for (String countryCode : splitCountries) {
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
					}

					// Other
					revenueOtherPromiseKey = StringUtils.join(
							Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3, FormType.FormTypeOther.toString(),
									REVENUE_LIST_PROPERTY.getValue()), ".");
					downloadsOtherPromiseKey = StringUtils.join(
							Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3, FormType.FormTypeOther.toString(),
									DOWNLOADS_LIST_PROPERTY.getValue()), ".");
					revenueOtherSummaryValue = newPromise();
					downloadsOtherSummaryValue = newPromise();
					persist.put(revenueOtherPromiseKey, revenueOtherSummaryValue.getHandle());
					persist.put(downloadsOtherPromiseKey, downloadsOtherSummaryValue.getHandle());

					// Tablet
					revenueTabletPromiseKey = StringUtils.join(
							Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3, FormType.FormTypeTablet.toString(),
									REVENUE_LIST_PROPERTY.getValue()), ".");
					downloadsTabletPromiseKey = StringUtils.join(Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3,
							FormType.FormTypeTablet.toString(), DOWNLOADS_LIST_PROPERTY.getValue()), ".");
					revenueTabletSummaryValue = newPromise();
					downloadsTabletSummaryValue = newPromise();
					persist.put(revenueTabletPromiseKey, revenueTabletSummaryValue.getHandle());
					persist.put(downloadsTabletPromiseKey, downloadsTabletSummaryValue.getHandle());

					futureCall(new GatherCountry(revenueOtherSummaryValue, downloadsOtherSummaryValue, revenueTabletSummaryValue, downloadsTabletSummaryValue),
							immediate(countryCode), immediate(code));

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