//
//  ModellerServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 15 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.predictors.Predictor;
import io.reflection.app.predictors.PredictorFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.service.ContextAwareServlet;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
@Deprecated
public class ModellerServlet extends ContextAwareServlet {

	private static final Logger LOG = Logger.getLogger(ModellerServlet.class.getName());

	@Override
	protected void doPost() {

		String appEngineQueue = REQUEST.get().getHeader("X-AppEngine-QueueName");
		boolean isNotQueue = false;

		// bail out if we have not been called by app engine model queue
		if ((isNotQueue = (appEngineQueue == null || !"model".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
			RESPONSE.get().setStatus(401);
			try {
				RESPONSE.get().getOutputStream().print("failure");
			} catch (IOException e) {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.log(Level.WARNING, "Could not print failure response", e);
				}
			}
			LOG.warning("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String store = REQUEST.get().getParameter("store");
		String country = REQUEST.get().getParameter("country");
		String type = REQUEST.get().getParameter("type");
		String codeParam = REQUEST.get().getParameter("code");
		Long code = codeParam == null ? null : Long.valueOf(codeParam);

		Modeller model = ModellerFactory.getModellerForStore(store);

		if (model != null) {
			try {
				model.modelVariables(country, type, code);
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
			
			Predictor p = PredictorFactory.getPredictorForStore(store);
			p.enqueue(country, type, code);
			
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning("Could not find Modeller for store [" + store + "]");
			}
		}

		RESPONSE.get().setHeader("Cache-Control", "no-cache");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doGet()
	 */
	@Override
	protected void doGet() throws ServletException, IOException {
		doPost();
	}

}
