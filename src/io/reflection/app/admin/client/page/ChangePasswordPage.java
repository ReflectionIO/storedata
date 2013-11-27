//
//  ChangePasswordPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 26 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.controller.SessionController;
import io.reflection.app.admin.client.controller.UserController;
import io.reflection.app.admin.client.helper.FormHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ChangePasswordPage extends Composite {

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

	public ChangePasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mPassword.getElement().setAttribute("placeholder", "Current Password");

		mNewPassword.getElement().setAttribute("placeholder", "New Password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm Password");
	}

	@UiHandler("mChangePassword")
	void onChangePassword(ClickEvent event) {
		if (validate()) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				UserController.get().setPassword(Long.valueOf(NavigationController.get().getStack().getParameter(0)), mNewPassword.getText());
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
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		
		mPassword.setText("");

		mNewPassword.setText("");
		mConfirmPassword.setText("");
		
		mPasswordGroup.setVisible(!SessionController.get().isLoggedInUserAdmin());
	}

}
