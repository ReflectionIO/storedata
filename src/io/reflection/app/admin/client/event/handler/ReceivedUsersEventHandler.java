//
//  ReceivedUsersEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event.handler;

import io.reflection.app.shared.datatypes.User;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author billy1380
 * 
 */
public interface ReceivedUsersEventHandler extends EventHandler {
	public void receivedUsers(List<User> users);
}
