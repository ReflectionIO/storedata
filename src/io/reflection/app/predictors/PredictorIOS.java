//
//  PredictorIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.apple.ItemPropertyLookupServlet;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.ModelRun;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.ItemPropertyWrapper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.modelrun.ModelRunServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;

import java.util.ArrayList;
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

/**
 * @author billy1380
 * 
 */
public class PredictorIOS implements Predictor {

	private static final Logger LOG = Logger.getLogger(PredictorIOS.class.getName());

	private static final String IOS_STORE_A3 = "ios";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.predictors.Predictor#enqueue(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void enqueue(String country, String type, Long code) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("predict");

			TaskOptions options = TaskOptions.Builder.withUrl("/predict").method(Method.POST);
			options.param("country", country);
			options.param("store", IOS_STORE_A3);
			options.param("type", type);
			options.param("code", code.toString());

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
			List<Item> foundItems = ItemServiceProvider.provide().getExternalIdItemBatch(itemIds);

			for (Item item : foundItems) {
				lookup.put(item.externalId, item);
			}
		}

		return lookup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.predictors.Predictor#predictRevenueAndDownloads(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void predictRevenueAndDownloads(String country, String type, Long code) throws DataAccessException {

		Country c = new Country();
		c.a2Code = country;

		Store s = new Store();
		s.a3Code = IOS_STORE_A3;

		Pager p = new Pager();
		p.start = Long.valueOf(0);
		p.count = new Long(Long.MAX_VALUE);

		Modeller modeller = ModellerFactory.getModellerForStore(IOS_STORE_A3);

		FormType form = modeller.getForm(type);
		ModelRun modelRun = ModelRunServiceProvider.provide().getGatherCodeModelRun(c, s, form, code);

		Category category = CategoryServiceProvider.provide().getAllCategory(s);

		List<Rank> foundRanks = RankServiceProvider.provide().getGatherCodeRanks(c, s, category, type, code, p, false);
		Map<String, Item> lookup = lookupItemsForRanks(foundRanks);

		Item item = null;
		for (Rank rank : foundRanks) {
			item = lookup.get(rank.itemId);
			ItemPropertyWrapper properties = new ItemPropertyWrapper(item.properties);

			boolean usesIap = properties.getBoolean(ItemPropertyLookupServlet.PROPERTY_IAP);

			if (item != null) {
				if (rank.price.floatValue() > 0) {
					if (usesIap) {
						if (rank.grossingPosition != null) {
							rank.downloads = Integer.valueOf((int) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
									-modelRun.grossingAIap)));
							rank.revenue = Float.valueOf((float) (rank.downloads.doubleValue() * (rank.price.doubleValue() + modelRun.theta.doubleValue())));
						} else {
							rank.downloads = Integer.valueOf((int) (modelRun.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.paidAIap)));
							rank.revenue = Float.valueOf((float) ((modelRun.theta.doubleValue() + rank.price.doubleValue()) * rank.downloads.doubleValue()));
						}

					} else {
						if (rank.grossingPosition != null) {
							rank.downloads = Integer.valueOf((int) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
									-modelRun.grossingA.doubleValue())));
							rank.revenue = Float.valueOf((float) (rank.downloads.floatValue() * rank.price.floatValue()));
						} else {
							rank.downloads = Integer.valueOf((int) (modelRun.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.paidA)));
							rank.revenue = Float.valueOf(rank.price.floatValue() * rank.downloads.floatValue());
						}

					}
				} else if (rank.price.floatValue() == 0) {
					if (usesIap || rank.grossingPosition != null) {
						if (rank.grossingPosition != null) {
							rank.downloads = Integer.valueOf((int) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
									-modelRun.grossingAIap.doubleValue())));
							rank.revenue = Float.valueOf((float) (rank.downloads.doubleValue() * modelRun.theta.doubleValue()));
						} else {
							rank.downloads = Integer.valueOf((int) (modelRun.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.freeA)));
							rank.revenue = Float.valueOf((float) (modelRun.theta.doubleValue() * rank.downloads.doubleValue()));
						}
					} else {
						if (rank.grossingPosition != null) {
							// ERROR!!!
						} else {
							rank.downloads = Integer.valueOf((int) (modelRun.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.freeA)));
							rank.revenue = Float.valueOf(0);

						}

					}
				}

				RankServiceProvider.provide().updateRank(rank);
			}
		}
	}

}
