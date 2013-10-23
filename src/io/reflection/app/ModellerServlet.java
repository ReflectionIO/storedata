//
//  ModellerServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 15 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.logging.GaeLevel;
import io.reflection.app.models.Model;
import io.reflection.app.models.ModelFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class ModellerServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(ModellerServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");
		boolean isNotQueue = false;

		// bail out if we have not been called by app engine model queue
		if ((isNotQueue = (appEngineQueue == null || !"model".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
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

		Model model = ModelFactory.getModelForStore(store);
		if (model != null) {
			model.prepare(store, country, type, code);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning("Could not find Collector for store [" + store + "]");
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

}
