//
//  ConfirmDialogEventHandler.java
//  storedata
//
//  Created by Stefano Capuzzi on 28 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.handler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author stefanocapuzzi
 * 
 */
public interface ConfirmDialogEventHandler extends EventHandler {
	public static final GwtEvent.Type<ConfirmDialogEventHandler> TYPE = new GwtEvent.Type<ConfirmDialogEventHandler>();

	public void onConfirmPressed();

	public class ConfirmClickEvent extends GwtEvent<ConfirmDialogEventHandler> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<ConfirmDialogEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(ConfirmDialogEventHandler handler) {
			handler.onConfirmPressed();
		}

	}
}
