//
//  ModelData.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import java.util.Map;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class ModelData extends Job4<Void, Long, Long, String, Map<String, Double>> {

	private static final long serialVersionUID = -3705962909963696521L;
	
	private String name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(Long count, Long feedFetchId, String summaryType, Map<String, Double> summary) throws Exception {
		ImmediateValue<Long> feedFetchIdValue = immediate(feedFetchId);

		// FIXME: we could probably just figure out the type from the feed fetch
		ImmediateValue<String> summaryTypeValue = immediate(summaryType);

		FutureValue<Long> simpleModelRunIdValue = futureCall(new CalibrateSimpleModel().name("Calibrate simple model"), summaryTypeValue, immediate(summary),
				feedFetchIdValue, PipelineSettings.onModelQueue, PipelineSettings.doNotRetry, PipelineSettings.onModelBackend);

		futureCall(new FillRevenue().name("Save " + summaryType.toLowerCase()), summaryTypeValue, feedFetchIdValue, simpleModelRunIdValue,
				PipelineSettings.onPredictQueue);

		return null;
	}

	public ModelData name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return name;
	}
}
