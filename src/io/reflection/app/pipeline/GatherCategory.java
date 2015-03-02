//
//  GatherCategory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import static io.reflection.app.collectors.CollectorIOS.TOP_FREE_APPS;
import static io.reflection.app.collectors.CollectorIOS.TOP_FREE_IPAD_APPS;
import static io.reflection.app.collectors.CollectorIOS.TOP_GROSSING_APPS;
import static io.reflection.app.collectors.CollectorIOS.TOP_GROSSING_IPAD_APPS;
import static io.reflection.app.collectors.CollectorIOS.TOP_PAID_APPS;
import static io.reflection.app.collectors.CollectorIOS.TOP_PAID_IPAD_APPS;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.DOWNLOADS_LIST_PROPERTY_VALUE;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.REVENUE_LIST_PROPERTY_VALUE;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.JobSetting.OnQueue;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class GatherCategory extends Job3<Void, String, Long, Long> {
	private static final long serialVersionUID = -2027106533638960844L;

	private static final Logger LOG = Logger.getLogger(GatherCategory.class.getName());

	private String revenueOtherSummaryHandle;
	private String downloadsOtherSummaryHandle;
	private String revenueTabletSummaryHandle;
	private String downloadsTabletSummaryHandle;

	/**
	 * @param revenueOtherSummaryValue
	 * @param downloadsOtherSummaryValue
	 * 
	 * @param revenueTabletSummaryValue
	 * @param downloadsTabletSummaryValue
	 */
	public GatherCategory(String revenueOtherSummaryHandle, String downloadsOtherSummaryHandle, String revenueTabletSummaryHandle,
			String downloadsTabletSummaryHandle) {
		this.revenueOtherSummaryHandle = revenueOtherSummaryHandle;
		this.downloadsOtherSummaryHandle = downloadsOtherSummaryHandle;
		this.revenueTabletSummaryHandle = revenueTabletSummaryHandle;
		this.downloadsTabletSummaryHandle = downloadsTabletSummaryHandle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Void> run(String countryCode, Long categoryId, Long code) throws Exception {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for category [%d]", categoryId.longValue()));
		}

		Category category = CategoryServiceProvider.provide().getCategory(categoryId);

		// Other

		FutureValue<Long> freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), immediate(category.internalId),
				immediate(code));
		FutureValue<Long> paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), immediate(category.internalId),
				immediate(code));
		FutureValue<Long> grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), immediate(category.internalId),
				immediate(code));

		JobSetting defaultQueue = new OnQueue(OnQueue.DEFAULT);

		FutureValue<Long> rankCount;
		final boolean ingestCountryFeeds = IngestorFactory.shouldIngestFeedFetch(DataTypeHelper.IOS_STORE_A3, countryCode);
		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId);

			futureCall(new PushRanksToBigQuery(), slimmedPaidFeed, paidFeedId, defaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedFreeFeed, freeFeedId, defaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedGrossingFeed, grossingFeedId, defaultQueue);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);

			PromisedValue<Map<String, Double>> downloadsOtherSummaryValue = promise(downloadsOtherSummaryHandle);
			PromisedValue<Map<String, Double>> revenueOtherSummaryValue = promise(revenueOtherSummaryHandle);

			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsOtherSummaryValue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsOtherSummaryValue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY_VALUE, revenueOtherSummaryValue);
		}

		// tablet

		freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), immediate(category.internalId), immediate(code));
		paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), immediate(category.internalId), immediate(code));
		grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), immediate(category.internalId),
				immediate(code));

		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId);

			futureCall(new PushRanksToBigQuery(), slimmedPaidFeed, paidFeedId, defaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedFreeFeed, freeFeedId, defaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedGrossingFeed, grossingFeedId, defaultQueue);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);

			PromisedValue<Map<String, Double>> downloadsTabletSummaryValue = promise(downloadsTabletSummaryHandle);
			PromisedValue<Map<String, Double>> revenueTabletSummaryValue = promise(revenueTabletSummaryHandle);

			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsTabletSummaryValue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsTabletSummaryValue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY_VALUE, revenueTabletSummaryValue);
		}

		return null;
	}

}