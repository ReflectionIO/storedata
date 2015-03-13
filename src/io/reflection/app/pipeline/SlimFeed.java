//
//  SlimFeed.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.ingestors.IngestorIosHelper;
import io.reflection.app.ingestors.ParserIOS;
import io.reflection.app.logging.GaeLevel;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.JsonArray;
import com.spacehopperstudios.utility.JsonUtils;

public class SlimFeed extends Job1<String, Long> {

	private static final long serialVersionUID = -627864366513850701L;

	private transient static final Logger LOG = Logger.getLogger(SlimFeed.class.getName());
	
	private transient String name = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<String> run(Long feedId) throws Exception {
		String slimmed = null;

		List<FeedFetch> stored = null;
		Map<Date, Map<Integer, FeedFetch>> grouped = null;
		Map<Date, String> combined = null;

		stored = IngestorIosHelper.get(Arrays.asList(feedId));
		grouped = IngestorIosHelper.groupDataByDate(stored);
		combined = IngestorIosHelper.combineDataParts(grouped);

		for (final Date key : combined.keySet()) {
			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Parsing [%s]", key.toString()));
			}

			Map<Integer, FeedFetch> group = grouped.get(key);
			Iterator<FeedFetch> iterator = group.values().iterator();
			FeedFetch firstFeedFetch = iterator.next();

			List<Item> items = (new ParserIOS()).parse(firstFeedFetch.country, firstFeedFetch.category.id, combined.get(key));

			JsonArray itemJsonArray = new JsonArray();

			for (Item item : items) {
				itemJsonArray.add(item.toJson());
			}

			slimmed = JsonUtils.cleanJson(itemJsonArray.toString());

			// we are only expecting to do this for one feed for break just in-case
			break;
		}

		return immediate(slimmed);
	}

	public SlimFeed name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return (name == null ? super.getJobDisplayName() : name);
	}

}