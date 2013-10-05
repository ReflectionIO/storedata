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

		service.getStores(input, new AsyncCallback<GetStoresResponse>() {

			@Override
			public void onSuccess(GetStoresResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.stores != null && result.stores.size() > 0) {
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
