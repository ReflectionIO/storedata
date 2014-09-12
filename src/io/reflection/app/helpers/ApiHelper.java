//
//  ApiHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.Calendar;
import java.util.Date;

import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

/**
 * @author billy1380
 * 
 */
public class ApiHelper {
	public static String getGrossingListName(Store store, String type) {
		String listName = null;

		if (DataTypeHelper.IOS_STORE_A3.equalsIgnoreCase(store.a3Code)) {
			if (type != null && type.contains("ipad") || type.toLowerCase().contains("ipad")) {
				listName = CollectorIOS.TOP_GROSSING_IPAD_APPS;
			} else {
				listName = CollectorIOS.TOP_GROSSING_APPS;
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
}
