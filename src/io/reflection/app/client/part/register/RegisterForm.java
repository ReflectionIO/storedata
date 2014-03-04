//
//  RegisterForm.java
//  storedata
//
//  Created by Stefano Capuzzi on 19 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.register;

import io.reflection.app.client.controller.UserController;
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
public class RegisterForm extends Composite {

	private static RegisterFormUiBinder uiBinder = GWT.create(RegisterFormUiBinder.class);

	interface RegisterFormUiBinder extends UiBinder<Widget, RegisterForm> {}

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
	private String mPasswordError;

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

	public RegisterForm() {
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

		mTabPanel.selectTab(0); // Select Request Invite Tab
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

		resetForm();

		mForenameRegister.setFocus(true);
	}

	@UiHandler({ "mForenameRegister", "mSurnameRegister", "mCompanyRegister", "mEmailRegister", "mPasswordRegister" })
	void onEnterKeyPressRegisterFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mRegister.click();
		}
	}

	@UiHandler("mRegister")
	void onRegisterClicked(ClickEvent event) {
		if (validate()) {

			mRegister.setText("Registration in progress..");

			// TODO Checking effect

			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - creating user account...",
			// false).setVisible(true);

			UserController.get().registerUser(mEmailRegister.getText(), mPasswordRegister.getText(), mForenameRegister.getText(), mSurnameRegister.getText(),
					mCompanyRegister.getText());

		} else {
			if (mForenameErrorRegister != null) {
				FormHelper.showNote(true, mForenameGroupRegister, mForenameNoteRegister, mForenameErrorRegister);
			} else {
				FormHelper.hideNote(mForenameGroupRegister, mForenameNoteRegister);
			}
			if (mSurnameErrorRegister != null) {
				FormHelper.showNote(true, mSurnameGroupRegister, mSurnameNoteRegister, mSurnameErrorRegister);
			} else {
				FormHelper.hideNote(mSurnameGroupRegister, mSurnameNoteRegister);
			}
			if (mCompanyErrorRegister != null) {
				FormHelper.showNote(true, mCompanyGroupRegister, mCompanyNoteRegister, mCompanyErrorRegister);
			} else {
				FormHelper.hideNote(mCompanyGroupRegister, mCompanyNoteRegister);
			}
			if (mEmailErrorRegister != null) {
				FormHelper.showNote(true, mEmailGroupRegister, mEmailNoteRegister, mEmailErrorRegister);
			} else {
				FormHelper.hideNote(mEmailGroupRegister, mEmailNoteRegister);
			}
			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroupRegister, mPasswordNoteRegister, mPasswordError);
			} else {
				FormHelper.hideNote(mPasswordGroupRegister, mPasswordNoteRegister);
			}
		}
	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String forename = mForenameRegister.getText();
		String surname = mSurnameRegister.getText();
		String company = mCompanyRegister.getText();
		String email = mEmailRegister.getText();
		String password = mPasswordRegister.getText();

		// Check fields constraints
		if (forename == null || forename.length() == 0) {
			mForenameErrorRegister = "Cannot be empty";
			validated = false;
		} else if (forename.length() < 2) {
			mForenameErrorRegister = "Too short (minimum 2 characters)";
			validated = false;
		} else if (forename.length() > 30) {
			mForenameErrorRegister = "Too long (maximum 30 characters)";
			validated = false;
		} else {
			mForenameErrorRegister = null;
			validated = validated && true;
		}
		if (surname == null || surname.length() == 0) {
			mSurnameErrorRegister = "Cannot be empty";
			validated = false;
		} else if (surname.length() < 2) {
			mSurnameErrorRegister = "(minimum 2 characters)";
			validated = false;
		} else if (surname.length() > 30) {
			mSurnameErrorRegister = "Too long (maximum 30 characters)";
			validated = false;
		} else {
			mSurnameErrorRegister = null;
			validated = validated && true;
		}
		if (company == null || company.length() == 0) {
			mCompanyErrorRegister = "Cannot be empty";
			validated = false;
		} else if (company.length() < 2) {
			mCompanyErrorRegister = "(minimum 2 characters)";
			validated = false;
		} else if (company.length() > 255) {
			mCompanyErrorRegister = "Too long (maximum 255 characters)";
			validated = false;
		} else {
			mCompanyErrorRegister = null;
			validated = validated && true;
		}
		if (email == null || email.length() == 0) {
			mEmailErrorRegister = "Cannot be empty";
			validated = false;
		} else if (email.length() < 6) {
			mEmailErrorRegister = "Too short (minimum 6 characters)";
			validated = false;
		} else if (email.length() > 255) {
			mEmailErrorRegister = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(email)) {
			mEmailErrorRegister = "Invalid email address";
			validated = false;
		} else {
			mEmailErrorRegister = null;
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
		mForenameRegister.setText("");
		mSurnameRegister.setText("");
		mCompanyRegister.setText("");
		mEmailRegister.setText("");
		mPasswordRegister.setText("");
		FormHelper.hideNote(mForenameGroupRegister, mForenameNoteRegister);
		FormHelper.hideNote(mSurnameGroupRegister, mSurnameNoteRegister);
		FormHelper.hideNote(mCompanyGroupRegister, mCompanyNoteRegister);
		FormHelper.hideNote(mEmailGroupRegister, mEmailNoteRegister);
		FormHelper.hideNote(mPasswordGroupRegister, mPasswordNoteRegister);

		// mAlertBox.setVisible(false);
	}
	
	// Login function
	
	@UiHandler({ "mEmailLogin", "mPasswordLogin" })
	void onEnterKeyPressLoginFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mLogin.click();
		}
	}

}
