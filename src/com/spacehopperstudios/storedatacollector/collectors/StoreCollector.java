package com.spacehopperstudios.storedatacollector.collectors;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.googlecode.objectify.Key;
import com.spacehopperstudios.storedatacollector.datatypes.FeedFetch;
import com.spacehopperstudios.storedatacollector.logging.GaeLevel;

public abstract class StoreCollector {

	public static final int MAX_DATA_CHUNK_LENGTH = 500000;

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

		AppEngineFile file = null;
		FileService fileService = FileServiceFactory.getFileService();

		boolean blob = false;
		FileWriteChannel writeChannel = null;
		BufferedWriter writer = null;

		try {
			file = fileService.createNewBlobFile("application/json", code + "_" + countryCode + "_" + type + ".json");
			writeChannel = fileService.openWriteChannel(file, true);

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
					writeChannel.closeFinally();
				} catch (IllegalStateException e) {
					LOG.log(Level.SEVERE, "Failed to close write channel", e);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Failed to close write channel", e);
				}
			}
		}

		if (blob) {
			FeedFetch entity = new FeedFetch();

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Saving as blob @ [%s]", file.getFullPath()));
			}

			entity.data = file.getFullPath();
			entity.country = countryCode;
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
