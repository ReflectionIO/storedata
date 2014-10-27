//
//  UserController.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.RevokePermissionRequest;
import io.reflection.app.api.admin.shared.call.RevokePermissionResponse;
import io.reflection.app.api.admin.shared.call.RevokeRoleRequest;
import io.reflection.app.api.admin.shared.call.RevokeRoleResponse;
import io.reflection.app.api.admin.shared.call.SetPasswordRequest;
import io.reflection.app.api.admin.shared.call.SetPasswordResponse;
import io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler;
import io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler;
import io.reflection.app.api.admin.shared.call.event.GetRolesAndPermissionsEventHandler;
import io.reflection.app.api.admin.shared.call.event.RevokePermissionEventHandler;
import io.reflection.app.api.admin.shared.call.event.RevokeRoleEventHandler;
import io.reflection.app.api.blog.shared.call.DeleteUserRequest;
import io.reflection.app.api.blog.shared.call.DeleteUserResponse;
import io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler;
import io.reflection.app.api.core.shared.call.event.RegisterUserEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChangeFailed;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChanged;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler.UserRegistered;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler.UserRegistrationFailed;
import io.reflection.app.client.handler.user.UsersEventHandler.ReceivedCount;
import io.reflection.app.client.handler.user.UsersEventHandler.ReceivedUsers;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class UserController extends AsyncDataProvider<User> implements ServiceConstants {

	private List<User> mUsers = new ArrayList<User>();
	private long mCount = -1;
	private Pager mPager;

	private Map<Long, User> userLookup = new HashMap<Long, User>();

	private static UserController mOne = null;

	public static UserController get() {
		if (mOne == null) {
			mOne = new UserController();
		}

		return mOne;
	}

	private void fetchUsers() {

		AdminService service = ServiceCreator.createAdminService();

		final GetUsersRequest input = new GetUsersRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = SHORT_STEP;
			mPager.start = Long.valueOf(0);
			mPager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = mPager;

		service.getUsers(input, new AsyncCallback<GetUsersResponse>() {

			@Override
			public void onSuccess(GetUsersResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.users != null) {
						mUsers.addAll(result.users);

						addToLookup(result.users);
					}

					if (result.pager != null) {
						mPager = result.pager;

						if (mPager.totalCount != null) {
							mCount = mPager.totalCount.longValue();

							EventController.get().fireEventFromSource(new ReceivedCount(result.pager.totalCount), UserController.this);
						}
					}

					updateRowCount((int) mCount, true);
					updateRowData(
							input.pager.start.intValue(),
							mUsers.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), mPager.totalCount.intValue())));

					EventController.get().fireEventFromSource(new ReceivedUsers(result.users), UserController.this);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
			}
		});
	}

	public List<User> getUsers() {
		return mUsers;
	}

	public long getUsersCount() {
		return mCount;
	}

	public boolean hasUsers() {
		return getUsersCount() > 0;
	}

	/**
	 * Return true if Users -already fetched
	 * 
	 * @return
	 */
	public boolean usersFetched() {
		return mCount != -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<User> display) {

		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (!usersFetched() || (usersFetched() && getUsersCount() != mUsers.size() && end > mUsers.size())) {
			fetchUsers();
		} else {
			updateRowData(start, mUsers.size() == 0 ? mUsers : mUsers.subList(start, Math.min(mUsers.size(), end)));
		}
	}

	/**
	 * 
	 */
	public void fetchUsersCount() {
		AdminService service = ServiceCreator.createAdminService();

		final GetUsersCountRequest input = new GetUsersCountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		service.getUsersCount(input, new AsyncCallback<GetUsersCountResponse>() {

			@Override
			public void onSuccess(GetUsersCountResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					mCount = result.count;

					EventController.get().fireEventFromSource(new ReceivedCount(result.count), UserController.this);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
			}
		});
	}

	public void assignUserRole(Long userId, Role role) {
		AdminService service = ServiceCreator.createAdminService();

		final AssignRoleRequest input = new AssignRoleRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.role = role;

		input.user = new User();
		input.user.id = userId;

		service.assignRole(input, new AsyncCallback<AssignRoleResponse>() {

			@Override
			public void onSuccess(AssignRoleResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					// not sure what to do
				}
				EventController.get().fireEventFromSource(new AssignRoleEventHandler.AssignRoleSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new AssignRoleEventHandler.AssignRoleFailure(input, caught), UserController.this);
			}
		});
	}

	public void assignUserRoleId(Long userId, String roleCode) {
		Role role = new Role();
		role.code = roleCode;
		assignUserRole(userId, role);
	}

	public void assignUserPermission(Long userId, Permission permission) {
		AdminService service = ServiceCreator.createAdminService();

		final AssignPermissionRequest input = new AssignPermissionRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.permission = permission;

		input.user = new User();
		input.user.id = userId;

		service.assignPermission(input, new AsyncCallback<AssignPermissionResponse>() {

			@Override
			public void onSuccess(AssignPermissionResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					// not sure what to do
				}
				EventController.get().fireEventFromSource(new AssignPermissionEventHandler.AssignPermissionSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new AssignPermissionEventHandler.AssignPermissionFailure(input, caught), UserController.this);
			}
		});
	}

	public void assignUserPermissionId(Long userId, String permissionCode) {
		Permission permission = new Permission();
		permission.code = permissionCode;
		assignUserPermission(userId, permission);
	}

	public void revokeUserRole(Long userId, Role role) {
		AdminService service = ServiceCreator.createAdminService();

		final RevokeRoleRequest input = new RevokeRoleRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.role = role;

		input.user = new User();
		input.user.id = userId;

		service.revokeRole(input, new AsyncCallback<RevokeRoleResponse>() {

			@Override
			public void onSuccess(RevokeRoleResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					// not sure what to do
				}
				EventController.get().fireEventFromSource(new RevokeRoleEventHandler.RevokeRoleSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new RevokeRoleEventHandler.RevokeRoleFailure(input, caught), UserController.this);
			}

		});

	}

	public void revokeUserRoleId(Long userId, String roleCode) {
		Role role = new Role();
		role.code = roleCode;
		revokeUserRole(userId, role);
	}

	public void revokeUserPermission(Long userId, Permission permission) {
		AdminService service = ServiceCreator.createAdminService();

		final RevokePermissionRequest input = new RevokePermissionRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.permission = permission;

		input.user = new User();
		input.user.id = userId;

		service.revokePermission(input, new AsyncCallback<RevokePermissionResponse>() {

			@Override
			public void onSuccess(RevokePermissionResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					// not sure what to do
				}
				EventController.get().fireEventFromSource(new RevokePermissionEventHandler.RevokePermissionSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new RevokePermissionEventHandler.RevokePermissionFailure(input, caught), UserController.this);
			}
		});
	}

	public void revokeUserPermissionId(Long userId, String permissionCode) {
		Permission permission = new Permission();
		permission.code = permissionCode;
		revokeUserPermission(userId, permission);
	}

	/**
	 * Deletes user with a given id
	 * 
	 * @param userId
	 */
	public void deleteUser(Long userId) {
		AdminService service = ServiceCreator.createAdminService();

		final DeleteUserRequest input = new DeleteUserRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.user = new User();
		input.user.id = userId;

		service.deleteUser(input, new AsyncCallback<DeleteUserResponse>() {

			@Override
			public void onSuccess(DeleteUserResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					mUsers.remove(userLookup.get(input.user.id.toString()));
					userLookup.remove(input.user.id.toString());
					mCount--;
					mPager.totalCount = Long.valueOf(mPager.totalCount.longValue() - 1);
					updateRowCount((int) mCount, true);
					updateRowData(0, mUsers);
				}
				EventController.get().fireEventFromSource(new DeleteUserEventHandler.DeleteUserSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new DeleteUserEventHandler.DeleteUserFailure(input, caught), UserController.this);
			}

		});

	}

	/**
	 * @param valueOf
	 * @param text
	 */
	public void setPassword(Long userId, String newPassword) {
		AdminService service = ServiceCreator.createAdminService();

		final SetPasswordRequest input = new SetPasswordRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.password = newPassword;

		input.user = new User();
		input.user.id = userId;

		service.setPassword(input, new AsyncCallback<SetPasswordResponse>() {

			@Override
			public void onFailure(Throwable caught) {

				Error e = new Error();

				e.code = Integer.valueOf(-1);
				e.message = caught.getMessage();

				EventController.get().fireEventFromSource(new UserPasswordChangeFailed(e), UserController.this);
			}

			@Override
			public void onSuccess(SetPasswordResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					EventController.get().fireEventFromSource(new UserPasswordChanged(input.user.id), UserController.this);
				} else {
					EventController.get().fireEventFromSource(new UserPasswordChangeFailed(output.error), UserController.this);
				}
			}
		});
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param forename
	 * @param surname
	 * @param company
	 */
	public void registerUser(String username, String password, String forename, String surname, String company) {
		CoreService service = ServiceCreator.createCoreService();

		RegisterUserRequest input = new RegisterUserRequest();
		input.accessCode = ACCESS_CODE;

		input.user = new User();
		input.user.company = company;
		input.user.forename = forename;
		input.user.surname = surname;
		input.user.username = username;
		input.user.password = password;

		final String email = username;

		service.registerUser(input, new AsyncCallback<RegisterUserResponse>() {

			@Override
			public void onSuccess(RegisterUserResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					EventController.get().fireEventFromSource(new UserRegistered(email), UserController.this);
				} else {
					EventController.get().fireEventFromSource(new UserRegistrationFailed(output.error), UserController.this);
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Error e = new Error();

				e.code = Integer.valueOf(-1);
				e.message = caught.getMessage();

				EventController.get().fireEventFromSource(new UserRegistrationFailed(e), UserController.this);
			}
		});
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param forename
	 * @param surname
	 * @param company
	 */
	public void registerUser(String actionCode, String password) {
		CoreService service = ServiceCreator.createCoreService();

		final RegisterUserRequest input = new RegisterUserRequest();
		input.accessCode = ACCESS_CODE;

		input.user = new User();
		input.user.password = password;

		input.actionCode = actionCode;

		service.registerUser(input, new AsyncCallback<RegisterUserResponse>() {

			@Override
			public void onSuccess(RegisterUserResponse output) {
				EventController.get().fireEventFromSource(new RegisterUserEventHandler.RegisterUserSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEvent(new RegisterUserEventHandler.RegisterUserFailure(input, caught));
			}
		});
	}

	private void addToLookup(List<User> users) {
		for (User user : users) {
			userLookup.put(user.id, user);
		}
	}

	public User getUser(Long id) {
		return userLookup.get(id);
	}

	/**
	 * Fetches user details from the server. Details of fetched user (if the call is successful) will be broadcast on the event bus.
	 * 
	 * @param id
	 *            The user Id to fetch the details for
	 */
	public void fetchUser(Long id) {
		CoreService service = ServiceCreator.createCoreService();

		final GetUserDetailsRequest input = new GetUserDetailsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.userId = id;

		service.getUserDetails(input, new AsyncCallback<GetUserDetailsResponse>() {

			@Override
			public void onSuccess(GetUserDetailsResponse output) {

				if (output.status == StatusType.StatusTypeSuccess && output.user != null) {
					userLookup.put(output.user.id, output.user);
				}

				EventController.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsFailure(input, caught), UserController.this);
			}
		});
	}

	/**
	 * Fetches user details from the server. Details of fetched user (if the call is successful) will be broadcast on the event bus.
	 * 
	 * @param actionCode
	 *            action code for the user to fetch
	 */
	public void fetchUser(String actionCode) {
		CoreService service = ServiceCreator.createCoreService();

		final GetUserDetailsRequest input = new GetUserDetailsRequest();
		input.accessCode = ACCESS_CODE;

		input.actionCode = actionCode;

		service.getUserDetails(input, new AsyncCallback<GetUserDetailsResponse>() {

			@Override
			public void onSuccess(GetUserDetailsResponse output) {

				if (output.status == StatusType.StatusTypeSuccess && output.user != null) {
					userLookup.put(output.user.id, output.user);
				}

				EventController.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsFailure(input, caught), UserController.this);
			}
		});
	}

	/**
	 * If the current user is Admin, fetch roles and permissions of the user defined as parameter
	 * 
	 * @param user
	 */
	public void fetchUserRolesAndPermissions(User user) {

		AdminService service = ServiceCreator.createAdminService();

		final GetRolesAndPermissionsRequest input = new GetRolesAndPermissionsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.idsOnly = Boolean.FALSE; // Retrieve the whole permission

		input.user = user;

		service.getRolesAndPermissions(input, new AsyncCallback<GetRolesAndPermissionsResponse>() {

			@Override
			public void onSuccess(GetRolesAndPermissionsResponse output) {
				EventController.get().fireEventFromSource(new GetRolesAndPermissionsEventHandler.GetRolesAndPermissionsSuccess(input, output),
						UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetRolesAndPermissionsEventHandler.GetRolesAndPermissionsFailure(input, caught),
						UserController.this);
			}

		});
	}

	public void searchUser(String query) {
		List<Long> foundUserIdList = new ArrayList<Long>();
		RegExp RegExpr = RegExp.compile("/" + query + "/i");
		for (Map.Entry<Long, User> entry : userLookup.entrySet()) {
			if (RegExpr.test(entry.getValue().forename)) {
				foundUserIdList.add(entry.getKey());
			}
		}
		//Window.alert(foundUserIdList.toString());
	}

}
