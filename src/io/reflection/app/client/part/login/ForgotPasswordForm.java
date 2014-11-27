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
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.Preloader;

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
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForgotPasswordForm extends Composite implements ForgotPasswordEventHandler {

	private static ForgotPasswordFormUiBinder uiBinder = GWT.create(ForgotPasswordFormUiBinder.class);

	interface ForgotPasswordFormUiBinder extends UiBinder<Widget, ForgotPasswordForm> {}

	@UiField FormPanel mForm;
	@UiField InlineHyperlink tryAgainLink;
	@UiField TextBox mEmail;
	@UiField HTMLPanel mEmailGroup;
	@UiField HTMLPanel mEmailNote;
	private String mEmailError = null;
	private Preloader preloaderRef;

	@UiField Button mSubmit;

	public ForgotPasswordForm() {
		initWidget(uiBinder.createAndBindUi(this));

		tryAgainLink.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken("requestinvite"));
		mEmail.getElement().setAttribute("placeholder", "Email");
	}

	@UiHandler("mSubmit")
	void onSubmitClick(ClickEvent event) {
		if (validate()) {
			FormHelper.hideNote(mEmailGroup, mEmailNote);
			preloaderRef.show();
			SessionController.get().forgotPassword(mEmail.getText());
		} else {
			FormHelper.showNote(true, mEmailGroup, mEmailNote, mEmailError);
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

		mEmail.setFocus(true);
	}

	/**
	 * Set preloader object from Login Page
	 * 
	 * @param p
	 */
	public void setPreloader(Preloader p) {
		preloaderRef = p;
	}

	@UiHandler("mEmail")
	void onEnterKeyPressForgotPassword(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mSubmit.click();
		}
	}

	private boolean validate() {
		boolean validated = true;
		String email = mEmail.getText();
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
		return validated;
	}

	private void setUsernameError(String error) {
		mEmailError = error;
	}

	private void resetForm() {
		mEmail.setEnabled(true);
		mEmail.setText("");
		mSubmit.setEnabled(true);
		FormHelper.hideNote(mEmailGroup, mEmailNote);
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
		if (output.status == StatusType.StatusTypeFailure && output.error != null && output.error.code == ApiError.UserNotFound.getCode()) {
			setUsernameError("Invalid email address");
			FormHelper.showNote(true, mEmailGroup, mEmailNote, mEmailError);
		}
		preloaderRef.hide();
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
		preloaderRef.hide();
	}

}
