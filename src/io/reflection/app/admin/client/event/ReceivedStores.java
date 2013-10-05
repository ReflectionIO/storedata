//
//  ReceivedStores.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event;

import io.reflection.app.admin.client.event.handler.ReceivedStoresEventHandler;
import io.reflection.app.shared.datatypes.Store;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public class ReceivedStores extends GwtEvent<ReceivedStoresEventHandler> {

	public static Type<ReceivedStoresEventHandler> TYPE = new Type<ReceivedStoresEventHandler>();

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
	public com.google.gwt.event.shared.GwtEvent.Type<ReceivedStoresEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ReceivedStoresEventHandler handler) {
		handler.receivedStores(mStores);

	}

}
