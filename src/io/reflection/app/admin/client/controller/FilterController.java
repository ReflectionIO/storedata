//
//  FilterController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.FilterEventHandler;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Store;

/**
 * @author billy1380
 * 
 */
public class FilterController {
	private static FilterController mOne = null;

	private String mStore;
	private String mCountry;
	private String mListType;

	public static FilterController get() {
		if (mOne == null) {
			mOne = new FilterController();
		}

		return mOne;
	}

	public void setStore(String store) {
		if (store != null && !store.equals(mStore)) {
			String previousStore = mStore;
			mStore = store;

			EventController.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameter<String>("store", mStore, previousStore), this);
		}
	}

	public void setCountry(String country) {
		if (country != null && !country.equals(mCountry)) {
			String previousCountry = mCountry;
			mCountry = country;

			EventController.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameter<String>("country", mCountry, previousCountry), this);
		}
	}

	public void setListType(String listType) {
		if (listType != null && !listType.equals(mListType)) {
			String previousListType = mListType;
			mListType = listType;

			EventController.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameter<String>("list.type", mListType, previousListType), this);
		}
	}
	
	public Store getStore() {
		return StoreController.get().getStore(mStore);
	}
	
	public Country getCountry() {
		return CountryController.get().getCountry(mCountry);
	}
	
	public String getListType() {
		return mListType;
	}
}
