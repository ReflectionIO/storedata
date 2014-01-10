//
//  PermissionsEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.handler;

import io.reflection.app.datatypes.shared.Permission;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface PermissionsEventHandler extends EventHandler {
	public static final GwtEvent.Type<PermissionsEventHandler> TYPE = new GwtEvent.Type<PermissionsEventHandler>();
	
	public class ReceivedPermissions extends GwtEvent<PermissionsEventHandler> {

		private List<Permission> mPermissions;

		public ReceivedPermissions(List<Permission> Permissions) {
			mPermissions = Permissions;
		}
		
		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<PermissionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(PermissionsEventHandler handler) {
			handler.receivedPermissions(mPermissions);
		}
		
	}
	
	public void receivedPermissions(List<Permission> Permissions);
}
