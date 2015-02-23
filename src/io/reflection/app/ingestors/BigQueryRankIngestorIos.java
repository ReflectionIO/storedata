//  
//  BigQueryRankIngestorIos.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.ingestors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.bigquery.BigQueryHelper;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.logging.GaeLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableRow;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

/**
 * @author billy1380
 * 
 */
public class BigQueryRankIngestorIos extends AbstractIngestorIos implements Ingestor {

	private static final Logger LOG = Logger.getLogger(BigQueryRankIngestorIos.class.getName());

	/**
	 * extractItemRanks
	 * 
	 * @param stored
	 * @param grouped
	 * @param combined
	 * @throws DataAccessException
	 */
	protected void extractItemRanks(List<FeedFetch> stored, final Map<Date, Map<Integer, FeedFetch>> grouped, Map<Date, String> combined)
			throws DataAccessException {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Extracting item ranks");
		}

		for (final Date key : combined.keySet()) {

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Parsing [%s]", key.toString()));
			}

			Map<Integer, FeedFetch> group = grouped.get(key);
			Iterator<FeedFetch> iterator = group.values().iterator();
			FeedFetch firstFeedFetch = iterator.next();

			List<Item> items = (new ParserIOS()).parse(firstFeedFetch.country, firstFeedFetch.category.id, combined.get(key));

			final int size = items.size();
			if (size > 0) {
				Item item;
				TableRow row;
				TableDataInsertAllRequest.Rows rows;
				List<TableDataInsertAllRequest.Rows> rowList = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					item = items.get(i);

					rows = new TableDataInsertAllRequest.Rows();

					row = new TableRow();
					row.set("feedfetchid", firstFeedFetch.id);
					row.set("categoryid", firstFeedFetch.category.id);
					row.set("code", firstFeedFetch.code);
					row.set("country", firstFeedFetch.country);
					row.set("store", firstFeedFetch.store);
					row.set("listtype", firstFeedFetch.type);
					row.set("date", Long.valueOf(firstFeedFetch.date.getTime() / 1000));
					row.set("currency", item.currency);
					row.set("itemid", item.internalId);
					row.set("position", Integer.valueOf(i + 1));
					row.set("price", item.price);

					rows.setInsertId(firstFeedFetch.id.toString() + "-" + Integer.toString(i + 1));
					rows.setJson(row);

					rowList.add(rows);
				}

				try {
					TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows(rowList);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, content.toPrettyString());
					}

					TableDataInsertAllResponse response;

					response = BigQueryHelper.insertAll("rank", content);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, response.toPrettyString());
					}
				} catch (IOException e) {
					throw new DataAccessException(e);
				}
			}
		}
	}

	@Override
	public void enqueue(List<Long> itemIds) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("default");

			if (queue != null) {
				enqueue(queue, itemIds, "itype=bigquery");
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}
}