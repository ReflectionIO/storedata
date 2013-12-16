//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.controller.NavigationController.Stack;
import io.reflection.app.admin.client.controller.SessionController;
import io.reflection.app.admin.client.handler.user.SessionEventHandler;
import io.reflection.app.admin.client.helper.AlertBoxHelper;
import io.reflection.app.admin.client.helper.FormHelper;
import io.reflection.app.admin.client.part.AlertBox;
import io.reflection.app.admin.client.part.AlertBox.AlertBoxType;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Composite implements SessionEventHandler {

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}

	@UiField FormPanel mForm;

	@UiField TextBox mUsername;
	@UiField HTMLPanel mUsernameGroup;
	@UiField HTMLPanel mUsernameNote;

	@UiField PasswordTextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;

	@UiField CheckBox mRememberMe;

	@UiField InlineHyperlink mRegister;
	@UiField InlineHyperlink mForgotPassword;

	@UiField AlertBox mAlertBox;

	private String mUsernameError = null;
	private String mPasswordError = null;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mUsername.getElement().setAttribute("placeholder", "Email address");
		mPassword.getElement().setAttribute("placeholder", "Password");

		EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
	}

	@UiHandler("mLogin")
	void onLoginClicked(ClickEvent event) {
		if (validate()) {
			mForm.setVisible(false);

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - verifying your username and password...", false)
					.setVisible(true);

			SessionController.get().login(mUsername.getText(), mPassword.getText(), mRememberMe.getValue().booleanValue());
		} else {
			if (mUsernameError != null) {
				FormHelper.showNote(true, mUsernameGroup, mUsernameNote, mUsernameError);
			}

			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
			}
		}
	}

	boolean validate() {
		boolean validated = true;

		String username = mUsername.getText();
		String password = mPassword.getText();

		if (username == null || username.length() == 0) {
			mUsernameError = "Cannot be empty";
			validated = false;
		} else if (username.length() < 6) {
			mUsernameError = "Too short (6 - 255)";
			validated = false;
		} else if (username.length() > 255) {
			mUsernameError = "Too long (6 -255)";
			validated = false;
		} else {
			mUsernameError = null;
			validated = validated && true;
		}

		if (password == null || password.length() == 0) {
			mPasswordError = "Cannot be empty";
			validated = false;
		} else if (password.length() < 6) {
			mPasswordError = "Too short (6-100)";
			validated = false;
		} else if (password.length() > 100) {
			mPasswordError = "Too long (6 - 100)";
			validated = false;
		} else {
			mPasswordError = null;
			validated = validated && true;
		}

		return validated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		resetForm();

		Stack s = NavigationController.get().getStack();
		if (s != null && s.hasAction()) {
			mUsername.setText(s.getAction());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Login Successfull",
				user.forename != null && user.forename.length() != 0 ? " - welcome back " + user.forename + "." : "", false).setVisible(true);

		Timer t = new Timer() {

			@Override
			public void run() {
				History.newItem("ranks");
			}
		};

		t.schedule(2000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		resetForm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		if (!mForm.isVisible()) {
			AlertBoxHelper
					.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
					.setVisible(true);

			mForm.setVisible(true);
		}
	}

	public void resetForm() {
		mForm.setVisible(true);
		mUsername.setText("");
		mPassword.setText("");
		mRememberMe.setValue(Boolean.TRUE);

		FormHelper.hideNote(mUsernameGroup, mUsernameNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		mAlertBox.setVisible(false);
	}
}
