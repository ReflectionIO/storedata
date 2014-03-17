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
import io.reflection.app.api.core.shared.call.event.LoginEventHandler.LoginFailure;
import io.reflection.app.api.core.shared.call.event.LoginEventHandler.LoginSuccess;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.handler.user.SessionEventHandler.UserLoggedIn;
import io.reflection.app.client.handler.user.SessionEventHandler.UserLoggedOut;
import io.reflection.app.client.handler.user.SessionEventHandler.UserLoginFailed;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChangeFailed;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChanged;
import io.reflection.app.client.handler.user.UserPowersEventHandler.GetUserPowersFailed;
import io.reflection.app.client.handler.user.UserPowersEventHandler.GotUserPowers;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class SessionController implements ServiceConstants {
	private static SessionController mOne;

	// Cache roles and permissions meanwhile user is logged in
	private Map<Long, Role> mRoleCache = new HashMap<Long, Role>(); // Role.id : Role
	private Map<Long, Permission> mPermissionCache = new HashMap<Long, Permission>(); // Permission.id : Permission

	private static final String COOKIE_KEY_TOKEN = SessionController.class.getName() + ".token";

	private User mLoggedInUser = null;
	private Session mSession = null;

	public static SessionController get() {
		if (mOne == null) {
			mOne = new SessionController();
		}

		return mOne;
	}

	private SessionController() {
		restoreSession();
	}

	public User getLoggedInUser() {
		return mLoggedInUser;
	}

	public Session getSession() {
		return mSession;
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
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new UserLoginFailed(FormHelper.convertToError(caught)), SessionController.this);
				EventController.get().fireEventFromSource(new LoginFailure(input, caught), SessionController.this);
			}

			@Override
			public void onSuccess(LoginResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.session != null && output.session.user != null) {
						setLoggedInUser(output.session.user, output.session); // Set User
					}
				} else {
					EventController.get().fireEventFromSource(new UserLoginFailed(output.error), SessionController.this);
				}
				
				EventController.get().fireEventFromSource(new LoginSuccess(input, output), SessionController.this);
			}
		});
	}

	/**
	 * Set User after login was successful
	 * 
	 * @param user
	 * @param session
	 */
	private void setLoggedInUser(User user, Session session) {

		if (mSession != session) {
			mSession = session;
		}

		if (mSession != null) {
			Cookies.setCookie(COOKIE_KEY_TOKEN, mSession.token, mSession.expires);
		} else {
			Cookies.removeCookie(COOKIE_KEY_TOKEN);
		}

		if (mLoggedInUser != user) {
			mLoggedInUser = user; // used if changed person

			if (mLoggedInUser == null) { // used if previous logged out
				EventController.get().fireEventFromSource(new UserLoggedOut(), SessionController.this);
			} else {
				EventController.get().fireEventFromSource(new UserLoggedIn(mLoggedInUser, mSession), SessionController.this); // Fire user logged in event

				setUserRolesAndPermissions(); // Set user roles and permissions after user successfully logged in
			}
		}

	}

	/**
	 * Retrieve user roles and permissions from DB and set them
	 */
	private void setUserRolesAndPermissions() {
		CoreService service = ServiceCreator.createCoreService();

		final GetRolesAndPermissionsRequest input = new GetRolesAndPermissionsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = new Session();
		input.session.token = mSession.token;

		// input.idsOnly = Boolean.FALSE;

		// Ask roles and permissions for the user
		service.getRolesAndPermissions(input, new AsyncCallback<GetRolesAndPermissionsResponse>() {

			@Override
			public void onSuccess(GetRolesAndPermissionsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (mSession != null && mSession.token != null && input.session != null && input.session.token != null
							&& mSession.token.equals(input.session.token)) {

						mLoggedInUser.roles = output.roles;
						mLoggedInUser.permissions = output.permissions;

						// Add retrieved roles into cache
						if (output.roles != null) {
							for (Role role : output.roles) {
								mRoleCache.put(role.id, role);
							}
						}

						// Add retrieved permissions into cache
						if (output.permissions != null) {
							for (Permission permission : output.permissions) {
								mPermissionCache.put(permission.id, permission);
							}
						}

						EventController.get().fireEventFromSource(new GotUserPowers(mLoggedInUser, mLoggedInUser.roles, mLoggedInUser.permissions),
								SessionController.this);
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

		setLoggedInUser(null, null);
		ItemController.get().clearItemCache();
		clearRolePermissionCache();
	}

	/**
	 * Is the user logged in as administrator
	 * 
	 * @return
	 */
	public boolean isLoggedInUserAdmin() {
		return hasRole(mLoggedInUser, 1);
	}

	/**
	 * If the user has a specific role
	 * 
	 * @param user
	 * @param id
	 * @return Boolean hasRole
	 */
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
	 * Retrieve a role from the cache
	 * 
	 * @param Id
	 *            id of the role to retrieve
	 * @return the role
	 */
	public Role lookupRole(String id) {
		Role role = mRoleCache.get(id);
		return role;
	}

	/**
	 * Retrieve a permission from the cache
	 * 
	 * @param Id
	 *            id of the permission to retrieve
	 * @return the permission
	 */
	public Permission lookupPermission(String id) {
		Permission permission = mPermissionCache.get(id);
		return permission;
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
					EventController.get().fireEventFromSource(new UserPasswordChanged(mLoggedInUser.id), SessionController.this);
				} else {
					EventController.get().fireEventFromSource(new UserPasswordChangeFailed(output.error), SessionController.this);
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
		// input.session.user = mSession.token;

		input.resetCode = code;
		input.newPassword = newPassword;

		service.changePassword(input, new AsyncCallback<ChangePasswordResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new ChangePasswordEventHandler.ChangePasswordFailure(input, caught), SessionController.this);
			}

			@Override
			public void onSuccess(ChangePasswordResponse output) {
				EventController.get().fireEventFromSource(new ChangePasswordEventHandler.ChangePasswordSuccess(input, output), SessionController.this);
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

		input.session = SessionController.get().getSessionForApiCall();

		input.user = new User();

		input.user.id = mSession.user.id;

		input.user.company = company;
		input.user.forename = forename;
		input.user.surname = surname;
		input.user.username = username;

		service.changeUserDetails(input, new AsyncCallback<ChangeUserDetailsResponse>() {

			@Override
			public void onSuccess(ChangeUserDetailsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					mSession.user.username = input.user.username;
					mSession.user.forename = input.user.forename;
					mSession.user.surname = input.user.surname;
					mSession.user.company = input.user.company;

				}

				EventController.get().fireEventFromSource(new ChangeUserDetailsEventHandler.ChangeUserDetailsSuccess(input, output), SessionController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new ChangeUserDetailsEventHandler.ChangeUserDetailsFailure(input, caught), SessionController.this);
			}
		});
	}

	private void restoreSession() {
		String token = Cookies.getCookie(COOKIE_KEY_TOKEN);

		if (token != null) {
			CoreService core = ServiceCreator.createCoreService();

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
						if (output.error != null && output.error.code.intValue() == 100034) {
							PageType.LoginPageType.show("timeout");
						}

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

	/**
	 * Check if the user has the necessary authorization
	 * 
	 * @param roles
	 * @param permissions
	 */
	public void fetchAuthorisation(List<Role> roles, List<Permission> permissions) {
		if (mSession != null && mLoggedInUser != null) {
			CoreService service = ServiceCreator.createCoreService();

			final IsAuthorisedRequest input = new IsAuthorisedRequest();
			input.accessCode = ACCESS_CODE;

			input.session = new Session();
			input.session.token = mSession.token;

			input.roles = roles;
			input.permissions = permissions;

			service.isAuthorised(input, new AsyncCallback<IsAuthorisedResponse>() {

				@Override
				public void onSuccess(IsAuthorisedResponse output) {
					EventController.get().fireEventFromSource(new IsAuthorisedEventHandler.IsAuthorisedSuccess(input, output), SessionController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					EventController.get().fireEventFromSource(new IsAuthorisedEventHandler.IsAuthorisedFailure(input, caught), SessionController.this);
				}
			});
		}

	}

	/**
	 * Clear Roles and Permissions cache when user logs out
	 */
	public void clearRolePermissionCache() {
		mRoleCache.clear();
		mPermissionCache.clear();
	}

	public void forgotPassword(String username) {
		if (mSession == null && mLoggedInUser == null) {
			CoreService service = ServiceCreator.createCoreService();

			final ForgotPasswordRequest input = new ForgotPasswordRequest();
			input.accessCode = ACCESS_CODE;

			input.username = username;

			service.forgotPassword(input, new AsyncCallback<ForgotPasswordResponse>() {

				@Override
				public void onSuccess(ForgotPasswordResponse output) {
					EventController.get().fireEventFromSource(new ForgotPasswordEventHandler.ForgotPasswordSuccess(input, output), SessionController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					EventController.get().fireEventFromSource(new ForgotPasswordEventHandler.ForgotPasswordFailure(input, caught), SessionController.this);
				}
			});
		}
	}

}
