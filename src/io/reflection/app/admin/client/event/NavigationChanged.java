//
//  NavigationChanged.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event;

import io.reflection.app.admin.client.controller.NavigationController.Stack;
import io.reflection.app.admin.client.event.NavigationChanged.Handler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public class NavigationChanged extends GwtEvent<Handler> {
	public interface Handler extends EventHandler {
		public void navigationChanged(Stack stack);
	}
	
	public static Type<Handler> TYPE = new Type<Handler>();
	
	private Stack mStack;
	
	public NavigationChanged(Stack stack) {
		mStack = stack;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(Handler handler) {
		handler.navigationChanged(mStack);
	}
}
