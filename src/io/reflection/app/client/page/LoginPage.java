//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.login.LoginForm;
import io.reflection.app.client.part.login.WelcomePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Page implements NavigationEventHandler {

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}

	@UiField WelcomePanel mWelcomePanel; // Welcome panel, showed when action 'welcome' is in the stack

	@UiField HTMLPanel mDefaultLogin;
	
	@UiField LoginForm mLoginForm; // Usual login panel

	@UiField AlertBox mAlertBox;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	/*
	 * @UiHandler("mLogin") void onLoginClicked(ClickEvent event) { if (validate()) { mForm.setVisible(false);
	 * 
	 * AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - verifying your username and password...", false)
	 * .setVisible(true);
	 * 
	 * SessionController.get().login(mUsername.getText(), mPassword.getText(), mRememberMe.getValue().booleanValue()); // Execute user login } else { if
	 * (mUsernameError != null) { FormHelper.showNote(true, mUsernameGroup, mUsernameNote, mUsernameError); } else { FormHelper.hideNote(mUsernameGroup,
	 * mUsernameNote); } if (mPasswordError != null) { FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError); } else {
	 * FormHelper.hideNote(mPasswordGroup, mPasswordNote); } } }
	 */
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
				mWelcomePanel.setVisible(false);
				mDefaultLogin.setVisible(true);
				mLoginForm.getEmail().setText(s.getAction());
			}
		} else {
			mWelcomePanel.setVisible(false);
			mDefaultLogin.setVisible(true);
		}

	}
}
