//
//  CalibrateSimpleModel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.ListPropertyType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrateSimpleModel extends Job4<Long, Long, String, Map<String, Double>, Date> {

	private static final long serialVersionUID = -8764419384476424579L;

	private transient static final Logger LOG = Logger.getLogger(CalibrateSimpleModel.class.getName());
	
	private transient String name = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job4#run(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Long> run(Long feedFetchId, String type, Map<String, Double> summary, Date summaryDate) throws Exception {
		if (LOG.getLevel() == GaeLevel.TRACE) {
			LOG.log(GaeLevel.TRACE, String.format("Entering CalibrateSimpleModel.run - type: %s, summaryDate:%s", type, summaryDate));
		}
		
		FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetchId);
		ListPropertyType listProperty = ListPropertyType.fromString(type);

		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, String.format("feedFetch==null : %b, listProperty==null : %b", feedFetch==null, listProperty==null));
		}
		
		List<Rank> ranks = RankServiceProvider.provide().getGatherCodeRanks(DataTypeHelper.createCountry(feedFetch.country),
				DataTypeHelper.createStore(feedFetch.store), feedFetch.category, feedFetch.type, feedFetch.code, PagerHelper.createInfinitePager(),
				Boolean.TRUE);

		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, String.format("ranks size: %d", ranks==null?-1:ranks.size()));
		}
		
		Map<String, Rank> itemRanks = new HashMap<String, Rank>();

		for (Rank rank : ranks) {
			itemRanks.put(rank.itemId, rank);
		}

		Set<String> usedSalesLookup = new HashSet<String>();
		Collection<String> usedSales = new ArrayList<String>();

		SimpleRegression regression = new SimpleRegression();
		Rank rank;
		Double position;
		for (String itemId : summary.keySet()) {
			rank = itemRanks.get(itemId);

			if (rank != null) {
				position = getPosition(listProperty, rank);

				if (position != null) {
					regression.addData(Math.log(position.doubleValue()), Math.log(summary.get(itemId).doubleValue()));
					usedSalesLookup.add(itemId);
					usedSales.add(createTruncatedRank(listProperty, itemId, position, summary).toString());
				}
			}
		}
		
		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, String.format("We have %d hits", usedSalesLookup.size()));
		}

		SimpleModelRun run = null;

		if (regression.getN() > 1) {
			run = new SimpleModelRun().a(Double.valueOf(-regression.getSlope())).b(Double.valueOf(Math.exp(regression.getIntercept()))).feedFetch(feedFetch)
					.summaryDate(summaryDate);

			if (regression.getN() > 2) {
				if (LOG.getLevel() == GaeLevel.DEBUG) {
					LOG.log(GaeLevel.DEBUG, String.format("We have a sample size of %d", regression.getN()));
				}
				
				RegressionResults results = regression.regress();
				run.aStandardError(Double.valueOf(regression.getSlopeStdErr())).bStandardError(regression.getInterceptStdErr())
						.regressionSumSquares(Double.valueOf(results.getRegressionSumSquares()))
						.adjustedRSquared(Double.valueOf(results.getAdjustedRSquared()));
				
				if (LOG.getLevel() == GaeLevel.DEBUG) {
					LOG.log(GaeLevel.DEBUG, "Ran the regression");
				}
			}
			
			run = SimpleModelRunServiceProvider.provide().addSimpleModelRun(run);
			if (LOG.getLevel() == GaeLevel.DEBUG) {
				LOG.log(GaeLevel.DEBUG, String.format("Saved the simple model run results to db: %b", run!=null));
			}
		}

		Collection<String> unusedSales = new ArrayList<String>();

		for (String itemId : summary.keySet()) {
			if (!usedSalesLookup.contains(itemId)) {
				unusedSales.add(createTruncatedRank(listProperty, itemId, null, summary).toString());
			}
		}

		ImmediateValue<Long> runIdValue = (run == null ? null : immediate(run.id));

		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, "Setting up the future call to store calibration summary");
		}

		futureCall(new StoreCalibrationSummaryFile().name("Store calibration summary file"), immediate(feedFetch.id), immediate(type), immediate(summaryDate),
				immediate(usedSales), immediate(unusedSales), runIdValue, PipelineSettings.onDefaultQueue);

		if (LOG.getLevel() == GaeLevel.TRACE) {
			LOG.log(GaeLevel.TRACE, String.format("Exiting CalibrateSimpleModel.run - runIdValue: %s", runIdValue));
		}

		return runIdValue;
	}

	private Double getPosition(ListPropertyType type, Rank rank) {
		Double position = null;

		switch (type) {
		case ListPropertyTypeRevenue:
			if (rank.grossingPosition != null && rank.grossingPosition.intValue() != 0) {
				position = Double.valueOf(rank.grossingPosition.doubleValue());
			}
			break;
		case ListPropertyTypeDownloads:
			if (rank.position != null && rank.position.intValue() != 0) {
				position = Double.valueOf(rank.position.doubleValue());
			}
			break;
		}

		return position;
	}

	private Rank createTruncatedRank(ListPropertyType type, String itemId, Double position, Map<String, Double> summary) {
		// add to used with clean rank
		Rank rank = new Rank().position(null).grossingPosition(null).itemId(itemId);

		if (position != null) {
			rank.position(Integer.valueOf(position.intValue()));
		}

		switch (type) {
		case ListPropertyTypeRevenue:
			rank.revenue(Float.valueOf(summary.get(itemId).floatValue()));
			break;
		case ListPropertyTypeDownloads:
			rank.downloads(Integer.valueOf(summary.get(itemId).intValue()));
			break;
		}

		return rank;
	}

	public CalibrateSimpleModel name(String value) {
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
