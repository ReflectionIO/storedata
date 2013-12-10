//
//  UserController.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChangeFailed;
import io.reflection.app.admin.client.handler.user.UserPasswordChangedEventHandler.UserPasswordChanged;
import io.reflection.app.admin.client.handler.user.UserRegisteredEventHandler.UserRegistered;
import io.reflection.app.admin.client.handler.user.UserRegisteredEventHandler.UserRegistrationFailed;
import io.reflection.app.admin.client.handler.user.UsersEventHandler.ReceivedCount;
import io.reflection.app.admin.client.handler.user.UsersEventHandler.ReceivedUsers;
import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.SetPasswordRequest;
import io.reflection.app.api.admin.shared.call.SetPasswordResponse;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.shared.datatypes.Role;
import io.reflection.app.shared.datatypes.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class UserController extends AsyncDataProvider<User> implements ServiceController {

	private List<User> mUsers = new ArrayList<User>();
	private long mCount = -1;
	private Pager mPager;

	private Map<Long, User> mUserLookup = new HashMap<Long, User>();

	private static UserController mOne = null;

	public static UserController get() {
		if (mOne == null) {
			mOne = new UserController();
		}

		return mOne;
	}

	private void fetchUsers() {

		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetUsersRequest input = new GetUsersRequest();
		input.accessCode = ACCESS_CODE;

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
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
		return mPager != null || mUsers.size() > 0;
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

		if (end > mUsers.size()) {
			fetchUsers();
		} else {
			updateRowData(start, mUsers.subList(start, end));
		}
	}

	/**
	 * 
	 */
	public void fetchUsersCount() {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetUsersCountRequest input = new GetUsersCountRequest();
		input.accessCode = ACCESS_CODE;

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

	/**
	 * @param selected
	 */
	public void makeAdmin(Long userId) {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final AssignRoleRequest input = new AssignRoleRequest();
		input.accessCode = ACCESS_CODE;

		input.role = new Role();
		input.role.name = "admin";

		input.user = new User();
		input.user.id = userId;

		service.assignRole(input, new AsyncCallback<AssignRoleResponse>() {

			@Override
			public void onSuccess(AssignRoleResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					// not sure what to do
				}
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	/**
	 * @param valueOf
	 * @param text
	 */
	public void setPassword(Long userId, String newPassword) {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final SetPasswordRequest input = new SetPasswordRequest();
		input.accessCode = ACCESS_CODE;

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
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

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

	private void addToLookup(List<User> users) {
		for (User user : users) {
			mUserLookup.put(user.id, user);
		}
	}

	public User getUser(Long id) {
		return mUserLookup.get(id);
	}

}
