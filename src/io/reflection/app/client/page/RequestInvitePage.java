//
//  RequestInvitePage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class RequestInvitePage extends Composite {

	private static RequestInvitePageUiBinder uiBinder = GWT.create(RequestInvitePageUiBinder.class);

	interface RequestInvitePageUiBinder extends UiBinder<Widget, RequestInvitePage> {}

	@UiField FormPanel mForm;
	
	@UiField TextBox mForename;
	@UiField HTMLPanel mForenameGroup;
	@UiField HTMLPanel mForenameNote;
	String mForenameError;

	@UiField TextBox mSurname;
	@UiField HTMLPanel mSurnameGroup;
	@UiField HTMLPanel mSurnameNote;
	String mSurnameError;

	@UiField TextBox mCompany;
	@UiField HTMLPanel mCompanyGroup;
	@UiField HTMLPanel mCompanyNote;
	String mCompanyError;

	@UiField TextBox mEmail;
	@UiField HTMLPanel mEmailGroup;
	@UiField HTMLPanel mEmailNote;
	String mUsernameError;
	
	@UiField TextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	String mPasswordError;
	
	@UiField CheckBox mTermAndCond;
	
	@UiField Button mRequestInvite;
	
	public RequestInvitePage() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}

}
