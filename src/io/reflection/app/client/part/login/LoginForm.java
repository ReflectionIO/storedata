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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class LoginForm extends Composite {

	private static LoginFormUiBinder uiBinder = GWT.create(LoginFormUiBinder.class);

	interface LoginFormUiBinder extends UiBinder<Widget, LoginForm> {}

	@UiField TabPanel mTabPanel;

	// Login fields
	@UiField FormPanel mLoginForm;
	@UiField FormPanel mForgotPasswordForm;
	@UiField HTMLPanel mForgotPasswordReminder;

	@UiField TextBox mEmailLogin;
	@UiField HTMLPanel mEmailGroupLogin;
	@UiField HTMLPanel mEmailNoteLogin;
	private String mEmailError = null;

	@UiField TextBox mEmailForgotPassword;
	@UiField HTMLPanel mEmailForgotPasswordGroup;
	@UiField HTMLPanel mEmailForgotPasswordNote;
	private String mEmailForgotPasswordError = null;

	@UiField TextBox mPasswordLogin;
	@UiField HTMLPanel mPasswordGroupLogin;
	@UiField HTMLPanel mPasswordNoteLogin;
	private String mPasswordErrorLogin = null;

	@UiField CheckBox mRememberMe;
	@UiField InlineHyperlink mForgotPassword;

	@UiField Button mLogin;
	@UiField Button mSubmitForgotPassword;

	// Register fields
	@UiField TextBox mForenameRegister;
	@UiField HTMLPanel mForenameGroupRegister;
	@UiField HTMLPanel mForenameNoteRegister;
	private String mForenameErrorRegister;

	@UiField TextBox mSurnameRegister;
	@UiField HTMLPanel mSurnameGroupRegister;
	@UiField HTMLPanel mSurnameNoteRegister;
	private String mSurnameErrorRegister;

	@UiField TextBox mCompanyRegister;
	@UiField HTMLPanel mCompanyGroupRegister;
	@UiField HTMLPanel mCompanyNoteRegister;
	private String mCompanyErrorRegister;

	@UiField TextBox mEmailRegister;
	@UiField HTMLPanel mEmailGroupRegister;
	@UiField HTMLPanel mEmailNoteRegister;
	private String mEmailErrorRegister;

	@UiField TextBox mPasswordRegister;
	@UiField HTMLPanel mPasswordGroupRegister;
	@UiField HTMLPanel mPasswordNoteRegister;
	private String mPasswordErrorRegister;

	@UiField CheckBox mTermAndCond;
	@UiField InlineHyperlink mTermAndCondLink;

	@UiField Button mRegister;

	Images images = GWT.create(Images.class);
	Image imageButtonLogin = new Image(images.buttonLogin());
	final String imageButtonLoginLink = "<img style=\"vertical-align: -2px;\" src=\"" + imageButtonLogin.getUrl() + "\"/>";
	Image imageButtonLogin2 = new Image(images.buttonArrowWhite());
	final String imageButtonLoginLink2 = "<img style=\"vertical-align: 1px;\" src=\"" + imageButtonLogin2.getUrl() + "\"/>";
	Image imageButtonRegister = new Image(images.buttonArrowWhite());
	final String imageButtonRegisterLink = "<img style=\"vertical-align: 1px;\" src=\"" + imageButtonRegister.getUrl() + "\"/>";

	public LoginForm() {
		initWidget(uiBinder.createAndBindUi(this));
		
		mRegister.setHTML(mRegister.getText() + "&nbsp;&nbsp;" + imageButtonRegisterLink);
		mForenameRegister.getElement().setAttribute("placeholder", "First name");
		mSurnameRegister.getElement().setAttribute("placeholder", "Last name");
		mCompanyRegister.getElement().setAttribute("placeholder", "Company");
		mEmailRegister.getElement().setAttribute("placeholder", "Email");
		mPasswordRegister.getElement().setAttribute("placeholder", "Password");
		
		mLogin.setHTML(mLogin.getText() + "&nbsp;&nbsp;" + imageButtonLoginLink);
		mSubmitForgotPassword.setHTML(mSubmitForgotPassword.getText() + "&nbsp;&nbsp;" + imageButtonLoginLink2);
		mEmailLogin.getElement().setAttribute("placeholder", "Email");
		mPasswordLogin.getElement().setAttribute("placeholder", "Password");
		mEmailForgotPassword.getElement().setAttribute("placeholder", "Email");

		mTabPanel.selectTab(1); // Select Log In Tab
	}

	@UiHandler("mTabPanel")
	void onLoginTabSelected(SelectionEvent<Integer> event) {
		if (event.getSelectedItem() == 0) {
			mForenameRegister.setFocus(true);
		} else {
			mEmailLogin.setFocus(true);
		}
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
		mEmailLogin.setFocus(true);
	}

	public TextBox getEmail() {
		return mEmailLogin;
	}

	/**
	 * Fire the button when pressing the 'enter' key on one of the form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mEmailLogin", "mPasswordLogin" })
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
			FormHelper.hideNote(mEmailGroupLogin, mEmailNoteLogin);
			FormHelper.hideNote(mPasswordGroupLogin, mPasswordNoteLogin);
			disableLoginForm();
			SessionController.get().login(mEmailLogin.getText(), mPasswordLogin.getText(), mRememberMe.getValue().booleanValue()); // Execute user login
		} else {
			if (mEmailError != null) {
				FormHelper.showNote(true, mEmailGroupLogin, mEmailNoteLogin, mEmailError);
			} else {
				FormHelper.hideNote(mEmailGroupLogin, mEmailNoteLogin);
			}
			if (mPasswordErrorLogin != null) {
				FormHelper.showNote(true, mPasswordGroupLogin, mPasswordNoteLogin, mPasswordErrorLogin);
			} else {
				FormHelper.hideNote(mPasswordGroupLogin, mPasswordNoteLogin);
			}
		}
	}

	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String email = mEmailLogin.getText();
		String password = mPasswordLogin.getText();

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
			mPasswordErrorLogin = "Cannot be empty";
			validated = false;
		} else if (password.length() < 6) {
			mPasswordErrorLogin = "Too short (minimum 6 characters)";
			validated = false;
		} else if (password.length() > 64) {
			mPasswordErrorLogin = "Too long (maximum 64 characters)";
			validated = false;
		} else {
			mPasswordErrorLogin = null;
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
		mEmailLogin.setEnabled(true);
		mEmailLogin.setText("");
		mPasswordLogin.setEnabled(true);
		mPasswordLogin.setText("");
		mRememberMe.setEnabled(true);
		mLogin.setEnabled(true);

		FormHelper.hideNote(mEmailGroupLogin, mEmailNoteLogin);
		FormHelper.hideNote(mPasswordGroupLogin, mPasswordNoteLogin);
		// mAlertBox.setVisible(false);
	}

	private void resetForgotPasswordForm() {
		mEmailForgotPassword.setEnabled(true);
		mEmailForgotPassword.setText("");
		mSubmitForgotPassword.setEnabled(true);
		FormHelper.hideNote(mEmailForgotPasswordGroup, mEmailForgotPasswordNote);
	}

	private void disableLoginForm() {
		mEmailLogin.setEnabled(false);
		mEmailLogin.setFocus(false);
		mPasswordLogin.setEnabled(false);
		mPasswordLogin.setFocus(false);
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

	// Register functions
	@UiHandler({ "mForenameRegister", "mSurnameRegister", "mCompanyRegister", "mEmailRegister", "mPasswordRegister" })
	void onEnterKeyPressRegisterFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mRegister.click();
		}
	}
	
}
