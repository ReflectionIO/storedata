//
//  IosRankPipeline.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class IosRankPipeline {

	private static final Logger LOG = Logger.getLogger(IosRankPipeline.class.getName());

	private static final String COUNTRIES_KEY = "gather." + DataTypeHelper.IOS_STORE_A3 + ".countries";

//	private static final String KEY_FORMAT = "gather." + DataTypeHelper.IOS_STORE_A3 + ".%s";
//	private static final String KEY_CATEGORY_FEED = "gather." + DataTypeHelper.IOS_STORE_A3 + ".category.feed.url";

	public static final String TOP_FREE_APPS = "topfreeapplications";
	public static final String TOP_PAID_APPS = "toppaidapplications";
	public static final String TOP_GROSSING_APPS = "topgrossingapplications";
	public static final String TOP_FREE_IPAD_APPS = "topfreeipadapplications";
	public static final String TOP_PAID_IPAD_APPS = "toppaidipadapplications";
	public static final String TOP_GROSSING_IPAD_APPS = "topgrossingipadapplications";

	public static class GatherAll extends Job0<Integer> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6950514903299207863L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job0#run()
		 */
		@Override
		public Value<Integer> run() throws Exception {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Entering...");
			}

			int count = 0;

			try {
				String countries = System.getProperty(COUNTRIES_KEY);
				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Found countries [%s].", countries));
				}

				List<String> splitCountries = new ArrayList<String>();
				Collections.addAll(splitCountries, countries.split(","));

				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("[%d] countries to fetch data for", splitCountries.size()));
				}

				Long code = null;

				try {
					code = FeedFetchServiceProvider.provide().getCode();

					if (code == null && LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, "Got null code");
					} else {
						LOG.log(GaeLevel.DEBUG, String.format("Got code [%d] from feed fetch service", code.longValue()));
					}

					for (String countryCode : splitCountries) {
						if (LOG.isLoggable(GaeLevel.DEBUG)) {
							LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
						}

						futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), null, immediate(code));
						futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), null, immediate(code));
						futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), null, immediate(code));
						futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), null, immediate(code));
						futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), null, immediate(code));
						futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS), null, immediate(code));

						count++;
					}
				} catch (DataAccessException dae) {
					LOG.log(GaeLevel.SEVERE, "A database error occured attempting to to get an id code for gather enqueueing", dae);
				}

				if (code != null) {
					// when we have gathered all the counties feeds we do the same but for each category
					try {
						Store store = StoreServiceProvider.provide().getA3CodeStore(DataTypeHelper.IOS_STORE_A3);

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

									// if we have some categories iterate through all the countries again
									for (String countryCode : splitCountries) {

										if (LOG.isLoggable(GaeLevel.DEBUG)) {
											LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
										}

										// for each category
										for (Category category : categories) {

											if (LOG.isLoggable(GaeLevel.DEBUG)) {
												LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for category [%d]", category.id.longValue()));
											}

											futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_APPS), immediate(category.internalId),
													immediate(code));
											futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_APPS), immediate(category.internalId),
													immediate(code));
											futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_APPS), immediate(category.internalId),
													immediate(code));
											futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_FREE_IPAD_APPS), immediate(category.internalId),
													immediate(code));
											futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_PAID_IPAD_APPS), immediate(category.internalId),
													immediate(code));
											futureCall(new GatherFeed(), immediate(countryCode), immediate(TOP_GROSSING_IPAD_APPS),
													immediate(category.internalId), immediate(code));

											count++;
										}
									}
								}
							}
						}
					} catch (DataAccessException dae) {
						LOG.log(GaeLevel.SEVERE, "A database error occured attempting to enqueue category top feeds for gathering phase", dae);
					}
				}

			} finally {
				if (LOG.isLoggable(GaeLevel.TRACE)) {
					LOG.log(GaeLevel.TRACE, "Exiting...");
				}
			}

			return immediate(Integer.valueOf(count));
		}
	}

	public static class GatherCountry extends Job1<String, String> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3208213738551499825L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<String> run(String param1) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class GatherCategory extends Job2<String, String, Long> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2027106533638960844L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<String> run(String param1, Long param2) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class GatherFeed extends Job4<String, String, String, Long, Long> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 959780630540839671L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job4#run(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<String> run(String param1, String param2, Long param3, Long param4) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
