//
//  UserController.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import java.util.ArrayList;
import java.util.List;

import io.reflection.app.admin.client.event.ReceivedUsers;
import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.User;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class UserController implements ServiceController {

	private List<User> mUsers = new ArrayList<User>();
	private long mCount = -1;
	private Pager mPager;

	private static UserController mOne = null;

	public static UserController get() {
		if (mOne == null) {
			mOne = new UserController();
		}

		return mOne;
	}

	public void fetchUsers() {
		
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetUsersRequest input = new GetUsersRequest();
		input.accessCode = ACCESS_CODE;

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
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
						}
					}

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
}
