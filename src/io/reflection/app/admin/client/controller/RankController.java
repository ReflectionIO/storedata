//
//  RankController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.event.ReceivedRanks;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Store;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RankController {

	private static RankController mOne = null;

	private Map<String, Item> mItemLookup = new HashMap<String, Item>();

	public static RankController get() {
		if (mOne == null) {
			mOne = new RankController();
		}

		return mOne;
	}

	public void getAllTopItems() {
		CoreService service = new CoreService();
		service.setUrl(ControllerHelper.CORE_END_POINT);

		final GetAllTopItemsRequest input = new GetAllTopItemsRequest();
		input.accessCode = ControllerHelper.ACCESS_CODE;

		input.country = new Country();
		input.country.a2Code = "gb";

		input.listType = "iphone";
		input.on = new Date();

		input.pager = new Pager();
		input.pager.count = Long.valueOf(25);
		input.pager.start = Long.valueOf(0);

		input.store = new Store();
		input.store.a3Code = "ios";

		service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

			@Override
			public void onSuccess(GetAllTopItemsResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.items != null) {
						for (Item item : result.items) {
							mItemLookup.put(item.externalId, item);
						}
					}

					EventController.get().fireEventFromSource(new ReceivedRanks("free" + input.listType, result.freeRanks), RankController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("paid" + input.listType, result.paidRanks), RankController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("grossing" + input.listType, result.grossingRanks), RankController.this);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
			}
		});
	}

	public Item lookupItem(String externalId) {
		return mItemLookup.get(externalId);
	}
}
