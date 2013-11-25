//
//  SessionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 25 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.UsersEventHandler.UserLoggedIn;
import io.reflection.app.admin.client.handler.UsersEventHandler.UserLoggedOut;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.User;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class SessionController implements ServiceController {
	private static SessionController mOne;

	private User mLoggedIn = null;
	private Session mSession = null;

	public static SessionController get() {
		if (mOne == null) {
			mOne = new SessionController();
		}

		return mOne;
	}

	public User getLoggedInUser() {
		return mLoggedIn;
	}

	public void setLoggedInUser(User user) {

		if (mLoggedIn != user) {
			mLoggedIn = user;

			if (mLoggedIn == null) {
				EventController.get().fireEventFromSource(new UserLoggedIn(mLoggedIn), SessionController.this);
			} else {
				EventController.get().fireEventFromSource(new UserLoggedOut(), SessionController.this);
			}
		}
	}

	/**
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final LoginRequest input = new LoginRequest();
		input.accessCode = ACCESS_CODE;

		service.login(input, new AsyncCallback<LoginResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("login failed");
			}

			@Override
			public void onSuccess(LoginResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					Window.alert("login was successful");
					
					mLoggedIn = output.user;
					mSession = output.session;
				} else {
					Window.alert("login failed");
				}
			}
		});
	}

}
