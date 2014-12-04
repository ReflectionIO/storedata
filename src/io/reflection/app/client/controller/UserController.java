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
import io.reflection.app.api.admin.shared.call.DeleteUserRequest;
import io.reflection.app.api.admin.shared.call.DeleteUserResponse;
import io.reflection.app.api.admin.shared.call.DeleteUsersRequest;
import io.reflection.app.api.admin.shared.call.DeleteUsersResponse;
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
import io.reflection.app.api.admin.shared.call.event.DeleteUserEventHandler;
import io.reflection.app.api.admin.shared.call.event.DeleteUsersEventHandler;
import io.reflection.app.api.admin.shared.call.event.GetRolesAndPermissionsEventHandler;
import io.reflection.app.api.admin.shared.call.event.GetUsersEventHandler.GetUsersFailure;
import io.reflection.app.api.admin.shared.call.event.GetUsersEventHandler.GetUsersSuccess;
import io.reflection.app.api.admin.shared.call.event.RevokePermissionEventHandler;
import io.reflection.app.api.admin.shared.call.event.RevokeRoleEventHandler;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler;
import io.reflection.app.api.core.shared.call.event.RegisterUserEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChangeFailed;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChanged;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler.UserRegistered;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler.UserRegistrationFailed;
import io.reflection.app.client.handler.user.UsersEventHandler.ReceivedCount;
import io.reflection.app.client.helper.MixPanelApiHelper;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.Request;
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

	// private List<User> userList = new ArrayList<User>();
	// private long count = -1;
	// private long totalCount = -1;
	private Pager pager;
	private String searchQuery = null;
	private Request current;

	// private Map<Long, User> userLookup = new HashMap<Long, User>();

	private static UserController mOne = null;

	public static UserController get() {
		if (mOne == null) {
			mOne = new UserController();
		}

		return mOne;
	}

	// public void fetchUsers() {
	//
	// AdminService service = ServiceCreator.createAdminService();
	//
	// final GetUsersRequest input = new GetUsersRequest();
	// input.accessCode = ACCESS_CODE;
	//
	// input.session = SessionController.get().getSessionForApiCall();
	//
	// if (pager == null) {
	// pager = new Pager();
	// pager.count = SHORT_STEP;
	// pager.start = Pager.DEFAULT_START;
	// pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
	// }
	// input.pager = pager;
	//
	// current = service.getUsers(input, new AsyncCallback<GetUsersResponse>() {
	//
	// @Override
	// public void onSuccess(GetUsersResponse output) {
	// if (output.status == StatusType.StatusTypeSuccess) {
	// // if (output.users != null) {
	// // userList.addAll(output.users);
	// //
	// // addToLookup(output.users);
	// // }
	//
	// if (output.pager != null) {
	// pager = output.pager;
	//
	// if (pager.totalCount != null) {
	// // count = totalCount = pager.totalCount.longValue();
	//
	// EventController.get().fireEventFromSource(new ReceivedCount(output.pager.totalCount), UserController.this);
	// }
	// }
	//
	// updateRowCount(Integer.MAX_VALUE, false);
	// updateRowData(input.pager.start.intValue(), output.users == null ? Collections.<User> emptyList() : output.users);
	//
	// EventController.get().fireEventFromSource(new ReceivedUsers(output.users), UserController.this);
	// EventController.get().fireEventFromSource(new GetUsersSuccess(input, output), UserController.this);
	// }
	// }
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// EventController.get().fireEventFromSource(new GetUsersFailure(input, caught), UserController.this);
	// }
	// });
	// }

	public void fetchUsers() {
		fetchUsers(searchQuery);
	}

	public void fetchUsers(String query) {
		if (current != null) {
			current.cancel();
			current = null;
		}

		AdminService service = ServiceCreator.createAdminService();

		final GetUsersRequest input = new GetUsersRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = SHORT_STEP;
			pager.start = Pager.DEFAULT_START;
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		input.query = searchQuery = query;

		if ("" == input.query) {
			input.query = searchQuery = null;
		}

		current = service.getUsers(input, new AsyncCallback<GetUsersResponse>() {

			@Override
			public void onSuccess(GetUsersResponse output) {
				current = null;
				if (output.status == StatusType.StatusTypeSuccess) {

					// if (output.users != null) {}

					if (output.pager != null) {
						pager = output.pager;

						// if (pager.totalCount != null) {
						// count = pager.totalCount.longValue();
						// }
					}

					updateRowCount(Integer.MAX_VALUE, false);
					updateRowData(input.pager.start.intValue(), output.users == null ? Collections.<User> emptyList() : output.users);
				}

				DefaultEventBus.get().fireEventFromSource(new GetUsersSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				current = null;
				DefaultEventBus.get().fireEventFromSource(new GetUsersFailure(input, caught), UserController.this);
			}
		});

	}

	public void reset() {
		pager = null;
		// count = -1;
		searchQuery = null;

		updateRowCount(0, false);
	}

	// public List<User> getUsers() {
	// return userList;
	// }

	// private long getUsersCount() {
	// return count;
	// }

	// public long getUsersTotalCount() {
	// return totalCount;
	// }

	// public boolean hasUsers() {
	// return getUsersCount() > 0;
	// }

	// /**
	// * Return true if Users -already fetched
	// *
	// * @return
	// */
	// public boolean usersFetched() {
	// return count != -1;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<User> display) {

		Range r = display.getVisibleRange();

		// int start = r.getStart();
		// int end = start + r.getLength();

		pager = PagerHelper.createDefaultPager();
		pager.start = Long.valueOf(r.getStart());
		pager.count = Long.valueOf(r.getLength());

		// if (!usersFetched() || (usersFetched() && getUsersCount() != userList.size() && end > userList.size())) {
		// if (searchQuery == null) {
		fetchUsers();
		// } else {
		// fetchUsersQuery(searchQuery);
		// }
		// } else {
		// updateRowData(start, userList.size() == 0 ? userList : userList.subList(start, Math.min(userList.size(), end)));
		// }

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
					// count = result.count;

					DefaultEventBus.get().fireEventFromSource(new ReceivedCount(result.count), UserController.this);
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
				DefaultEventBus.get().fireEventFromSource(new AssignRoleEventHandler.AssignRoleSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new AssignRoleEventHandler.AssignRoleFailure(input, caught), UserController.this);
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
				DefaultEventBus.get().fireEventFromSource(new AssignPermissionEventHandler.AssignPermissionSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new AssignPermissionEventHandler.AssignPermissionFailure(input, caught), UserController.this);
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
				DefaultEventBus.get().fireEventFromSource(new RevokeRoleEventHandler.RevokeRoleSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new RevokeRoleEventHandler.RevokeRoleFailure(input, caught), UserController.this);
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
				DefaultEventBus.get().fireEventFromSource(new RevokePermissionEventHandler.RevokePermissionSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new RevokePermissionEventHandler.RevokePermissionFailure(input, caught), UserController.this);
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
				// only refresh the user list if the current user is an admin or has manage user permission
				if (SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_MANAGE_USERS_CODE)) {
					PagerHelper.moveBackward(pager);

					fetchUsers();
				}

				DefaultEventBus.get().fireEventFromSource(new DeleteUserEventHandler.DeleteUserSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new DeleteUserEventHandler.DeleteUserFailure(input, caught), UserController.this);
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

				DefaultEventBus.get().fireEventFromSource(new UserPasswordChangeFailed(e), UserController.this);
			}

			@Override
			public void onSuccess(SetPasswordResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					DefaultEventBus.get().fireEventFromSource(new UserPasswordChanged(input.user.id), UserController.this);
				} else {
					DefaultEventBus.get().fireEventFromSource(new UserPasswordChangeFailed(output.error), UserController.this);
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

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("company", company);
		
		if (password == null || password.length() == 0) {
			params.put("requestInvite", Boolean.TRUE);
		}

		service.registerUser(input, new AsyncCallback<RegisterUserResponse>() {

			@Override
			public void onSuccess(RegisterUserResponse output) {

				if (output.status == StatusType.StatusTypeSuccess) {
					// only refresh the user list if the current user is an admin or has manage user permission
					if (SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_MANAGE_USERS_CODE)) {
						PagerHelper.moveBackward(pager);

						fetchUsers();
					}

					params.put("status", "success");
					MixPanelApiHelper.track("registerUser", params);

					DefaultEventBus.get().fireEventFromSource(new UserRegistered(email), UserController.this);
				} else {
					params.put("status", "failure");
					if (output.error != null && output.error.message != null) {
						params.put("error", output.error.message);
					}
					MixPanelApiHelper.track("registerUser", params);

					DefaultEventBus.get().fireEventFromSource(new UserRegistrationFailed(output.error), UserController.this);
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Error e = new Error();

				e.code = Integer.valueOf(-1);
				e.message = caught.getMessage();

				params.put("status", "failure");
				params.put("error", caught.getMessage());
				MixPanelApiHelper.track("registerUser", params);

				DefaultEventBus.get().fireEventFromSource(new UserRegistrationFailed(e), UserController.this);
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

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("actionCode", actionCode);

		final RegisterUserRequest input = new RegisterUserRequest();
		input.accessCode = ACCESS_CODE;

		input.user = new User();
		input.user.password = password;

		input.actionCode = actionCode;

		service.registerUser(input, new AsyncCallback<RegisterUserResponse>() {

			@Override
			public void onSuccess(RegisterUserResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess) {
					// only refresh the user list if the current user is an admin or has manage user permission
					if (SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_MANAGE_USERS_CODE)) {
						PagerHelper.moveBackward(pager);

						fetchUsers();
					}

					params.put("status", "success");
					MixPanelApiHelper.track("registerUser", params);
				} else {
					params.put("status", "failure");
					if (output.error != null && output.error.message != null) {
						params.put("error", output.error.message);
					}
					
					MixPanelApiHelper.track("registerUser", params);
				}

				DefaultEventBus.get().fireEventFromSource(new RegisterUserEventHandler.RegisterUserSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				params.put("status", "failure");
				params.put("error", caught.getMessage());
				MixPanelApiHelper.track("registerUser", params);
				
				DefaultEventBus.get().fireEvent(new RegisterUserEventHandler.RegisterUserFailure(input, caught));
			}
		});
	}

	// private void addToLookup(List<User> users) {
	// for (User user : users) {
	// userLookup.put(user.id, user);
	// }
	// }

	// public User getUser(Long id) {
	// return userLookup.get(id);
	// }

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
					// userLookup.put(output.user.id, output.user);
				}

				DefaultEventBus.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsFailure(input, caught), UserController.this);
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
					// userLook|up.put(output.user.id, output.user);
				}

				DefaultEventBus.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetUserDetailsEventHandler.GetUserDetailsFailure(input, caught), UserController.this);
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
				DefaultEventBus.get().fireEventFromSource(new GetRolesAndPermissionsEventHandler.GetRolesAndPermissionsSuccess(input, output),
						UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetRolesAndPermissionsEventHandler.GetRolesAndPermissionsFailure(input, caught),
						UserController.this);
			}

		});
	}

	private void deleteUsers(List<User> users, boolean allTestUsers) {
		AdminService service = ServiceCreator.createAdminService();

		final DeleteUsersRequest input = new DeleteUsersRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.users = users;
		input.allTestUsers = allTestUsers;

		service.deleteUsers(input, new AsyncCallback<DeleteUsersResponse>() {

			@Override
			public void onSuccess(DeleteUsersResponse output) {
				// only refresh the user list if the current user is an admin or has manage user permission
				if (SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_MANAGE_USERS_CODE)) {
					PagerHelper.moveBackward(pager);

					fetchUsers();
				}

				DefaultEventBus.get().fireEventFromSource(new DeleteUsersEventHandler.DeleteUsersSuccess(input, output), UserController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new DeleteUsersEventHandler.DeleteUsersFailure(input, caught), UserController.this);
			}

		});
	}

	public void deleteTestUsers() {
		deleteUsers(null, true);
	}

	public void deleteUsers(List<User> users) {
		deleteUsers(users, false);
	}

}
