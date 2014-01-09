//
//  UserPowersEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler.user;

import io.reflection.app.shared.datatypes.Permission;
import io.reflection.app.shared.datatypes.Role;
import io.reflection.app.shared.datatypes.User;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public interface UserPowersEventHandler extends EventHandler {
	public static final GwtEvent.Type<UserPowersEventHandler> TYPE = new GwtEvent.Type<UserPowersEventHandler>();

	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions);

	public void getGetUserPowersFailed(Error error);

	public class GotUserPowers extends GwtEvent<UserPowersEventHandler> {
		private User mUser = null;
		private List<Role> mRoles = null;
		private List<Permission> mPermissions = null;

		public GotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
			mUser = user;
			mRoles = roles;
			mPermissions = permissions;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UserPowersEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UserPowersEventHandler handler) {
			handler.gotUserPowers(mUser, mRoles, mPermissions);
		}
	}

	public class GetUserPowersFailed extends GwtEvent<UserPowersEventHandler> {
		private Error mError;

		/**
		 * 
		 * @param error
		 */
		public GetUserPowersFailed(Error error) {
			mError = error;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UserPowersEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UserPowersEventHandler handler) {
			if (mError != null) {
				handler.getGetUserPowersFailed(mError);
			}
		}

	}
}
