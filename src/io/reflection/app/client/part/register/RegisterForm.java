//
//  RegisterForm.java
//  storedata
//
//  Created by Stefano Capuzzi on 19 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.register;

import io.reflection.app.client.component.FormCheckbox;
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.PasswordField;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class RegisterForm extends Composite {

	private static RegisterFormUiBinder uiBinder = GWT.create(RegisterFormUiBinder.class);

	interface RegisterFormUiBinder extends UiBinder<Widget, RegisterForm> {}

	@UiField TextField forename;
	private String forenameNote;

	@UiField TextField surname;
	private String surnameNote;

	@UiField TextField company;
	private String companyNote;

	@UiField TextField email;
	private String emailNote;

	@UiField(provided = true) PasswordField password = new PasswordField(true);
	@UiField PasswordField confirmPassword;
	private String passwordError;

	@UiField HTMLPanel termAndCondGroup;
	@UiField FormCheckbox termAndCond;
	private String termAndCondError;

	@UiField ParagraphElement generalErrorParagraph;
	private String generalErrorNote = "";

	@UiField LoadingButton registerBtn;

	private String actionCode;

	private HeadingElement registerTitle = Document.get().createHElement(2);

	public RegisterForm() {
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().setAttribute("autocomplete", "off");
		termAndCond.setHTML("I agree with the <a href='" + PageType.TermsPageType.asHref().asString() + "' target='_blank'>terms and conditions</a>");
		registerTitle.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().headingStyleHeadingFive() + " "
				+ Styles.STYLES_INSTANCE.reflectionMainStyle().accountFormHeading());
		registerTitle.setInnerText("Create your password to get started");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		if (SessionController.get().isLoggedInUserAdmin()) {
			termAndCond.setVisible(Boolean.FALSE);
		} else {
			termAndCond.setVisible(Boolean.TRUE);
		}

		resetForm();
	}

	@UiHandler({ "forename", "surname", "company", "email", "password", "confirmPassword" })
	void onEnterKeyDownRegisterFields(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			registerBtn.click();
		}
	}

	@UiHandler("registerBtn")
	void onRegisterClicked(ClickEvent event) {
		if (validate()) {
			clearErrors();
			setEnabled(false);
			registerBtn.setStatusLoading("Sending ..");
			if (actionCode == null) { // Create new user
				UserController.get().registerUser(email.getText(), password.getText(), forename.getText(), surname.getText(), company.getText());
			} else { // Update user
				UserController.get().registerUser(actionCode, password.getText());
			}

		} else {
			if (forenameNote != null) {
				forename.showNote(forenameNote, true);
			} else {
				forename.hideNote();
			}
			if (surnameNote != null) {
				surname.showNote(surnameNote, true);
			} else {
				surname.hideNote();
			}
			if (companyNote != null) {
				company.showNote(companyNote, true);
			} else {
				company.hideNote();
			}
			if (emailNote != null) {
				email.showNote(emailNote, true);
			} else {
				email.hideNote();
			}
			if (passwordError != null) {
				password.showNote(passwordError, true);
				confirmPassword.showNote(passwordError, true);
			} else {
				password.hideNote();
				confirmPassword.hideNote();
			}

			if (termAndCondError != null) {
				termAndCond.showError(termAndCondError);
			} else {
				termAndCond.hideError();
			}
			generalErrorParagraph.setInnerText(generalErrorNote);
			registerBtn.setStatusError(generalErrorNote.equals(FormHelper.ERROR_FORM_EMPTY_FIELDS) ? FormHelper.ERROR_BUTTON_INCOMPLETE
					: FormHelper.ERROR_BUTTON_WRONG);
			generalErrorNote = "";
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
			forenameNote = FormHelper.ERROR_FIRST_NAME_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (forenameValue.length() < 2) {
			forenameNote = FormHelper.ERROR_NAME_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (forenameValue.length() > 30) {
			forenameNote = "Too long (maximum 30 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(forenameValue)) {
			forenameNote = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			forenameNote = null;
			validated = validated && true;
		}
		if (surnameValue == null || surnameValue.length() == 0) {
			surnameNote = FormHelper.ERROR_LAST_NAME_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (surnameValue.length() < 2) {
			surnameNote = FormHelper.ERROR_NAME_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (surnameValue.length() > 30) {
			surnameNote = "Too long (maximum 30 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(surnameValue)) {
			surnameNote = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			surnameNote = null;
			validated = validated && true;
		}
		if (companyValue == null || companyValue.length() == 0) {
			companyNote = FormHelper.ERROR_COMPANY_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (companyValue.length() < 2) {
			companyNote = FormHelper.ERROR_COMPANY_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (companyValue.length() > 255) {
			companyNote = "Too long (maximum 255 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(companyValue)) {
			companyNote = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			companyNote = null;
			validated = validated && true;
		}
		if (emailValue == null || emailValue.length() == 0) {
			emailNote = FormHelper.ERROR_EMAIL_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (emailValue.length() > 255) {
			emailNote = "Too long (maximum 255 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!emailValue.contains("@")) {
			emailNote = FormHelper.ERROR_EMAIL_MISSING_AT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isValidEmail(emailValue)) {
			emailNote = FormHelper.ERROR_EMAIL_WRONG;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			emailNote = null;
			validated = validated && true;
		}

		if (passwordValue == null || passwordValue.length() == 0) {
			passwordError = FormHelper.ERROR_PASSWORD_CREATE_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (passwordValue.length() < 6) {
			passwordError = FormHelper.ERROR_PASSWORD_CREATE_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (passwordValue.length() > 64) {
			passwordError = "Too long (maximum 64 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!passwordValue.equals(confirmPasswordValue)) {
			passwordError = FormHelper.ERROR_PASSWORD_CREATE_CONFIRMATION_MATCH;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(passwordValue)) {
			passwordError = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			passwordError = null;
			validated = validated && true;
		}

		if (!SessionController.get().isLoggedInUserAdmin()) {
			if (termAndCond.getValue() == Boolean.FALSE) {
				termAndCondError = "Just checking you agree with our very reasonable terms before we continue";
				generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
				validated = false;
			} else {
				termAndCondError = null;
				validated = validated && true;
			}
		}

		return validated;
	}

	public void setEmail(String value) {
		email.setText(value);
		email.setEnabled(false);
	}

	public void setEmailError(String errorText) {
		email.showNote(errorText, true);
		generalErrorParagraph.setInnerText(FormHelper.ERROR_FORM_WRONG_FIELDS);
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
		password.clear();
		confirmPassword.clear();
		termAndCond.setValue(Boolean.FALSE);
		clearErrors();
		actionCode = null;
		registerBtn.resetStatus();
		setEnabled(true);
		forename.setFocus(true);
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
		registerBtn.setEnabled(value);
	}

	private void clearErrors() {
		forename.hideNote();
		surname.hideNote();
		company.hideNote();
		email.hideNote();
		password.hideNote();
		confirmPassword.hideNote();
		termAndCond.hideError();
		generalErrorParagraph.setInnerText("");
	}

	public void setButtonLoading(String loadingText) {
		setEnabled(false);
		registerBtn.setStatusLoading(loadingText);
	}

	public void setButtonSuccess(String successText, int hideTimeout) {
		registerBtn.setStatusSuccess(successText, hideTimeout);
	}

	public void setButtonError(String errorText) {
		registerBtn.setStatusError(errorText);
	}

	public void setButtonError() {
		registerBtn.setStatusError();
	}

	public void resetButtonStatus() {
		registerBtn.resetStatus();
	}

}
