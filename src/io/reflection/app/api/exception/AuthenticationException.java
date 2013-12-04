//
//  AuthenticationException.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.exception;

import io.reflection.app.shared.datatypes.User;

import com.willshex.gson.json.service.server.ServiceException;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class AuthenticationException extends ServiceException {

	private User mUser;

	/**
	 * 
	 * @param user
	 */
	public AuthenticationException(User user) {
		super(200000, "User could not be authenticated.");

		mUser = user;
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

		return description.toString();
	}
}
