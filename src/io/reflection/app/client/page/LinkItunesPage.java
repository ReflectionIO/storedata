//
//  LinkItunesPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 11 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.helper.FormHelper;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class LinkItunesPage extends Composite {

	private static LinkItunesPageUiBinder uiBinder = GWT.create(LinkItunesPageUiBinder.class);

	interface LinkItunesPageUiBinder extends UiBinder<Widget, LinkItunesPage> {}

	@UiField HTMLPanel mPanel;

	@UiField TextBox mAccountUsername;
	@UiField HTMLPanel mAccountUsernameGroup;
	@UiField HTMLPanel mAccountUsernameNote;
	private String mAccountUsernameError;

	@UiField TextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	private String mPasswordError;

	@UiField Button mLinkAccount;

	Images images = GWT.create(Images.class);
	Image imageButton = new Image(images.buttonLinkedAccount());
	final String imageButtonLink = "<img style=\"vertical-align: 1px;\" src=\"" + imageButton.getUrl() + "\"/>";

	public LinkItunesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mLinkAccount.setHTML(mLinkAccount.getText() + "&nbsp;&nbsp;" + imageButtonLink);
		mAccountUsername.getElement().setAttribute("placeholder", "Account Username");
		mPassword.getElement().setAttribute("placeholder", "Password");
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
		mAccountUsername.setFocus(true);
	}

	/**
	 * Fire the button when pressing the 'enter' key on one of the form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mAccountUsername", "mPassword" })
	void onEnterKeyPressLoginFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mLinkAccount.click();
		}
	}

	@UiHandler("mLinkAccount")
	void onLinkAccountClicked(ClickEvent event) {
		if (validate()) {
			mPanel.setVisible(false);

		} else {
			if (mAccountUsernameError != null) {
				FormHelper.showNote(true, mAccountUsernameGroup, mAccountUsernameNote, mAccountUsernameError);
			} else {
				FormHelper.hideNote(mAccountUsernameGroup, mAccountUsernameNote);
			}
			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
			} else {
				FormHelper.hideNote(mPasswordGroup, mPasswordNote);
			}
		}
	}

	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String accountUsername = mAccountUsername.getText();
		String password = mPassword.getText();

		// Check fields constraints
		if (accountUsername == null || accountUsername.length() == 0) {
			mAccountUsernameError = "Cannot be empty";
			validated = false;
			/**
			 * } else if (accountUsername.length() < 6) { mAccountUsernameError = "Too short (minimum 6 characters)"; validated = false; } else if
			 * (accountUsername.length() > 255) { mAccountUsernameError = "Too long (maximum 255 characters)"; validated = false;
			 */
		} else {
			mAccountUsernameError = null;
			validated = validated && true;
		}

		if (password == null || password.length() == 0) {
			mPasswordError = "Cannot be empty";
			validated = false;
		} else if (password.length() < 6) {
			mPasswordError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (password.length() > 64) {
			mPasswordError = "Too long (maximum 64 characters)";
			validated = false;
		} else {
			mPasswordError = null;
			validated = validated && true;
		}

		return validated;
	}

	private void resetForm() {
		mPanel.setVisible(true);
		mAccountUsername.setText("");
		mPassword.setText("");
		FormHelper.hideNote(mAccountUsernameGroup, mAccountUsernameNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		// mAlertBox.setVisible(false);
	}
}
