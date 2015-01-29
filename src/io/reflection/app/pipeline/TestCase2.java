//
//  TestCase2.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class TestCase2 {

	public static class TestCase2Generator extends Job2<Rank, Category, Rank> {

		private static final long serialVersionUID = -6698702257767292151L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<Rank> run(Category category, Rank rank) throws Exception {
			FutureValue<Rank> subCategoryRank = futureCall(new GetSubcategoryRank(), immediate(rank), immediate(category));

			return subCategoryRank;
		}

	}

	public static class SubCategoryBranch extends Job3<Void, Rank, Category, Rank> {

		private static final long serialVersionUID = -2487696358427962804L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<Void> run(Rank rank, Category category, Rank subCategoryRank) throws Exception {

			if (category == null) {
				futureCall(new HasNoSubCategory(), immediate(rank));
			} else {
				futureCall(new HasSubCategory(), immediate(rank), immediate(category), immediate(subCategoryRank));
			}

			return null;
		}

	}

	public static class HasSubCategory extends Job3<Void, Rank, Category, Rank> {

		private static final long serialVersionUID = 3209813458334544306L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<Void> run(Rank rank, Category category, Rank subCategoryRank) throws Exception {
			FeedFetch rankFeedFetch = FeedFetchServiceProvider.provide().getRankFeedFetch(rank).get(0);

			SimpleModelRun rankSimpleModelRun = SimpleModelRunServiceProvider.provide().getFeedFetchSimpleModelRun(rankFeedFetch);

			FeedFetch subCategoryFeedFetch = FeedFetchServiceProvider.provide().getRankFeedFetch(subCategoryRank).get(0);

			SimpleModelRun subCategoryModelRun = SimpleModelRunServiceProvider.provide().getFeedFetchSimpleModelRun(subCategoryFeedFetch);

			if (rankSimpleModelRun.adjustedRSquared.doubleValue() > subCategoryModelRun.adjustedRSquared) {
				rank.revenue = Float.valueOf((float) (rank.price.doubleValue() * rank.downloads.doubleValue()));

				// commit

				// loop
				futureCall(new SubCatetgoryRevenueSetter(), immediate(subCategoryRank), immediate(rank.revenue));

			} else {

				rank.revenue = subCategoryRank.revenue;

				// commit
			}

			return null;
		}

	}

	public static class SubCatetgoryRevenueSetter extends Job2<Void, Rank, Float> {

		private static final long serialVersionUID = 5092815602557654268L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Value<Void> run(Rank param1, Float param2) throws Exception {

			if (param1 != null) {
				param1.revenue = param2;

				// commit

				FutureValue<Rank> subCategoryRank = futureCall(new GetSubcategoryRank(), immediate(param1), immediate(param1.category));

				futureCall(new SubCatetgoryRevenueSetter(), subCategoryRank, immediate(param2));
			}

			return null;
		}

	}

	public static class HasNoSubCategory extends Job1<Void, Rank> {

		private static final long serialVersionUID = -247515890190021528L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<Void> run(Rank rank) throws Exception {
			rank.revenue = rank.price * rank.downloads;

			// commit to datastore

			return null;
		}

	}

	public static class GetSubcategoryRank extends Job2<Rank, Rank, Category> {

		private static final long serialVersionUID = -174418267956110993L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
		 */
		@Override
		public Value<Rank> run(Rank param1, Category category) throws Exception {

			return null;
		}

	}

}
