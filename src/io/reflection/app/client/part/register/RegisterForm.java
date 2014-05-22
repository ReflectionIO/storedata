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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class RegisterForm extends Composite {

	private static RegisterFormUiBinder uiBinder = GWT.create(RegisterFormUiBinder.class);

	interface RegisterFormUiBinder extends UiBinder<Widget, RegisterForm> {}

	@UiField TextBox forename;
	@UiField HTMLPanel forenameGroup;
	@UiField HTMLPanel forenameNote;
	private String forenameError;

	@UiField TextBox surname;
	@UiField HTMLPanel surnameGroup;
	@UiField HTMLPanel surnameNote;
	private String surnameError;

	@UiField TextBox company;
	@UiField HTMLPanel companyGroup;
	@UiField HTMLPanel companyNote;
	private String companyError;

	@UiField TextBox email;
	@UiField HTMLPanel emailGroup;
	@UiField HTMLPanel emailNote;
	private String emailError;

	@UiField TextBox password;
	@UiField TextBox confirmPassword;
	@UiField HTMLPanel passwordGroup;
	@UiField HTMLPanel passwordNote;
	private String passwordError;

	@UiField CheckBox termAndCond;
	@UiField HTMLPanel termAndCondGroup;
	@UiField HTMLPanel termAndCondNote;
	private String termAndCondError;

	@UiField Button mRegister;

	private boolean isRequestInvite;

	final String imageButtonLink = "<img style=\"vertical-align: 1px;\" src=\"" + Images.INSTANCE.buttonArrowWhite().getSafeUri().asString() + "\"/>";

	private String actionCode;

	public RegisterForm() {
		initWidget(uiBinder.createAndBindUi(this));

		mRegister.setHTML(mRegister.getText() + "&nbsp;&nbsp;" + imageButtonLink);
		forename.getElement().setAttribute("placeholder", "First name");
		surname.getElement().setAttribute("placeholder", "Last name");
		company.getElement().setAttribute("placeholder", "Company");
		email.getElement().setAttribute("placeholder", "Email");
		password.getElement().setAttribute("placeholder", "Password");
		confirmPassword.getElement().setAttribute("placeholder", "Confirm password");

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
		focusFirstActiveField();
	}

	@UiHandler({ "forename", "surname", "company", "email", "password", "confirmPassword", "termAndCond" })
	void onEnterKeyPressRegisterFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mRegister.click();
		}
	}

	@UiHandler("mRegister")
	void onRegisterClicked(ClickEvent event) {
		if (validate()) {
			clearErrors();
			setEnabled(false);
			if (actionCode == null) {
				UserController.get().registerUser(email.getText(), password.getText(), forename.getText(), surname.getText(), company.getText());
			} else {
				UserController.get().registerUser(actionCode, password.getText());
			}

		} else {
			if (forenameError != null) {
				FormHelper.showNote(true, forenameGroup, forenameNote, forenameError);
			} else {
				FormHelper.hideNote(forenameGroup, forenameNote);
			}
			if (surnameError != null) {
				FormHelper.showNote(true, surnameGroup, surnameNote, surnameError);
			} else {
				FormHelper.hideNote(surnameGroup, surnameNote);
			}
			if (companyError != null) {
				FormHelper.showNote(true, companyGroup, companyNote, companyError);
			} else {
				FormHelper.hideNote(companyGroup, companyNote);
			}
			if (emailError != null) {
				FormHelper.showNote(true, emailGroup, emailNote, emailError);
			} else {
				FormHelper.hideNote(emailGroup, emailNote);
			}

			if (passwordError != null) {
				FormHelper.showNote(true, passwordGroup, passwordNote, passwordError);
			} else {
				FormHelper.hideNote(passwordGroup, passwordNote);
			}

			if (termAndCondError != null) {
				FormHelper.showNote(true, termAndCondGroup, termAndCondNote, termAndCondError);
			} else {
				FormHelper.hideNote(termAndCondGroup, termAndCondNote);
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
		String forenameValue = forename.getText();
		String surnameValue = surname.getText();
		String companyValue = company.getText();
		String emailValue = email.getText();
		String passwordValue = password.getText();
		String confirmPasswordValue = confirmPassword.getText();

		// Check fields constraints
		if (forenameValue == null || forenameValue.length() == 0) {
			forenameError = "Cannot be empty";
			validated = false;
		} else if (forenameValue.length() < 2) {
			forenameError = "Too short (minimum 2 characters)";
			validated = false;
		} else if (forenameValue.length() > 30) {
			forenameError = "Too long (maximum 30 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(forenameValue)) {
			forenameError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			forenameError = null;
			validated = validated && true;
		}
		if (surnameValue == null || surnameValue.length() == 0) {
			surnameError = "Cannot be empty";
			validated = false;
		} else if (surnameValue.length() < 2) {
			surnameError = "(minimum 2 characters)";
			validated = false;
		} else if (surnameValue.length() > 30) {
			surnameError = "Too long (maximum 30 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(surnameValue)) {
			surnameError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			surnameError = null;
			validated = validated && true;
		}
		if (companyValue == null || companyValue.length() == 0) {
			companyError = "Cannot be empty";
			validated = false;
		} else if (companyValue.length() < 2) {
			companyError = "(minimum 2 characters)";
			validated = false;
		} else if (companyValue.length() > 255) {
			companyError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(companyValue)) {
			companyError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			companyError = null;
			validated = validated && true;
		}
		if (emailValue == null || emailValue.length() == 0) {
			emailError = "Cannot be empty";
			validated = false;
		} else if (emailValue.length() < 6) {
			emailError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (emailValue.length() > 255) {
			emailError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(emailValue)) {
			emailError = "Invalid email address";
			validated = false;
		} else {
			emailError = null;
			validated = validated && true;
		}

		if (!isRequestInvite) {
			if (passwordValue == null || passwordValue.length() == 0) {
				passwordError = "Cannot be empty";
				validated = false;
			} else if (passwordValue.length() < 6) {
				passwordError = "Too short (minimum 6 characters)";
				validated = false;
			} else if (passwordValue.length() > 64) {
				passwordError = "Too long (maximum 64 characters)";
				validated = false;
			} else if (!passwordValue.equals(confirmPasswordValue)) {
				passwordError = "Password and confirmation should match";
				validated = false;
			} else if (!FormHelper.isTrimmed(passwordValue)) {
				passwordError = "Whitespaces not allowed either before or after the string";
				validated = false;
			} else {
				passwordError = null;
				validated = validated && true;
			}
		}

		if (termAndCond.getValue() == Boolean.FALSE) {
			termAndCondError = "Must accept terms and conditions";
			validated = false;
		} else {
			termAndCondError = null;
			validated = validated && true;
		}

		return validated;
	}

	public void setUsername(String value) {
		email.setText(value);
		email.setEnabled(false);
	}

	public void setForename(String value) {
		forename.setText(value);
		forename.setEnabled(false);
	}

	public void setSurname(String value) {
		surname.setText(value);
		surname.setEnabled(false);
	}

	public void setCompany(String value) {
		company.setText(value);
		company.setEnabled(false);
	}

	public void setTermAndCond(boolean value) {
		termAndCond.setValue(value);
	}

	public void clearPasswordValue() {
		password.setValue("");
		confirmPassword.setValue("");
	}

	public void setActionCode(String value) {
		actionCode = value;
	}

	/**
	 * Clear form fields, focuses and errors, then enable it
	 */
	public void resetForm() {
		forename.setText("");
		surname.setText("");
		company.setText("");
		email.setText("");
		password.setText("");
		confirmPassword.setText("");
		forename.setFocus(false);
		surname.setFocus(false);
		company.setFocus(false);
		email.setFocus(false);
		password.setFocus(false);
		confirmPassword.setFocus(false);
		termAndCond.setValue(Boolean.FALSE);
		clearErrors();
		actionCode = null;
		setEnabled(true);
	}

	/**
	 * Enable form
	 * 
	 * @param value
	 */
	public void setEnabled(boolean value) {
		forename.setEnabled(value);
		surname.setEnabled(value);
		company.setEnabled(value);
		email.setEnabled(value);
		password.setEnabled(value);
		confirmPassword.setEnabled(value);
		termAndCond.setEnabled(value);
		mRegister.setEnabled(value);
	}

	private void clearErrors() {
		FormHelper.hideNote(forenameGroup, forenameNote);
		FormHelper.hideNote(surnameGroup, surnameNote);
		FormHelper.hideNote(companyGroup, companyNote);
		FormHelper.hideNote(emailGroup, emailNote);
		FormHelper.hideNote(passwordGroup, passwordNote);
		FormHelper.hideNote(termAndCondGroup, termAndCondNote);
	}

	public void focusFirstActiveField() {
		if (forename.isEnabled()) {
			forename.setFocus(true);
		} else {
			password.setFocus(true);
		}
	}

	/**
	 * Set the form as request invite mode
	 * 
	 * @param requestInvite
	 */
	public void setRequestInvite(boolean requestInvite) {
		isRequestInvite = requestInvite;
		if (requestInvite) {
			resetForm();
			focusFirstActiveField();
			passwordGroup.setVisible(false);
		} else {
			passwordGroup.setVisible(true);
		}
	}

}
