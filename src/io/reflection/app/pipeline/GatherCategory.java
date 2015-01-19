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

import java.util.logging.Logger;

import io.reflection.app.CollectorServlet;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

public class GatherCategory extends Job3<Void, String, Long, Long> {
	private static final long serialVersionUID = -2027106533638960844L;

	private static final Logger LOG = Logger.getLogger(GatherCategory.class.getName());

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

		FutureValue<Long> free = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), immediate(category.internalId), immediate(code));
		FutureValue<Long> paid = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), immediate(category.internalId), immediate(code));
		FutureValue<Long> grossing = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), immediate(category.internalId),
				immediate(code));

		final boolean ingestCountryFeeds = CollectorServlet.shouldIngestFeedFetch(countryCode);
		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paid);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), free);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossing);

			futureCall(new IngestRanks(), paid, slimmedPaidFeed, free, slimmedFreeFeed, grossing, slimmedGrossingFeed);
		}

		free = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), immediate(category.internalId), immediate(code));
		paid = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), immediate(category.internalId), immediate(code));
		grossing = futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), immediate(category.internalId), immediate(code));

		if (ingestCountryFeeds) {
			FutureValue<String> slimmedPaidFeed = futureCall(new SlimFeed(), paid);
			FutureValue<String> slimmedFreeFeed = futureCall(new SlimFeed(), free);
			FutureValue<String> slimmedGrossingFeed = futureCall(new SlimFeed(), grossing);

			futureCall(new IngestRanks(), paid, slimmedPaidFeed, free, slimmedFreeFeed, grossing, slimmedGrossingFeed);
		}

		return null;
	}

}