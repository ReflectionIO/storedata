//
//  SessionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 25 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.SessionEventHandler.UserLoggedIn;
import io.reflection.app.admin.client.handler.SessionEventHandler.UserLoggedOut;
import io.reflection.app.admin.client.handler.SessionEventHandler.UserLoginFailed;
import io.reflection.app.admin.client.helper.FormHelper;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.LogoutRequest;
import io.reflection.app.api.core.shared.call.LogoutResponse;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.User;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class SessionController implements ServiceController {
	private static SessionController mOne;

	private static final String COOKIE_KEY_TOKEN = SessionController.class.getName() + ".token";
	private static final long COOKIE_SHORT_DURATION = 1000 * 60 * 60 * 24 * 1;
	// private static final long COOKIE_LONG_DURATION = 1000 * 60 * 60 * 24 * 30;

	private User mLoggedIn = null;
	private Session mSession = null;

	public static SessionController get() {
		if (mOne == null) {
			mOne = new SessionController();

			mOne.restoreSession();
		}

		return mOne;
	}

	public User getLoggedInUser() {
		return mLoggedIn;
	}

	public Session getSession() {
		return mSession;
	}

	private void setLoggedInUser(User user, Session session) {

		if (mLoggedIn != user) {
			mLoggedIn = user;

			if (mLoggedIn == null) {
				EventController.get().fireEventFromSource(new UserLoggedOut(), SessionController.this);
			} else {
				EventController.get().fireEventFromSource(new UserLoggedIn(mLoggedIn, mSession), SessionController.this);
			}
		}

		if (mSession != session) {
			mSession = session;

			if (mSession != null) {
				Cookies.setCookie(COOKIE_KEY_TOKEN, mSession.token, new Date(System.currentTimeMillis() + COOKIE_SHORT_DURATION));
			}
		} else {
			Cookies.removeCookie(COOKIE_KEY_TOKEN);
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

		input.username = username;
		input.password = password;

		service.login(input, new AsyncCallback<LoginResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new UserLoginFailed(FormHelper.convertToError(caught)), SessionController.this);
			}

			@Override
			public void onSuccess(LoginResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.session != null && output.session.user != null) {
						setLoggedInUser(output.session.user, output.session);
					}
				} else {
					EventController.get().fireEventFromSource(new UserLoginFailed(output.error), SessionController.this);
				}
			}
		});
	}

	public void logout() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final LogoutRequest input = new LogoutRequest();
		input.accessCode = ACCESS_CODE;

		service.logout(input, new AsyncCallback<LogoutResponse>() {

			@Override
			public void onSuccess(LogoutResponse output) {
				Cookies.removeCookie(COOKIE_KEY_TOKEN);
			}

			@Override
			public void onFailure(Throwable caught) {
				Cookies.removeCookie(COOKIE_KEY_TOKEN);
			}
		});

		setLoggedInUser(null, null);
	}

	/**
	 * @return
	 */
	public boolean isLoggedInUserAdmin() {
		return true;
	}

	/**
	 * 
	 * @param password
	 * @param newPassword
	 */
	public void changePassword(String password, String newPassword) {

	}

	private void restoreSession() {
		String token = Cookies.getCookie(COOKIE_KEY_TOKEN);

		if (token != null) {
			CoreService core = new CoreService();
			core.setUrl(CORE_END_POINT);

			LoginRequest input = new LoginRequest();
			input.accessCode = ACCESS_CODE;

			input.session = new Session();
			input.session.token = token;

			core.login(input, new AsyncCallback<LoginResponse>() {

				@Override
				public void onSuccess(LoginResponse output) {

					if (output.status == StatusType.StatusTypeSuccess) {
						if (output.session != null && output.session.user != null) {
							setLoggedInUser(output.session.user, output.session);
						}
					} else {
						EventController.get().fireEventFromSource(new UserLoginFailed(output.error), SessionController.this);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					EventController.get().fireEventFromSource(new UserLoginFailed(FormHelper.convertToError(caught)), SessionController.this);
				}
			});
		}
	}
}
