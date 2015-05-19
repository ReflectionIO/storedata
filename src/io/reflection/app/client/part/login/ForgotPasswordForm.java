//
//  ForgotPasswordForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;
import io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.FormButton;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForgotPasswordForm extends Composite implements ForgotPasswordEventHandler {

	private static ForgotPasswordFormUiBinder uiBinder = GWT.create(ForgotPasswordFormUiBinder.class);

	interface ForgotPasswordFormUiBinder extends UiBinder<Widget, ForgotPasswordForm> {}

	@UiField TextField email;
	private String emailError = null;
	@UiField FormButton submit;

	public ForgotPasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("submit")
	void onSubmitClick(ClickEvent event) {
		if (validate()) {
			email.hideNote();
			SessionController.get().forgotPassword(email.getText());
			setEnabled(false);
			submit.setStatusLoading("Sending");
		} else {
			email.showNote(emailError, true);
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

		DefaultEventBus.get().addHandlerToSource(ForgotPasswordEventHandler.TYPE, SessionController.get(), this);

		resetForm();
	}

	@UiHandler("email")
	void onEnterKeyPressForgotPassword(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			submit.click();
		}
	}

	private boolean validate() {
		boolean validated = true;
		String emailText = email.getText();
		if (emailText == null || emailText.length() == 0) {
			emailError = "Cannot be empty";
			validated = false;
		} else if (emailText.length() < 6) {
			emailError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (emailText.length() > 255) {
			emailError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(emailText)) {
			emailError = "Invalid email address";
			validated = false;
		} else {
			emailError = null;
			validated = validated && true;
		}
		return validated;
	}

	public void setEnabled(boolean enabled) {
		email.setEnabled(enabled);
		email.setFocus(enabled);
	}

	private void resetForm() {
		email.setText("");
		email.hideNote();
		submit.resetStatus();
		setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler#forgotPasswordSuccess(io.reflection.app.api.core.shared.call.ForgotPasswordRequest
	 * , io.reflection.app.api.core.shared.call.ForgotPasswordResponse)
	 */
	@Override
	public void forgotPasswordSuccess(ForgotPasswordRequest input, ForgotPasswordResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			submit.setStatusSuccess("Email is on the way", 0);
		} else if (output.status == StatusType.StatusTypeFailure && output.error != null && output.error.code == ApiError.UserNotFound.getCode()) {
			submit.setStatusError("Invalid email address");
			setEnabled(true);
		} else {
			submit.setStatusError();
			setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler#forgotPasswordFailure(io.reflection.app.api.core.shared.call.ForgotPasswordRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void forgotPasswordFailure(ForgotPasswordRequest input, Throwable caught) {
		submit.setStatusError();
		setEnabled(true);
	}

}
