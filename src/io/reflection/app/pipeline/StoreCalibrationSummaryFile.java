//
//  StoreCalibrationSummaryFile.java
//  storedata
//
//  Created by William Shakour (billy1380) on 7 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.CalibrationSummary;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ListPropertyType;
import io.reflection.app.datatypes.shared.ListTypeType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class StoreCalibrationSummaryFile extends Job6<String, Long, String, Date, Collection<Rank>, Collection<Rank>, Long> {

	private static final long serialVersionUID = -5621991524846384264L;
	
	private transient static final String CALIBRATE_SUMMARY_BUCKET_KEY = "calibrate.bucket";
	private transient static final Logger LOG = Logger.getLogger(StoreCalibrationSummaryFile.class.getName());

	private transient String name = null;

	@Override
	public Value<String> run(Long feedfetchId, final String type, Date summariesDate, Collection<Rank> used, Collection<Rank> unused, Long simpleModelRunId)
			throws Exception {
		FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedfetchId);
		SimpleModelRun simpleModelRun = null;

		if (simpleModelRunId != null) {
			simpleModelRun = SimpleModelRunServiceProvider.provide().getSimpleModelRun(simpleModelRunId);
		}

		CalibrationSummary summary = new CalibrationSummary();
		summary.feedFetch = feedFetch;
		summary.simpleModelRun = simpleModelRun;

		if (simpleModelRun == null) {
			summary.salesSummaryDate = summariesDate;
		}

		if (used != null) {
			summary.hits = new ArrayList<Rank>(used);
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

			final ListPropertyType listProperty = ListPropertyType.fromString(type);
			summary.misses = new ArrayList<Rank>(unused);
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

					if (value1 > value2) {
						sort = 1;
					} else if (value2 > value1) {
						sort = -1;
					}
					return sort;
				}
			});
		}

		GcsService fileService = GcsServiceFactory.createGcsService();

		FormType form = ModellerFactory.getModellerForStore(feedFetch.store).getForm(feedFetch.type);
		ListTypeType listType = CollectorFactory.getCollectorForStore(feedFetch.store).getListType(feedFetch.type);

		String path = feedFetch.store + "/" + feedFetch.country + "/" + feedFetch.category.id.toString() + "/" + form + "/" + listType + "/" + "summary_"
				+ feedFetch.code.toString() + "_" + DateTimeFormat.forPattern("yyyy_MM_dd").print(summariesDate.getTime()) + ".json";

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("File name [%s]", path));
		}

		GcsFilename gcsFileName = new GcsFilename(System.getProperty(CALIBRATE_SUMMARY_BUCKET_KEY), path);
		GcsOutputChannel writeChannel = null;
		BufferedWriter writer = null;
		boolean success = false;
		try {
			writeChannel = fileService.createOrReplace(gcsFileName, new GcsFileOptions.Builder().mimeType("application/json").build());

			writer = new BufferedWriter(Channels.newWriter(writeChannel, "UTF8"));
			writer.write(summary.toString());

			success = true;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Could not write data to cloud store", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Failed to close writer", e);
				}
			}

			if (writeChannel != null) {
				try {
					writeChannel.close();
				} catch (IllegalStateException e) {
					LOG.log(Level.SEVERE, "Failed to close write channel", e);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Failed to close write channel", e);
				}
			}
		}

		return (path == null || !success ? null : immediate(path));
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
