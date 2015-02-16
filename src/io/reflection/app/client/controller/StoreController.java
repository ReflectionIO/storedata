//
//  StoreController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.event.GetStoresEventHandler.GetStoresFailure;
import io.reflection.app.api.core.shared.call.event.GetStoresEventHandler.GetStoresSuccess;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class StoreController implements ServiceConstants {

	private static StoreController mOne = null;

	private Map<String, Store> mStoreLookup = null;
	private List<Store> stores;

	public static StoreController get() {
		if (mOne == null) {
			mOne = new StoreController();
		}

		return mOne;
	}

	private void fetchAllStores() {
		CoreService service = ServiceCreator.createCoreService();

		final GetStoresRequest input = new GetStoresRequest();
		input.accessCode = ACCESS_CODE;

		input.query = "*";

		service.getStores(input, new AsyncCallback<GetStoresResponse>() {

			@Override
			public void onSuccess(GetStoresResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.stores != null && output.stores.size() > 0) {

						stores = output.stores;

						if (mStoreLookup == null) {
							mStoreLookup = new HashMap<String, Store>();
						}

						Store iosStore = null;
						for (Store store : output.stores) {
							if (DataTypeHelper.IOS_STORE_A3.equals(store.a3Code)) {
								iosStore = store;
							}

							mStoreLookup.put(store.a3Code, store);
						}

						if (iosStore != null) {
							Store ipad = new Store();
							ipad.a3Code = DataTypeHelper.STORE_IPAD_A3_CODE;
							ipad.countries = iosStore.countries;
							ipad.created = iosStore.created;
							ipad.deleted = iosStore.deleted;
							ipad.id = iosStore.id;
							ipad.name = "iPad Store";
							ipad.url = iosStore.url;

							output.stores.add(ipad);

							Store iphone = new Store();
							iphone.a3Code = DataTypeHelper.STORE_IPHONE_A3_CODE;
							iphone.countries = iosStore.countries;
							iphone.created = iosStore.created;
							iphone.deleted = iosStore.deleted;
							iphone.id = iosStore.id;
							iphone.name = "iPhone Store";
							iphone.url = iosStore.url;

							output.stores.add(iphone);

							mStoreLookup.put(ipad.a3Code, mStoreLookup.get(DataTypeHelper.IOS_STORE_A3));
							mStoreLookup.put(iphone.a3Code, mStoreLookup.get(DataTypeHelper.IOS_STORE_A3));

							output.stores.remove(iosStore);
						}
					}
				}

				DefaultEventBus.get().fireEventFromSource(new GetStoresSuccess(input, output), StoreController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetStoresFailure(input, caught), StoreController.this);
			}
		});
	}

	/**
	 * @param mStore
	 * @return
	 */
	public Store getStore(String code) {
		Store store = null;

		if (code != null && (mStoreLookup == null || (store = mStoreLookup.get(code)) == null)) {
			store = new Store();

			if (DataTypeHelper.STORE_IPAD_A3_CODE.equalsIgnoreCase(code) || DataTypeHelper.STORE_IPHONE_A3_CODE.equalsIgnoreCase(code)) {
				store.a3Code = DataTypeHelper.IOS_STORE_A3;
			} else {
				store.a3Code = code;
			}
		}

		return store;
	}

	/**
	 * @param item
	 * @return
	 */
	public SafeUri getExternalUri(Item item) {
		SafeUri externalUri = null;

		if (DataTypeHelper.IOS_STORE_A3.equals(item.source)) {
			externalUri = UriUtils.fromString("http://itunes.apple.com/app/id" + item.internalId);
		}

		return externalUri;
	}

	/**
	 * @return
	 */
	public boolean prefetchStores() {
		boolean attemptPrefetch;

		if (attemptPrefetch = (mStoreLookup == null || mStoreLookup.size() == 0)) {
			fetchAllStores();
		}

		return attemptPrefetch;
	}

	/**
	 * @return
	 */
	public List<Store> getStores() {
		return stores;
	}
}
