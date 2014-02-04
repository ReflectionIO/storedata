//
//  IosMacLinkAccountForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part.linkaccount;

import io.reflection.app.client.handler.EnterPressedEventHandler;
import io.reflection.app.client.helper.FormHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.shared.Convert;

/**
 * @author billy1380
 * 
 */
public class IosMacLinkAccountForm extends Composite implements LinkableAccountFields {

	private static IosMacLinkAccountFormUiBinder uiBinder = GWT.create(IosMacLinkAccountFormUiBinder.class);

	interface IosMacLinkAccountFormUiBinder extends UiBinder<Widget, IosMacLinkAccountForm> {}

	@UiField TextBox mUsername;
	@UiField HTMLPanel mUsernameGroup;
	@UiField HTMLPanel mUsernameNote;
	String mUsernameError;

	@UiField PasswordTextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;
	String mPasswordError;

	@UiField TextBox mVendorId;
	@UiField HTMLPanel mVendorIdGroup;
	@UiField HTMLPanel mVendorIdNote;
	String mVendorIdError;
	private EnterPressedEventHandler mEnterHandler;

	public IosMacLinkAccountForm() {
		initWidget(uiBinder.createAndBindUi(this));

		mUsername.getElement().setAttribute("placeholder", "iTunes Connect Username");
		mPassword.getElement().setAttribute("placeholder", "iTunes Connect Password");
		mVendorId.getElement().setAttribute("placeholder", "Vendor number (8xxxxxxx)");

	}

	/**
	 * Set the focus on 'mUsername' field when this part of the page is set to visible
	 * 
	 * @see com.google.gwt.user.client.ui.UIObject#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible == Boolean.TRUE) {
			// mUsername.setFocus(true);
		}
	}

	/**
	 * On pressing key on form fields 
	 * 
	 * @param e
	 */
	@UiHandler({ "mUsername", "mPassword", "mVendorId" })
	void onKeyPressed(KeyPressEvent e) {
		if (mEnterHandler != null && e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mEnterHandler.onEnterPressed();
		}
	}

	/**
	 * Validate linked account fields
	 * 
	 * @see io.reflection.app.admin.client.part.linkaccount.LinkableAccountFields#validate()
	 */
	@Override
	public boolean validate() {
		boolean validated = true;
		// Retrieve fields to validate
		String username = mUsername.getText();
		String password = mPassword.getText();
		String vendorId = mVendorId.getText();

		// Check fields constraints
		if (username == null || username.length() == 0) {
			mUsernameError = "Cannot be empty";
			validated = false;
		/*} else if (username.length() < 6) {
			mUsernameError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (username.length() > 255) {
			mUsernameError = "Too long (maximum 255 characters)";
			validated = false; */
		} else {
			mUsernameError = null;
			validated = validated && true;
		}

		if (password == null || password.length() == 0) {
			mPasswordError = "Cannot be empty";
			validated = false;
			/*
			 * } else if (password.length() < 6) { mPasswordError = "Too short (minimum 6 characters)"; validated = false; } else if (password.length() > 64) {
			 * mPasswordError = "Too long (maximum 64 characters)"; validated = false;
			 */// depend by data account
		} else {
			mPasswordError = null;
			validated = validated && true;
		}

		if (vendorId == null || vendorId.length() == 0) {
			mVendorIdError = "Cannot be empty";
			validated = false;
		} else if (!FormHelper.isValidAppleVendorId(vendorId)) {
			mVendorIdError = "Invalid vendor id";
			validated = false;
		} else {
			mVendorIdError = null;
			validated = validated && true;
		}

		return validated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.part.linkaccount.LinkableAccountFields#getAccountTypeName()
	 */
	@Override
	public String getAccountSourceName() {
		return "iOS/Mac (iTunes connect)";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getAccountSourceId()
	 */
	@Override
	public Long getAccountSourceId() {
		return Long.valueOf(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getUsername()
	 */
	@Override
	public String getUsername() {
		return mUsername.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getPassword()
	 */
	@Override
	public String getPassword() {
		return mPassword.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getProperties()
	 */
	@Override
	public String getProperties() {
		JsonObject properties = new JsonObject();
		JsonArray vendors = new JsonArray();

		JsonPrimitive vendor = new JsonPrimitive(mVendorId.getText());

		vendors.add(vendor);

		properties.add("vendors", vendors);

		return Convert.fromJsonObject(properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setOnEnterPressed(io.reflection.app.client.handler.EnterPressedEventHandler)
	 */
	@Override
	public void setOnEnterPressed(EnterPressedEventHandler handler) {
		mEnterHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getFirstToFocus()
	 */
	@Override
	public Focusable getFirstToFocus() {
		return mUsername;
	}

	/**
	 * Show the errors in the form note in case validation failed
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setFormErrors()
	 */
	@Override
	public void setFormErrors() {
		if (mUsernameError != null) {
			FormHelper.showNote(true, mUsernameGroup, mUsernameNote, mUsernameError);
		} else {
			FormHelper.hideNote(mUsernameGroup, mUsernameNote);
		}
		if (mPasswordError != null) {
			FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
		} else {
			FormHelper.hideNote(mPasswordGroup, mPasswordNote);
		}
		if (mVendorIdError != null) {
			FormHelper.showNote(true, mVendorIdGroup, mVendorIdNote, mVendorIdError);
		} else {
			FormHelper.hideNote(mVendorIdGroup, mVendorIdNote);
		}

	}

}
