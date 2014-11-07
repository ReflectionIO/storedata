//
//  ApiHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import static io.reflection.app.collectors.CollectorIOS.*;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author billy1380
 * 
 */
public class ApiHelper {
	public static String getGrossingListName(Store store, String type) {
		String listName = null;

		if (DataTypeHelper.IOS_STORE_A3.equalsIgnoreCase(store.a3Code)) {
			if (type != null && type.contains("ipad") || type.toLowerCase().contains("ipad")) {
				listName = TOP_GROSSING_IPAD_APPS;
			} else {
				listName = TOP_GROSSING_APPS;
			}
		}

		return listName;
	}

	public static Date removeTime(Date date) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * Get all list types uses the form type in the list name and returns the names of all the lists (3) for a specified store
	 * 
	 * @param store
	 * @param listType
	 * @return
	 */
	public static List<String> getAllListTypes(Store store, String type) {
		List<String> listTypes = null;

		if (DataTypeHelper.IOS_STORE_A3.equalsIgnoreCase(store.a3Code)) {
			if (type != null && type.contains("ipad") || type.toLowerCase().contains("ipad")) {
				listTypes = Arrays.asList(TOP_GROSSING_IPAD_APPS, TOP_FREE_IPAD_APPS, TOP_PAID_IPAD_APPS);
			} else {
				listTypes = Arrays.asList(TOP_GROSSING_APPS, TOP_FREE_APPS, TOP_PAID_APPS);
			}
		}

		return listTypes;
	}
}
