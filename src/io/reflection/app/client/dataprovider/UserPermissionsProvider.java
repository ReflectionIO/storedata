//
//  UserPermissionsProvider.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 25 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.dataprovider;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.datatypes.shared.Permission;
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
public class UserPermissionsProvider extends AsyncDataProvider<Permission> {

	private User user;
	private List<Permission> userPermissions = new ArrayList<Permission>();

	/**
	 * 
	 */
	public UserPermissionsProvider(User user) {
		this.user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Permission> display) {

		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > userPermissions.size()) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				// UserController.get().fetchUserRolesAndPermissions(user); TODO
			} else {
				List<Permission> currentUserPermissions = SessionController.get().getLoggedInUser().permissions;
				if (currentUserPermissions != null) {
					updateRowData(0, currentUserPermissions);
				} else {
					updateRowCount(0, true);
				}
			}
		} else {
			updateRowData(start, userPermissions.subList(start, end));
		}
	}
}
