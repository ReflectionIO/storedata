//
//  CalibrateSimpleModel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import static io.reflection.app.pipeline.SummariseDataAccountFetch.DOWNLOADS_LIST_PROPERTY;
import static io.reflection.app.pipeline.SummariseDataAccountFetch.REVENUE_LIST_PROPERTY;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrateSimpleModel extends Job3<Long, String, Map<String, Double>, Long> {

	private static final long serialVersionUID = -8764419384476424579L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job3#run(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Long> run(String type, Map<String, Double> summary, Long feedFetchId) throws Exception {

		FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetchId);

		List<Rank> ranks = RankServiceProvider.provide().getGatherCodeRanks(DataTypeHelper.createCountry(feedFetch.country),
				DataTypeHelper.createStore(feedFetch.store), feedFetch.category, feedFetch.type, feedFetch.code, PagerHelper.createInfinitePager(),
				Boolean.TRUE);

		Map<String, Rank> itemRanks = new HashMap<String, Rank>();

		for (Rank rank : ranks) {
			itemRanks.put(rank.itemId, rank);
		}

		SimpleRegression regression = new SimpleRegression();
		Rank rank;
		Double position;
		for (String itemId : summary.keySet()) {
			rank = itemRanks.get(itemId);

			if (rank != null) {
				position = getPosition(type, rank);

				if (position != null) {
					regression.addData(Math.log(position.doubleValue()), Math.log(summary.get(itemId).doubleValue()));
				}
			}
		}

		SimpleModelRun run = null;

		if (regression.getN() > 1) {
			run = new SimpleModelRun().a(Double.valueOf(-regression.getSlope())).b(Double.valueOf(Math.exp(regression.getIntercept()))).feedFetch(feedFetch);

			if (regression.getN() > 2) {
				RegressionResults results = regression.regress();
				run.aStandardError(Double.valueOf(regression.getSlopeStdErr())).bStandardError(regression.getInterceptStdErr())
						.regressionSumSquares(Double.valueOf(results.getRegressionSumSquares()))
						.adjustedRSquared(Double.valueOf(results.getAdjustedRSquared()));
			}

			run = SimpleModelRunServiceProvider.provide().addSimpleModelRun(run);
		}

		return run == null ? null : immediate(run.id);
	}

	private Double getPosition(String type, Rank rank) {
		Double position = null;

		switch (type) {
		case REVENUE_LIST_PROPERTY:
			if (rank.grossingPosition != null && rank.grossingPosition.intValue() != 0) {
				position = Double.valueOf(rank.grossingPosition.doubleValue());
			}
			break;
		case DOWNLOADS_LIST_PROPERTY:
			if (rank.position != null && rank.position.intValue() != 0) {
				position = Double.valueOf(rank.position.doubleValue());
			}
			break;
		default:
			break;
		}

		return position;
	}
}
