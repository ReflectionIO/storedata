//
//  UsersEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler;

import io.reflection.app.shared.datatypes.User;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public interface UsersEventHandler extends EventHandler {

	public static final GwtEvent.Type<UsersEventHandler> TYPE = new GwtEvent.Type<UsersEventHandler>();

	public void receivedUsers(List<User> users);

	public void receivedUsersCount(Long count);

	public void userRegistered(String email);

	public void userRegistrationFailed(Error error);

	public class ReceivedCount extends GwtEvent<UsersEventHandler> {

		private Long mCount = null;

		public ReceivedCount(Long count) {
			mCount = count;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UsersEventHandler handler) {
			handler.receivedUsersCount(mCount);

		}
	}

	public class ReceivedUsers extends GwtEvent<UsersEventHandler> {

		private List<User> mUser = null;

		public ReceivedUsers(List<User> user) {
			mUser = user;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UsersEventHandler handler) {
			handler.receivedUsers(mUser);

		}
	}

	public class UserRegistered extends GwtEvent<UsersEventHandler> {

		private String mUsername;

		public UserRegistered(String username) {
			mUsername = username;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UsersEventHandler handler) {
			handler.userRegistered(mUsername);
		}
	}

	public class UserRegistrationFailed extends GwtEvent<UsersEventHandler> {
		private Error mError;

		public UserRegistrationFailed(Error error) {
			mError = error;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UsersEventHandler handler) {
			handler.userRegistrationFailed(mError);
		}
	}
}