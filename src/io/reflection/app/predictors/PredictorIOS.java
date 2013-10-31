//
//  PredictorIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.apple.ItemPropertyLookupServlet;
import io.reflection.app.helpers.ItemPropertyWrapper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.ModelRun;
import io.reflection.app.shared.datatypes.Rank;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.predictors.Predictor#enqueue(io.reflection.app.shared.datatypes.ModelRun, io.reflection.app.shared.datatypes.Rank)
	 */
	@Override
	public void enqueue(ModelRun modelRun, Rank rank) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("predict");

			TaskOptions options = TaskOptions.Builder.withUrl("/predict").method(Method.POST);
			options.param("modelRun", modelRun.toString());
			options.param("rank", rank.toString());

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
	 * @see io.reflection.app.predictors.Predictor#predictRevenueAndDownloads(io.reflection.app.shared.datatypes.ModelRun,
	 * io.reflection.app.shared.datatypes.Rank)
	 */
	@Override
	public void predictRevenueAndDownloads(ModelRun modelRun, Rank rank) {
		Item item = ItemServiceProvider.provide().getExternalIdItem(rank.itemId);

		ItemPropertyWrapper properties = new ItemPropertyWrapper(item.properties);

		boolean usesIap = properties.getBoolean(ItemPropertyLookupServlet.PROPERTY_IAP);

		if (item != null) {
			if (rank.price.floatValue() > 0) {
				if (usesIap) {

					if (rank.grossingPosition != null) {
						rank.revenue = Float.valueOf((float) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
								-modelRun.grossingAIap.doubleValue())));
						rank.downloads = (int) (rank.revenue.doubleValue() / (rank.price.doubleValue() + modelRun.theta.doubleValue()));
					} else {
						rank.downloads = Integer.valueOf((int) (modelRun.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.paidAIap)));
						rank.revenue = Float.valueOf((float) ((modelRun.theta.doubleValue() + rank.price.doubleValue()) * rank.downloads.doubleValue()));
					}

				} else {

					if (rank.grossingPosition != null) {
						rank.revenue = Float.valueOf((float) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
								-modelRun.grossingA.doubleValue())));
						rank.downloads = (int) (rank.revenue.floatValue() / rank.price.floatValue());
					} else {
						rank.downloads = Integer.valueOf((int) (modelRun.paidB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.paidA)));
						rank.revenue = Float.valueOf(rank.price.floatValue() * rank.downloads.floatValue());
					}

				}
			} else if (rank.price.floatValue() == 0) {
				if (usesIap || rank.grossingPosition != null) {
					if (rank.grossingPosition != null) {
						rank.revenue = Float.valueOf((float) (modelRun.grossingB.doubleValue() * Math.pow(rank.grossingPosition.doubleValue(),
								-modelRun.grossingAIap.doubleValue())));
						rank.downloads = (int) (rank.revenue.doubleValue() / modelRun.theta.doubleValue());
					} else {
						rank.downloads = Integer.valueOf((int) (modelRun.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.freeA)));
						rank.revenue = Float.valueOf((float) (modelRun.theta.doubleValue() * rank.downloads.doubleValue()));
					}
				} else {

					if (rank.grossingPosition != null) {
						// ERROR!!!
					} else {
						rank.downloads = Integer.valueOf((int) (modelRun.freeB.doubleValue() * Math.pow(rank.position.doubleValue(), -modelRun.freeB)));
						rank.revenue = Float.valueOf(0);
					}

				}
			}
		}
		
		RankServiceProvider.provide().updateRank(rank);

	}

}
