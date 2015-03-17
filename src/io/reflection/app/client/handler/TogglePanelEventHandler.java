//
//  ToggleLeftPanelEventHandler.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 16 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.handler;

import io.reflection.app.client.part.navigation.Header.PanelType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public interface TogglePanelEventHandler extends EventHandler {

	public static final GwtEvent.Type<TogglePanelEventHandler> TYPE = new GwtEvent.Type<TogglePanelEventHandler>();

	public void panelToggled(PanelType panelType, boolean wasOpen, boolean isOpen);

	public class ChangedEvent extends GwtEvent<TogglePanelEventHandler> {

		private PanelType panelType;
		private boolean isOpen;
		private boolean wasOpen;

		public ChangedEvent(PanelType panelType, boolean wasOpen, boolean isOpen) {
			this.panelType = panelType;
			this.isOpen = isOpen;
			this.wasOpen = wasOpen;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<TogglePanelEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(TogglePanelEventHandler handler) {
			handler.panelToggled(panelType, wasOpen, isOpen);
		}
	}

}
