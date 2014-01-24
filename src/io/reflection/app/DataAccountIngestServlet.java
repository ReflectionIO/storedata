//
//  DataAccountIngestServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.accountdataingestors.DataAccountIngestor;
import io.reflection.app.accountdataingestors.DataAccountIngestorFactory;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.service.ContextAwareServlet;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class DataAccountIngestServlet extends ContextAwareServlet {

	private static final Logger LOG = Logger.getLogger(DataAccountIngestServlet.class.getName());

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
		if (isNotQueue = (appEngineQueue == null || !"dataaccountingest".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
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

		String fetchId = REQUEST.get().getParameter("fetchId");

		if (fetchId != null) {
			try {
				DataAccountFetch fetch = DataAccountFetchServiceProvider.provide().getDataAccountFetch(Long.parseLong(fetchId));

				if (fetch != null) {

					DataAccount linkedAccount = DataAccountServiceProvider.provide().getDataAccount(fetch.linkedAccount.id);

					if (linkedAccount != null) {
						DataSource source = DataSourceServiceProvider.provide().getDataSource(linkedAccount.source.id);

						fetch.linkedAccount = linkedAccount;

						if (source != null) {
							linkedAccount.source = source;

							DataAccountIngestor ingestor = DataAccountIngestorFactory.getIngestorForSource(source.a3Code);

							if (ingestor != null) {
								ingestor.ingest(fetch);
							} else {
								LOG.info(String.format("Could not find ingestor for source with a3Code [%s], skipping", source.a3Code));
							}

						} else {
							LOG.info(String.format("Could not find source for id [%d], skipping", linkedAccount.source.id.longValue()));
						}

					} else {
						LOG.info(String.format("Could not find data account for id [%d], skipping", fetch.linkedAccount.id.longValue()));
					}

				} else {
					LOG.info(String.format("Could not find data account fetch for id [%s], skipping", fetchId));
				}

			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Database error occured while trying to ingest data with fetch id [%s]", fetchId), e);
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
