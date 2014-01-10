package io.reflection.app.collectors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public abstract class StoreCollector {

	// public static final int MAX_DATA_CHUNK_LENGTH = 500000;
	public static final String GATHER_BUCKET_KEY = "gather.bucket";

	private static final Logger LOG = Logger.getLogger(StoreCollector.class.getName());

	// private List<String> splitEqually(String text, int size) {
	// List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
	//
	// for (int start = 0; start < text.length(); start += size) {
	// ret.add(text.substring(start, Math.min(text.length(), start + size)));
	// }
	//
	// return ret;
	// }

	protected List<Long> store(String data, String countryCode, String store, String type, Date date, String code) throws DataAccessException {
		return store(data, countryCode, store, type, date, code, false);
	}

	protected List<Long> store(String data, String countryCode, String store, String type, Date date, String code, boolean ingested) throws DataAccessException {

		List<Long> ids = new ArrayList<Long>(4);

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Saving Data to data store");
		}

		GcsService fileService = GcsServiceFactory.createGcsService();
		GcsFilename fileName = new GcsFilename(System.getProperty(GATHER_BUCKET_KEY), store + "/" + countryCode + "_" + type + "_" + code);

		boolean blob = false;
		GcsOutputChannel writeChannel = null;
		BufferedWriter writer = null;

		try {
			writeChannel = fileService.createOrReplace(fileName, new GcsFileOptions.Builder().mimeType("application/json").build());

			writer = new BufferedWriter(Channels.newWriter(writeChannel, "UTF8"));
			writer.write(data);

			blob = true;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Could not write data to blob store", e);
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

		if (blob) {
			FeedFetch feed = new FeedFetch();
			String fileNameForEntitiy = "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName();

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Saving as blob @ [%s]", fileNameForEntitiy));
			}

			feed.data = fileNameForEntitiy;
			feed.country = countryCode;
			feed.store = store;
			feed.type = type;
			feed.date = date;

			// entity.ingested = Boolean.valueOf(ingested);
			feed.code = code;

			feed = FeedFetchServiceProvider.provide().addFeedFetch(feed);
			ids.add(feed.id);

		} else {
			FeedFetch feed = new FeedFetch();

			feed.data = data;
			feed.country = countryCode;
			feed.store = store;
			feed.type = type;
			feed.date = date;
			feed.code = code;

			feed = FeedFetchServiceProvider.provide().addFeedFetch(feed);
			ids.add(feed.id);
		}

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Storing entity complete [%s] [%s] [%s] at [%s]", countryCode, store, type, date.toString()));
		}

		return ids;
	}
}
