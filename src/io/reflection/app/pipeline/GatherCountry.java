//
//  GatherCountry.java
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
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.JobSetting.OnQueue;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class GatherCountry extends Job2<Void, String, Long> {

	private static final long serialVersionUID = 3208213738551499825L;

	private static final Logger LOG = Logger.getLogger(GatherCountry.class.getName());

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
	public GatherCountry(String revenueOtherSummaryHandle, String downloadsOtherSummaryHandle, String revenueTabletSummaryHandle,
			String downloadsTabletSummaryHandle) {
		this.revenueOtherSummaryHandle = revenueOtherSummaryHandle;
		this.downloadsOtherSummaryHandle = downloadsOtherSummaryHandle;
		this.revenueTabletSummaryHandle = revenueTabletSummaryHandle;
		this.downloadsTabletSummaryHandle = downloadsTabletSummaryHandle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(String countryCode, Long code) throws Exception {
		JobSetting onGatherQueue = new JobSetting.OnQueue("gather");
		JobSetting onIngestQueue = new JobSetting.OnQueue("ingest");
		JobSetting onDefaultQueue = new JobSetting.OnQueue(OnQueue.DEFAULT);

		FutureValue<Long> freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), null, immediate(code), onGatherQueue);
		FutureValue<Long> paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), null, immediate(code), onGatherQueue);
		FutureValue<Long> grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), null, immediate(code),
				onGatherQueue);

		final boolean ingestCountryFeeds = IngestorFactory.shouldIngestFeedFetch(DataTypeHelper.IOS_STORE_A3, countryCode);

		FutureValue<Long> rankCount;
		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId, onDefaultQueue);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId, onDefaultQueue);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId, onDefaultQueue);

			futureCall(new PushRanksToBigQuery(), slimmedPaidFeed, paidFeedId, onDefaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedFreeFeed, freeFeedId, onDefaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedGrossingFeed, grossingFeedId, onDefaultQueue);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed,
					onIngestQueue);

			PromisedValue<Map<String, Double>> downloadsOtherSummaryValue = promise(downloadsOtherSummaryHandle);
			PromisedValue<Map<String, Double>> revenueOtherSummaryValue = promise(revenueOtherSummaryHandle);

			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsOtherSummaryValue, onDefaultQueue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsOtherSummaryValue, onDefaultQueue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY_VALUE, revenueOtherSummaryValue, onDefaultQueue);
		}

		freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), null, immediate(code), onGatherQueue);
		paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), null, immediate(code), onGatherQueue);
		grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), null, immediate(code), onGatherQueue);

		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId, onDefaultQueue);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId, onDefaultQueue);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId, onDefaultQueue);

			futureCall(new PushRanksToBigQuery(), slimmedPaidFeed, paidFeedId, onDefaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedFreeFeed, freeFeedId, onDefaultQueue);
			futureCall(new PushRanksToBigQuery(), slimmedGrossingFeed, grossingFeedId, onDefaultQueue);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed,
					onIngestQueue);

			PromisedValue<Map<String, Double>> downloadsTabletSummaryValue = promise(downloadsTabletSummaryHandle);
			PromisedValue<Map<String, Double>> revenueTabletSummaryValue = promise(revenueTabletSummaryHandle);

			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsTabletSummaryValue, onDefaultQueue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY_VALUE, downloadsTabletSummaryValue, onDefaultQueue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY_VALUE, revenueTabletSummaryValue, onDefaultQueue);
		}

		// when we have gathered all the counties feeds we do the same but for each category
		try {
			Store store = DataTypeHelper.getIosStore();

			// get the parent category all which references all lower categories
			Category all = CategoryServiceProvider.provide().getAllCategory(store);

			List<Category> categories = null;

			if (all != null) {
				Pager p = new Pager();
				p.start = Pager.DEFAULT_START;
				p.sortBy = Pager.DEFAULT_SORT_BY;
				p.count = CategoryServiceProvider.provide().getParentCategoriesCount(all);

				if (p.count != null && p.count.longValue() > 0) {
					categories = CategoryServiceProvider.provide().getParentCategories(all, p);

					if (categories != null && categories.size() > 0) {
						if (LOG.isLoggable(GaeLevel.DEBUG)) {
							LOG.log(GaeLevel.DEBUG, String.format("Found [%d] categories", categories.size()));
						}

						if (LOG.isLoggable(GaeLevel.DEBUG)) {
							LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
						}

						for (Category category : categories) {
							futureCall(new GatherCategory(revenueOtherSummaryHandle, downloadsOtherSummaryHandle, revenueTabletSummaryHandle,
									downloadsTabletSummaryHandle), immediate(countryCode), immediate(category.id), immediate(code));
						}
					}
				}
			}

		} catch (DataAccessException dae) {
			LOG.log(GaeLevel.SEVERE, "A database error occured attempting to enqueue category top feeds for gathering phase", dae);
		}

		return null;
	}

}