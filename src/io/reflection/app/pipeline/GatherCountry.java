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
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

public class GatherCountry extends Job2<Void, String, Long> {

	private static final long serialVersionUID = 3208213738551499825L;

	private static final Logger LOG = Logger.getLogger(GatherCountry.class.getName());

	private transient String name = null;

	private String revenueOtherSummaryHandle;
	private String downloadsOtherSummaryHandle;
	private String revenueTabletSummaryHandle;
	private String downloadsTabletSummaryHandle;
	private String summariesDateHandle;

	@Override
	public Value<Void> run(String countryCode, Long code) throws Exception {
		ImmediateValue<String> countryCodeValue = immediate(countryCode);
		ImmediateValue<Long> codeValue = immediate(code);

		final boolean ingestCountryFeeds = IngestorFactory.shouldIngestFeedFetch(DataTypeHelper.IOS_STORE_A3, countryCode);

		GatherFeedJobHelper.processFeeds(this, "Phone and Other", countryCodeValue, codeValue, null, TOP_PAID_APPS, TOP_FREE_APPS, TOP_GROSSING_APPS,
				ingestCountryFeeds, downloadsOtherSummaryHandle, revenueOtherSummaryHandle, summariesDateHandle);

		GatherFeedJobHelper.processFeeds(this, "Tablet", countryCodeValue, codeValue, null, TOP_FREE_IPAD_APPS, TOP_PAID_IPAD_APPS, TOP_GROSSING_IPAD_APPS,
				ingestCountryFeeds, downloadsTabletSummaryHandle, revenueTabletSummaryHandle, summariesDateHandle);

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
							futureCall(
									new GatherCategory().revenueOtherSummaryHandle(revenueOtherSummaryHandle)
											.downloadsOtherSummaryHandle(downloadsOtherSummaryHandle).revenueTabletSummaryHandle(revenueTabletSummaryHandle)
											.downloadsTabletSummaryHandle(downloadsTabletSummaryHandle).summariesDateHandle(summariesDateHandle)
											.name("Gather " + category.name), countryCodeValue, immediate(category.id), codeValue,
									PipelineSettings.onDefaultQueue);
						}
					}
				}
			}

		} catch (DataAccessException dae) {
			LOG.log(GaeLevel.SEVERE, "A database error occured attempting to enqueue category top feeds for gathering phase", dae);
		}

		return null;
	}

	public GatherCountry revenueOtherSummaryHandle(String value) {
		revenueOtherSummaryHandle = value;
		return this;
	}

	public GatherCountry downloadsOtherSummaryHandle(String value) {
		downloadsOtherSummaryHandle = value;
		return this;
	}

	public GatherCountry revenueTabletSummaryHandle(String value) {
		revenueTabletSummaryHandle = value;
		return this;
	}

	public GatherCountry downloadsTabletSummaryHandle(String value) {
		downloadsTabletSummaryHandle = value;
		return this;
	}

	public GatherCountry summariesDateHandle(String value) {
		summariesDateHandle = value;
		return this;
	}

	public GatherCountry name(String value) {
		name = value;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return (name == null ? super.getJobDisplayName() : name);
	}
}