//
//  PredictorServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.ModelTypeType;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.predictors.Predictor;
import io.reflection.app.predictors.PredictorFactory;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;

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
public class PredictorServlet extends ContextAwareServlet {

	private static final Logger LOG = Logger.getLogger(PredictorServlet.class.getName());

	@Override
	protected void doPost() {

		String appEngineQueue = REQUEST.get().getHeader("X-AppEngine-QueueName");
		boolean isNotQueue = false;

		// bail out if we have not been called by app engine model queue
		if ((isNotQueue = (appEngineQueue == null || !"predict".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
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

		String modelTypeParam = REQUEST.get().getParameter("modeltype");

		ModelTypeType modelType = modelTypeParam == null ? ModelTypeType.ModelTypeTypeCorrelation : ModelTypeType.fromString(modelTypeParam);

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Running predict for model type [" + modelType.toString() + "]");
		}

		if (modelType == ModelTypeType.ModelTypeTypeCorrelation) {
			String country = REQUEST.get().getParameter("country");
			String store = REQUEST.get().getParameter("store");
			String type = REQUEST.get().getParameter("type");
			String codeParam = REQUEST.get().getParameter("code");
			String categoryIdParam = REQUEST.get().getParameter("categoryid");

			Long code = codeParam == null ? null : Long.valueOf(codeParam);
			Long categoryId = categoryIdParam == null ? null : Long.valueOf(categoryIdParam);

			Predictor predictor = PredictorFactory.getPredictorForStore(store);

			if (predictor != null) {
				try {
					predictor.predictRevenueAndDownloads(country, type, code, categoryId);
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}
			} else {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning("Could not find Predictor for store [" + store + "]");
				}
			}
		} else if (modelType == ModelTypeType.ModelTypeTypeSimple) {
			String runIdParam = REQUEST.get().getParameter("runid");

			if (runIdParam == null) {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning("Could not find runid parameter");
				}
			} else {
				try {
					Long runId = Long.valueOf(runIdParam);

					SimpleModelRun simpleModelRun = SimpleModelRunServiceProvider.provide().getSimpleModelRun(runId);

					if (simpleModelRun != null) {
						FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(simpleModelRun.feedFetch.id);

						if (feedFetch == null) {
							if (LOG.isLoggable(Level.WARNING)) {
								LOG.warning("Could not find feed fetch for id [" + simpleModelRun.id.toString() + "]");
							}
						} else {
							Predictor predictor = PredictorFactory.getPredictorForStore(feedFetch.store);

							simpleModelRun.feedFetch = feedFetch;

							if (predictor != null) {
								predictor.predictWithSimpleModel(simpleModelRun);
							} else {
								if (LOG.isLoggable(Level.WARNING)) {
									LOG.warning("Could not find Predictor for store [" + feedFetch.store + "]");
								}
							}

						}
					} else {
						if (LOG.isLoggable(Level.WARNING)) {
							LOG.warning("Could not find simple model run for id [" + runIdParam + "]");
						}
					}
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}
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
