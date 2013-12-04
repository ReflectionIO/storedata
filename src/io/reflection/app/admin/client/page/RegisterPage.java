//
//  RegisterPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.UserController;
import io.reflection.app.admin.client.handler.user.UserRegisteredEventHandler;
import io.reflection.app.admin.client.helper.AlertBoxHelper;
import io.reflection.app.admin.client.helper.FormHelper;
import io.reflection.app.admin.client.part.AlertBox;
import io.reflection.app.admin.client.part.AlertBox.AlertBoxType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class RegisterPage extends Composite implements UserRegisteredEventHandler {

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	@UiField TextBox mUsername;
	@UiField HTMLPanel mUsernameGroup;
	@UiField HTMLPanel mUsernameNote;
	String mUsernameError;

	@UiField PasswordTextBox mPassword;
	@UiField PasswordTextBox mConfirmPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	String mPasswordError;

	@UiField TextBox mForename;
	@UiField HTMLPanel mForenameGroup;
	@UiField HTMLPanel mForenameNote;
	String mForenameError;

	@UiField TextBox mSurname;
	@UiField HTMLPanel mSurnameGroup;
	@UiField HTMLPanel mSurnameNote;
	String mSurnameError;

	@UiField TextBox mCompany;
	@UiField HTMLPanel mCompanyGroup;
	@UiField HTMLPanel mCompanyNote;
	String mCompanyError;

	@UiField AlertBox mAlertBox;

	@UiField FormPanel mForm;

	public RegisterPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mUsername.getElement().setAttribute("placeholder", "Email Address");
		mPassword.getElement().setAttribute("placeholder", "Password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm Password");
		mForename.getElement().setAttribute("placeholder", "First name");
		mSurname.getElement().setAttribute("placeholder", "Last name");
		mCompany.getElement().setAttribute("placeholder", "Company");

		EventController.get().addHandlerToSource(UserRegisteredEventHandler.TYPE, UserController.get(), this);

	}

	@UiHandler("mRegister")
	void onRegisterClicked(ClickEvent event) {
		if (validate()) {
			mForm.setVisible(false);

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - creating user account...", false).setVisible(true);

			UserController.get().registerUser(mUsername.getText(), mPassword.getText(), mForename.getText(), mSurname.getText(), mCompany.getText());
		} else {
			if (mUsernameError != null) {
				FormHelper.showNote(true, mUsernameGroup, mUsernameNote, mUsernameError);
			}

			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
			}

			if (mForenameError != null) {
				FormHelper.showNote(true, mForenameGroup, mForenameNote, mForenameError);
			}

			if (mSurnameError != null) {
				FormHelper.showNote(true, mSurnameGroup, mSurnameNote, mSurnameError);
			}

			if (mCompanyError != null) {
				FormHelper.showNote(true, mCompanyGroup, mCompanyNote, mCompanyError);
			}
		}
	}

	boolean validate() {
		boolean validated = true;

		String username = mUsername.getText();
		String password = mPassword.getText();
		String confirmPassword = mConfirmPassword.getText();

		String forename = mForename.getText();
		String surname = mForename.getText();

		String company = mForename.getText();

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
		} else if (!password.equals(confirmPassword)) {
			mPasswordError = "Password and confirmation should match";
			validated = false;
		} else {
			mPasswordError = null;
			validated = validated && true;
		}

		if (forename == null || forename.length() == 0) {
			mForenameError = "Cannot be empty";
			validated = false;
		} else if (forename.length() < 6) {
			mForenameError = "Too short (6 - 255)";
			validated = false;
		} else if (forename.length() > 255) {
			mForenameError = "Too long (6 -255)";
			validated = false;
		} else {
			mForenameError = null;
			validated = validated && true;
		}

		if (surname == null || surname.length() == 0) {
			mSurnameError = "Cannot be empty";
			validated = false;
		} else if (surname.length() < 6) {
			mSurnameError = "Too short (6 - 255)";
			validated = false;
		} else if (surname.length() > 255) {
			mSurnameError = "Too long (6 -255)";
			validated = false;
		} else {
			mSurnameError = null;
			validated = validated && true;
		}

		if (company == null || company.length() == 0) {
			mCompanyError = "Cannot be empty";
			validated = false;
		} else if (company.length() < 6) {
			mCompanyError = "Too short (6 - 255)";
			validated = false;
		} else if (company.length() > 255) {
			mCompanyError = "Too long (6 -255)";
			validated = false;
		} else {
			mCompanyError = null;
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
	}

	private void resetForm() {
		mUsername.setText("");
		mPassword.setText("");
		mConfirmPassword.setText("");
		mForename.setText("");
		mSurname.setText("");
		mCompany.setText("");

		FormHelper.hideNote(mUsernameGroup, mUsernameNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);
		FormHelper.hideNote(mForenameGroup, mForenameNote);
		FormHelper.hideNote(mSurnameGroup, mSurnameNote);
		FormHelper.hideNote(mCompanyGroup, mCompanyNote);

		mAlertBox.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.user.UserRegisteredEventHandler#userRegistered(java.lang.String)
	 */
	@Override
	public void userRegistered(String email) {
		final String username = email;

		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Account created", " - you can now login and use Reflection.io.",
				false).setVisible(true);

		Timer t = new Timer() {

			@Override
			public void run() {
				History.newItem("login/" + username);
			}
		};

		t.schedule(2000);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.user.UserRegisteredEventHandler#userRegistrationFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userRegistrationFailed(Error error) {
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
				.setVisible(true);

		mForm.setVisible(true);
	}

}
