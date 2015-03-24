//
//  StoreCalibrationSummaryFile.java
//  storedata
//
//  Created by William Shakour (billy1380) on 7 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.CalibrationSummary;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.ListPropertyType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.CalibrationSummaryHelper;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class StoreCalibrationSummaryFile extends Job6<String, Long, String, Date, Collection<String>, Collection<String>, Long> {

	private static final long serialVersionUID = -5621991524846384264L;

	private transient static final Logger LOG = Logger.getLogger(StoreCalibrationSummaryFile.class.getName());
	
	private transient String name = null;

	@Override
	public Value<String> run(Long feedfetchId, final String type, Date summariesDate, Collection<String> used, Collection<String> unused, Long simpleModelRunId)
			throws Exception {
		if (LOG.getLevel() == GaeLevel.TRACE) {
			LOG.log(GaeLevel.TRACE, "Entering StoreCalibrationSummaryFile.run");
		}
		
		FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedfetchId);
		SimpleModelRun simpleModelRun = null;

		if (simpleModelRunId != null) {
			simpleModelRun = SimpleModelRunServiceProvider.provide().getSimpleModelRun(simpleModelRunId);
		}
		
		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, "simpleModelRun==null : %b", simpleModelRun==null);
		}
		
		CalibrationSummary summary = new CalibrationSummary();
		summary.feedFetch = feedFetch;
		summary.simpleModelRun = simpleModelRun;

		if (simpleModelRun == null) {
			summary.salesSummaryDate = summariesDate;
		}

		Rank rank;
		if (used != null) {
			if (LOG.getLevel() == GaeLevel.DEBUG) {
				LOG.log(GaeLevel.DEBUG, "Adding hits to the summary");
			}

			summary.hits = new ArrayList<Rank>();
			for (String json : used) {
				rank = new Rank();
				rank.fromJson(json);
				summary.hits.add(rank);
			}

			Collections.sort(summary.hits, new Comparator<Rank>() {
				@Override
				public int compare(Rank r1, Rank r2) {
					int sort = 0;
					if (r1.position.intValue() > r2.position.intValue()) {
						sort = 1;
					} else if (r2.position.intValue() > r1.position.intValue()) {
						sort = -1;
					}

					return sort;
				}
			});
		}

		if (unused != null) {
			if (LOG.getLevel() == GaeLevel.DEBUG) {
				LOG.log(GaeLevel.DEBUG, "Adding misses to the summary");
			}
			
			summary.misses = new ArrayList<Rank>();
			for (String json : unused) {
				rank = new Rank();
				rank.fromJson(json);
				summary.misses.add(rank);
			}

			final ListPropertyType listProperty = ListPropertyType.fromString(type);

			Collections.sort(summary.misses, new Comparator<Rank>() {

				@Override
				public int compare(Rank r1, Rank r2) {
					int sort = 0;
					double value1 = 0.0, value2 = 0.0;

					switch (listProperty) {
					case ListPropertyTypeRevenue:
						value1 = r1.revenue.doubleValue();
						value2 = r2.revenue.doubleValue();
						break;
					case ListPropertyTypeDownloads:
						value1 = r1.downloads.doubleValue();
						value2 = r2.downloads.doubleValue();
						break;
					default:
						break;
					}

					if (value2 > value1) {
						sort = 1;
					} else if (value1 > value2) {
						sort = -1;
					}

					return sort;
				}
			});
		}

		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, "CalibrationSummary complete. Calling CalibrationSummaryHelper to write it out");
		}
		
		String path = CalibrationSummaryHelper.write(summary);
		
		if (LOG.getLevel() == GaeLevel.DEBUG) {
			LOG.log(GaeLevel.DEBUG, String.format("Calibration summary path: %s", path));
		}
		
		return (path == null ? null : immediate(path));
	}

	public StoreCalibrationSummaryFile name(String value) {
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
