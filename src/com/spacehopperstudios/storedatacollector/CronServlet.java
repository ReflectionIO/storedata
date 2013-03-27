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

import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;

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

		// bail out if we have not been called by app engine cron
		if (appEngineCron == null || !Boolean.parseBoolean(appEngineCron)) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.warn("Attempt to run script directly, this is not permitted");
			return;
		}

		String store = req.getParameter("store");
		boolean success = false;

		if (store != null) {
			if ("iOS".toUpperCase().equals(store.toUpperCase())) {
				success = (new DataCollectorIOS()).collect();
			} /*
			 * else if (false) {
			 * 
			 * }
			 */
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Imported completed status = [%s]", success ? "success" : "failure"));
		}

		resp.setHeader("Cache-Control", "no-cache");
		resp.getOutputStream().print(success ? "success" : "failure");
	}
}
