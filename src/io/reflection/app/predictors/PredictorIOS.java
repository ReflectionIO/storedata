//
//  PredictorIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import static io.reflection.app.helpers.ItemPropertyWrapper.PROPERTY_IAP;
import io.reflection.app.CallServiceMethodServlet;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.archivers.ArchiverFactory;
import io.reflection.app.archivers.ItemRankArchiver;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.ModelRun;
import io.reflection.app.datatypes.shared.ModelTypeType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.ItemPropertyWrapper;
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.modelrun.ModelRunServiceProvider;
import io.reflection.app.service.rank.IRankService;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * @author billy1380
 *
 */
public class PredictorIOS implements Predictor {

	private static final Logger LOG = Logger.getLogger(PredictorIOS.class.getName());

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.predictors.Predictor#enqueue(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void enqueue(String country, String type, Long code) {
		QueueHelper.enqueue("predict", "/predict", Method.POST, new SimpleEntry<String, String>("country", country), new SimpleEntry<String, String>("store",
				DataTypeHelper.IOS_STORE_A3), new SimpleEntry<String, String>("type", type), new SimpleEntry<String, String>("code", code.toString()));
	}

	/**
	 * @param foundRanks
	 * @param lookup
	 * @return
	 * @throws DataAccessException
	 */
	private Map<String, Item> lookupItemsForRanks(List<Rank> ranks) throws DataAccessException {
		final Map<String, Item> lookup = new HashMap<String, Item>();

		final List<String> itemIds = new ArrayList<String>();

		for (final Rank rank : ranks) {
			if (!itemIds.contains(rank.itemId)) {
				itemIds.add(rank.itemId);
			}
		}

		if (itemIds.size() > 0) {
			final List<Item> foundItems = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);

			for (final Item item : foundItems) {
				lookup.put(item.internalId, item);
			}
		}

		return lookup;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.predictors.Predictor#predictRevenueAndDownloads(java.lang.String, java.lang.String, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void predictRevenueAndDownloads(String country, String type, Long code, Long categoryId) throws DataAccessException {

		final Country c = new Country();
		c.a2Code = country;

		final Store s = DataTypeHelper.getIosStore();

		final Pager p = new Pager();
		p.start = Long.valueOf(0);
		p.count = new Long(Long.MAX_VALUE);

		final Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);

		final Collector collector = CollectorFactory.getCollectorForStore(DataTypeHelper.IOS_STORE_A3);
		final List<String> listTypes = new ArrayList<String>();
		listTypes.addAll(collector.getCounterpartTypes(type));
		listTypes.add(type);

		final FormType form = modeller.getForm(type);
		final ModelRun modelRun = ModelRunServiceProvider.provide().getGatherCodeModelRun(c, s, form, code);

		final Category category = CategoryServiceProvider.provide().getAllCategory(s);

		final List<Rank> foundRanks = RankServiceProvider.provide().getGatherCodeRanks(c, category, type, code);
		final Map<String, Item> lookup = lookupItemsForRanks(foundRanks);

		ItemRankArchiver archiver = null;

		Item item = null;
		for (final Rank rank : foundRanks) {
			item = lookup.get(rank.itemId);
			final ItemPropertyWrapper properties = new ItemPropertyWrapper(item.properties);

			if (archiver == null) {
				archiver = ArchiverFactory.getItemRankArchiver();
			}

			final boolean usesIap = properties.getBoolean(PROPERTY_IAP);

			if (item != null) {
				setDownloadsAndRevenue(rank, modelRun, usesIap, rank.price.floatValue());

				// FIXME: we need to fix this
				if (rank.revenue.longValue() == Long.MAX_VALUE) {
					continue;
				}

				RankServiceProvider.provide().updateRank(rank);
				archiver.enqueueIdRank(rank.id);
			}
		}

		alterFeedFetchStatus(s, c, category, listTypes, code);

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("predictRevenueAndDownloads completed and status updated");
		}
	}

	/**
	 *
	 * @param country
	 * @param store
	 * @param category
	 * @param listTypes
	 * @param code
	 * @throws DataAccessException
	 */
	private void alterFeedFetchStatus(Store store, Country country, Category category, List<String> listTypes, Long code) throws DataAccessException {
		final List<FeedFetch> feeds = FeedFetchServiceProvider.provide().getGatherCodeFeedFetches(country, store, listTypes, code);

		for (final FeedFetch feedFetch : feeds) {
			if (feedFetch.category.id.longValue() == category.id.longValue()) {
				alterFeedFetchStatus(feedFetch);
			}
		}
	}

	private void alterFeedFetchStatus(FeedFetch feedFetch) throws DataAccessException {
		feedFetch.status = FeedFetchStatusType.FeedFetchStatusTypePredicted;
		FeedFetchServiceProvider.provide().updateFeedFetch(feedFetch);
	}

	private void setDownloadsAndRevenue(Rank rank, ModelRun output, boolean usesIap, float price) {
		double revenue = 0.0;
		double downloads = 0.0;

		if (usesIap) {
			if (DataTypeHelper.isZero(price)) {
				if (rank.grossingPosition == null && rank.grossingPosition.intValue() == 0) {
					downloads = output.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.freeA.doubleValue());
					revenue = output.theta.doubleValue() * downloads;
				} else {
					revenue = output.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -output.grossingA.doubleValue());
					downloads = revenue / output.theta.doubleValue();
				}
			} else {
				if (rank.grossingPosition == null && rank.grossingPosition.intValue() == 0) {
					downloads = output.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.paidAIap.doubleValue());
					revenue = downloads * (price + output.theta.doubleValue());
				} else {
					// downloads = (double) (output.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.paidAIap.doubleValue()));
					revenue = output.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -output.grossingAIap.doubleValue());
					downloads = revenue / (output.theta.doubleValue() + price);
				}
			}
		} else {
			if (DataTypeHelper.isZero(price)) {
				// revenue is zero since it is a free app and no IAP. Thus only
				// download calculated here
				downloads = output.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.freeA.doubleValue());
			} else {
				if (rank.grossingPosition == null && rank.grossingPosition.intValue() == 0) {
					downloads = output.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.paidA.doubleValue());
					revenue = downloads * price;
				} else {
					// downloads = (double) (output.paidB * Math.pow(rank, -output.paidA));
					revenue = output.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -output.grossingA.doubleValue());
					// revenue = reconcile(revenue, grossing_revenue);
					downloads = revenue / price;
				}
			}
		}

		if (rank.downloads == null || rank.downloads.intValue() == 0 && (int) downloads != 0) {
			rank.downloads = Integer.valueOf((int) downloads);
		}

		if (rank.revenue == null || DataTypeHelper.isZero(rank.revenue.floatValue()) && !DataTypeHelper.isZero((float) revenue)) {
			rank.revenue = Float.valueOf((float) revenue);
		}

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Downloads :" + downloads);
			LOG.info("Revenue :" + revenue);
		}
	}

	private boolean isDownloadListType(String listType) {
		return !listType.contains("grossing");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.predictors.Predictor#enqueue(io.reflection.app.datatypes.shared.SimpleModelRun)
	 */
	@Override
	public void enqueue(SimpleModelRun simpleModelRun) {
		QueueHelper.enqueue("predict", "/predict", Method.POST, new SimpleEntry<String, String>("runid", simpleModelRun.id.toString()),
				new SimpleEntry<String, String>("modeltype", ModelTypeType.ModelTypeTypeSimple.toString()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.predictors.Predictor#enqueue(io.reflection.app.datatypes.shared.ModelRun)
	 */
	@Override
	public void enqueue(ModelRun modelRun) {
		QueueHelper.enqueue("predict", "/predict", Method.POST, new SimpleEntry<String, String>("runid", modelRun.id.toString()),
				new SimpleEntry<String, String>("modeltype", ModelTypeType.ModelTypeTypeCorrelation.toString()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.predictors.Predictor#predictWithSimpleModel(io.reflection.app.datatypes.shared.SimpleModelRun)
	 */
	@Override
	public void predictWithSimpleModel(SimpleModelRun simpleModelRun) throws DataAccessException {
		final IRankService rankService = RankServiceProvider.provide();

		final Store s = new Store();
		s.a3Code = simpleModelRun.feedFetch.store;

		final Country c = new Country();
		c.a2Code = simpleModelRun.feedFetch.country;

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Loading ranks that are covered by the simplemodelrun (feedfetch type: %s and code: %s)",
					simpleModelRun.feedFetch.type, simpleModelRun.feedFetch.code));
		}

		final List<Rank> foundRanks = rankService.getGatherCodeRanks(c, simpleModelRun.feedFetch.category, simpleModelRun.feedFetch.type,
				simpleModelRun.feedFetch.code);

		final Map<String, Item> lookup = lookupItemsForRanks(foundRanks);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Found %d ranks and %d items from them", foundRanks.size(), lookup.size()));
		}

		final ItemRankArchiver archiver = ArchiverFactory.getItemRankArchiver();

		Item item = null;
		Boolean usesIap = null;

		for (final Rank rank : foundRanks) {
			item = lookup.get(rank.itemId);

			if (item == null) {
				usesIap = null;
			} else {
				final ItemPropertyWrapper properties = new ItemPropertyWrapper(item.properties);
				usesIap = properties.getBoolean(PROPERTY_IAP, null);
			}

			setSimpleDownloadsAndRevenue(rank, simpleModelRun, usesIap);

			RankServiceProvider.provide().updateRank(rank);

			if (archiver != null) {
				archiver.enqueueIdRank(rank.id);
			}
		}

		alterFeedFetchStatus(simpleModelRun.feedFetch);

		Calendar cal = Calendar.getInstance();
		cal.setTime(simpleModelRun.feedFetch.date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		final String memcacheKey = RankServiceProvider.provide().getName() + ".getRanks." + c.a2Code + "." + simpleModelRun.feedFetch.category.id.toString()
				+ "." + simpleModelRun.feedFetch.type + "." + cal.getTime().getTime();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("PredictorIOS clearing rank cache with key: %s and enqueuing call to re-get the rank", memcacheKey));
		}

		PersistentMapFactory.createObjectify().delete(memcacheKey);

		CallServiceMethodServlet.enqueueGetAllRanks(c.a2Code, s.a3Code, simpleModelRun.feedFetch.category.id, simpleModelRun.feedFetch.type,
				simpleModelRun.feedFetch.code);

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("predictRevenueAndDownloads completed and status updated");
		}
	}

	private void setSimpleDownloadsAndRevenue(Rank rank, SimpleModelRun simpleModelRun, Boolean usesIap) {
		final float price = rank.price.floatValue();
		final boolean isDownload = isDownloadListType(simpleModelRun.feedFetch.type), isFree = DataTypeHelper.isZero(price);
		double revenue = 0.0, downloads = 0.0;

		if (usesIap == null || usesIap.booleanValue()) {
			if (isFree) {
				if (isDownload) {
					downloads = simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue());
				} else {
					revenue = simpleModelRun.b.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -simpleModelRun.a.doubleValue());
				}
			} else {
				if (isDownload) {
					downloads = simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue());
				} else {
					revenue = simpleModelRun.b.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -simpleModelRun.a.doubleValue());
				}
			}
		} else {
			if (isFree) {
				// revenue is zero since it is a free app and no IAP. Thus only
				// download calculated here
				downloads = simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue());
			} else {
				if (isDownload) {
					downloads = simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue());

					if (rank.grossingPosition == null || rank.grossingPosition.intValue() == 0) {
						revenue = downloads * price;
					}
				} else {
					revenue = simpleModelRun.b.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -simpleModelRun.a.doubleValue());

					if (rank.position == null || rank.position.intValue() == 0) {
						downloads = (int) (revenue / price);
					}
				}
			}
		}

		// These numbers still overflow using the current model
		rank.downloads = Integer.valueOf((int) downloads);
		rank.revenue = Float.valueOf((float) revenue);

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Downloads :" + downloads);
			LOG.info("Revenue :" + revenue);
		}
	}
}
