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
import static io.reflection.app.pipeline.SummariseDataAccountFetch.DOWNLOADS_LIST_PROPERTY;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.REVENUE_LIST_PROPERTY;
import io.reflection.app.CollectorServlet;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class GatherCountry extends Job2<Void, String, Long> {

	private static final long serialVersionUID = 3208213738551499825L;

	private static final Logger LOG = Logger.getLogger(GatherCountry.class.getName());

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
	public GatherCountry(PromisedValue<Map<String, Double>> revenueOtherSummaryValue, PromisedValue<Map<String, Double>> downloadsOtherSummaryValue,
			PromisedValue<Map<String, Double>> revenueTabletSummaryValue, PromisedValue<Map<String, Double>> downloadsTabletSummaryValue) {
		this.revenueOtherSummaryValue = revenueOtherSummaryValue;
		this.downloadsOtherSummaryValue = downloadsOtherSummaryValue;
		this.revenueTabletSummaryValue = revenueTabletSummaryValue;
		this.downloadsTabletSummaryValue = downloadsTabletSummaryValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(String countryCode, Long code) throws Exception {
		FutureValue<Long> freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), null, immediate(code));
		FutureValue<Long> paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), null, immediate(code));
		FutureValue<Long> grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), null, immediate(code));

		final boolean ingestCountryFeeds = CollectorServlet.shouldIngestFeedFetch(countryCode);
		FutureValue<Long> rankCount;
		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);

			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY, downloadsOtherSummaryValue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY, downloadsOtherSummaryValue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY, revenueOtherSummaryValue);
		}

		freeFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), null, immediate(code));
		paidFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), null, immediate(code));
		grossingFeedId = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), null, immediate(code));

		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paidFeedId);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), freeFeedId);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossingFeedId);

			rankCount = futureCall(new IngestRanks(), paidFeedId, slimmedPaidFeed, freeFeedId, slimmedFreeFeed, grossingFeedId, slimmedGrossingFeed);

			futureCall(new ModelData(), rankCount, paidFeedId, DOWNLOADS_LIST_PROPERTY, downloadsTabletSummaryValue);
			futureCall(new ModelData(), rankCount, freeFeedId, DOWNLOADS_LIST_PROPERTY, downloadsTabletSummaryValue);
			futureCall(new ModelData(), rankCount, grossingFeedId, REVENUE_LIST_PROPERTY, revenueTabletSummaryValue);
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
							futureCall(new GatherCategory(revenueOtherSummaryValue, downloadsOtherSummaryValue, revenueTabletSummaryValue,
									downloadsTabletSummaryValue), immediate(countryCode), immediate(category.id), immediate(code));
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