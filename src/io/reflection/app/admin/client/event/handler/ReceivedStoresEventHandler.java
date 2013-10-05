//
//  ReceivedStoresEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event.handler;

import io.reflection.app.shared.datatypes.Store;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author billy1380
 * 
 */
public interface ReceivedStoresEventHandler extends EventHandler {
	public void receivedStores(List<Store> stores);
}
