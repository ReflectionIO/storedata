//
//  LinkedAccountController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.shared.datatypes.DataSource;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountController implements ServiceController {
	private static LinkedAccountController mOne = null;

	public static LinkedAccountController get() {
		if (mOne == null) {
			mOne = new LinkedAccountController();
		}

		return mOne;
	}

	public void linkAccount(Long sourceId, String username, String password) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		LinkAccountRequest input = new LinkAccountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.username = username;
		input.password = password;

		DataSource source = new DataSource();
		source.id = sourceId;

		input.source = source;

		service.linkAccount(input, new AsyncCallback<LinkAccountResponse>() {

			@Override
			public void onSuccess(LinkAccountResponse output) {
				Window.alert("success");

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("failure");

			}
		});

	}
}
