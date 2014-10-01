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
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

/**
 * @author billy1380
 * 
 */
public class ApiCallHelper {
	public static Store createStoreForApiCall(Store store) {
		Store apiStore = new Store();
		apiStore.a3Code = storeCodeForApiCall(store.a3Code);

		return apiStore;
	}

	public static String storeCodeForApiCall(String a3Code) {
		String storeA3Code = a3Code;
		switch (storeA3Code) {
		case StoreController.IPHONE_A3_CODE:
		case StoreController.IPAD_A3_CODE:
			storeA3Code = DataTypeHelper.IOS_STORE_A3;
			break;
		default:
			break;
		}

		return storeA3Code;
	}

	public static Country createCountryForApiCall(Country country) {
		Country apiCountry = new Country();
		apiCountry.a2Code = country.a2Code;

		return apiCountry;
	}

	public static Session getSessionForApiCall() {
		return SessionController.get().getSessionForApiCall();
	}
}
