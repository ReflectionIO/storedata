//
//  CronServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on Jul 1, 2012.
//  Copyright © 2012 Spacehopper Studios Ltd. All rights reserved.
//
package com.spacehopperstudios.storedatacollector;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.spacehopperstudios.storedatacollector.collectors.DataCollectorAmazon;
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.ingestors.IngestorIOS;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(CronServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineCron = req.getHeader("X-AppEngine-Cron");
		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotCron = false, isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotCron = (appEngineCron == null || !Boolean.parseBoolean(appEngineCron)))
				&& (isNotQueue = (appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.warn("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isDebugEnabled()) {
			if (!isNotCron) {
				LOG.debug("Servelet is being called by Cron");
			}

			if (!isNotQueue) {
				LOG.debug(String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String store = req.getParameter("store");
		boolean success = false;

		if (store != null) {
			if ("iOS".toUpperCase().equals(store.toUpperCase())) {
				// ios app store
				success = (new DataCollectorIOS()).collect();
			} else if ("Amazon".toUpperCase().equals(store.toUpperCase())) {
				// amazon store
				success = (new DataCollectorAmazon()).collect();
			} else if ("Play".toUpperCase().equals(store.toUpperCase())) {
				// google play store
			}

		}

		String ingest = req.getParameter("ingest");
		if (ingest != null) {
			if ("iOS".toUpperCase().equals(ingest.toUpperCase())) {
				boolean gotCount = false;
				boolean gotType = false;

				String count = req.getParameter("count"), type = req.getParameter("type");

				gotCount = (count != null);
				gotType = (type != null);

				if (gotCount && gotType) {
					success = (new IngestorIOS()).ingest(Integer.parseInt(count), type);
				} else if (gotCount) {
					success = (new IngestorIOS()).ingest(Integer.parseInt(count));
				} else {
					success = (new IngestorIOS()).ingest();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Imported completed status = [%s]", success ? "success" : "failure"));
		}

		resp.setHeader("Cache-Control", "no-cache");
		resp.getOutputStream().print(success ? "success" : "failure");
	}
}
