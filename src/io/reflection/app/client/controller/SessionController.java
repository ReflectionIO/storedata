//
//  SessionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 25 Nov 2013.
//  Copyright �� 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.shared.call.event.IsAuthorisedEventHandler;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.IsAuthorisedRequest;
import io.reflection.app.api.core.shared.call.IsAuthorisedResponse;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.LogoutRequest;
import io.reflection.app.api.core.shared.call.LogoutResponse;
import io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler;
import io.reflection.app.api.core.shared.call.event.GetRolesAndPermissionsEventHandler.GetRolesAndPermissionsFailure;
import io.reflection.app.api.core.shared.call.event.GetRolesAndPermissionsEventHandler.GetRolesAndPermissionsSuccess;
import io.reflection.app.api.core.shared.call.event.LoginEventHandler.LoginFailure;
import io.reflection.app.api.core.shared.call.event.LoginEventHandler.LoginSuccess;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.user.SessionEventHandler.UserLoggedIn;
import io.reflection.app.client.handler.user.SessionEventHandler.UserLoggedOut;
import io.reflection.app.client.handler.user.SessionEventHandler.UserLoginFailed;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChangeFailed;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChanged;
import io.reflection.app.client.handler.user.UserPowersEventHandler.GetUserPowersFailed;
import io.reflection.app.client.handler.user.UserPowersEventHandler.GotUserPowers;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.MixPanelApiHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;
import com.willshex.gson.json.service.client.JsonServiceCallEventHandler;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.Request;
import com.willshex.gson.json.service.shared.Response;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class SessionController implements ServiceConstants, JsonServiceCallEventHandler {
	private static SessionController mOne;

	// Cache roles and permissions meanwhile user is logged in

	private static final String COOKIE_KEY_TOKEN = SessionController.class.getName() + ".token";
	private static final String COOKIE_KEY_LAST_USER = SessionController.class.getName() + ".lastUser";

	private User loggedInUser = null;
	private Session userSession = null;

	private boolean isSessionRestored;

	private SessionController() {
		DefaultEventBus.get().addHandler(JsonServiceCallEventHandler.TYPE, this);
	}

	public static SessionController get() {
		if (mOne == null) {
			mOne = new SessionController();
		}

		return mOne;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public Session getSession() {
		return userSession;
	}

	/**
	 * Execute user login
	 * 
	 * @param username
	 * @param password
	 */
	public void login(String username, String password, boolean longTerm) {
		CoreService service = ServiceCreator.createCoreService();

		final LoginRequest input = new LoginRequest();
		input.accessCode = ACCESS_CODE;

		input.username = username;
		input.password = password;

		input.longTerm = Boolean.valueOf(longTerm);

		service.login(input, new AsyncCallback<LoginResponse>() {

			@Override
			public void onSuccess(LoginResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.session != null && output.session.user != null) {
						setLoggedInUser(output.session.user, output.session); // Set User
					}
				} else {
					DefaultEventBus.get().fireEventFromSource(new UserLoginFailed(output.error), SessionController.this);
				}

				DefaultEventBus.get().fireEventFromSource(new LoginSuccess(input, output), SessionController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new UserLoginFailed(FormHelper.convertToError(caught)), SessionController.this);
				DefaultEventBus.get().fireEventFromSource(new LoginFailure(input, caught), SessionController.this);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void updateSessionExpiryWithTimezone() {
		if (userSession != null && userSession.expires != null) {
			long time = userSession.expires.getTime();
			time += TimeZone.createTimeZone(userSession.expires.getTimezoneOffset()).getDaylightAdjustment(userSession.expires) * 60 * 1000;
			userSession.expires = new Date(time);
		}
	}

	/**
	 * Set User after login was successful
	 * 
	 * @param user
	 * @param session
	 */
	private void setLoggedInUser(User user, Session session) {

		if (userSession != session) {
			userSession = session;
			updateSessionExpiryWithTimezone();
		}

		if (userSession != null) {
			Cookies.setCookie(COOKIE_KEY_TOKEN, userSession.token, userSession.expires);
			if (userSession.user != null && userSession.user.username != null) {
				Cookies.setCookie(COOKIE_KEY_LAST_USER, userSession.user.username);
			}
		} else {
			Cookies.removeCookie(COOKIE_KEY_TOKEN);
		}

		if (loggedInUser != user) {
			loggedInUser = user; // used if changed person

			MixPanelApiHelper.trackLoginUser(loggedInUser);

			if (loggedInUser == null) { // used if previous logged out
				DefaultEventBus.get().fireEventFromSource(new UserLoggedOut(), SessionController.this);
			} else {
				DefaultEventBus.get().fireEventFromSource(new UserLoggedIn(loggedInUser, userSession), SessionController.this); // Fire user logged in event
			}
		}

	}

	/**
	 * Retrieve user roles and permissions from DB and set them
	 */
	public boolean prefetchRolesAndPermissions() {
		boolean attemptPrefetch;
		Session session;

		if (attemptPrefetch = ((session = getSessionForApiCall()) != null)) {
			CoreService service = ServiceCreator.createCoreService();

			final GetRolesAndPermissionsRequest input = new GetRolesAndPermissionsRequest();
			input.accessCode = ACCESS_CODE;

			input.session = session;

			// Ask roles and permissions for the user
			service.getRolesAndPermissions(input, new AsyncCallback<GetRolesAndPermissionsResponse>() {

				@Override
				public void onSuccess(GetRolesAndPermissionsResponse output) {
					if (output.status == StatusType.StatusTypeSuccess) {
						if (userSession != null && userSession.token != null && input.session != null && input.session.token != null
								&& userSession.token.equals(input.session.token)) {

							setUserRole(output.roles.get(0));

							setUserPermissions(output.permissions);

							NavigationController.get().resetSemiPublicPages();

							DefaultEventBus.get().fireEventFromSource(
									new GotUserPowers(loggedInUser, loggedInUser.roles, loggedInUser.permissions, output.daysSinceRoleAssigned),
									SessionController.this);
						}
					} else {
						DefaultEventBus.get().fireEventFromSource(new GetUserPowersFailed(output.error), SessionController.this);
					}
					DefaultEventBus.get().fireEventFromSource(new GetRolesAndPermissionsSuccess(input, output), SessionController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new GetUserPowersFailed(FormHelper.convertToError(caught)), SessionController.this);
					DefaultEventBus.get().fireEventFromSource(new GetRolesAndPermissionsFailure(input, caught), SessionController.this);
				}
			});
		}

		return attemptPrefetch;
	}

	public void fetchRolesAndPermissions() {

		CoreService service = ServiceCreator.createCoreService();

		final GetRolesAndPermissionsRequest input = new GetRolesAndPermissionsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		service.getRolesAndPermissions(input, new AsyncCallback<GetRolesAndPermissionsResponse>() {

			@Override
			public void onSuccess(GetRolesAndPermissionsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (userSession != null && userSession.token != null && input.session != null && input.session.token != null
							&& userSession.token.equals(input.session.token)) {

						setUserRole(output.roles.get(0));

						setUserPermissions(output.permissions);

						DefaultEventBus.get().fireEventFromSource(
								new GotUserPowers(loggedInUser, loggedInUser.roles, loggedInUser.permissions, output.daysSinceRoleAssigned),
								SessionController.this);
					}
				} else {
					DefaultEventBus.get().fireEventFromSource(new GetUserPowersFailed(output.error), SessionController.this);
				}
				DefaultEventBus.get().fireEventFromSource(new GetRolesAndPermissionsSuccess(input, output), SessionController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetUserPowersFailed(FormHelper.convertToError(caught)), SessionController.this);
				DefaultEventBus.get().fireEventFromSource(new GetRolesAndPermissionsFailure(input, caught), SessionController.this);
			}

		});
	}

	/**
	 * Release the session and clear user data
	 */
	public void logout() {

		CoreService service = ServiceCreator.createCoreService();

		final LogoutRequest input = new LogoutRequest();
		input.accessCode = ACCESS_CODE;

		input.session = getSessionForApiCall();

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

		MixPanelApiHelper.trackLoggedOut();

		makeSessionInvalid();

		// Clear user data, filters and pages
		LinkedAccountController.get().reset();
		ItemController.get().resetUserItem();
		RankController.get().reset();
		PostController.get().reset();
		FilterController.get().reset();
		// Remove all the pages from the Navigation Controller
		NavigationController.get().purgeAllPages();

		PageType.LoginPageType.show();
	}

	public void makeSessionInvalid() {
		isSessionRestored = false;
		setLoggedInUser(null, null);
		// ItemController.get().clearItemCache();
		NavigationController.get().setNotLoaded();
	}

	/**
	 * Is the user logged in as administrator
	 * 
	 * @return
	 */
	public boolean isAdmin() {
		return loggedInUserIs(DataTypeHelper.ROLE_ADMIN_CODE);
	}

	public boolean isStandardDeveloper() {
		return loggedInUserIs(DataTypeHelper.ROLE_DEVELOPER_CODE);
	}

	public boolean isPremiumDeveloper() {
		return loggedInUserIs(DataTypeHelper.ROLE_PREMIUM_CODE);
	}

	public boolean hasLinkedAccount() {
		return loggedInUserHas(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE);
	}

	public boolean canSeePredictions() {
		return isAdmin()
				|| (hasLinkedAccount() && (isPremiumDeveloper() || isStandardDeveloper() || loggedInUserIs(DataTypeHelper.ROLE_FIRST_CLOSED_BETA_CODE)));
	}

	/**
	 * Returns true if the user is a role with a given id
	 * 
	 * @param id
	 *            rank id
	 * @return
	 */
	public boolean loggedInUserIs(String code) {
		return hasRole(loggedInUser, code);
	}

	/**
	 * Returns true if the user has a permission with a given code
	 * 
	 * @param code
	 *            permission code
	 * @return
	 */
	public boolean loggedInUserHas(String code) {
		return hasPermission(loggedInUser, code);
	}

	/**
	 * If the user has a specific role
	 * 
	 * @param user
	 * @param code
	 * @return Boolean hasRole
	 */
	public boolean hasRole(User user, String code) {
		boolean hasRole = false;

		if (user != null && user.roles != null) {
			for (Role role : user.roles) {
				if (role.code != null && role.code.equals(code)) {
					hasRole = true;
					break;
				}
			}
		}

		return hasRole;
	}

	public boolean hasPermission(User user, String code) {
		boolean hasPermission = isAdmin();

		if (!hasPermission && user != null) {
			if (user.roles != null) {
				for (Role role : user.roles) {
					if (!hasPermission && role.permissions != null) {
						hasPermission = hasCodePermission(role.permissions, code);
						if (hasPermission) {
							break;
						}
					}
				}
			}
			if (!hasPermission && user.permissions != null) {
				hasPermission = hasCodePermission(user.permissions, code);
			}

		}

		return hasPermission;
	}

	private Boolean hasCodePermission(Collection<Permission> permissions, String code) {
		boolean hasCodePermission = false;
		for (Permission p : permissions) {
			if (p.code != null && p.code.equals(code)) {
				hasCodePermission = true;
				break;
			}
		}
		return hasCodePermission;
	}

	/**
	 * Set user role deleting the current one (if exists)
	 * 
	 * @param p
	 */
	public void setUserRole(Role r) {
		loggedInUser.roles = new ArrayList<Role>(1);
		loggedInUser.roles.add(r);
	}

	public void setUserPermissions(List<Permission> permissions) {
		loggedInUser.permissions = new ArrayList<Permission>();
		if (permissions != null) {
			for (Permission p : permissions) {
				loggedInUser.permissions.add(p);
			}
		}
	}

	/**
	 * Add a permission to the cache
	 * 
	 * @param p
	 */
	public void addUserPermission(Permission p) {
		if (loggedInUser.permissions == null) {
			loggedInUser.permissions = new ArrayList<Permission>();
		}
		loggedInUser.permissions.add(p);
	}

	/**
	 * Remove Permission from the cache
	 * 
	 * @param p
	 */
	public void deleteUserPermission(Permission p) {
		for (Permission userPermission : loggedInUser.permissions) {
			if (userPermission.code.equals(p.code)) {
				loggedInUser.permissions.remove(userPermission);
				break;
			}
		}
	}

	/**
	 * Change the user password
	 * 
	 * @param password
	 * @param newPassword
	 */
	public void changePassword(String password, String newPassword) {
		CoreService service = ServiceCreator.createCoreService();

		final ChangePasswordRequest input = new ChangePasswordRequest();
		input.accessCode = ACCESS_CODE;

		input.session = new Session();
		input.session.token = userSession.token;

		input.password = password;
		input.newPassword = newPassword;

		service.changePassword(input, new AsyncCallback<ChangePasswordResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				Error e = new Error();

				e.code = Integer.valueOf(-1);
				e.message = caught.getMessage();

				DefaultEventBus.get().fireEventFromSource(new UserPasswordChangeFailed(e), SessionController.this);
			}

			@Override
			public void onSuccess(ChangePasswordResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					DefaultEventBus.get().fireEventFromSource(new UserPasswordChanged(loggedInUser.id), SessionController.this);
				} else {
					DefaultEventBus.get().fireEventFromSource(new UserPasswordChangeFailed(output.error), SessionController.this);
				}
			}
		});
	}

	/**
	 * Reset the user password
	 * 
	 * @param code
	 * @param newPassword
	 */
	public void resetPassword(String code, String newPassword) {
		CoreService service = ServiceCreator.createCoreService();

		final ChangePasswordRequest input = new ChangePasswordRequest();
		input.accessCode = ACCESS_CODE;

		// input.session = new Session();
		// input.session.user = userSession.token;

		input.resetCode = code;
		input.newPassword = newPassword;

		service.changePassword(input, new AsyncCallback<ChangePasswordResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new ChangePasswordEventHandler.ChangePasswordFailure(input, caught), SessionController.this);
			}

			@Override
			public void onSuccess(ChangePasswordResponse output) {
				DefaultEventBus.get().fireEventFromSource(new ChangePasswordEventHandler.ChangePasswordSuccess(input, output), SessionController.this);
			}
		});
	}

	/**
	 * Change the user details
	 * 
	 * @param username
	 * @param forename
	 * @param surname
	 * @param company
	 */
	public void changeUserDetails(String username, String forename, String surname, String company) {
		CoreService service = ServiceCreator.createCoreService();
		final ChangeUserDetailsRequest input = new ChangeUserDetailsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = getSessionForApiCall();

		input.user = new User();

		input.user.id = userSession.user.id;

		input.user.company = company;
		input.user.forename = forename;
		input.user.surname = surname;
		input.user.username = username;

		service.changeUserDetails(input, new AsyncCallback<ChangeUserDetailsResponse>() {

			@Override
			public void onSuccess(ChangeUserDetailsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					userSession.user.username = input.user.username;
					userSession.user.forename = input.user.forename;
					userSession.user.surname = input.user.surname;
					userSession.user.company = input.user.company;
					loggedInUser.forename = userSession.user.forename;
					loggedInUser.surname = userSession.user.surname;
					loggedInUser.company = userSession.user.company;
					loggedInUser.username = userSession.user.username;
				}

				DefaultEventBus.get().fireEventFromSource(new ChangeUserDetailsEventHandler.ChangeUserDetailsSuccess(input, output), SessionController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new ChangeUserDetailsEventHandler.ChangeUserDetailsFailure(input, caught), SessionController.this);
			}
		});
	}

	public boolean isSessionRestored() {
		return isSessionRestored;
	}

	public boolean restoreSession() {

		boolean attemptRestore;

		String token = Cookies.getCookie(COOKIE_KEY_TOKEN);

		if (attemptRestore = (token != null && getSession() == null)) {

			isSessionRestored = true;

			CoreService core = ServiceCreator.createCoreService();

			final LoginRequest input = new LoginRequest();
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
						DefaultEventBus.get().fireEventFromSource(new UserLoginFailed(output.error), SessionController.this);
					}

					DefaultEventBus.get().fireEventFromSource(new LoginSuccess(input, output), SessionController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new UserLoginFailed(FormHelper.convertToError(caught)), SessionController.this);

					DefaultEventBus.get().fireEventFromSource(new LoginFailure(input, caught), SessionController.this);
				}
			});
		}

		return attemptRestore;
	}

	/**
	 * @return
	 */
	public Session getSessionForApiCall() {
		Session session = null;
		if (userSession != null && userSession.token != null) {
			session = new Session();
			session.token = userSession.token;
		} else {
			String token = Cookies.getCookie(COOKIE_KEY_TOKEN);

			if (token != null) {
				session = new Session();
				session.token = token;
			}
		}

		return session;
	}

	/**
	 * Check if the user has the necessary authorization
	 * 
	 * @param roles
	 * @param permissions
	 */
	public void fetchAuthorisation(List<Role> roles, List<Permission> permissions) {
		if (userSession != null && loggedInUser != null) {
			CoreService service = ServiceCreator.createCoreService();

			final IsAuthorisedRequest input = new IsAuthorisedRequest();
			input.accessCode = ACCESS_CODE;

			input.session = new Session();
			input.session.token = userSession.token;

			input.roles = roles;
			input.permissions = permissions;

			service.isAuthorised(input, new AsyncCallback<IsAuthorisedResponse>() {

				@Override
				public void onSuccess(IsAuthorisedResponse output) {
					DefaultEventBus.get().fireEventFromSource(new IsAuthorisedEventHandler.IsAuthorisedSuccess(input, output), SessionController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new IsAuthorisedEventHandler.IsAuthorisedFailure(input, caught), SessionController.this);
				}
			});
		}

	}

	public void forgotPassword(String username) {
		if (userSession == null && loggedInUser == null) {
			CoreService service = ServiceCreator.createCoreService();

			final ForgotPasswordRequest input = new ForgotPasswordRequest();
			input.accessCode = ACCESS_CODE;

			input.username = username;

			service.forgotPassword(input, new AsyncCallback<ForgotPasswordResponse>() {

				@Override
				public void onSuccess(ForgotPasswordResponse output) {
					DefaultEventBus.get().fireEventFromSource(new ForgotPasswordEventHandler.ForgotPasswordSuccess(input, output), SessionController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new ForgotPasswordEventHandler.ForgotPasswordFailure(input, caught), SessionController.this);
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallStart(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, com.google.gwt.http.client.Request)
	 */
	@Override
	public void jsonServiceCallStart(JsonService origin, String callName, Request input, com.google.gwt.http.client.Request handle) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallSuccess(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, com.willshex.gson.json.service.shared.Response)
	 */
	@Override
	public void jsonServiceCallSuccess(JsonService origin, String callName, Request input, Response output) {
		if (output.error == null) {
			if (output instanceof io.reflection.app.api.shared.datatypes.Response) {
				io.reflection.app.api.shared.datatypes.Response sessionOutput = (io.reflection.app.api.shared.datatypes.Response) output;

				if (sessionOutput.session != null) {
					setLoggedInUser(loggedInUser, sessionOutput.session);
				}
			}
		} else {
			// Session error redirection
			if (output.error.code.intValue() == ApiError.SessionNull.getCode() || output.error.code.intValue() == ApiError.SessionNotFound.getCode()
					|| output.error.code.intValue() == ApiError.SessionNoLookup.getCode()) {

				String loginParams = "timeout";
				Stack s = NavigationController.get().getStack();

				if (loggedInUser != null && loggedInUser.username != null) {
					loginParams += "/" + loggedInUser.username;
				}

				if (s != null) {
					String page = s.getPage();
					if (page != null && !PageType.LoadingPageType.equals(page) && !PageType.LoginPageType.equals(page)) {
						loginParams += "/" + s.asNextParameter();
					}
				}

				makeSessionInvalid();
				PageType.LoginPageType.show(loginParams);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallFailure(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, java.lang.Throwable)
	 */
	@Override
	public void jsonServiceCallFailure(JsonService origin, String callName, Request input, Throwable caught) {}

	/**
	 * @return
	 */
	public boolean isValidSession() {
		return getSessionForApiCall() != null
				&& (userSession == null || (userSession.expires != null && userSession.expires.getTime() > (new Date()).getTime()));
	}

	/**
	 * @param requiredPermissions
	 * @return
	 */
	public boolean isAuthorised(Collection<Permission> requiredPermissions) {
		boolean authorised = false;

		if (isValidSession()) {
			if (requiredPermissions == null || requiredPermissions.isEmpty()) {
				authorised = true;
			} else if (loggedInUser.permissions != null) {
				for (Permission permission : requiredPermissions) {
					if (permission.code != null && loggedInUserHas(permission.code)) {
						authorised = true;
						break;
					}
				}
			}
		}

		return authorised;
	}

	public String getLastUsername() {
		return Cookies.getCookie(COOKIE_KEY_LAST_USER);
	}

}
