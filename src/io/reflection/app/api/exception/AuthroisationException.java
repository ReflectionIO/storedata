//
//  AuthroisationException.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.exception;

import io.reflection.app.shared.datatypes.Role;
import io.reflection.app.shared.datatypes.User;

import java.util.Arrays;
import java.util.List;

import com.google.apphosting.api.search.AclPb.Entry.Permission;
import com.willshex.gson.json.service.server.ServiceException;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class AuthroisationException extends ServiceException {

	private User mUser;
	private List<Permission> mPermissions;
	private List<Role> mRoles;

	/**
	 * 
	 * @param user
	 * @param permission
	 */
	public AuthroisationException(User user, Permission... permission) {
		super(300000, "User does not have required permissions");

		mUser = user;

		if (permission != null) {
			// the list created by Arrays.asList is not modifiable
			mPermissions = Arrays.asList(permission);
		}

	}

	/**
	 * 
	 * @param user
	 * @param role
	 */
	public AuthroisationException(User user, Role... role) {
		super(300001, "User does not have required role");

		mUser = user;

		if (role != null) {
			mRoles = Arrays.asList(role);
		}
	}

	public AuthroisationException(User user, List<Role> roles, List<Permission> permissions) {
		super(300002, "User does not have required roles and/or permissions");

		mUser = user;
		mRoles = roles;
		mPermissions = permissions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.server.ServiceException#toString()
	 */
	@Override
	public String toString() {
		StringBuffer description = new StringBuffer();

		description.append("code[");
		description.append(code);
		description.append("] = ");
		description.append(getClass().getSimpleName());
		description.append("[");
		description.append(getMessage());
		description.append("]");
		description.append(", user[");

		if (mUser.id != null) {
			description.append("id=");
			description.append(mUser.id.toString());
			description.append(",");
		}

		if (mUser.username != null) {
			description.append("username=");
			description.append(mUser.username);
		}

		description.append("]");

		if (mPermissions != null) {

		}

		if (mRoles != null) {

		}

		return description.toString();
	}
}
