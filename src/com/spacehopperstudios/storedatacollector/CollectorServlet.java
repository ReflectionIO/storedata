//
//  DataCollectionServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on May 25, 2013.
//  Copyright © 2013 Spacehopper Studios Ltd. All rights reserved.
//
package com.spacehopperstudios.storedatacollector;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spacehopperstudios.storedatacollector.collectors.CollectorAmazon;
import com.spacehopperstudios.storedatacollector.collectors.CollectorIOS;
import com.spacehopperstudios.storedatacollector.ingestors.IngestorIOS;
import com.spacehopperstudios.storedatacollector.logging.GaeLevel;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class CollectorServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(CollectorServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotQueue = (appEngineQueue == null || !"gather".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
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
		String country = req.getParameter("country");
		String type = req.getParameter("type");
		String code = req.getParameter("code");

		List<Long> collected = null;

		if ("ios".equals(store.toLowerCase())) {
			// ios app store
			collected = (new CollectorIOS()).collect(country, type, code);
			(new IngestorIOS()).enqueue(collected);
		} else if ("amazon".equals(store.toLowerCase())) {
			// amazon store
			collected = (new CollectorAmazon()).collect(country, type, code);
		} else if ("play".equals(store.toLowerCase())) {
			// google play store
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
}
