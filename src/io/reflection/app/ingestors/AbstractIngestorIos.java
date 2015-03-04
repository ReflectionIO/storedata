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
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
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
			stored = IngestorIosHelper.get(itemIds);
			grouped = IngestorIosHelper.groupDataByDate(stored);
			combined = IngestorIosHelper.combineDataParts(grouped);
			
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

	
}
