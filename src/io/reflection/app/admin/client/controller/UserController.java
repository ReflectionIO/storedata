//
//  UserController.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.UsersEventHandler.ReceivedCount;
import io.reflection.app.admin.client.handler.UsersEventHandler.ReceivedUsers;
import io.reflection.app.admin.client.handler.UsersEventHandler.UserLoggedIn;
import io.reflection.app.admin.client.handler.UsersEventHandler.UserLoggedOut;
import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.shared.datatypes.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class UserController extends AsyncDataProvider<User> implements ServiceController {

	private List<User> mUsers = new ArrayList<User>();
	private long mCount = -1;
	private Pager mPager;

	private User mLoggedIn = null;

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

	public User getLoggedInUser() {
		return mLoggedIn;
	}

	public void setLoggedInUser(User user) {

		if (mLoggedIn != user) {
			mLoggedIn = user;

			if (mLoggedIn == null) {
				EventController.get().fireEventFromSource(new UserLoggedIn(mLoggedIn), UserController.this);
			} else {
				EventController.get().fireEventFromSource(new UserLoggedOut(), UserController.this);
			}
		}
	}
}
