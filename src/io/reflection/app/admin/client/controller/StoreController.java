//
//  StoreController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.event.ReceivedStores;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.shared.datatypes.Store;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class StoreController {

	private static StoreController mOne = null;

	public static StoreController get() {
		if (mOne == null) {
			mOne = new StoreController();
		}

		return mOne;
	}

	public void getAllStores() {
		CoreService service = new CoreService();
		service.setUrl(ControllerHelper.CORE_END_POINT);

		final GetStoresRequest input = new GetStoresRequest();
		input.accessCode = ControllerHelper.ACCESS_CODE;
		
		input.query = "*";

		service.getStores(input, new AsyncCallback<GetStoresResponse>() {

			@Override
			public void onSuccess(GetStoresResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.stores != null && result.stores.size() > 0) {
						
						Store iosStore = null;
						for (Store store : result.stores) {
							if ("ios".equals(store.a3Code)) {
								iosStore = store;
								break;
							}
						}
						
						if (iosStore != null) {
							iosStore.a3Code = "ipa";
							iosStore.name = "iPad Store";
							
							Store iphone = new Store();
							iphone.a3Code = "ipo";
							iphone.countries = iosStore.countries;
							iphone.created = iosStore.created;
							iphone.deleted = iosStore.deleted;
							iphone.id = iosStore.id;
							iphone.name = "iPhone Store";
							iphone.url = iosStore.url;
							
							result.stores.add(iphone);
						}
						
						
						EventController.get().fireEventFromSource(new ReceivedStores(result.stores), StoreController.this);
					}
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");

			}
		});
	}
}
