//
//  SimplePredictorIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.ModelTypeType;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class SimplePredictorIOS implements Predictor {

	private static final Logger LOG = Logger.getLogger(SimplePredictorIOS.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.predictors.PredictorIOS#enqueue(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void enqueue(String country, String type, Long code) {
		Store s = DataTypeHelper.getIosStore();

		Category all = null;

		try {
			all = CategoryServiceProvider.provide().getAllCategory(s);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex);
		}

		QueueHelper.enqueue("predict", "/predict", Method.POST, new SimpleEntry<String, String>("country", country), new SimpleEntry<String, String>("store",
				DataTypeHelper.IOS_STORE_A3), new SimpleEntry<String, String>("type", type), new SimpleEntry<String, String>("code", code.toString()),
				new SimpleEntry<String, String>("categoryid", all == null ? Long.toString(24) : all.id.toString()), new SimpleEntry<String, String>(
						"modeltype", ModelTypeType.ModelTypeTypeSimple.toString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.predictors.PredictorIOS#getModelType()
	 */
	@Override
	public ModelTypeType getModelType() {
		return ModelTypeType.ModelTypeTypeSimple;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.predictors.Predictor#predictRevenueAndDownloads(java.lang.String, java.lang.String, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void predictRevenueAndDownloads(String country, String type, Long code, Long categoryId) throws DataAccessException {
		LOG.finer("predictRevenueAndDownloads");
	}
}
