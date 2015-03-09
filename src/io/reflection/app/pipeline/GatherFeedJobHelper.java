//
//  GatherFeedJobHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.ListPropertyType;

import java.util.Map;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job;
import com.google.appengine.tools.pipeline.PromisedValue;

/**
 * @author William Shakour (billy1380)
 *
 */
public class GatherFeedJobHelper {

	public transient static final ImmediateValue<String> DOWNLOADS_LIST_PROPERTY_VALUE = Job.immediate(ListPropertyType.ListPropertyTypeDownloads.toString());
	public transient static final ImmediateValue<String> REVENUE_LIST_PROPERTY_VALUE = Job.immediate(ListPropertyType.ListPropertyTypeRevenue.toString());

	public static void processFeeds(Job<?> job, String form, ImmediateValue<String> countryCodeValue, ImmediateValue<Long> codeValue,
			ImmediateValue<Long> categoryInternalIdValue, String paidListName, String freeListName, String grossingListName, boolean ingestCountryFeeds,
			String downloadsHandle, String revenueHandle, String summariesDateHandle) {

		FutureValue<Long> paidFeedId = job.futureCall(new GatherFeed().name("Download " + form.toLowerCase() + " paid feed"), countryCodeValue,
				Job.immediate(paidListName), categoryInternalIdValue, codeValue, PipelineSettings.onGatherQueue);
		FutureValue<Long> freeFeedId = job.futureCall(new GatherFeed().name("Download " + form.toLowerCase() + " free feed"), countryCodeValue,
				Job.immediate(freeListName), categoryInternalIdValue, codeValue, PipelineSettings.onGatherQueue);
		FutureValue<Long> grossingFeedId = job.futureCall(new GatherFeed().name("Download " + form.toLowerCase() + " grossing feed"), countryCodeValue,
				Job.immediate(grossingListName), categoryInternalIdValue, codeValue, PipelineSettings.onGatherQueue);

		FutureValue<Long> rankCount;
		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = job.futureCall(new SlimFeed().name("Parse " + form.toLowerCase() + " paid feed"), paidFeedId,
					PipelineSettings.onIngestQueue);
			FutureValue<String> slimmedFreeFeed = job.futureCall(new SlimFeed().name("Parse " + form.toLowerCase() + " free feed"), freeFeedId,
					PipelineSettings.onIngestQueue);
			FutureValue<String> slimmedGrossingFeed = job.futureCall(new SlimFeed().name("Parse " + form.toLowerCase() + " grossing feed"), grossingFeedId,
					PipelineSettings.onIngestQueue);

			job.futureCall(new PushRanksToBigQuery().name("Push " + form.toLowerCase() + " paid list to BigQuery"), slimmedPaidFeed, paidFeedId,
					PipelineSettings.onDefaultQueue);
			job.futureCall(new PushRanksToBigQuery().name("Push " + form.toLowerCase() + " free list to BigQuery"), slimmedFreeFeed, freeFeedId,
					PipelineSettings.onDefaultQueue);
			job.futureCall(new PushRanksToBigQuery().name("Push " + form.toLowerCase() + " grossing list to BigQuery"), slimmedGrossingFeed, grossingFeedId,
					PipelineSettings.onDefaultQueue);

			rankCount = job.futureCall(new IngestRanks().name("Adding " + form.toLowerCase() + " merged lists to CloudSQL"), paidFeedId, slimmedPaidFeed,
					freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);

			PromisedValue<Map<String, Double>> downloadsSummaryValue = job.promise(downloadsHandle);
			PromisedValue<Map<String, Double>> revenueSummaryValue = job.promise(revenueHandle);

			job.futureCall(new ModelData().summariesDateHandle(summariesDateHandle).name("Model " + form.toLowerCase() + " paid list"), rankCount, paidFeedId,
					DOWNLOADS_LIST_PROPERTY_VALUE, downloadsSummaryValue, PipelineSettings.onDefaultQueue);
			job.futureCall(new ModelData().summariesDateHandle(summariesDateHandle).name("Model " + form.toLowerCase() + " free list"), rankCount, freeFeedId,
					DOWNLOADS_LIST_PROPERTY_VALUE, downloadsSummaryValue, PipelineSettings.onDefaultQueue);
			job.futureCall(new ModelData().summariesDateHandle(summariesDateHandle).name("Model " + form.toLowerCase() + " grossing feed"), rankCount,
					grossingFeedId, REVENUE_LIST_PROPERTY_VALUE, revenueSummaryValue, PipelineSettings.onDefaultQueue);
		}
	}
}
