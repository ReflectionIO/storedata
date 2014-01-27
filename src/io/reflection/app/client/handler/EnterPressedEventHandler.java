//
//  EnterPressed.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Oct 2014.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.client.handler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */

public interface EnterPressedEventHandler extends EventHandler {
	public static final GwtEvent.Type<EnterPressedEventHandler> TYPE = new GwtEvent.Type<EnterPressedEventHandler>();

	public void onEnterPressed();

	public class EnterEvent extends GwtEvent<EnterPressedEventHandler> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<EnterPressedEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(EnterPressedEventHandler handler) {
			handler.onEnterPressed();
		}

	}
}