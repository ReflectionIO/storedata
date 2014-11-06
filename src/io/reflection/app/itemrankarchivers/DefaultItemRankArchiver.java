//
//  DefaultItemRankArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.archivablekeyvalue.peristence.ValueAppender;
import io.reflection.app.archivablekeyvalue.peristence.objectify.ArchivableKeyValue;
import io.reflection.app.archivablekeyvalue.peristence.objectify.KeyValueArchiveManager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.SliceHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.spacehopperstudios.utility.StringUtils;

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
	public void enqueueIdRank(Long id) {
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
	public void archiveIdRank(Long id) throws DataAccessException {
		archiveRank(RankServiceProvider.provide().getRank(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#archive(io.reflection.app.datatypes.shared.Rank)
	 */
	@Override
	public void archiveRank(Rank rank) throws DataAccessException {
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
				allCategory = CategoryServiceProvider.provide().getAllCategory(DataTypeHelper.createStore(rank.source));
			}

			rank.category = allCategory;
		}

		KeyValueArchiveManager.get().appendToValue(createArchiveKey(rank), rank);
	}

	private String createArchiveKey(Rank rank) {
		Item item = new Item();
		item.internalId = rank.itemId;

		Store store = DataTypeHelper.createStore(rank.source);

		Country country = DataTypeHelper.createCountry(rank.country);

		FormType form = ModellerFactory.getModellerForStore(store.a3Code).getForm(rank.type);

		return createKey(Long.valueOf(SliceHelper.offset(rank.date)), item, form, store, country, rank.category);
	}

	@Override
	public String createKey(Long slice, Item item, FormType form, Store store, Country country, Category category) {
		return StringUtils.join(Arrays.asList("archiver", "item", "rank", slice.toString(), item.internalId == null ? item.id.toString() : item.internalId,
				form.toString(), store.a3Code, country.a2Code, category.id.toString()), ".");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#getItemRanks(java.lang.String)
	 */
	@Override
	public List<Rank> getItemRanks(String key) {
		List<Rank> ranks = null;
		Map<Date, Rank> ranksLookup = new HashMap<Date, Rank>();
		ArchivableKeyValue value = KeyValueArchiveManager.get().getArchiveKeyValue(key);

		// Date date;

		if (value != null && value.value != null && value.value.length() > 0) {
			JsonElement jsonElement = (new JsonParser()).parse(value.value);

			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();

				Rank rank, existingRank;
				for (JsonElement jsonArrayElement : jsonArray) {
					if (jsonArrayElement.isJsonObject()) {
						rank = new Rank();
						rank.fromJson(jsonArrayElement.getAsJsonObject());

						if (ranks == null) {
							ranks = new ArrayList<Rank>();
						}

						rank.date = ApiHelper.removeTime(rank.date);

						if ((existingRank = ranksLookup.get(rank.date)) == null) {
							ranks.add(rank);
							ranksLookup.put(rank.date, rank);
						} else {
							if (rank.code.longValue() == existingRank.code.longValue()) {
								if ((existingRank.position == null || existingRank.position.intValue() == 0) && rank.position != null
										&& rank.position.intValue() != 0) {
									existingRank.position = rank.position;
								}

								if ((existingRank.grossingPosition == null || existingRank.grossingPosition.intValue() == 0) && rank.grossingPosition != null
										&& rank.grossingPosition.intValue() != 0) {
									existingRank.grossingPosition = rank.grossingPosition;
								}
							}
						}
					}
				}
			}
		}

		if (ranks != null) {
			Collections.sort(ranks, new Comparator<Rank>() {

				@Override
				public int compare(Rank o1, Rank o2) {
					int compare = 0;

					if (o1.date.getTime() > o2.date.getTime()) {
						compare = 1;
					} else if (o1.date.getTime() < o2.date.getTime()) {
						compare = -1;
					}

					return compare;
				}
			});
		}

		return ranks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#enqueue(io.reflection.app.api.shared.datatypes.Pager, java.lang.Boolean)
	 */
	@Override
	public void enqueuePagerRanks(Pager pager, Boolean next) {
		try {
			List<Long> rankIds = RankServiceProvider.provide().getRankIds(pager);

			if (rankIds != null) {
				for (Long rankId : rankIds) {
					enqueueIdRank(rankId);
				}

				if (next != null && next.booleanValue() && pager.count.intValue() == rankIds.size()) {
					enqueueNext(pager);
				}
			}
		} catch (DataAccessException daEx) {
			throw new RuntimeException(daEx);
		}
	}

	private void enqueueNext(Pager pager) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("archive");

			TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
			options.param("type", "itemrank");
			options.param("pager", Boolean.TRUE.toString());
			options.param("next", Boolean.TRUE.toString());
			options.param("start", Long.toString(pager.start.longValue() + pager.count.longValue()));
			options.param("count", pager.count.toString());

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
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#enqueueFeedFetch(java.lang.Long)
	 */
	@Override
	public void archiveIdFeedFetchRanks(Long id) {
		try {
			FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(id);

			List<Rank> ranks = RankServiceProvider.provide().getGatherCodeRanks(DataTypeHelper.createCountry(feedFetch.country),
					DataTypeHelper.createStore(feedFetch.store), feedFetch.category, feedFetch.type, feedFetch.code, PagerHelper.infinitePager(), Boolean.TRUE);

			for (Rank rank : ranks) {
				archiveRank(rank);
			}
		} catch (DataAccessException daEx) {
			throw new RuntimeException(daEx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemrankarchivers.ItemRankArchiver#enqueueIdFeedFetch(java.lang.Long)
	 */
	@Override
	public void enqueueIdFeedFetch(Long id) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("archive");

			TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
			options.param("type", "feedfetchranks");
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
}
