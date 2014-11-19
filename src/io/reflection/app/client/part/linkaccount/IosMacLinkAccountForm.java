//
//  IosMacLinkAccountForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part.linkaccount;

import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
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

	@UiField TextBox accountUsername;
	@UiField HTMLPanel accountUsernameGroup;
	@UiField HTMLPanel accountUsernameNote;
	private String accountUsernameError;

	@UiField PasswordTextBox password;
	@UiField HTMLPanel passwordGroup;
	@UiField HTMLPanel passwordNote;
	private String passwordError;

	@UiField TextBox vendorId;
	@UiField HTMLPanel vendorIdGroup;
	@UiField HTMLPanel vendorIdNote;
	private String vendorIdError;

	private EnterPressedEventHandler enterHandler;

	public IosMacLinkAccountForm() {
		initWidget(uiBinder.createAndBindUi(this));

		accountUsername.getElement().setAttribute("placeholder", "iTunes Connect Username");
		password.getElement().setAttribute("placeholder", "iTunes Connect Password");
		vendorId.getElement().setAttribute("placeholder", "Vendor number (8xxxxxxx)");

		accountUsername.setTabIndex(1);
		password.setTabIndex(2);
		vendorId.setTabIndex(3);

	}

	/**
	 * Set the focus on 'mAccountUsername' field when this part of the page is set to visible
	 * 
	 * @see com.google.gwt.user.client.ui.UIObject#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible == Boolean.TRUE) {
			// mAccountUsername.setFocus(true);
		}
	}

	/**
	 * On pressing key on form fields
	 * 
	 * @param e
	 */
	@UiHandler({ "accountUsername", "password", "vendorId" })
	void onKeyPressed(KeyPressEvent e) {
		if (enterHandler != null && e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			enterHandler.onEnterPressed();
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
		String username = accountUsername.getText();
		String pswd = password.getText();
		String vendor = vendorId.getText();

		Stack stack = NavigationController.get().getStack();

		// Check fields constraints
		if (username == null || username.length() == 0) {
			accountUsernameError = "Cannot be empty";
			validated = false;
		} else if (username.length() < 2) {
			accountUsernameError = "Too short";
			validated = false;
		} else if (username.length() > 255) {
			accountUsernameError = "Too long";
			validated = false;
		} else if (stack.getParameter(1) != null && stack.getParameter(1).equals("add") && LinkedAccountController.get().hasLinkedAccount(username)) {
			accountUsernameError = "Linked account already exists";
			validated = false;
		} else {
			accountUsernameError = null;
			validated = validated && true;
		}

		if (pswd == null || pswd.length() == 0) {
			passwordError = "Cannot be empty";
			validated = false;

		} else if (pswd.length() < 2) {
			passwordError = "Too short";
			validated = false;
		} else if (pswd.length() > 64) {
			passwordError = "Too long";
			validated = false;
		} else {
			passwordError = null;
			validated = validated && true;
		}

		if (vendor == null || vendor.length() == 0) {
			vendorIdError = "Cannot be empty";
			validated = false;
		} else if (!FormHelper.isValidAppleVendorId(vendor)) {
			vendorIdError = "Invalid vendor id";
			validated = false;
		} else {
			vendorIdError = null;
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
		return accountUsername.getText();
	}

	public void setAccountUsername(String username) {
		accountUsername.setValue(username);
	}

	public void setVendorNumber(String vendorNumber) {
		vendorId.setValue(vendorNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getPassword()
	 */
	@Override
	public String getPassword() {
		return password.getText();
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

		JsonPrimitive vendor = new JsonPrimitive(vendorId.getText());

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
		enterHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#getFirstToFocus()
	 */
	@Override
	public Focusable getFirstToFocus() {
		return (accountUsername.isEnabled()) ? accountUsername : password;
	}

	/**
	 * Show the errors in the form note in case validation failed
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setFormErrors()
	 */
	@Override
	public void setFormErrors() {
		if (accountUsernameError != null) {
			FormHelper.showNote(true, accountUsernameGroup, accountUsernameNote, accountUsernameError);
		} else {
			FormHelper.hideNote(accountUsernameGroup, accountUsernameNote);
		}
		if (passwordError != null) {
			FormHelper.showNote(true, passwordGroup, passwordNote, passwordError);
		} else {
			FormHelper.hideNote(passwordGroup, passwordNote);
		}
		if (vendorIdError != null) {
			FormHelper.showNote(true, vendorIdGroup, vendorIdNote, vendorIdError);
		} else {
			FormHelper.hideNote(vendorIdGroup, vendorIdNote);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setUsernameError(java.lang.String)
	 */
	@Override
	public void setUsernameError(String error) {
		accountUsernameError = error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setPasswordError(java.lang.String)
	 */
	@Override
	public void setPasswordError(String error) {
		passwordError = error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setVendorError(java.lang.String)
	 */
	@Override
	public void setVendorError(String error) {
		vendorIdError = error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#setEnabled()
	 */
	@Override
	public void setEnabled(boolean enabled) {
		accountUsername.setEnabled(enabled);
		accountUsername.setFocus(enabled);
		password.setEnabled(enabled);
		vendorId.setEnabled(enabled);
		if (!enabled) {
			password.setFocus(false);
			vendorId.setFocus(false);
		}
	}

	public void setAccountUsernameEnabled(boolean enabled) {
		accountUsername.setEnabled(enabled);
		accountUsername.setFocus(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#resetForm()
	 */
	@Override
	public void resetForm() {
		// setEnabled(true);

		accountUsername.setText("");
		accountUsername.setFocus(true);
		password.setText("");
		vendorId.setText("");

		FormHelper.hideNote(accountUsernameGroup, accountUsernameNote);
		FormHelper.hideNote(passwordGroup, passwordNote);
		FormHelper.hideNote(vendorIdGroup, vendorIdNote);
	}

}
