//
//  LinkedAccountSaveEventHandler.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 17 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.linkaccount;

import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class LinkedAccountChangeEvent extends GwtEvent<LinkedAccountChangeEventHandler> {

	public static final GwtEvent.Type<LinkedAccountChangeEventHandler> TYPE = new GwtEvent.Type<LinkedAccountChangeEventHandler>();

	public static enum EVENT_TYPE {
		ADD,
		UPDATE,
		DELETE
	}

	public interface HasLinkedAccountChangeEventHandlers extends HasHandlers {
		HandlerRegistration addLinkedAccountChangeEventHander(LinkedAccountChangeEventHandler handler);
	}

	public interface LinkedAccountChangeEventHandler extends EventHandler {
		public void onChange(DataAccount dataAccount, EVENT_TYPE eventType);
	}

	private DataAccount dataAccount;
	private EVENT_TYPE eventType;

	/**
	 * @param dataAccount
	 */
	public LinkedAccountChangeEvent(DataAccount dataAccount, EVENT_TYPE eventType) {
		this.dataAccount = dataAccount;
		this.eventType = eventType;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LinkedAccountChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LinkedAccountChangeEventHandler handler) {
		handler.onChange(this.dataAccount, eventType);
	}
}
