//
//  UserRolesProvider.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 17 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.dataprovider;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class UserRolesProvider extends AsyncDataProvider<Role> {

	private User user;
	private List<Role> userRoles = new ArrayList<Role>();

	/**
	 * 
	 */
	public UserRolesProvider(User user) {
		this.user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Role> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > userRoles.size()) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				UserController.get().fetchUserRolesAndPermissions(user);
			} else {
				List<Role> currentUserRoles = SessionController.get().getLoggedInUser().roles;
				if (currentUserRoles != null) {
					updateRowData(0, currentUserRoles);
				} else {
					updateRowCount(0, true);
				}
			}
		} else {
			updateRowData(start, userRoles.subList(start, end));
		}
	}

}
