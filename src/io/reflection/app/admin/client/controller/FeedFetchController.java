//
//  FeedFetchController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.FeedFetchesEventHandler;
import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class FeedFetchController implements ServiceController {
	private static FeedFetchController mOne = null;

	public static FeedFetchController get() {
		if (mOne == null) {
			mOne = new FeedFetchController();
		}

		return mOne;
	}

	public void fetchFeedFetches() {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetFeedFetchesRequest input = new GetFeedFetchesRequest();
		input.accessCode = ACCESS_CODE;

		input.country = FilterController.get().getCountry();
		input.store = FilterController.get().getStore();
		input.listType = FilterController.get().getListType();

		service.getFeedFetches(input, new AsyncCallback<GetFeedFetchesResponse>() {

			@Override
			public void onSuccess(GetFeedFetchesResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.mixed != null) {
						EventController.get().fireEventFromSource(new FeedFetchesEventHandler.ReceivedMixedFeedFetches(output.mixed), FeedFetchController.this);
					} else if (output.ingested != null || output.uningested != null) {
						EventController.get().fireEventFromSource(new FeedFetchesEventHandler.ReceivedFeedFetches(output.ingested, output.uningested),
								FeedFetchController.this);
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
}
