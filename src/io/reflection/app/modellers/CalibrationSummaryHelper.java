//
//  CalibrationSummaryHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.modellers;

import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.CalibrationSummary;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ListTypeType;
import io.reflection.app.logging.GaeLevel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrationSummaryHelper {

	private transient static final String CALIBRATE_SUMMARY_BUCKET_KEY = "calibrate.bucket";
	private transient static final Logger LOG = Logger.getLogger(CalibrationSummaryHelper.class.getName());

	public static String write(CalibrationSummary summary) {
		GcsService fileService = GcsServiceFactory.createGcsService();

		String path = pathFromFeedFetch(summary.feedFetch);

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

		return (path == null || !success ? null : path);
	}

	public static CalibrationSummary read(FeedFetch feedFetch) {
		GcsService fileService = GcsServiceFactory.createGcsService();
		CalibrationSummary summary = null;

		String path = pathFromFeedFetch(feedFetch);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("File name [%s]", path));
		}

		GcsFilename gcsFileName = new GcsFilename(System.getProperty(CALIBRATE_SUMMARY_BUCKET_KEY), path);
		GcsInputChannel readChannel = null;
		BufferedReader reader = null;
		StringBuffer json = new StringBuffer();
		boolean success = false;
		String line;
		try {
			readChannel = fileService.openReadChannel(gcsFileName, 0);

			reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}

			if (json != null && json.length() > 0) {
				summary = new CalibrationSummary();
				summary.fromJson(json.toString());
			}

			success = true;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Could not read data from cloud store", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Failed to close reader", e);
				}
			}

			if (readChannel != null) {
				try {
					readChannel.close();
				} catch (IllegalStateException e) {
					LOG.log(Level.SEVERE, "Failed to close read channel", e);
				}
			}
		}

		return (summary == null || !success ? null : summary);
	}

	public static String pathFromFeedFetch(FeedFetch feedFetch) {
		FormType form = ModellerFactory.getModellerForStore(feedFetch.store).getForm(feedFetch.type);
		ListTypeType listType = CollectorFactory.getCollectorForStore(feedFetch.store).getListType(feedFetch.type);

		return feedFetch.store + "/" + feedFetch.country + "/" + feedFetch.category.id.toString() + "/" + form + "/" + listType + "/" + "summary_"
				+ feedFetch.code.toString() + ".json";
	}
}
