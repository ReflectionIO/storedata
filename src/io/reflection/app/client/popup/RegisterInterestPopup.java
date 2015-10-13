//
//  RegisterInterestPopup.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 9 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import io.reflection.app.api.core.shared.call.RegisterInterestBusinessRequest;
import io.reflection.app.api.core.shared.call.RegisterInterestBusinessResponse;
import io.reflection.app.api.core.shared.call.event.RegisterInterestBusinessEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class RegisterInterestPopup extends Composite implements RegisterInterestBusinessEventHandler {

	private static RegisterInterestPopupUiBinder uiBinder = GWT.create(RegisterInterestPopupUiBinder.class);

	interface RegisterInterestPopupUiBinder extends UiBinder<Widget, RegisterInterestPopup> {}

	@UiField PopupBase popup;
	@UiField Button continueBrowsing;
	@UiField TextField firstNameTextField;
	private String firstNameError;
	@UiField TextField lastNameTextField;
	private String lastNameError;
	@UiField TextField companyTextField;
	private String companyError;
	@UiField TextField emailTextField;
	private String emailError;
	@UiField ParagraphElement generalErrorParagraph;
	private String generalErrorNote = "";
	@UiField LoadingButton registerInterest;

	public RegisterInterestPopup() { // TODO error already registered
		initWidget(uiBinder.createAndBindUi(this));

		popup.addStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().pageOverlayRegister());
	}

	public void show() {
		if (!this.asWidget().isAttached()) {
			RootPanel.get().add(this);
		}
		popup.show();
		firstNameTextField.setFocus(true);
	}

	public void hide() {
		popup.closePopup();
	}

	@UiHandler("popup")
	void onPopupClosed(CloseEvent<PopupBase> event) {
		RootPanel.get().remove(this.asWidget());
	}

	@UiHandler({ "firstNameTextField", "lastNameTextField", "companyTextField", "emailTextField" })
	void onEnterKeyDownRegisterFields(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			registerInterest.click();
		}
	}

	@UiHandler("registerInterest")
	void onRegisterInterestClicked(ClickEvent event) {
		event.preventDefault();
		if (validate()) {
			clearErrors();
			setFieldsEnabled(false);
			registerInterest.setStatusLoading("Sending ..");
			UserController.get().registerInterestBusiness(firstNameTextField.getText(), lastNameTextField.getText(), companyTextField.getText(),
					emailTextField.getText());
		} else {
			if (firstNameError != null) {
				firstNameTextField.showNote(firstNameError, true);
			} else {
				firstNameTextField.hideNote();
			}
			if (lastNameError != null) {
				lastNameTextField.showNote(lastNameError, true);
			} else {
				lastNameTextField.hideNote();
			}
			if (companyError != null) {
				companyTextField.showNote(companyError, true);
			} else {
				companyTextField.hideNote();
			}
			if (emailError != null) {
				emailTextField.showNote(emailError, true);
			} else {
				emailTextField.hideNote();
			}

			generalErrorParagraph.setInnerText(generalErrorNote);
			registerInterest.setStatusError(generalErrorNote.equals(FormHelper.ERROR_FORM_EMPTY_FIELDS) ? FormHelper.ERROR_BUTTON_INCOMPLETE
					: FormHelper.ERROR_BUTTON_WRONG);
			generalErrorNote = "";
		}
	}

	@UiHandler("continueBrowsing")
	void onContinueBrowsingClicked(ClickEvent event) {
		event.preventDefault();
		popup.closePopup();
	}

	public void setFieldsEnabled(boolean value) {
		firstNameTextField.setEnabled(value);
		lastNameTextField.setEnabled(value);
		companyTextField.setEnabled(value);
		emailTextField.setEnabled(value);
	}

	private void clearErrors() {
		firstNameTextField.hideNote();
		lastNameTextField.hideNote();
		companyTextField.hideNote();
		emailTextField.hideNote();
		generalErrorParagraph.setInnerText("");
	}

	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String forenameValue = firstNameTextField.getText();
		String surnameValue = lastNameTextField.getText();
		String companyValue = companyTextField.getText();
		String emailValue = emailTextField.getText();

		// Check fields constraints
		if (forenameValue == null || forenameValue.length() == 0) {
			firstNameError = FormHelper.ERROR_FIRST_NAME_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (forenameValue.length() < 2) {
			firstNameError = FormHelper.ERROR_NAME_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (forenameValue.length() > 30) {
			firstNameError = "Too long (maximum 30 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(forenameValue)) {
			firstNameError = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			firstNameError = null;
			validated = validated && true;
		}
		if (surnameValue == null || surnameValue.length() == 0) {
			lastNameError = FormHelper.ERROR_LAST_NAME_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (surnameValue.length() < 2) {
			lastNameError = FormHelper.ERROR_NAME_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (surnameValue.length() > 30) {
			lastNameError = "Too long (maximum 30 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(surnameValue)) {
			lastNameError = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			lastNameError = null;
			validated = validated && true;
		}
		if (companyValue == null || companyValue.length() == 0) {
			companyError = FormHelper.ERROR_COMPANY_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (companyValue.length() < 2) {
			companyError = FormHelper.ERROR_COMPANY_SHORT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (companyValue.length() > 255) {
			companyError = "Too long (maximum 255 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(companyValue)) {
			companyError = "Whitespaces not allowed either before or after the string";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			companyError = null;
			validated = validated && true;
		}
		if (emailValue == null || emailValue.length() == 0) {
			emailError = FormHelper.ERROR_EMAIL_EMPTY;
			generalErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (emailValue.length() > 255) {
			emailError = "Too long (maximum 255 characters)";
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!emailValue.contains("@")) {
			emailError = FormHelper.ERROR_EMAIL_MISSING_AT;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isValidEmail(emailValue)) {
			emailError = FormHelper.ERROR_EMAIL_WRONG;
			generalErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			emailError = null;
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
		DefaultEventBus.get().addHandlerToSource(RegisterInterestBusinessEventHandler.TYPE, UserController.get(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		firstNameTextField.setText("");
		lastNameTextField.setText("");
		companyTextField.setText("");
		emailTextField.setText("");
		clearErrors();
		registerInterest.resetStatus();
		setFieldsEnabled(true);
		popup.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmitted());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.RegisterInterestBusinessEventHandler#registerInterestBusinessSuccess(io.reflection.app.api.core.shared.call
	 * .RegisterInterestBusinessRequest, io.reflection.app.api.core.shared.call.RegisterInterestBusinessResponse)
	 */
	@Override
	public void registerInterestBusinessSuccess(RegisterInterestBusinessRequest input, RegisterInterestBusinessResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			popup.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmitted());
			registerInterest.setStatusSuccess("Sent!");
		} else {
			registerInterest.setStatusError();
		}
		setFieldsEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.RegisterInterestBusinessEventHandler#registerInterestBusinessFailure(io.reflection.app.api.core.shared.call
	 * .RegisterInterestBusinessRequest, java.lang.Throwable)
	 */
	@Override
	public void registerInterestBusinessFailure(RegisterInterestBusinessRequest input, Throwable caught) {
		registerInterest.setStatusError();
		setFieldsEnabled(true);
	}
}
