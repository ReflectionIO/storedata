//
//  SummariseFetchedDataAccounts.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SummariseFetchedDataAccounts extends Job1<Map<String, Map<String, Double>>, List<Long>> {

	private static final long serialVersionUID = 7344309686985145135L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Map<String, Map<String, Double>>> run(List<Long> dataAccountFetchIds) throws Exception {
		dataAccountFetchIds.removeAll(Collections.singleton(null));

		JobSetting onDefaultQueue = new JobSetting.OnQueue(JobSetting.OnQueue.DEFAULT);

		List<FutureValue<Map<String, Double>>> summaries = new ArrayList<>();
		FutureValue<Map<String, Double>> summary;
		for (Long id : dataAccountFetchIds) {
			summary = futureCall(new SummariseDataAccountFetch(), immediate(id), onDefaultQueue);
			summaries.add(summary);
		}

		FutureValue<Map<String, Map<String, Double>>> organised = futureCall(new OrganiseSummaries(), futureList(summaries), onDefaultQueue);

		return organised;
	}

}
