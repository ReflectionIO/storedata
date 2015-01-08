//
//  PredictorIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.CallServiceMethodServlet;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.apple.ItemPropertyLookupServlet;
import io.reflection.app.archivers.ItemRankArchiver;
import io.reflection.app.archivers.ArchiverFactory;
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
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.modelrun.ModelRunServiceProvider;
import io.reflection.app.service.rank.IRankService;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.spacehopperstudios.utility.StringUtils;

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
		Map<String, Item> lookup = new HashMap<String, Item>();

		List<String> itemIds = new ArrayList<String>();

		for (Rank rank : ranks) {
			if (!itemIds.contains(rank.itemId)) {
				itemIds.add(rank.itemId);
			}
		}

		if (itemIds.size() > 0) {
			List<Item> foundItems = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);

			for (Item item : foundItems) {
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

		Country c = new Country();
		c.a2Code = country;

		Store s = DataTypeHelper.getIosStore();

		Pager p = new Pager();
		p.start = Long.valueOf(0);
		p.count = new Long(Long.MAX_VALUE);

		Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);

		Collector collector = CollectorFactory.getCollectorForStore(DataTypeHelper.IOS_STORE_A3);
		List<String> listTypes = new ArrayList<String>();
		listTypes.addAll(collector.getCounterpartTypes(type));
		listTypes.add(type);

		FormType form = modeller.getForm(type);
		ModelRun modelRun = ModelRunServiceProvider.provide().getGatherCodeModelRun(c, s, form, code);

		Category category = CategoryServiceProvider.provide().getAllCategory(s);

		List<Rank> foundRanks = RankServiceProvider.provide().getGatherCodeRanks(c, s, category, type, code, p, Boolean.TRUE);
		Map<String, Item> lookup = lookupItemsForRanks(foundRanks);

		ItemRankArchiver archiver = null;

		Item item = null;
		for (Rank rank : foundRanks) {
			item = lookup.get(rank.itemId);
			ItemPropertyWrapper properties = new ItemPropertyWrapper(item.properties);

			if (archiver == null) {
				archiver = ArchiverFactory.getItemRankArchiver();
			}

			boolean usesIap = properties.getBoolean(ItemPropertyLookupServlet.PROPERTY_IAP);

			if (item != null) {
				setDownloadsAndRevenue(rank, modelRun, usesIap, rank.price.floatValue());

				// FIXME: we need to fix this
				if (rank.revenue.longValue() == Long.MAX_VALUE) {
					continue;
				}

				// if (rank.price.floatValue() > 0) {
				// if (usesIap) {
				// if (rank.grossingPosition != null) {
				// rank.downloads = Integer.valueOf((int) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
				// -modelRun.grossingAIap)));
				// rank.revenue = Float.valueOf((float) (rank.downloads.doubleValue() * (rank.price.doubleValue() + modelRun.theta.doubleValue())));
				// } else {
				// rank.downloads = Integer.valueOf((int) (modelRun.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.paidAIap)));
				// rank.revenue = Float.valueOf((float) ((modelRun.theta.doubleValue() + rank.price.doubleValue()) * rank.downloads.doubleValue()));
				// }
				//
				// } else {
				// if (rank.grossingPosition != null) {
				// rank.downloads = Integer.valueOf((int) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
				// -modelRun.grossingA.doubleValue())));
				// rank.revenue = Float.valueOf((float) (rank.downloads.floatValue() * rank.price.floatValue()));
				// } else {
				// rank.downloads = Integer.valueOf((int) (modelRun.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.paidA)));
				// rank.revenue = Float.valueOf(rank.price.floatValue() * rank.downloads.floatValue());
				// }
				//
				// }
				// } else if (rank.price.floatValue() == 0) {
				// if (usesIap || rank.grossingPosition != null) {
				// if (rank.grossingPosition != null) {
				// rank.downloads = Integer.valueOf((int) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
				// -modelRun.grossingAIap.doubleValue())));
				// rank.revenue = Float.valueOf((float) (rank.downloads.doubleValue() * modelRun.theta.doubleValue()));
				// } else {
				// rank.downloads = Integer.valueOf((int) (modelRun.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.freeA)));
				// rank.revenue = Float.valueOf((float) (modelRun.theta.doubleValue() * rank.downloads.doubleValue()));
				// }
				// } else {
				// if (rank.grossingPosition != null) {
				// // ERROR!!!
				// } else {
				// rank.downloads = Integer.valueOf((int) (modelRun.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.freeA)));
				// rank.revenue = Float.valueOf(0);
				//
				// }
				//
				// }
				// }

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
		List<FeedFetch> feeds = FeedFetchServiceProvider.provide().getGatherCodeFeedFetches(country, store, category, listTypes, code);

		for (FeedFetch feedFetch : feeds) {
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
					downloads = (double) (output.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.freeA.doubleValue()));
					revenue = output.theta.doubleValue() * downloads;
				} else {
					revenue = output.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -output.grossingA.doubleValue());
					downloads = revenue / output.theta.doubleValue();
				}
			} else {
				if (rank.grossingPosition == null && rank.grossingPosition.intValue() == 0) {
					downloads = (double) (output.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.paidAIap.doubleValue()));
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
				downloads = (double) (output.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.freeA.doubleValue()));
			} else {
				if (rank.grossingPosition == null && rank.grossingPosition.intValue() == 0) {
					downloads = (double) (output.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -output.paidA.doubleValue()));
					revenue = downloads * price;
				} else {
					// downloads = (double) (output.paidB * Math.pow(rank, -output.paidA));
					revenue = output.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -output.grossingA.doubleValue());
					// revenue = reconcile(revenue, grossing_revenue);
					downloads = revenue / price;
				}
			}
		}

		if (rank.downloads == null || (rank.downloads.intValue() == 0 && (int) downloads != 0)) {
			rank.downloads = Integer.valueOf((int) downloads);
		}

		if (rank.revenue == null || (DataTypeHelper.isZero(rank.revenue.floatValue()) && !DataTypeHelper.isZero((float) revenue))) {
			rank.revenue = Float.valueOf((float) revenue);
		}

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Downloads :" + downloads);
			LOG.info("Revenue :" + revenue);
		}
	}

	// private void rank(ModelRun output, int downloads, double revenue, String rankType, boolean usesIap, float price) {
	// double predictedRank = 0;
	//
	// if (usesIap) {
	// if ("revenue".equals(rankType)) {
	// if (isFree(price)) {
	// predictedRank = (double) Math.pow(((revenue / output.theta.doubleValue()) / output.freeB.doubleValue()),
	// (1.0 / -output.freeA.doubleValue()));
	// } else {
	// predictedRank = (double) Math.pow((revenue / (price + output.theta.doubleValue()) / output.paidB.doubleValue()),
	// (-1.0 / output.paidA.doubleValue()));
	// }
	// } else {
	// if (isFree(price)) {
	// predictedRank = (double) Math.pow((downloads / output.freeB.doubleValue()), (1.0 / -output.freeA.doubleValue()));
	// } else {
	// predictedRank = (double) Math.pow((downloads / output.paidB.doubleValue()), (1.0 / -output.paidA.doubleValue()));
	// }
	// }
	// } else {
	// if ("revenue".equals(rankType)) {
	// if (!isFree(price)) {
	// predictedRank = (double) Math.pow((revenue / output.grossingB.doubleValue()), (1.0 / -output.grossingA.doubleValue()));
	// }
	// } else {
	// if (isFree(price)) {
	// predictedRank = (double) Math.pow((downloads / output.freeB.doubleValue()), (1.0 / -output.freeA.doubleValue()));
	// } else {
	// predictedRank = (double) Math.pow((downloads / output.paidB.doubleValue()), (1.0 / -output.paidA.doubleValue()));
	// }
	// }
	// }
	//
	// LOG.info("predictedRank :" + predictedRank);
	// }

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
		IRankService rankService = RankServiceProvider.provide();

		Store s = new Store();
		s.a3Code = simpleModelRun.feedFetch.store;

		Country c = new Country();
		c.a2Code = simpleModelRun.feedFetch.country;

		List<Rank> foundRanks = rankService.getGatherCodeRanks(c, s, simpleModelRun.feedFetch.category, simpleModelRun.feedFetch.type,
				simpleModelRun.feedFetch.code, PagerHelper.createInfinitePager(), Boolean.TRUE);

		Map<String, Item> lookup = lookupItemsForRanks(foundRanks);

		ItemRankArchiver archiver = ArchiverFactory.getItemRankArchiver();

		Item item = null;
		Boolean usesIap = null;

		for (Rank rank : foundRanks) {
			item = lookup.get(rank.itemId);

			if (item == null) {
				usesIap = null;
			} else {
				ItemPropertyWrapper properties = new ItemPropertyWrapper(item.properties);
				usesIap = properties.getBoolean(ItemPropertyLookupServlet.PROPERTY_IAP, null);
			}

			setSimpleDownloadsAndRevenue(rank, simpleModelRun, usesIap);

			RankServiceProvider.provide().updateRank(rank);

			if (archiver != null) {
				archiver.enqueueIdRank(rank.id);
			}
		}

		alterFeedFetchStatus(simpleModelRun.feedFetch);

		Collector collector = CollectorFactory.getCollectorForStore(s.a3Code);
		boolean isGrossing = collector.isGrossing(simpleModelRun.feedFetch.type);
		Pager pager = PagerHelper.createInfinitePager();
		if (isGrossing) {
			pager.sortBy = "grossingposition";
		} else {
			pager.sortBy = "position";
		}

		if (pager.sortDirection == null) {
			pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
		}

		List<String> listTypes = new ArrayList<String>();

		if (isGrossing) {
			listTypes.addAll(collector.getCounterpartTypes(simpleModelRun.feedFetch.type));
		}

		listTypes.add(simpleModelRun.feedFetch.type);
		String memcacheKey = RankServiceProvider.provide().getName() + ".gathercoderanks." + simpleModelRun.feedFetch.code.toString() + "." + c.a2Code + "."
				+ s.a3Code + "." + simpleModelRun.feedFetch.category.id.toString() + "." + StringUtils.join(listTypes, ".") + "." + pager.start + "."
				+ pager.count + "." + pager.sortDirection + "." + pager.sortBy;

		PersistentMapFactory.createObjectify().delete(memcacheKey);

		CallServiceMethodServlet.enqueueGetAllRanks(c.a2Code, s.a3Code, simpleModelRun.feedFetch.category.id, simpleModelRun.feedFetch.type,
				simpleModelRun.feedFetch.code);

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("predictRevenueAndDownloads completed and status updated");
		}
	}

	private void setSimpleDownloadsAndRevenue(Rank rank, SimpleModelRun simpleModelRun, Boolean usesIap) {
		float price = rank.price.floatValue();
		boolean isDownload = isDownloadListType(simpleModelRun.feedFetch.type), isFree = DataTypeHelper.isZero(price);
		double revenue = 0.0, downloads = 0.0;

		if (usesIap == null || usesIap.booleanValue()) {
			if (isFree) {
				if (isDownload) {
					downloads = (double) (simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue()));
				} else {
					revenue = simpleModelRun.b.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -simpleModelRun.a.doubleValue());
				}
			} else {
				if (isDownload) {
					downloads = (double) (simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue()));
				} else {
					revenue = simpleModelRun.b.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -simpleModelRun.a.doubleValue());
				}
			}
		} else {
			if (isFree) {
				// revenue is zero since it is a free app and no IAP. Thus only
				// download calculated here
				downloads = (double) (simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue()));
			} else {
				if (isDownload) {
					downloads = (double) (simpleModelRun.b.doubleValue() * Math.pow(rank.position.doubleValue(), -simpleModelRun.a.doubleValue()));

					if (rank.grossingPosition == null || rank.grossingPosition.intValue() == 0) {
						revenue = (double) downloads * (double) price;
					}
				} else {
					revenue = simpleModelRun.b.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(), -simpleModelRun.a.doubleValue());

					if (rank.position == null || rank.position.intValue() == 0) {
						downloads = (int) (revenue / (double) price);
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
