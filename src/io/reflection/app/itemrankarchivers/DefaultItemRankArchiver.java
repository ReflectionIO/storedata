//
//  DefaultItemRankArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.archivablekeyvalue.peristence.ValueAppender;
import io.reflection.app.archivablekeyvalue.peristence.objectify.KeyValueArchiveManager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.SliceHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spacehopperstudios.utility.JsonUtils;

/**
 * @author billy1380
 * 
 */
public class DefaultItemRankArchiver implements ItemRankArchiver {

	private static final Logger LOG = Logger.getLogger(DefaultItemRankArchiver.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#enqueue(java.lang.Long)
	 */
	@Override
	public void enqueue(Long id) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("archive");

			TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
			options.param("type", "itemrank");
			options.param("id", id.toString());

			try {
				queue.add(options);
			} catch (TransientFailureException ex) {

				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
				}

				// retry once
				try {
					queue.add(options);
				} catch (TransientFailureException reEx) {
					if (LOG.isLoggable(Level.SEVERE)) {
						LOG.log(Level.SEVERE,
								String.format("Retry of with payload [%s] failed while adding to queue [%s] twice", options.toString(), queue.getQueueName()),
								reEx);
					}
				}
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#archive(java.lang.Long)
	 */
	@Override
	public void archiveRank(Long id) throws DataAccessException {
		archive(RankServiceProvider.provide().getRank(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#archive(io.reflection.app.datatypes.shared.Rank)
	 */
	@Override
	public void archive(Rank rank) throws DataAccessException {
		KeyValueArchiveManager.get().setAppender(Rank.class, new ValueAppender<Rank>() {

			@Override
			public String getNewValue(String currentValue, Rank object) {
				String newValue = currentValue;

				if (object != null) {
					JsonElement jsonElement = currentValue == null ? null : (new JsonParser()).parse(currentValue);
					JsonArray newJsonArray = new JsonArray();

					if (jsonElement != null && jsonElement.isJsonArray()) {
						JsonArray jsonArray = jsonElement.getAsJsonArray();

						Rank existingRank;
						for (JsonElement jsonRank : jsonArray) {
							existingRank = new Rank();
							existingRank.fromJson(jsonRank.getAsJsonObject());

							if (existingRank.id.longValue() != object.id.longValue()) {
								newJsonArray.add(jsonRank);
							}
						}
					}

					newJsonArray.add(object.toJson());
					newValue = JsonUtils.cleanJson(newJsonArray.toString());
				}

				return newValue;
			}
		});

		Category allCategory = null;

		if (rank.category == null) {
			if (allCategory == null) {
				Store store = new Store();
				store.a3Code = rank.source;
				allCategory = CategoryServiceProvider.provide().getAllCategory(store);
			}

			rank.category = allCategory;
		}

		KeyValueArchiveManager.get().appendToValue(createArchiveKey(rank), rank);
	}

	private String createArchiveKey(Rank rank) {
		StringBuffer sb = new StringBuffer();

		sb.append("archiver.rank.");
		sb.append(rank.source);
		sb.append(".");
		sb.append(rank.country);
		sb.append(".");
		sb.append(rank.category.id.toString());
		sb.append(".");
		sb.append(rank.itemId);
		sb.append(".");
		sb.append(Long.toString(SliceHelper.offset(rank.date)));

		return sb.toString();
	}

}
