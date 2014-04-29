//
//  LoginForm.java
//  storedata
//
//  Created by Stefano Capuzzi on 14 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.res.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class LoginForm extends Composite {

	private static LoginFormUiBinder uiBinder = GWT.create(LoginFormUiBinder.class);

	interface LoginFormUiBinder extends UiBinder<Widget, LoginForm> {}

	@UiField FormPanel mForm;

	@UiField TextBox mEmail;
	@UiField HTMLPanel mEmailGroup;
	@UiField HTMLPanel mEmailNote;
	private String mEmailError = null;

	@UiField TextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	private String mPasswordError = null;

	@UiField CheckBox mRememberMe;

	@UiField Button mLogin;

	final String imageButtonLink = "<img style=\"vertical-align: -2px;\" src=\"" + Images.INSTANCE.buttonLogin().getSafeUri().asString() + "\"/>";

	public LoginForm() {
		initWidget(uiBinder.createAndBindUi(this));

		mLogin.setHTML(mLogin.getText() + "&nbsp;&nbsp;" + imageButtonLink);

		mEmail.getElement().setAttribute("placeholder", "Email");
		mPassword.getElement().setAttribute("placeholder", "Password");
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

		if (!mLogin.isVisible()) {
			mLogin.setVisible(true);
		}

	}

	public void setUsername(String value) {
		if (value != null && value.length() > 0) {
			mEmail.setText(value);
			mPassword.setFocus(true);
		}
	}

	/**
	 * Fire the button when pressing the 'enter' key on one of the form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mEmail", "mPassword" })
	void onEnterKeyPressFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mLogin.click();
		}
	}

	@UiHandler("mLogin")
	void onLoginClicked(ClickEvent event) {
		if (validate()) {
			clearErrors();
			setEnabled(false);
			SessionController.get().login(mEmail.getText(), mPassword.getText(), mRememberMe.getValue().booleanValue()); // Execute user login
		} else {
			if (mEmailError != null) {
				FormHelper.showNote(true, mEmailGroup, mEmailNote, mEmailError);
			} else {
				FormHelper.hideNote(mEmailGroup, mEmailNote);
			}
			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
			} else {
				FormHelper.hideNote(mPasswordGroup, mPasswordNote);
			}
		}
	}

	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String email = mEmail.getText();
		String password = mPassword.getText();

		// Check fields constraints
		if (email == null || email.length() == 0) {
			mEmailError = "Cannot be empty";
			validated = false;
		} else if (email.length() < 6) {
			mEmailError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (email.length() > 255) {
			mEmailError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(email)) {
			mEmailError = "Invalid email address";
			validated = false;
		} else {
			mEmailError = null;
			validated = validated && true;
		}

		if (password == null || password.length() == 0) {
			mPasswordError = "Cannot be empty";
			validated = false;
		} else if (password.length() < 6) {
			mPasswordError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (password.length() > 64) {
			mPasswordError = "Too long (maximum 64 characters)";
			validated = false;
		} else {
			mPasswordError = null;
			validated = validated && true;
		}

		return validated;
	}

	private void resetForm() {
		setEnabled(true);

		mEmail.setText("");
		mEmail.setFocus(true);

		mPassword.setText("");
		clearErrors();
	}

	private void clearErrors() {
		FormHelper.hideNote(mEmailGroup, mEmailNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);
	}

	public void setEnabled(boolean value) {
		mEmail.setEnabled(value);
		mEmail.setFocus(value);
		mRememberMe.setEnabled(value);
		mLogin.setEnabled(value);
		mPassword.setEnabled(value);
		if (!value) {
			mRememberMe.setFocus(false);
			mLogin.setFocus(false);
			mPassword.setFocus(value);
		}

	}

}
