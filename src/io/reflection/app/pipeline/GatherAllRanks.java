//
//  GatherAllRanks.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import static io.reflection.app.collectors.CollectorIOS.COUNTRIES_KEY;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.DOWNLOADS_LIST_PROPERTY_VALUE;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.REVENUE_LIST_PROPERTY_VALUE;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.JobSetting;
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
		JobSetting onDefaultQueue = new JobSetting.OnQueue(JobSetting.OnQueue.DEFAULT);

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
				String revenueTabletSummaryHandle, downloadsTabletSummaryHandle, revenueOtherSummaryHandle, downloadsOtherSummaryHandle;

				Collection<String> countriesToIngest = IngestorFactory.getIngestorCountries(DataTypeHelper.IOS_STORE_A3);
				for (String countryCode : splitCountries) {
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
					}

					revenueOtherSummaryHandle = null;
					downloadsOtherSummaryHandle = null;
					revenueTabletSummaryHandle = null;
					downloadsTabletSummaryHandle = null;

					if (countriesToIngest.contains(countryCode)) {
						// Other
						revenueOtherPromiseKey = StringUtils.join(Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3,
								FormType.FormTypeOther.toString(), REVENUE_LIST_PROPERTY_VALUE.getValue()), ".");
						downloadsOtherPromiseKey = StringUtils.join(Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3,
								FormType.FormTypeOther.toString(), DOWNLOADS_LIST_PROPERTY_VALUE.getValue()), ".");

						revenueOtherSummaryHandle = newPromise().getHandle();
						downloadsOtherSummaryHandle = newPromise().getHandle();
						persist.put(revenueOtherPromiseKey, revenueOtherSummaryHandle);
						persist.put(downloadsOtherPromiseKey, downloadsOtherSummaryHandle);

						// Tablet
						revenueTabletPromiseKey = StringUtils.join(Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3,
								FormType.FormTypeTablet.toString(), REVENUE_LIST_PROPERTY_VALUE.getValue()), ".");
						downloadsTabletPromiseKey = StringUtils.join(Arrays.asList(code.toString(), countryCode, DataTypeHelper.IOS_STORE_A3,
								FormType.FormTypeTablet.toString(), DOWNLOADS_LIST_PROPERTY_VALUE.getValue()), ".");

						revenueTabletSummaryHandle = newPromise().getHandle();
						downloadsTabletSummaryHandle = newPromise().getHandle();
						persist.put(revenueTabletPromiseKey, revenueTabletSummaryHandle);
						persist.put(downloadsTabletPromiseKey, downloadsTabletSummaryHandle);
					}

					futureCall(new GatherCountry(revenueOtherSummaryHandle, downloadsOtherSummaryHandle, revenueTabletSummaryHandle,
							downloadsTabletSummaryHandle), immediate(countryCode), immediate(code), onDefaultQueue);

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