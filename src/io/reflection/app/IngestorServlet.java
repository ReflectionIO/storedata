//
//  DataIngestorServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on June 9, 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2013 reflection.io. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class IngestorServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(IngestorServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		// default queue is allowed to call this serlet to test higher speed using bigquery ingest
		if ((isNotQueue = (appEngineQueue == null || !("ingest".toLowerCase().equals(appEngineQueue.toLowerCase()) || "default".toLowerCase().equals(
				appEngineQueue.toLowerCase()))))) {
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

		String store = req.getParameter("store");
		String commaDelimitedItemIds = req.getParameter("iids");
		String ingestorType = req.getParameter("itype");

		if (store != null) {
			String stringItemIds[] = null;
			List<Long> itemIds = null;
			if (commaDelimitedItemIds != null && commaDelimitedItemIds.length() != 0) {
				stringItemIds = commaDelimitedItemIds.split(",");
				itemIds = new ArrayList<Long>(stringItemIds.length);

				for (int i = 0; i < stringItemIds.length; i++) {
					itemIds.add(Long.valueOf(stringItemIds[i]));
				}
			}

			if (itemIds != null && itemIds.size() != 0) {
				try {
					if (ingestorType == null || "".equals(ingestorType)) {
						IngestorFactory.getIngestorForStore(store.toLowerCase()).ingest(itemIds);
					} else if ("bigquery".equalsIgnoreCase(ingestorType)) {
						IngestorFactory.getBigQueryIngestorForStore(store.toLowerCase()).ingest(itemIds);
					}
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
}
