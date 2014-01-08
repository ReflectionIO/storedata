//
//  NavigationHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.handler;

import io.reflection.app.client.controller.NavigationController.Stack;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface NavigationEventHandler extends EventHandler {

	public static final GwtEvent.Type<NavigationEventHandler> TYPE = new GwtEvent.Type<NavigationEventHandler>();

	public void navigationChanged(Stack stack);
	
	public class ChangedEvent extends GwtEvent<NavigationEventHandler> {

		private Stack mStack;

		public ChangedEvent(Stack stack) {
			mStack = stack;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<NavigationEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(NavigationEventHandler handler) {
			handler.navigationChanged(mStack);
		}
	}

}
