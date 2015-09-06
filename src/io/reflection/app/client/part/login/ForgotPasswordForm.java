//
//  ForgotPasswordForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ForgotPasswordForm extends Composite {

	private static ForgotPasswordFormUiBinder uiBinder = GWT.create(ForgotPasswordFormUiBinder.class);

	interface ForgotPasswordFormUiBinder extends UiBinder<Widget, ForgotPasswordForm> {}

	@UiField TextField email;
	private String emailError = null;
	@UiField LoadingButton submit;
	@UiField Anchor backToLogin;

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

		resetForm();
	}

	@UiHandler("email")
	void onEnterKeyDownForgotPassword(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			submit.click();
		}
	}

	private boolean validate() {
		boolean validated = true;
		String emailText = email.getText();
		if (emailText == null || emailText.length() == 0) {
			emailError = FormHelper.ERROR_EMAIL_EMPTY;
			validated = false;
		} else if (emailText.length() > 255) {
			emailError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!emailText.contains("@")) {
			emailError = FormHelper.ERROR_EMAIL_MISSING_AT;
			validated = false;
		} else if (!FormHelper.isValidEmail(emailText)) {
			emailError = FormHelper.ERROR_EMAIL_WRONG;
			validated = false;
		} else {
			emailError = null;
			validated = validated && true;
		}
		return validated;
	}

	public void setEnabled(boolean enabled) {
		email.setEnabled(enabled);
	}

	public void resetForm() {
		email.setText("");
		email.hideNote();
		submit.resetStatus();
		setEnabled(true);
	}

	public void setEmail(String text) {
		email.setText(text);
	}

	public void setStatusSuccess() {
		submit.setStatusSuccess("Email Sent", 0);
	}

	public void setStatusError(String errorText) {
		submit.setStatusError(errorText);
		setEnabled(true);
	}

	public void setStatusError() {
		submit.setStatusError();
		setEnabled(true);
	}

	public boolean isStatusLoading() {
		return submit.isStatusLoading();
	}

	public Anchor getBackToLoginLink() {
		return backToLogin;
	}

}
