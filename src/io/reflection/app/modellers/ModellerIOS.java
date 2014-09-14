//
//  ModellerIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.modellers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ModelTypeType;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.AbstractMap.SimpleEntry;

import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * @author billy1380
 * 
 */
public class ModellerIOS
// extends RenjinRModellerBase
		implements Modeller {

	// private static final Logger LOG = Logger.getLogger(ModellerIOS.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.Modeller#getForm(java.lang.String)
	 */
	@Override
	public FormType getForm(String type) {
		return type.contains("ipad") ? FormType.FormTypeTablet : FormType.FormTypeOther;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.Modeller#getGrossingType(io.reflection.app.datatypes.shared.FormType)
	 */
	@Override
	public String getGrossingType(FormType formType) {
		return formType == FormType.FormTypeTablet ? CollectorIOS.TOP_GROSSING_IPAD_APPS : CollectorIOS.TOP_GROSSING_APPS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.Modeller#getType(io.reflection.app.datatypes.shared.FormType, java.lang.Boolean)
	 */
	@Override
	public String getType(FormType formType, Boolean isFree) {
		return formType == FormType.FormTypeTablet ? (isFree != null && isFree.booleanValue() ? CollectorIOS.TOP_FREE_IPAD_APPS
				: CollectorIOS.TOP_PAID_IPAD_APPS) : (isFree != null && isFree.booleanValue() ? CollectorIOS.TOP_FREE_APPS : CollectorIOS.TOP_PAID_APPS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.Modeller#getModelType()
	 */
	@Override
	public ModelTypeType getModelType() {
		return ModelTypeType.ModelTypeTypeCorrelation;
	}

	@Override
	public void enqueue(ModelTypeType modelType, String country, Category category, String listType, Long code) {
		Store s = DataTypeHelper.getIosStore();

		if (category == null) {
			try {
				category = CategoryServiceProvider.provide().getAllCategory(s);
			} catch (DataAccessException ex) {
				throw new RuntimeException(ex);
			}
		}

		QueueHelper.enqueue("model", Method.PULL, new SimpleEntry<String, String>("store", DataTypeHelper.IOS_STORE_A3), new SimpleEntry<String, String>(
				"country", country), new SimpleEntry<String, String>("type", listType), new SimpleEntry<String, String>("code", code.toString()),
				new SimpleEntry<String, String>("categoryid", category == null ? Long.toString(24) : category.id.toString()), new SimpleEntry<String, String>(
						"modeltype", modelType.toString()));
	}
}
