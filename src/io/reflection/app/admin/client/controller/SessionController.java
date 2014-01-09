//
//  SessionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 25 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.user.SessionEventHandler.UserLoggedIn;
import io.reflection.app.admin.client.handler.user.SessionEventHandler.UserLoggedOut;
import io.reflection.app.admin.client.handler.user.SessionEventHandler.UserLoginFailed;
import io.reflection.app.admin.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChangeFailed;
import io.reflection.app.admin.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChanged;
import io.reflection.app.admin.client.handler.user.UserPowersEventHandler.GetUserPowersFailed;
import io.reflection.app.admin.client.handler.user.UserPowersEventHandler.GotUserPowers;
import io.reflection.app.admin.client.helper.FormHelper;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.LogoutRequest;
import io.reflection.app.api.core.shared.call.LogoutResponse;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.Role;
import io.reflection.app.shared.datatypes.User;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class SessionController implements ServiceController {
	private static SessionController mOne;

	private static final String COOKIE_KEY_TOKEN = SessionController.class.getName() + ".token";

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

		if (mSession != session) {
			mSession = session;

			if (mSession != null) {
				Cookies.setCookie(COOKIE_KEY_TOKEN, mSession.token, mSession.expires);
			}
		} else {
			Cookies.removeCookie(COOKIE_KEY_TOKEN);
		}

		if (mLoggedIn != user) {
			mLoggedIn = user;

			if (mLoggedIn == null) {
				EventController.get().fireEventFromSource(new UserLoggedOut(), SessionController.this);
			} else {
				EventController.get().fireEventFromSource(new UserLoggedIn(mLoggedIn, mSession), SessionController.this);

				getUserPowers();
			}
		}

	}

	/**
	 * @param username
	 * @param password
	 */
	public void login(String username, String password, boolean longTerm) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final LoginRequest input = new LoginRequest();
		input.accessCode = ACCESS_CODE;

		input.username = username;
		input.password = password;

		input.longTerm = Boolean.valueOf(longTerm);

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
		return hasRole(mLoggedIn, 1);
	}

	public boolean hasRole(User user, long id) {
		boolean hasRole = false;

		if (user != null && user.roles != null) {
			for (Role role : user.roles) {
				if (role.id != null) {
					if (role.id.longValue() == id) {
						hasRole = true;
						break;
					}
				}
			}
		}

		return hasRole;
	}

	/**
	 * 
	 * @param password
	 * @param newPassword
	 */
	public void changePassword(String password, String newPassword) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final ChangePasswordRequest input = new ChangePasswordRequest();
		input.accessCode = ACCESS_CODE;

		input.session = new Session();
		input.session.token = mSession.token;

		input.password = password;
		input.newPassword = newPassword;

		service.changePassword(input, new AsyncCallback<ChangePasswordResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				Error e = new Error();

				e.code = Integer.valueOf(-1);
				e.message = caught.getMessage();

				EventController.get().fireEventFromSource(new UserPasswordChangeFailed(e), SessionController.this);
			}

			@Override
			public void onSuccess(ChangePasswordResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					EventController.get().fireEventFromSource(new UserPasswordChanged(mLoggedIn.id), SessionController.this);
				} else {
					EventController.get().fireEventFromSource(new UserPasswordChangeFailed(output.error), SessionController.this);
				}
			}
		});
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

	public void getUserPowers() {
		CoreService service = new CoreService();

		service.setUrl(CORE_END_POINT);

		final GetRolesAndPermissionsRequest input = new GetRolesAndPermissionsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = new Session();
		input.session.token = mSession.token;

		// input.idsOnly = Boolean.FALSE;

		service.getRolesAndPermissions(input, new AsyncCallback<GetRolesAndPermissionsResponse>() {

			@Override
			public void onSuccess(GetRolesAndPermissionsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (mSession != null && mSession.token != null && input.session != null && input.session.token != null
							&& mSession.token.equals(input.session.token)) {
						mLoggedIn.roles = output.roles;
						mLoggedIn.permissions = output.permissions;

						EventController.get().fireEventFromSource(new GotUserPowers(mLoggedIn, mLoggedIn.roles, mLoggedIn.permissions), SessionController.this);
					}
				} else {
					EventController.get().fireEventFromSource(new GetUserPowersFailed(output.error), SessionController.this);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetUserPowersFailed(FormHelper.convertToError(caught)), SessionController.this);
			}
		});

	}

	/**
	 * @return
	 */
	public Session getSessionForApiCall() {
		Session session = null;
		if (mSession != null && mSession.token != null) {
			session = new Session();
			session.token = mSession.token;
		} else {
			String token = Cookies.getCookie(COOKIE_KEY_TOKEN);

			if (token != null) {
				session = new Session();
				session.token = token;
			}
		}

		return session;
	}
}
