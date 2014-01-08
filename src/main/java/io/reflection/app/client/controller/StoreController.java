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
import io.reflection.app.client.handler.StoresEventHandler;
import io.reflection.app.datatypes.shared.Store;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class StoreController implements ServiceController {

	public static final String IPHONE_A3_CODE = "iph";
	public static final String IPAD_A3_CODE = "ipa";

	private static StoreController mOne = null;

	private Map<String, Store> mStoreLookup = null;

	public static StoreController get() {
		if (mOne == null) {
			mOne = new StoreController();
		}

		return mOne;
	}

	public void fetchAllStores() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetStoresRequest input = new GetStoresRequest();
		input.accessCode = ACCESS_CODE;

		input.query = "*";

		service.getStores(input, new AsyncCallback<GetStoresResponse>() {

			@Override
			public void onSuccess(GetStoresResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.stores != null && result.stores.size() > 0) {

						if (mStoreLookup == null) {
							mStoreLookup = new HashMap<String, Store>();
						}

						Store iosStore = null;
						for (Store store : result.stores) {
							if ("ios".equals(store.a3Code)) {
								iosStore = store;
							}

							mStoreLookup.put(store.a3Code, store);
						}

						if (iosStore != null) {
							Store ipad = new Store();
							ipad.a3Code = IPAD_A3_CODE;
							ipad.countries = iosStore.countries;
							ipad.created = iosStore.created;
							ipad.deleted = iosStore.deleted;
							ipad.id = iosStore.id;
							ipad.name = "iPad Store";
							ipad.url = iosStore.url;

							result.stores.add(ipad);

							Store iphone = new Store();
							iphone.a3Code = IPHONE_A3_CODE;
							iphone.countries = iosStore.countries;
							iphone.created = iosStore.created;
							iphone.deleted = iosStore.deleted;
							iphone.id = iosStore.id;
							iphone.name = "iPhone Store";
							iphone.url = iosStore.url;

							result.stores.add(iphone);

							mStoreLookup.put(ipad.a3Code, mStoreLookup.get("ios"));
							mStoreLookup.put(iphone.a3Code, mStoreLookup.get("ios"));

							result.stores.remove(iosStore);

						}

						EventController.get().fireEventFromSource(new StoresEventHandler.ReceivedStores(result.stores), StoreController.this);
					}
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");

			}
		});
	}

	/**
	 * @param mStore
	 * @return
	 */
	public Store getStore(String code) {
		Store store = null;

		if (code != null && (mStoreLookup == null || (store = mStoreLookup.get(code)) != null)) {
			store = new Store();

			if (IPAD_A3_CODE.equalsIgnoreCase(code) || IPHONE_A3_CODE.equalsIgnoreCase(code)) {
				store.a3Code = "ios";
			} else {
				store.a3Code = code;
			}
		}

		return store;
	}
}
