//
//  SimpleModellerIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.modellers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.ModelTypeType;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.service.category.CategoryServiceProvider;

import java.util.AbstractMap.SimpleEntry;

import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class SimpleModellerIOS extends ModellerIOS {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.ModellerIOS#enqueue(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void enqueue(String country, String type, Long code) {
		Store s = new Store();
		s.a3Code = STORE;

		Category all = null;

		try {
			all = CategoryServiceProvider.provide().getAllCategory(s);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex);
		}

		QueueHelper.enqueue("model", Method.PULL, new SimpleEntry<String, String>("store", STORE), new SimpleEntry<String, String>("country", country),
				new SimpleEntry<String, String>("type", type), new SimpleEntry<String, String>("code", code.toString()), new SimpleEntry<String, String>(
						"categoryid", all == null ? Long.toString(24) : all.id.toString()), new SimpleEntry<String, String>("modeltype",
						ModelTypeType.ModelTypeTypeSimple.toString()));
	}
}
