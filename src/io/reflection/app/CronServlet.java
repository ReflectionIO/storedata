//
//  CronServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on Jul 1, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.collectors.CollectorAmazon;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Rank;

import java.io.IOException;
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
				QueryKeys<Rank> query = ofy().load().type(Rank.class).limit(200).keys();
				ofy().delete().keys(query.iterable());
				count = 200;
			} else if ("Item".equals(deleteSome)) {
				QueryKeys<Item> query = ofy().load().type(Item.class).limit(100).keys();
				ofy().delete().keys(query.iterable());
				count = 200;
			}
			
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("%d %ss deleted successfully", count, deleteSome));
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}

}
