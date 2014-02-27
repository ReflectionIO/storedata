//
//  LoginForm.java
//  storedata
//
//  Created by Stefano Capuzzi on 14 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.res.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class LoginForm extends Composite {

	private static LoginFormUiBinder uiBinder = GWT.create(LoginFormUiBinder.class);

	interface LoginFormUiBinder extends UiBinder<Widget, LoginForm> {}

	@UiField FormPanel mLoginForm;
	@UiField FormPanel mForgotPasswordForm;
	@UiField HTMLPanel mForgotPasswordReminder;

	@UiField HeadingElement mLoginTitle;

	@UiField TextBox mEmail;
	@UiField HTMLPanel mEmailGroup;
	@UiField HTMLPanel mEmailNote;
	private String mEmailError = null;

	@UiField TextBox mEmailForgotPassword;
	@UiField HTMLPanel mEmailForgotPasswordGroup;
	@UiField HTMLPanel mEmailForgotPasswordNote;
	private String mEmailForgotPasswordError = null;

	@UiField TextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	private String mPasswordError = null;

	@UiField CheckBox mRememberMe;
	@UiField InlineHyperlink mForgotPassword;

	@UiField Button mLogin;
	@UiField Button mSubmitForgotPassword;

	Images images = GWT.create(Images.class);
	Image imageButton = new Image(images.buttonLogin());
	final String imageButtonLink = "<img style=\"vertical-align: -2px;\" src=\"" + imageButton.getUrl() + "\"/>";
	Image imageButton2 = new Image(images.buttonArrowWhite());
	final String imageButtonLink2 = "<img style=\"vertical-align: 1px;\" src=\"" + imageButton2.getUrl() + "\"/>";

	public LoginForm() {
		initWidget(uiBinder.createAndBindUi(this));

		mLogin.setHTML(mLogin.getText() + "&nbsp;&nbsp;" + imageButtonLink);
		mSubmitForgotPassword.setHTML(mSubmitForgotPassword.getText() + "&nbsp;&nbsp;" + imageButtonLink2);
		mEmail.getElement().setAttribute("placeholder", "Email");
		mPassword.getElement().setAttribute("placeholder", "Password");
		mEmailForgotPassword.getElement().setAttribute("placeholder", "Email");
	}

	public void setLoginTitle(String title) {
		mLoginTitle.setInnerText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		resetLoginForm();
		resetForgotPasswordForm();

		if (!mLoginForm.isVisible()) {
			mLoginForm.setVisible(true);
			mForgotPasswordForm.setVisible(false);
			mForgotPasswordReminder.setVisible(false);

		}
		mEmail.setFocus(true);

	}

	public TextBox getEmail() {
		return mEmail;
	}

	/**
	 * Fire the button when pressing the 'enter' key on one of the form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mEmail", "mPassword" })
	void onEnterKeyPressLoginFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mLogin.click();
		}
	}

	@UiHandler("mEmailForgotPassword")
	void onEnterKeyPressForgotPassword(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mSubmitForgotPassword.click();
		}
	}

	@UiHandler("mLogin")
	void onLoginClicked(ClickEvent event) {
		if (validate()) {
			FormHelper.hideNote(mEmailGroup, mEmailNote);
			FormHelper.hideNote(mPasswordGroup, mPasswordNote);
			disableLoginForm();
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

	@UiHandler("mSubmitForgotPassword")
	void onSubmitForgotPasswordClick(ClickEvent event) {
		if (validateForgotPassword()) {
			mForgotPasswordForm.setVisible(false);
			mForgotPasswordReminder.setVisible(true);
			FormHelper.hideNote(mEmailForgotPasswordGroup, mEmailForgotPasswordNote);
			mSubmitForgotPassword.setEnabled(false);
		} else {
			FormHelper.showNote(true, mEmailForgotPasswordGroup, mEmailForgotPasswordNote, mEmailForgotPasswordError);
		}
	}

	private boolean validateForgotPassword() {
		boolean validated = true;
		String email = mEmailForgotPassword.getText();
		if (email == null || email.length() == 0) {
			mEmailForgotPasswordError = "Cannot be empty";
			validated = false;
		} else if (email.length() < 6) {
			mEmailForgotPasswordError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (email.length() > 255) {
			mEmailForgotPasswordError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(email)) {
			mEmailForgotPasswordError = "Invalid email address";
			validated = false;
		} else {
			mEmailForgotPasswordError = null;
			validated = validated && true;
		}
		return validated;
	}

	private void resetLoginForm() {
		// mPanel.setVisible(true);
		mEmail.setEnabled(true);
		mEmail.setText("");
		mPassword.setEnabled(true);
		mPassword.setText("");
		mRememberMe.setEnabled(true);
		mLogin.setEnabled(true);

		FormHelper.hideNote(mEmailGroup, mEmailNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);
		// mAlertBox.setVisible(false);
	}

	private void resetForgotPasswordForm() {
		mEmailForgotPassword.setEnabled(true);
		mEmailForgotPassword.setText("");
		mSubmitForgotPassword.setEnabled(true);
		FormHelper.hideNote(mEmailForgotPasswordGroup, mEmailForgotPasswordNote);
	}

	private void disableLoginForm() {
		mEmail.setEnabled(false);
		mEmail.setFocus(false);
		mPassword.setEnabled(false);
		mPassword.setFocus(false);
		mRememberMe.setEnabled(false);
		mLogin.setEnabled(false);
	}

	@UiHandler("mForgotPassword")
	void onForgotPasswordClick(ClickEvent event) {
		Stack s = NavigationController.get().getStack();
		if (s != null && s.hasAction()) {
			if (s.getAction().equals("welcome")) {
				mForgotPassword.setTargetHistoryToken("login/welcome");
			} else if (FormHelper.isValidEmail(s.getAction())) {
				mForgotPassword.setTargetHistoryToken("login/" + s.getAction());
				mEmailForgotPassword.setText(s.getAction());
			} else {
				mForgotPassword.setTargetHistoryToken("login");
			}
		} else {
			mForgotPassword.setTargetHistoryToken("login");
		}
		mLoginForm.setVisible(false);
		mForgotPasswordForm.setVisible(true);
		mSubmitForgotPassword.setEnabled(true);
		mEmailForgotPassword.setFocus(true);
	}

}
