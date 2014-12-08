//
//  NotificationsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author William Shakour (billy1380)
 *
 */
public class NotificationsPage extends Page implements NavigationEventHandler {

	private static NotificationsPageUiBinder uiBinder = GWT.create(NotificationsPageUiBinder.class);

	interface NotificationsPageUiBinder extends UiBinder<Widget, NotificationsPage> {}

	@UiField MyAccountSidePanel myAccountSidePanel;

	public NotificationsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.reflection().ensureInjected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		myAccountSidePanel.setActive(getPageType());
	}

}
