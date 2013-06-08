//
//  DataCollectionServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on May 25, 2013.
//  Copyright © 2013 Spacehopper Studios Ltd. All rights reserved.
//
package com.spacehopperstudios.storedatacollector;

import java.io.IOException;
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
public class DataCollectorServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(DataCollectorServlet.class.getName());

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
			LOG.warning("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(Level.FINE)) {
			if (!isNotCron) {
				LOG.fine("Servelet is being called by Cron");
			}

			if (!isNotQueue) {
				LOG.fine(String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		int count = 0;

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("%d Tasks added successfully", count));
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
}
