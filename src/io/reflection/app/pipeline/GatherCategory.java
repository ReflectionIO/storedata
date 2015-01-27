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
import static io.reflection.app.pipeline.SummariseDataAccountFetch.DOWNLOADS_LIST_PROPERTY;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.REVENUE_LIST_PROPERTY;
import io.reflection.app.CollectorServlet;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;

import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class GatherCategory extends Job3<Void, String, Long, Long> {
	private static final long serialVersionUID = -2027106533638960844L;

	private static final Logger LOG = Logger.getLogger(GatherCategory.class.getName());

	private PromisedValue<Map<String, Double>> revenueOtherSummaryValue;
	private PromisedValue<Map<String, Double>> downloadsOtherSummaryValue;
	private PromisedValue<Map<String, Double>> revenueTabletSummaryValue;
	private PromisedValue<Map<String, Double>> downloadsTabletSummaryValue;

	/**
	 * @param revenueOtherSummaryValue
	 * @param downloadsOtherSummaryValue
	 * 
	 * @param revenueTabletSummaryValue
	 * @param downloadsTabletSummaryValue
	 */
	public GatherCategory(PromisedValue<Map<String, Double>> revenueOtherSummaryValue, PromisedValue<Map<String, Double>> downloadsOtherSummaryValue,
			PromisedValue<Map<String, Double>> revenueTabletSummaryValue, PromisedValue<Map<String, Double>> downloadsTabletSummaryValue) {
		this.revenueOtherSummaryValue = revenueOtherSummaryValue;
		this.downloadsOtherSummaryValue = downloadsOtherSummaryValue;
		this.revenueTabletSummaryValue = revenueTabletSummaryValue;
		this.downloadsTabletSummaryValue = downloadsTabletSummaryValue;
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
		
		FutureValue<Long> freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), immediate(category.internalId), immediate(code));
		FutureValue<Long> paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), immediate(category.internalId), immediate(code));
		FutureValue<Long> grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), immediate(category.internalId),
				immediate(code));
		
		FutureValue<Long> rankCount;
		final boolean ingestCountryFeeds = CollectorServlet.shouldIngestFeedFetch(countryCode);
		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);
			
			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY, downloadsOtherSummaryValue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY, downloadsOtherSummaryValue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY, revenueOtherSummaryValue);
		}

		// tablet
		
		freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), immediate(category.internalId), immediate(code));
		paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), immediate(category.internalId), immediate(code));
		grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), immediate(category.internalId), immediate(code));

		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);
			
			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY, downloadsTabletSummaryValue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY, downloadsTabletSummaryValue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY, revenueTabletSummaryValue);
		}

		return null;
	}

}