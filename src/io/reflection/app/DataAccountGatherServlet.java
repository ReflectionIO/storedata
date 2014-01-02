//
//  DataAccountGatherServlet.java
//  storedata
//
//  Created by William Shakour on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.accountdatacollectors.DataAccountCollector;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.shared.datatypes.DataAccount;
import io.reflection.app.shared.datatypes.DataSource;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.service.ContextAwareServlet;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class DataAccountGatherServlet extends ContextAwareServlet {
	private static final Logger LOG = Logger.getLogger(DataAccountGatherServlet.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doGet()
	 */
	@Override
	protected void doGet() throws ServletException, IOException {
		String appEngineQueue = REQUEST.get().getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineQueue is [%s]", appEngineQueue));
		}

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if (isNotQueue = (appEngineQueue == null || !"itempropertylookup".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
			RESPONSE.get().setStatus(401);
			RESPONSE.get().getOutputStream().print("failure");
			LOG.log(Level.WARNING, "Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String accountIdParameter = REQUEST.get().getParameter("accountId");

		String daysParameter = REQUEST.get().getParameter("days");

		String beginParameter = REQUEST.get().getParameter("begin");
		String endParameter = REQUEST.get().getParameter("end");

		if (accountIdParameter != null) {
			try {
				DataAccount account = DataAccountServiceProvider.provide().getDataAccount(Long.valueOf(accountIdParameter));

				if (account != null) {
					Date begin, end;
					int days = 1;

					if (beginParameter != null) {
						begin = new Date(Long.parseLong(beginParameter));
					} else {
						begin = new Date();
					}

					if (endParameter != null) {
						end = new Date(Long.parseLong(endParameter));
					} else {
						if (daysParameter != null) {
							days = Integer.valueOf(daysParameter);
						}

						Calendar cal = Calendar.getInstance();

						cal.setTime(begin);
						cal.add(Calendar.DAY_OF_MONTH, days);

						end = cal.getTime();
					}

					DataSource dataSource = DataSourceServiceProvider.provide().getDataSource(account.source.id);

					DataAccountCollector collector = DataAccountCollectorFactory.getCollectorForSource(dataSource.a3Code);

					if (collector != null) {
						collector.collect(account, begin, end);
					} else {
						LOG.log(GaeLevel.WARNING, "Could not find a collector for %s", dataSource.a3Code);
					}

				}
			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE, String.format(
						"Database error occured while trying to import data with accountid [%s] to begin [%s], end [%s], days [%s]", accountIdParameter,
						beginParameter, endParameter, daysParameter), e);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doPost()
	 */
	@Override
	protected void doPost() throws ServletException, IOException {
		doGet();
	}

}
