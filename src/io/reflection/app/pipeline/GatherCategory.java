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
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

public class GatherCategory extends Job3<Void, String, Long, Long> {
	private static final long serialVersionUID = -2027106533638960844L;

	private static final Logger LOG = Logger.getLogger(GatherCategory.class.getName());

	private transient String name = null;

	private String revenueOtherSummaryHandle;
	private String downloadsOtherSummaryHandle;
	private String revenueTabletSummaryHandle;
	private String downloadsTabletSummaryHandle;
	private String summariesDateHandle;

	/**
	 * @param countryCode
	 * @param categoryId
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@Override
	public Value<Void> run(String countryCode, Long categoryId, Long code) throws Exception {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for category [%d]", categoryId.longValue()));
		}

		Category category = CategoryServiceProvider.provide().getCategory(categoryId);

		Store store = DataTypeHelper.getIosStore();

		final boolean ingestCountryFeeds = IngestorFactory.shouldIngestFeedFetch(store.a3Code, countryCode)
				&& IngestorFactory.shouldIngestCategories(store.a3Code);

		ImmediateValue<String> countryCodeValue = immediate(countryCode);
		ImmediateValue<Long> codeValue = immediate(code);
		ImmediateValue<Long> categoryInternalIdValue = immediate(category.internalId);

		GatherFeedJobHelper.processFeeds(this, "Phone and Other", countryCodeValue, codeValue, categoryInternalIdValue, TOP_PAID_APPS, TOP_FREE_APPS,
				TOP_GROSSING_APPS, ingestCountryFeeds, downloadsOtherSummaryHandle, revenueOtherSummaryHandle, summariesDateHandle);

		GatherFeedJobHelper.processFeeds(this, "Tablet", countryCodeValue, codeValue, categoryInternalIdValue, TOP_FREE_IPAD_APPS, TOP_PAID_IPAD_APPS,
				TOP_GROSSING_IPAD_APPS, ingestCountryFeeds, downloadsTabletSummaryHandle, revenueTabletSummaryHandle, summariesDateHandle);

		return null;
	}

	public GatherCategory revenueOtherSummaryHandle(String value) {
		revenueOtherSummaryHandle = value;
		return this;
	}

	public GatherCategory downloadsOtherSummaryHandle(String value) {
		downloadsOtherSummaryHandle = value;
		return this;
	}

	public GatherCategory revenueTabletSummaryHandle(String value) {
		revenueTabletSummaryHandle = value;
		return this;
	}

	public GatherCategory downloadsTabletSummaryHandle(String value) {
		downloadsTabletSummaryHandle = value;
		return this;
	}

	public GatherCategory summariesDateHandle(String value) {
		summariesDateHandle = value;
		return this;
	}

	public GatherCategory name(String value) {
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