//
//  ResetPasswordForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.PasswordField;
import io.reflection.app.client.controller.SessionController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ResetPasswordForm extends Composite {

	private static ResetPasswordFormUiBinder uiBinder = GWT.create(ResetPasswordFormUiBinder.class);

	interface ResetPasswordFormUiBinder extends UiBinder<Widget, ResetPasswordForm> {}

	private String resetCode;

	@UiField PasswordField newPassword;
	@UiField PasswordField confirmPassword;

	private String newPasswordError = null;

	@UiField LoadingButton submit;

	public ResetPasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("submit")
	void onSubmitClick(ClickEvent event) {
		if (validate()) {
			newPassword.hideNote();
			confirmPassword.hideNote();
			setEnabled(false);
			submit.setStatusLoading("Sending");
			SessionController.get().resetPassword(resetCode, newPassword.getText());
		} else {
			if (newPasswordError != null) {
				newPassword.showNote(newPasswordError, true);
				confirmPassword.showNote(newPasswordError, true);
			} else {
				newPassword.hideNote();
				confirmPassword.hideNote();
			}
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

	@UiHandler({ "newPassword", "confirmPassword" })
	void onEnterKeyPressResetPassword(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			submit.click();
		}
	}

	private boolean validate() {
		boolean validated = true;

		// Retrieve fields to validate
		String newPasswordValue = newPassword.getText();
		String confirmPasswordValue = confirmPassword.getText();

		// Check password constraints for normal user
		if (newPasswordValue == null || newPasswordValue.length() == 0) {
			newPasswordError = "Cannot be empty";
			validated = false;
		} else if (newPasswordValue.length() < 6) {
			newPasswordError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (newPasswordValue.length() > 64) {
			newPasswordError = "Too long (maximum 64 characters)";
			validated = false;
		} else if (!newPasswordValue.equals(confirmPasswordValue)) {
			newPasswordError = "Password and confirmation should match";
			validated = false;
		} else {
			newPasswordError = null;
			validated = validated && true;
		}

		return validated;
	}

	private void resetForm() {
		resetCode = "";
		newPassword.clear();
		confirmPassword.clear();
		newPassword.hideNote();
		confirmPassword.hideNote();
		submit.resetStatus();
		setEnabled(true);
	}

	private void setEnabled(boolean value) {
		newPassword.setEnabled(value);
		confirmPassword.setEnabled(value);
		submit.setEnabled(value);
	}

	public void setResetCode(String code) {
		resetCode = code;
	}

	public void setStatusSuccess() {
		submit.setStatusSuccess("Your Password's Been Changed", 0);
	}

	/**
	 * 
	 */
	public void setStatusError() {
		submit.setStatusError();
		newPassword.clear();
		confirmPassword.clear();
		setEnabled(true);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler#changePasswordSuccess(io.reflection.app.api.core.shared.call.ChangePasswordRequest
	// * , io.reflection.app.api.core.shared.call.ChangePasswordResponse)
	// */
	// @Override
	// public void changePasswordSuccess(ChangePasswordRequest input, ChangePasswordResponse output) {
	// if (output.status == StatusType.StatusTypeSuccess) {
	//
	// } else {
	//
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler#changePasswordFailure(io.reflection.app.api.core.shared.call.ChangePasswordRequest
	// * , java.lang.Throwable)
	// */
	// @Override
	// public void changePasswordFailure(ChangePasswordRequest input, Throwable caught) {
	// // preloaderRef.hide();
	// }

}
