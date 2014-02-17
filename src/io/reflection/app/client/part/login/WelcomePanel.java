//
//  WelcomePanel.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 14 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author stefanocapuzzi
 * 
 */
public class WelcomePanel extends Page implements SessionEventHandler {

	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);

	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePanel> {}

	@UiField LoginForm mLoginForm;

	public WelcomePanel() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		// resetForm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		// TODO Auto-generated method stub

	}

}
