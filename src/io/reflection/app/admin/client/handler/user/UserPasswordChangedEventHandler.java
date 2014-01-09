//
//  UserPasswordChangedEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler.user;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public interface UserPasswordChangedEventHandler extends EventHandler {

	public static final GwtEvent.Type<UserPasswordChangedEventHandler> TYPE = new GwtEvent.Type<UserPasswordChangedEventHandler>();

	public void userPasswordChanged(Long userId);

	public void userPasswordChangeFailed(Error e);

	public class UserPasswordChanged extends GwtEvent<UserPasswordChangedEventHandler> {

		private Long mUserId;

		/**
		 * 
		 */
		public UserPasswordChanged(Long userId) {
			mUserId = userId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UserPasswordChangedEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UserPasswordChangedEventHandler handler) {
			handler.userPasswordChanged(mUserId);
		}

	}

	public class UserPasswordChangeFailed extends GwtEvent<UserPasswordChangedEventHandler> {

		private Error mError;

		public UserPasswordChangeFailed(Error error) {
			mError = error;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<UserPasswordChangedEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UserPasswordChangedEventHandler handler) {
			handler.userPasswordChangeFailed(mError);

		}

	}
}
