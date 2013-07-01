package com.spacehopperstudios.storedata.collectors;

import static com.spacehopperstudios.storedata.objectify.PersistenceService.ofy;

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
import com.googlecode.objectify.Key;
import com.spacehopperstudios.storedata.datatypes.FeedFetch;
import com.spacehopperstudios.storedata.logging.GaeLevel;

public abstract class StoreCollector {

	public static final int MAX_DATA_CHUNK_LENGTH = 500000;
	public static final String GATHER_BUCKET_KEY = "gather.bucket";

	private static final Logger LOG = Logger.getLogger(StoreCollector.class.getName());

	private List<String> splitEqually(String text, int size) {
		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}

		return ret;
	}

	protected List<Long> store(String data, String countryCode, String store, String type, Date date, String code) {
		return store(data, countryCode, store, type, date, code, false);
	}

	protected List<Long> store(String data, String countryCode, String store, String type, Date date, String code, boolean ingested) {

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
			FeedFetch entity = new FeedFetch();
			String fileNameForEntitiy = "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName();

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Saving as blob @ [%s]", fileNameForEntitiy));
			}

			entity.data = fileNameForEntitiy;
			entity.store = store;
			entity.type = type;
			entity.date = date;
			entity.part = 1;
			entity.totalParts = 1;
			entity.ingested = ingested;
			entity.code = code;

			Key<FeedFetch> key = ofy().save().entity(entity).now();
			ids.add(Long.valueOf(key.getId()));

		} else {
			List<String> splitData = splitEqually(data, MAX_DATA_CHUNK_LENGTH);

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Data split into [%d] chuncks", splitData.size()));
			}

			Key<FeedFetch> key;
			for (int i = 0; i < splitData.size(); i++) {
				FeedFetch entity = new FeedFetch();

				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Data chunck [%d of %d] is [%d] characters", i, splitData.size(), splitData.get(i).length()));
				}

				entity.data = splitData.get(i);
				entity.country = countryCode;
				entity.store = store;
				entity.type = type;
				entity.date = date;
				entity.part = Integer.valueOf(i + 1);
				entity.totalParts = Integer.valueOf(splitData.size());
				entity.ingested = ingested;
				entity.code = code;

				key = ofy().save().entity(entity).now();
				ids.add(Long.valueOf(key.getId()));
			}
		}

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Storing entity complete [%s] [%s] [%s] at [%s]", countryCode, store, type, date.toString()));
		}

		return ids;
	}
}
