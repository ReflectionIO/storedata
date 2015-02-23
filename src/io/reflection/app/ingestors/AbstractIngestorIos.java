//
//  AbstractIngestorIos.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.ingestors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
@SuppressWarnings("deprecation")
public abstract class AbstractIngestorIos implements Ingestor {

	private static final Logger LOG = Logger.getLogger(AbstractIngestorIos.class.getName());

	@Override
	public void ingest(List<Long> itemIds) throws DataAccessException {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		List<FeedFetch> stored = null;
		Map<Date, Map<Integer, FeedFetch>> grouped = null;
		Map<Date, String> combined = null;

		try {
			stored = get(itemIds);
			grouped = groupDataByDate(stored);
			combined = combineDataParts(grouped);
			extractItemRanks(stored, grouped, combined);
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

	protected abstract void extractItemRanks(List<FeedFetch> stored, final Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined)
			throws DataAccessException;

	protected void enqueue(Queue queue, List<Long> itemIds) {
		enqueue(queue, itemIds, (String) null);
	}

	protected void enqueue(Queue queue, List<Long> itemIds, String... params) {
		StringBuffer buffer = new StringBuffer();
		for (Long id : itemIds) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}

			buffer.append(id.toString());
		}

		String store = DataTypeHelper.IOS_STORE_A3, ids = buffer.toString();

		try {
			String url = String.format(ENQUEUE_INGEST_FORMAT, store, ids);

			if (params != null && params.length > 0) {
				url += "&" + StringUtils.join(Arrays.asList(params), "&");
			}

			queue.add(TaskOptions.Builder.withUrl(url).method(Method.GET));
		} catch (TransientFailureException ex) {

			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
			}

			// retry once
			try {
				queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_INGEST_FORMAT, store, ids)).method(Method.GET));
			} catch (TransientFailureException reEx) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							String.format("Retry of with parameters store [%s] ids [%s] failed while adding to queue [%s] twice", store, ids,
									queue.getQueueName()), reEx);
				}
			}
		}
	}

	/**
	 * @param ranks
	 * @return
	 */
	protected Map<String, Rank> indexRanks(List<Rank> ranks) {
		Map<String, Rank> indexed = new HashMap<String, Rank>();

		for (Rank rank : ranks) {
			indexed.put(constructKey(rank), rank);
		}

		return indexed;
	}

	/**
	 * @param rank
	 * @return
	 */
	protected String constructKey(Rank rank) {
		return rank.code + rank.source + rank.country + rank.category.id.toString() + rank.itemId;
	}

	protected Map<Date, Map<Integer, FeedFetch>> groupDataByDate(List<FeedFetch> entities) {
		Map<Date, Map<Integer, FeedFetch>> map = new HashMap<Date, Map<Integer, FeedFetch>>();
		Map<Date, Integer> sizeMap = new HashMap<Date, Integer>();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Grouping entites by date");
		}

		for (FeedFetch entity : entities) {
			Date date = entity.date;
			Map<Integer, FeedFetch> dataChunks = null;
			// String data = ((Text) entity.getProperty(DataStoreDataCollector.ENTITY_COLUMN_DATA)).getValue();

			int chunkIndex = entity.part;
			int totalChunks = entity.totalParts;

			if ((dataChunks = map.get(date)) == null) {
				dataChunks = new HashMap<Integer, FeedFetch>(totalChunks);
				map.put(date, dataChunks);
				sizeMap.put(date, Integer.valueOf(totalChunks));
			}

			dataChunks.put(Integer.valueOf(chunkIndex), entity);
		}

		List<Date> remove = new ArrayList<Date>();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Creating list of incomplete feeds");
		}

		// remove any items with incomplete sets
		int i = 0;
		for (Date key : map.keySet()) {
			Map<Integer, FeedFetch> part = map.get(key);
			if (part.size() < sizeMap.get(key).intValue()) {
				remove.add(key);
				i++;
			}
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("List contains [%d] items", i));
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "remove any items with incomplete sets");
		}

		i = 0;
		for (Date date : remove) {
			map.remove(date);

			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, String.format("Removed item with key [%s]", date.toString()));
			}

			i++;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Removed [%d] items", i));
		}

		return map;
	}

	protected Map<Date, String> combineDataParts(Map<Date, Map<Integer, FeedFetch>> grouped) {

		Map<Date, String> combined = new HashMap<Date, String>(grouped.size());

		for (Date date : grouped.keySet()) {
			Map<Integer, FeedFetch> group = grouped.get(date);
			String data = null;
			boolean blob = false;

			if (group.size() == 1) {
				data = group.values().iterator().next().data;
				if (data.startsWith("/blobstore/writable:") || data.startsWith("/gs/")) {
					blob = true;
				}
			}

			StringBuffer buffer = new StringBuffer();

			if (blob) {
				FileService fileService = FileServiceFactory.getFileService();

				AppEngineFile file = new AppEngineFile(data);
				FileReadChannel readChannel = null;
				BufferedReader reader = null;

				try {
					readChannel = fileService.openReadChannel(file, false);
					reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
					int length;
					char[] bytes = new char[1024];
					while ((length = reader.read(bytes)) > 0) {
						buffer.append(bytes, 0, length);
					}
				} catch (IOException e) {
					LOG.log(Level.SEVERE, String.format("Error closing read channel for file [%s]", file.getFullPath()), e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							LOG.log(Level.SEVERE, "Error closing reader", e);
						}
					}

					if (readChannel != null) {
						try {
							readChannel.close();
						} catch (IOException e) {
							LOG.log(Level.SEVERE, String.format("Error closing read channel for file [%s]", file.getFullPath()), e);
						}
					}
				}

			} else {
				for (int i = 0; i < group.size(); i++) {
					FeedFetch part = group.get(Integer.valueOf(i + 1));
					data = part.data;
					buffer.append(data);
				}
			}

			combined.put(date, buffer.toString());
		}

		return combined;
	}

	protected List<FeedFetch> get(List<Long> itemIds) throws DataAccessException {

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Fetching [%d] items", itemIds == null ? 0 : itemIds.size()));
		}

		List<FeedFetch> stored = new ArrayList<FeedFetch>();

		// for now ingest so that we don't kill the band width

		int i = 0;
		FeedFetch row = null;
		for (Long itemId : itemIds) {
			row = FeedFetchServiceProvider.provide().getFeedFetch(itemId);
			stored.add(row);
			i++;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Found [%d] ingestable items", i));
		}

		return stored;
	}
}
