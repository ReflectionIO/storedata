//
//  CronServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on Jul 1, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.api.PagerHelper;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.collectors.CollectorAmazon;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccount.IDataAccountService;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.dataaccountfetch.IDataAccountFetchService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.cmd.QueryKeys;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(CronServlet.class.getName());

	private static final int DELETE_COUNT = 1000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineCron = req.getHeader("X-AppEngine-Cron");
		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineCron is [%s] and appEngineQueue is [%s]", appEngineCron, appEngineQueue));
		}

		boolean isNotCron = false, isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotCron = (appEngineCron == null || !Boolean.parseBoolean(appEngineCron)))
				&& (isNotQueue = (appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.log(Level.WARNING, "Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotCron) {
				LOG.log(GaeLevel.DEBUG, "Servelet is being called by Cron");
			}

			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String store = req.getParameter("store");
		String deleteSome = req.getParameter("deletesome");
		String process = req.getParameter("process");
		int count = 0;

		if (store != null) {
			if ("ios".equals(store.toLowerCase())) {
				// ios app store
				count = (new CollectorIOS()).enqueue();
			} else if ("amazon".equals(store.toLowerCase())) {
				// amazon store
				count = (new CollectorAmazon()).enqueue();
			} else if ("play".equals(store.toLowerCase())) {
				// google play store
			}

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("%d Tasks added successfully", count));
			}
		} else if (deleteSome != null) {
			if ("Rank".equals(deleteSome)) {
				QueryKeys<Rank> query = ofy().load().type(Rank.class).limit(DELETE_COUNT).keys();
				ofy().delete().keys(query.iterable());
				count = DELETE_COUNT;
			} else if ("Item".equals(deleteSome)) {
				QueryKeys<Item> query = ofy().load().type(Item.class).limit(DELETE_COUNT).keys();
				ofy().delete().keys(query.iterable());
				count = DELETE_COUNT;
			}

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("%d %ss deleted successfully", count, deleteSome));
			}
		} else if (process != null) {
			if ("accounts".equals(process)) {

				Pager pager = new Pager();
				pager.count = Long.valueOf(100);

				try {
					IDataAccountFetchService dataAccountFetchService = DataAccountFetchServiceProvider.provide();
					IDataAccountService dataAccountService = DataAccountServiceProvider.provide();
					
					// get the total number of accounts there are
					pager.totalCount = dataAccountService.getDataAccountsCount();

					// get data accounts 100 at a time
					for (pager.start = Long.valueOf(0); pager.start.longValue() < pager.totalCount.longValue(); pager.start = Long.valueOf(pager.start
							.longValue() + pager.count.longValue())) {
						List<DataAccount> dataAccounts = dataAccountService.getDataAccounts(pager);

						for (DataAccount dataAccount : dataAccounts) {
							// if the account has some errors then don't bother otherwise enqueue a message to do a gather for it

							if (DataAccountFetchServiceProvider.provide().isFetchable(dataAccount) == Boolean.TRUE) {
								dataAccountService.triggerDataAccountFetch(dataAccount);

								// go through all the failed attempts and get them too (failed attempts = less than 30 days old)
								List<DataAccountFetch> failedDataAccountFetches = dataAccountFetchService.getFailedDataAccountFetches(dataAccount,
										PagerHelper.infinitePager());

								for (DataAccountFetch dataAccountFetch : failedDataAccountFetches) {
									dataAccountService.triggerSingleDateDataAccountFetch(dataAccount, dataAccountFetch.date);
								}
							}
						}

					}
				} catch (DataAccessException daEx) {
					throw new RuntimeException(daEx);
				}
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}

}
