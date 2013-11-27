//
//  RegisterPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 *
 */
public class RegisterPage extends Composite {

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	@UiField TextBox mUsername;
	@UiField HTMLPanel mUsernameGroup;
    @UiField HTMLPanel mUsernameNote;
    
    @UiField PasswordTextBox mPassword;
    @UiField PasswordTextBox mConfirmPassword;
	@UiField HTMLPanel mPasswordGroup;
    @UiField HTMLPanel mPasswordNote;
    
    @UiField TextBox mForename;
	@UiField HTMLPanel mForenameGroup;
    @UiField HTMLPanel mForenameNote;
    
    @UiField TextBox mSurname;
	@UiField HTMLPanel mSurnameGroup;
    @UiField HTMLPanel mSurnameNote;
    
    @UiField TextBox mCompany;
	@UiField HTMLPanel mCompanyGroup;
    @UiField HTMLPanel mCompanyNote;
	
	public RegisterPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		mUsername.getElement().setAttribute("placeholder", "Email Address");
		mPassword.getElement().setAttribute("placeholder", "Password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm Password");
		mForename.getElement().setAttribute("placeholder", "First name");
		mSurname.getElement().setAttribute("placeholder", "Last name");
		mCompany.getElement().setAttribute("placeholder", "Company");
		
	}
	
	@UiHandler("mRegister")
	void onRegisterClicked(ClickEvent event) {
		
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		
		mUsername.setText("");
		mPassword.setText("");
		mConfirmPassword.setText("");
		mForename.setText("");
		mSurname.setText("");
		mCompany.setText("");
	}
	

}
