//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Composite {

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}
	
    @UiField TextBox mUsername;
    @UiField HTMLPanel mUsernameGroup;
    @UiField HTMLPanel mUsernameNote;
    
    @UiField PasswordTextBox mPassword;
    @UiField HTMLPanel mPasswordGroup;
    @UiField HTMLPanel mPasswordNote;
    
    @UiField CheckBox mRememberMe;

    @UiField InlineHyperlink mRegister;
    @UiField InlineHyperlink mForgotPassword;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		mUsername.getElement().setAttribute("placeholder", "Email address");
		mPassword.getElement().setAttribute("placeholder", "Password");
		
	}

	@UiHandler("mLogin")
	void onLoginClicked(ClickEvent event) {
		// please show thinking
	}
	
}
