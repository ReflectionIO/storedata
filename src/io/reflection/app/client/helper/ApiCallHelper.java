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
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

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
}
