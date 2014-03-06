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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class RegisterForm extends Composite {

	private static RegisterFormUiBinder uiBinder = GWT.create(RegisterFormUiBinder.class);

	interface RegisterFormUiBinder extends UiBinder<Widget, RegisterForm> {}

	@UiField TextBox mForename;
	@UiField HTMLPanel mForenameGroup;
	@UiField HTMLPanel mForenameNote;
	private String mForenameError;

	@UiField TextBox mSurname;
	@UiField HTMLPanel mSurnameGroup;
	@UiField HTMLPanel mSurnameNote;
	private String mSurnameError;

	@UiField TextBox mCompany;
	@UiField HTMLPanel mCompanyGroup;
	@UiField HTMLPanel mCompanyNote;
	private String mCompanyError;

	@UiField TextBox mEmail;
	@UiField HTMLPanel mEmailGroup;
	@UiField HTMLPanel mEmailNote;
	private String mEmailError;

	@UiField TextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	private String mPasswordError;

	@UiField CheckBox mTermAndCond;
	@UiField InlineHyperlink mTermAndCondLink;

	@UiField Button mRegister;

	final String imageButtonLink = "<img style=\"vertical-align: 1px;\" src=\"" + Images.INSTANCE.buttonArrowWhite().getSafeUri().asString() + "\"/>";

	public RegisterForm() {
		initWidget(uiBinder.createAndBindUi(this));

		mRegister.setHTML(mRegister.getText() + "&nbsp;&nbsp;" + imageButtonLink);
		mForename.getElement().setAttribute("placeholder", "First name");
		mSurname.getElement().setAttribute("placeholder", "Last name");
		mCompany.getElement().setAttribute("placeholder", "Company");
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

		mForename.setFocus(true);
	}

	@UiHandler({ "mForename", "mSurname", "mCompany", "mEmail", "mPassword" })
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

			UserController.get().registerUser(mEmail.getText(), mPassword.getText(), mForename.getText(), mSurname.getText(), mCompany.getText());

		} else {
			if (mForenameError != null) {
				FormHelper.showNote(true, mForenameGroup, mForenameNote, mForenameError);
			} else {
				FormHelper.hideNote(mForenameGroup, mForenameNote);
			}
			if (mSurnameError != null) {
				FormHelper.showNote(true, mSurnameGroup, mSurnameNote, mSurnameError);
			} else {
				FormHelper.hideNote(mSurnameGroup, mSurnameNote);
			}
			if (mCompanyError != null) {
				FormHelper.showNote(true, mCompanyGroup, mCompanyNote, mCompanyError);
			} else {
				FormHelper.hideNote(mCompanyGroup, mCompanyNote);
			}
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

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String forename = mForename.getText();
		String surname = mSurname.getText();
		String company = mCompany.getText();
		String email = mEmail.getText();
		String password = mPassword.getText();

		// Check fields constraints
		if (forename == null || forename.length() == 0) {
			mForenameError = "Cannot be empty";
			validated = false;
		} else if (forename.length() < 2) {
			mForenameError = "Too short (minimum 2 characters)";
			validated = false;
		} else if (forename.length() > 30) {
			mForenameError = "Too long (maximum 30 characters)";
			validated = false;
		} else {
			mForenameError = null;
			validated = validated && true;
		}
		if (surname == null || surname.length() == 0) {
			mSurnameError = "Cannot be empty";
			validated = false;
		} else if (surname.length() < 2) {
			mSurnameError = "(minimum 2 characters)";
			validated = false;
		} else if (surname.length() > 30) {
			mSurnameError = "Too long (maximum 30 characters)";
			validated = false;
		} else {
			mSurnameError = null;
			validated = validated && true;
		}
		if (company == null || company.length() == 0) {
			mCompanyError = "Cannot be empty";
			validated = false;
		} else if (company.length() < 2) {
			mCompanyError = "(minimum 2 characters)";
			validated = false;
		} else if (company.length() > 255) {
			mCompanyError = "Too long (maximum 255 characters)";
			validated = false;
		} else {
			mCompanyError = null;
			validated = validated && true;
		}
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
		mForename.setText("");
		mSurname.setText("");
		mCompany.setText("");
		mEmail.setText("");
		mPassword.setText("");
		
		FormHelper.hideNote(mForenameGroup, mForenameNote);
		FormHelper.hideNote(mSurnameGroup, mSurnameNote);
		FormHelper.hideNote(mCompanyGroup, mCompanyNote);
		FormHelper.hideNote(mEmailGroup, mEmailNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		// mAlertBox.setVisible(false);
	}

}
