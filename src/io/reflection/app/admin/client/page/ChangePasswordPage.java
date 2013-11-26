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
	
	public ChangePasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		mPassword.getElement().setAttribute("placeholder", "Current Password");
		
		mNewPassword.getElement().setAttribute("placeholder", "New Password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm Password");
		
		mPasswordGroup.setVisible(!SessionController.get().isLoggedInUserAdmin());
	}
	
	@UiHandler("mChangePassword")
	void onChangePassword(ClickEvent event) {
		if (validate()) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				UserController.get().setPassword(Long.valueOf(NavigationController.get().getStack().getParameter(0)), mNewPassword.getText());
			} else {
				
			}
		} else {
//			if (mUsernameError != null) {
//				FormHelper.showNote(true, mUsernameGroup, mUsernameNote, mUsernameError);
//			}
//
//			if (mPasswordError != null) {
//				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
//			}
		}
	}
	
	boolean validate() {
		boolean validated = true;

//		String username = mUsername.getText();
//		String password = mPassword.getText();
//
//		if (username == null || username.length() == 0) {
//			mUsernameError = "Cannot be empty";
//			validated = false;
//		} else if (username.length() < 6) {
//			mUsernameError = "Too short (6 - 255)";
//			validated = false;
//		} else if (username.length() > 255) {
//			mUsernameError = "Too long (6 -255)";
//			validated = false;
//		} else {
//			mUsernameError = null;
//			validated = validated && true;
//		}
//
//		if (password == null || password.length() == 0) {
//			mPasswordError = "Cannot be empty";
//			validated = false;
//		} else if (password.length() < 6) {
//			mPasswordError = "Too short (6-100)";
//			validated = false;
//		} else if (password.length() > 100) {
//			mPasswordError = "Too long (6 - 100)";
//			validated = false;
//		} else {
//			mPasswordError = null;
//			validated = validated && true;
//		}

		return validated;
	}
	
	

}
