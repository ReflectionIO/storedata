//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.login.LoginForm;
import io.reflection.app.client.part.login.WelcomePanel;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Page implements NavigationEventHandler, SessionEventHandler {

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}

	@UiField WelcomePanel mWelcomePanel; // Welcome panel, showed when action 'welcome' is in the stack

	@UiField HTMLPanel mDefaultLogin;

	@UiField LoginForm mLoginForm; // Usual login panel

	@UiField AlertBox mAlertBox;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	/**
	 * Fire the login button when pressing the 'enter' key on one of the login form fields
	 * 
	 * @param event
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {

		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		Stack s = NavigationController.get().getStack();
		if (s != null && s.hasAction()) {
			if (s.getAction().equals("welcome")) { // If action == 'welcome', show the Welcome panel
				mWelcomePanel.setVisible(true);
				mDefaultLogin.setVisible(false);
			} else if (FormHelper.isValidEmail(s.getAction())) { // If action == email (user has been just registered to the system) attach him email to field
				mLoginForm.setLoginTitle("Log in to your account");
				mWelcomePanel.setVisible(false);
				mDefaultLogin.setVisible(true);
				mLoginForm.getEmail().setText(s.getAction());
			}
		} else {
			mLoginForm.setLoginTitle("Log in to your account");
			mWelcomePanel.setVisible(false);
			mDefaultLogin.setVisible(true);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Login Successfull",
		// user.forename != null && user.forename.length() != 0 ? " - welcome back " + user.forename + "." : "", false).setVisible(true);

		Timer t = new Timer() {

			@Override
			public void run() {
				History.newItem("linkitunes"); // After login is successful, redirect to leader-board page
			}
		};

		t.schedule(2000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		if (!this.isVisible()) {
			// AlertBoxHelper
			// .configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
			// .setVisible(true);

		}
	}

}
