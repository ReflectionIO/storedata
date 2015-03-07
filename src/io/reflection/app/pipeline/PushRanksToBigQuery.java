//
//  PushRanksToBigQuery.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.ingestors.BigQueryRankIngestorIos;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author William Shakour (billy1380)
 *
 */
public class PushRanksToBigQuery extends Job2<Void, String, Long> {

	private static final long serialVersionUID = 158264446923346332L;
	
	private String name;

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

			List<Item> items = new ArrayList<>();
			Item item;
			for (JsonElement element : array) {
				if (element.isJsonObject()) {
					item = new Item();
					item.fromJson(element.getAsJsonObject());

					items.add(item);
				}
			}

			BigQueryRankIngestorIos.storeIngested(items, feedFetch);
		}

		return null;
	}
	
	public PushRanksToBigQuery name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return name;
	}
}
