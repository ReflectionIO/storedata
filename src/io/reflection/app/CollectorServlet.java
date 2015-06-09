//
//  CollectorServlet.java
//  storedata
//
//  Created by William Shakour on May 25, 2013.
//  Copyright © 2013 Spacehopper Studios Ltd. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.ingestors.Ingestor;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author William Shakour
 *
 */
@SuppressWarnings("serial")
public class CollectorServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(CollectorServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if (isNotQueue = appEngineQueue == null || !"gather".toLowerCase().equals(appEngineQueue.toLowerCase())) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.warning("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		final String store = req.getParameter("store");
		final String country = req.getParameter("country");
		final String type = req.getParameter("type");

		final String codeParam = req.getParameter("code");
		final Long code = codeParam == null ? null : Long.valueOf(codeParam);

		final String category = req.getParameter("category");

		List<Long> collected = null;

		final Collector collector = CollectorFactory.getCollectorForStore(store);

		if (collector != null) {
			try {
				collected = collector.collect(country, type, category, code);
			} catch (final DataAccessException e) {
				throw new RuntimeException(e);
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning("Could not find Collector for store [" + store + "]");
			}
		}

		final List<String> countries = IngestorFactory.getIngestorCountries(store);

		if (countries.contains(country)) {
			final Ingestor ingestor = IngestorFactory.getIngestorForStore(store);

			if (ingestor != null) {
				ingestor.enqueue(collected);
			} else {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning("Could not find Ingestor for store [" + store + "]");
				}
			}
		} else {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Country [" + country + "] not in list of countries to ingest.");
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
}
