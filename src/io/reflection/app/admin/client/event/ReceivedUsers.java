//
//  ReceivedUsers.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event;

import io.reflection.app.admin.client.event.handler.ReceivedUsersEventHandler;
import io.reflection.app.shared.datatypes.User;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public class ReceivedUsers extends GwtEvent<ReceivedUsersEventHandler> {
	public static Type<ReceivedUsersEventHandler> TYPE = new Type<ReceivedUsersEventHandler>();

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
	public com.google.gwt.event.shared.GwtEvent.Type<ReceivedUsersEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ReceivedUsersEventHandler handler) {
		handler.receivedUsers(mUser);

	}
}
