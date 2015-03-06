//
//  FillRevenue.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.predictors.Predictor;
import io.reflection.app.predictors.PredictorFactory;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class FillRevenue extends Job3<Void, String, Long, Long> {

	private static final long serialVersionUID = -8880236066719929555L;

	private static final Logger LOG = Logger.getLogger(FillRevenue.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Void> run(String type, Long feedFetchId, Long simpleModelRunId) throws Exception {
		if (simpleModelRunId != null) {
			FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetchId);
			
			SimpleModelRun run = SimpleModelRunServiceProvider.provide().getSimpleModelRun(simpleModelRunId);
			run.feedFetch = feedFetch;

			Predictor predictor = PredictorFactory.getPredictorForStore(feedFetch.store);
			predictor.predictWithSimpleModel(run);
		} else {
			if (LOG.getLevel() == Level.INFO) {
				LOG.log(Level.INFO, "Simple model run does not seem to have succeeded skipping revenue prediction for feed fetch [" + feedFetchId.toString()
						+ "]");
			}
		}

		return null;
	}
	
	public FillRevenue name(String value) {
		setJobDisplayName(value);
		return this;
	}
}
