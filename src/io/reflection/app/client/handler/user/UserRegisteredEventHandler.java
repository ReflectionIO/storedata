//
//  UserRegisteredEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.handler.user;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public interface UserRegisteredEventHandler extends EventHandler {

	public static final GwtEvent.Type<UserRegisteredEventHandler> TYPE = new GwtEvent.Type<UserRegisteredEventHandler>();

	public void userRegistered(String email);

	public void userRegistrationFailed(Error error);

	public class UserRegistered extends GwtEvent<UserRegisteredEventHandler> {

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
		public com.google.gwt.event.shared.GwtEvent.Type<UserRegisteredEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UserRegisteredEventHandler handler) {
			handler.userRegistered(mUsername);
		}
	}

	public class UserRegistrationFailed extends GwtEvent<UserRegisteredEventHandler> {
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
		public com.google.gwt.event.shared.GwtEvent.Type<UserRegisteredEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(UserRegisteredEventHandler handler) {
			handler.userRegistrationFailed(mError);
		}
	}
}
