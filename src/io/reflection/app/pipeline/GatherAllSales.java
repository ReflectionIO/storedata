//
//  GatherAllSales.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccount.IDataAccountService;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.dataaccountfetch.IDataAccountFetchService;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

public class GatherAllSales extends Job1<Integer, Date> {

	private static final long serialVersionUID = 8112347752177694061L;

	private static final Logger LOG = Logger.getLogger(GatherAllSales.class.getName());
	
	private transient String name = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job0#run()
	 */
	@Override
	public Value<Integer> run(Date date) throws Exception {
		Pager pager = PagerHelper.createDefaultPager().count(Long.valueOf(100));

		int count = 0;
		List<FutureValue<Long>> ids = new ArrayList<>();

		ImmediateValue<Date> forDate = immediate(date);

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
						ids.add(futureCall(new GatherDataAccountOn().name("Gather " + dataAccount.username + " sales"), immediate(dataAccount.id), forDate,
								PipelineSettings.onDataAccountGatherQueue));

						// go through all the failed attempts and get them too (failed attempts = less than 30 days old)
						List<DataAccountFetch> failedDataAccountFetches = dataAccountFetchService.getFailedDataAccountFetches(dataAccount,
								PagerHelper.createInfinitePager());

						for (DataAccountFetch dataAccountFetch : failedDataAccountFetches) {
							// gather these - but we cannot use them
							// TODO: figure out what to do instead
							futureCall(
									new GatherDataAccountOn().name("Gather " + dataAccount.username + " failed sales from "
											+ DateTimeFormat.shortDate().print(dataAccountFetch.date.getTime())), immediate(dataAccount.id),
									immediate(dataAccountFetch.date), PipelineSettings.onDataAccountGatherQueue);
						}

						count++;
					}
				}

				PagerHelper.moveForward(pager);
			} while (dataAccounts != null && dataAccounts.size() == pager.count.intValue());

			FutureValue<Map<String, Map<String, Double>>> organizedSummaries = futureCall(new SummariseFetchedDataAccounts().name("Summarise data"),
					futureList(ids), PipelineSettings.onDefaultQueue);

			futureCall(new SubmitPromisses().name("Submit promisses"), organizedSummaries, forDate, PipelineSettings.onDefaultQueue);

		} catch (DataAccessException dae) {
			LOG.log(GaeLevel.SEVERE, "A database error occured attempting to start sales gather process", dae);
		}

		return immediate(Integer.valueOf(count));
	}

	public GatherAllSales name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return (name == null ? super.getJobDisplayName() : name);
	}
}