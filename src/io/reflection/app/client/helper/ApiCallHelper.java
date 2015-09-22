//
//  ServiceHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Date;

/**
 * @author billy1380
 *
 */
public class ApiCallHelper {
	public static Store createStoreForApiCall(Store store) {
		return DataTypeHelper.createStore(store.a3Code);
	}

	public static String storeCodeForApiCall(String a3Code) {
		String storeA3Code = a3Code;
		switch (storeA3Code) {
		case DataTypeHelper.STORE_IPHONE_A3_CODE:
		case DataTypeHelper.STORE_IPAD_A3_CODE:
			storeA3Code = DataTypeHelper.IOS_STORE_A3;
			break;
		default:
			break;
		}

		return storeA3Code;
	}

	public static Country createCountryForApiCall(Country country) {
		return DataTypeHelper.createCountry(country.a2Code);
	}

	public static Session getSessionForApiCall() {
		return SessionController.get().getSessionForApiCall();
	}

	public static Category createCategoryForApiCall(Category category) {
		return DataTypeHelper.createCategory(category.id);
	}

	/**
	 * @param feedFetch
	 * @return
	 */
	public static FeedFetch createFeedFetchForApiCall(FeedFetch feedFetch) {
		return DataTypeHelper.createFeedFetch(feedFetch.id);
	}

	/**
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getUTCDate(int year, int month, int day) {
		if (year < 0 || month < 1 || month > 12 || day < 1 || day > 31) return null;

		Date date = new Date(year - 1900, month - 1, day);
		return getUTCDate(date);
	}

	@SuppressWarnings("deprecation")
	public static Date getUTCDate(Date date) {
		if (date == null) return null;

		return new Date(date.getTime() - (date.getTimezoneOffset() * 60 * 1000));
	}
}
