//
//  ResetPasswordForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.res.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ResetPasswordForm extends Composite implements ChangePasswordEventHandler {

	private static ResetPasswordFormUiBinder uiBinder = GWT.create(ResetPasswordFormUiBinder.class);

	interface ResetPasswordFormUiBinder extends UiBinder<Widget, ResetPasswordForm> {}

	@UiField FormPanel form;

	@UiField TextBox resetCode;
	@UiField HTMLPanel resetCodeGroup;

	@UiField PasswordTextBox newPassword;
	@UiField PasswordTextBox confirmPassword;
	@UiField HTMLPanel newPasswordGroup;
	@UiField HTMLPanel newPasswordNote;
	private Preloader preloaderRef;

	private String newPasswordError = null;

	@UiField Button submit;

	final String imageButtonLink = "<img style=\"vertical-align: 1px;\" src=\"" + Images.INSTANCE.buttonArrowWhite().getSafeUri().asString() + "\"/>";

	public ResetPasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));

		resetCode.getElement().setAttribute("placeholder", "Code");

		newPassword.getElement().setAttribute("placeholder", "New Password");
		confirmPassword.getElement().setAttribute("placeholder", "Confirm Password");

		submit.setHTML(submit.getText() + "&nbsp;&nbsp;" + imageButtonLink);
	}

	@UiHandler("submit")
	void onSubmitClick(ClickEvent event) {
		if (validate()) {
			FormHelper.hideNote(newPasswordGroup, newPasswordNote);
			preloaderRef.show();
			SessionController.get().resetPassword(resetCode.getText(), newPassword.getText());
		} else {
			if (newPasswordError != null) {
				FormHelper.showNote(true, newPasswordGroup, newPasswordNote, newPasswordError);
			} else {
				FormHelper.hideNote(newPasswordGroup, newPasswordNote);
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

		newPassword.setFocus(true);
	}

	/**
	 * Set preloader object from Reset Password Page
	 * 
	 * @param p
	 */
	public void setPreloader(Preloader p) {
		preloaderRef = p;
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
		resetCode.setText("");
		newPassword.setText("");
		confirmPassword.setText("");
		setEnabled(true);
		FormHelper.hideNote(newPasswordGroup, newPasswordNote);
	}

	private void setEnabled(boolean value) {
		newPassword.setEnabled(value);
		confirmPassword.setEnabled(value);
		submit.setEnabled(value);
	}

	public void setResetCode(String code) {
		resetCode.setText(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler#changePasswordSuccess(io.reflection.app.api.core.shared.call.ChangePasswordRequest
	 * , io.reflection.app.api.core.shared.call.ChangePasswordResponse)
	 */
	@Override
	public void changePasswordSuccess(ChangePasswordRequest input, ChangePasswordResponse output) {
		preloaderRef.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler#changePasswordFailure(io.reflection.app.api.core.shared.call.ChangePasswordRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void changePasswordFailure(ChangePasswordRequest input, Throwable caught) {
		preloaderRef.hide();
	}

}
