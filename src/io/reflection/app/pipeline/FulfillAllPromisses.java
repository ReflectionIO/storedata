//
//  FulfillAllPromisses.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.Value;
import com.spacehopperstudios.utility.StringUtils;

public final class FulfillAllPromisses extends Job2<Void, Map<String, Map<String, Double>>, Date> {

	private static final long serialVersionUID = -2335419676158668911L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(Map<String, Map<String, Double>> dataAccountFetchSummary, Date on) throws Exception {
		Long code = FeedFetchServiceProvider.provide().getDateCode(on, Integer.valueOf(0));

		PersistentMap persist = PersistentMapFactory.createObjectify();

		PipelineService pipelineService = PipelineServiceFactory.newPipelineService();

		String key;
		String promiseHandle;
		for (String summaryKey : dataAccountFetchSummary.keySet()) {
			key = StringUtils.join(Arrays.asList(code.toString(), summaryKey), ".");
			promiseHandle = (String) persist.get(key);

			pipelineService.submitPromisedValue(promiseHandle, dataAccountFetchSummary.get(summaryKey));
		}

		return null;
	}

}