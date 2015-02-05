//
//  RanksToBigQuery.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableRow;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author William Shakour (billy1380)
 *
 */
public class PushRanksToBigQuery extends Job2<Void, String, Long> {

	private static final long serialVersionUID = 158264446923346332L;

	private static final Logger LOG = Logger.getLogger(BigQueryHelper.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Void> run(String slimmedRanks, Long feedFetchId) throws Exception {
		FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetchId);

		if ((slimmedRanks != null && !slimmedRanks.equals("null")) && feedFetch != null) {
			JsonArray array = (new JsonParser()).parse(slimmedRanks).getAsJsonArray();

			final int size = array.size();
			if (size > 0) {
				Item item;
				TableRow row;
				JsonObject object;
				TableDataInsertAllRequest.Rows rows;
				List<TableDataInsertAllRequest.Rows> rowList = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					object = array.get(i).getAsJsonObject();

					item = new Item();
					item.fromJson(object);

					rows = new TableDataInsertAllRequest.Rows();

					row = new TableRow();
					row.set("feedfetchid", feedFetch.id);
					row.set("categoryid", feedFetch.category.id);
					row.set("code", feedFetch.code);
					row.set("country", feedFetch.country);
					row.set("store", feedFetch.store);
					row.set("listtype", feedFetch.type);
					row.set("date", Long.valueOf(feedFetch.date.getTime() / 1000));
					row.set("currency", item.currency);
					row.set("itemid", item.internalId);
					row.set("position", Integer.valueOf(i + 1));
					row.set("price", item.price);

					rows.setInsertId(feedFetchId.toString() + "-" + Integer.toString(i + 1));
					rows.setJson(row);

					rowList.add(rows);
				}

				TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows(rowList);

				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(content.toPrettyString());
				}

				TableDataInsertAllResponse response = BigQueryHelper.insertAll("rank", content);

				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(response.toPrettyString());
				}
			}
		}

		return null;
	}
}
