//
//  StoresEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler;

import io.reflection.app.shared.datatypes.Store;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface StoresEventHandler extends EventHandler {
	public static final GwtEvent.Type<StoresEventHandler> TYPE = new GwtEvent.Type<StoresEventHandler>();
	
	
	public void receivedStores(List<Store> stores);

public class ReceivedStores extends GwtEvent<StoresEventHandler> {

	

	
	

	private List<Store> mStores;

	public ReceivedStores(List<Store> stores) {
		mStores = stores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<StoresEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(StoresEventHandler handler) {
		handler.receivedStores(mStores);

	}

}
}