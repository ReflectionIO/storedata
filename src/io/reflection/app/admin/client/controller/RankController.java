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
public class RankController implements ServiceController {

	private static RankController mOne = null;

	private Map<String, Item> mItemLookup = new HashMap<String, Item>();

	private Pager mPager;

	public static RankController get() {
		if (mOne == null) {
			mOne = new RankController();
		}

		return mOne;
	}

	public void fetchTopItems() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetAllTopItemsRequest input = new GetAllTopItemsRequest();
		input.accessCode = ACCESS_CODE;

		input.country = new Country();
		input.country.a2Code = "us";

		input.listType = "iphone";
		input.on = new Date();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
		}
		input.pager = mPager;

		input.store = new Store();
		input.store.a3Code = "ios";

		service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

			@Override
			public void onSuccess(GetAllTopItemsResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (input.pager != null) {
						mPager = result.pager;
					}

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
