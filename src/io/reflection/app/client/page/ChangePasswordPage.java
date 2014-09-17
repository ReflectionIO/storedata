//
//  ChangePasswordPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 26 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.AlertBox.AlertBoxType;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class ChangePasswordPage extends Page implements NavigationEventHandler, UserPasswordChangedEventHandler {

	private static ChangePasswordPageUiBinder uiBinder = GWT.create(ChangePasswordPageUiBinder.class);

	interface ChangePasswordPageUiBinder extends UiBinder<Widget, ChangePasswordPage> {}

	@UiField MyAccountSidePanel myAccountSidePanel;

	private User user = SessionController.get().getLoggedInUser();

	@UiField PasswordTextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;

	@UiField PasswordTextBox mNewPassword;
	@UiField PasswordTextBox mConfirmPassword;
	@UiField HTMLPanel mNewPasswordGroup;
	@UiField HTMLPanel mNewPasswordNote;

	@UiField Button mChangePassword;

	// Error definition during validation
	private String mPasswordError = null;
	private String mNewPasswordError = null;

	@UiField AlertBox mAlertBox;

	@UiField FormPanel mForm;

	private Long mUserId;

	@UiField Preloader preloader;

	public ChangePasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mPassword.getElement().setAttribute("placeholder", "Current password");
		mNewPassword.getElement().setAttribute("placeholder", "New password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm new password");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	@UiHandler("mChangePassword")
	void onChangePassword(ClickEvent event) {

		if (validate()) {
			clearErrors();
			// mForm.setVisible(false);
			preloader.show();

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
			} else {
				FormHelper.hideNote(mPasswordGroup, mPasswordNote);
			}
			if (mNewPasswordError != null) {
				FormHelper.showNote(true, mNewPasswordGroup, mNewPasswordNote, mNewPasswordError);
			} else {
				FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
			}
		}
	}

	private void clearErrors() {
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);
		FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
	}

	/**
	 * Fire the change password button when pressing the 'enter' key on one of the change password form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mPassword", "mNewPassword", "mConfirmPassword" })
	void onEnterKeyPressChangePasswordFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mChangePassword.click();
		}
	}

	@UiHandler({ "mPassword", "mNewPassword", "mConfirmPassword" })
	void onFieldsModified(KeyUpEvent event) {
		if (!SessionController.get().isLoggedInUserAdmin()) {
			if (!mPassword.getText().isEmpty() && !mNewPassword.getText().isEmpty() && !mConfirmPassword.getText().isEmpty()) {
				mChangePassword.setEnabled(true);
			} else {
				mChangePassword.setEnabled(false);
			}
		} else {
			if (!mNewPassword.getText().isEmpty() && !mConfirmPassword.getText().isEmpty()) {
				mChangePassword.setEnabled(true);
			} else {
				mChangePassword.setEnabled(false);
			}
		}
	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	boolean validate() {
		boolean validated = true;
		// Retrieve fields to validate
		String newPassword = mNewPassword.getText();
		String confirmPassword = mConfirmPassword.getText();
		String password = mPassword.getText();
		// Check password constraints for normal user
		if (newPassword == null || newPassword.length() == 0) {
			mNewPasswordError = "Cannot be empty";
			validated = false;
		} else if (newPassword.length() < 6) {
			mNewPasswordError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (newPassword.length() > 64) {
			mNewPasswordError = "Too long (maximum 64 characters)";
			validated = false;
		} else if (!newPassword.equals(confirmPassword)) {
			mNewPasswordError = "Password and confirmation should match";
			validated = false;
		} else if (!FormHelper.isTrimmed(newPassword)) {
			mNewPasswordError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			mNewPasswordError = null;
			validated = validated && true;
		}
		// Check password constraints for not admin user
		if (!SessionController.get().isLoggedInUserAdmin()) {
			if (password == null || password.length() == 0) {
				mPasswordError = "Cannot be empty";
				validated = false;
			} else if (password.length() < 6) {
				mPasswordError = "Too short (minimum 6 characters)";
				validated = false;
			} else if (password.length() > 64) {
				mPasswordError = "Too long (maximum 64 characters)";
				validated = false;
			} else if (!FormHelper.isTrimmed(password)) {
				mPasswordError = "Whitespaces not allowed either before or after the string";
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
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChanged(java.lang.Long)
	 */
	@Override
	public void userPasswordChanged(Long userId) {

		final String userIdString = userId.toString();

		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Password changed",
				" - " + (SessionController.get().isLoggedInUserAdmin() ? "user with id " + userIdString : "you") + " can now login with new password.", false)
				.setVisible(true);

		mChangePassword.setEnabled(false);
		resetForm();

		if (SessionController.get().isLoggedInUserAdmin()) {
			Timer t = new Timer() {

				@Override
				public void run() {
					PageType.UsersPageType.show();
				}
			};

			t.schedule(2000);
		} else {
			// TODO Inform user about success
		}

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
		preloader.hide();
		resetForm();

	}

	private void resetForm() {

		mPassword.setText("");

		mNewPassword.setText("");
		mConfirmPassword.setText("");

		mPasswordGroup.setVisible(!SessionController.get().isLoggedInUserAdmin());

		FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		mAlertBox.setVisible(false);

		// mForm.setVisible(true);
		preloader.hide();

		if (SessionController.get().isLoggedInUserAdmin()) {
			mNewPassword.setFocus(true);
		} else {
			mPassword.setFocus(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		mChangePassword.setEnabled(false);

		myAccountSidePanel.setChangePasswordLinkActive();

		user = SessionController.get().getLoggedInUser();

		if (user != null) {
			myAccountSidePanel.getLinkedAccountsLink().setTargetHistoryToken(
					PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));

			myAccountSidePanel.getCreatorNameLink().setInnerText(user.company);

			myAccountSidePanel.getPersonalDetailsLink().setTargetHistoryToken(
					PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));

		}

		String currentFilter = FilterController.get().asMyAppsFilterString();
		if (currentFilter != null && currentFilter.length() > 0) {
			if (user != null) {
				myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
						PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
								.asMyAppsFilterString()));
			}
		}

		resetForm();

	}

}
