//
//  CronServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on Jul 1, 2012.
//  Copyright © 2012 Spacehopper Studios Ltd. All rights reserved.
//
package com.spacehopperstudios.storedatacollector;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spacehopperstudios.storedatacollector.collectors.CollectorAmazon;
import com.spacehopperstudios.storedatacollector.collectors.CollectorIOS;
import com.spacehopperstudios.storedatacollector.logging.GaeLevel;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(CronServlet.class.getName());

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
		}

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("%d Tasks added successfully", count));
		}

		resp.setHeader("Cache-Control", "no-cache");
	}

}
