//
//  RolesEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.handler;

import io.reflection.app.datatypes.shared.Role;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface RolesEventHandler extends EventHandler {
	public static final GwtEvent.Type<RolesEventHandler> TYPE = new GwtEvent.Type<RolesEventHandler>();

	public class ReceivedRoles extends GwtEvent<RolesEventHandler> {

		private List<Role> mRoles;

		public ReceivedRoles(List<Role> Roles) {
			mRoles = Roles;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<RolesEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(RolesEventHandler handler) {
			handler.receivedRoles(mRoles);
		}

	}

	public void receivedRoles(List<Role> Roles);
}
