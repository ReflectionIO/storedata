//
//  DataIngestorServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on June 9, 2013.
//  Copyright © 2013 Holy Bananas Ltd. All rights reserved.
//
package com.spacehopperstudios.storedatacollector;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spacehopperstudios.storedatacollector.ingestors.DataStorePersist;
import com.spacehopperstudios.storedatacollector.logging.GaeLevel;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class PersistServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(PersistServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotQueue = (appEngineQueue == null || !"persist".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
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
		
		(new DataStorePersist()).perisist(req);

		resp.setHeader("Cache-Control", "no-cache");
	}
}
