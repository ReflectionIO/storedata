//
//  ChangePasswordPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 26 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class ChangePasswordPage extends Composite implements UserPasswordChangedEventHandler, KeyPressHandler {

	private static ChangePasswordPageUiBinder uiBinder = GWT.create(ChangePasswordPageUiBinder.class);

	interface ChangePasswordPageUiBinder extends UiBinder<Widget, ChangePasswordPage> {}

	@UiField PasswordTextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;

	@UiField PasswordTextBox mNewPassword;
	@UiField PasswordTextBox mConfirmPassword;
	@UiField HTMLPanel mNewPasswordGroup;
	@UiField HTMLPanel mNewPasswordNote;

	@UiField Button mChangePassword;

	private String mPasswordError = null;
	private String mNewPasswordError = null;

	@UiField AlertBox mAlertBox;

	@UiField FormPanel mForm;

	private Long mUserId;

	public ChangePasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mPassword.getElement().setAttribute("placeholder", "Current Password");

		mNewPassword.getElement().setAttribute("placeholder", "New Password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm Password");

		EventController.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, UserController.get(), this);
		EventController.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, SessionController.get(), this);
	
		mPassword.addKeyPressHandler(this);
		mNewPassword.addKeyPressHandler(this);
	}

	@UiHandler("mChangePassword")
	void onChangePassword(ClickEvent event) {
		if (validate()) {
			mForm.setVisible(false);

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - changing user password...", false)
					.setVisible(true);

			if (SessionController.get().isLoggedInUserAdmin()) {
				mUserId = Long.valueOf(NavigationController.get().getStack().getParameter(0));

				UserController.get().setPassword(mUserId, mNewPassword.getText());
			} else {
				SessionController.get().changePassword(mPassword.getText(), mNewPassword.getText());
			}
		} else {
			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
			}

			if (mNewPasswordError != null) {
				FormHelper.showNote(true, mNewPasswordGroup, mNewPasswordNote, mNewPasswordError);
			}
		}
	}

	boolean validate() {
		boolean validated = true;

		String newPassword = mNewPassword.getText();
		String confirmPassword = mConfirmPassword.getText();
		String password = mPassword.getText();

		if (newPassword == null || newPassword.length() == 0) {
			mNewPasswordError = "Cannot be empty";
			validated = false;
		} else if (newPassword.length() < 6) {
			mNewPasswordError = "Too short (6-100)";
			validated = false;
		} else if (newPassword.length() > 100) {
			mNewPasswordError = "Too long (6 - 100)";
			validated = false;
		} else if (!newPassword.equals(confirmPassword)) {
			mNewPasswordError = "Password and confirmation should match";
			validated = false;
		} else {
			mNewPasswordError = null;
			validated = validated && true;
		}

		if (!SessionController.get().isLoggedInUserAdmin()) {
			if (password == null || password.length() == 0) {
				mPasswordError = "Cannot be empty";
				validated = false;
			} else if (password.length() < 6) {
				mPasswordError = "Too short (6-100)";
				validated = false;
			} else if (password.length() > 100) {
				mPasswordError = "Too long (6 - 100)";
				validated = false;
			} else {
				mPasswordError = null;
				validated = validated && true;
			}
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
			
		if (SessionController.get().isLoggedInUserAdmin())
			mNewPassword.setFocus(true);
		else
			mPassword.setFocus(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChanged(java.lang.Long)
	 */
	@Override
	public void userPasswordChanged(Long userId) {

		final String userIdString = userId.toString();

		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Password changed",
				" - " + (SessionController.get().isLoggedInUserAdmin() ? "user with id " + userIdString : "you") + " can now login with new password.", false)
				.setVisible(true);

		Timer t = new Timer() {

			@Override
			public void run() {
				History.newItem(SessionController.get().isLoggedInUserAdmin() ? "users" : "logout");
			}
		};

		t.schedule(2000);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChangeFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userPasswordChangeFailed(Error error) {
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
				.setVisible(true);

		mForm.setVisible(true);
	}

	private void resetForm() {

		mPassword.setText("");

		mNewPassword.setText("");
		mConfirmPassword.setText("");

		mPasswordGroup.setVisible(!SessionController.get().isLoggedInUserAdmin());

		FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		mAlertBox.setVisible(false);

		mForm.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
		mPassword.setVisible(false);
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
			mChangePassword.click();
		}
		
	}

}
