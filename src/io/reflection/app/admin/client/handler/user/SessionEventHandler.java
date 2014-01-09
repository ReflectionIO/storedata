//
//  SessionEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler.user;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.User;
import com.willshex.gson.json.service.shared.Error;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface SessionEventHandler extends EventHandler {

	public static final GwtEvent.Type<SessionEventHandler> TYPE = new GwtEvent.Type<SessionEventHandler>();

	public void userLoggedIn(User user, Session session);

	public void userLoggedOut();

	public void userLoginFailed(Error error);

	public class UserLoggedIn extends GwtEvent<SessionEventHandler> {
		private User mUser = null;
		private Session mSession = null;

		public UserLoggedIn(User user, Session session) {
			mUser = user;
			mSession = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<SessionEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(SessionEventHandler handler) {
			handler.userLoggedIn(mUser, mSession);
		}
	}

	public class UserLoggedOut extends GwtEvent<SessionEventHandler> {
		public UserLoggedOut() {}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<SessionEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(SessionEventHandler handler) {
			handler.userLoggedOut();
		}
	}

	public class UserLoginFailed extends GwtEvent<SessionEventHandler> {
		private Error mError;

		public UserLoginFailed(Error error) {
			mError = error;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<SessionEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(SessionEventHandler handler) {
			if (mError != null) handler.userLoginFailed(mError);
		}

	}

}