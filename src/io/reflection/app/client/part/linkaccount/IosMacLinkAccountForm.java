//
//  IosMacLinkAccountForm.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part.linkaccount;

import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.PasswordField;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.mixpanel.MixpanelHelper;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.EVENT_TYPE;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.HasLinkedAccountChangeEventHandlers;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.datatypes.shared.DataAccount;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class IosMacLinkAccountForm extends Composite implements LinkableAccountFields, HasLinkedAccountChangeEventHandlers {

	private static IosMacLinkAccountFormUiBinder uiBinder = GWT.create(IosMacLinkAccountFormUiBinder.class);

	interface IosMacLinkAccountFormUiBinder extends UiBinder<Widget, IosMacLinkAccountForm> {}

	@UiField FieldSetElement fieldset;

	@UiField HeadingElement title;
	@UiField ParagraphElement subtitle;

	@UiField TextField accountUsername;
	private String accountUsernameError;

	@UiField PasswordField password;
	private String passwordError;

	@UiField LoadingButton linkAccountBtn;

	private DataAccount dataAccount;

	public IosMacLinkAccountForm() {
		initWidget(uiBinder.createAndBindUi(this));

		subtitle.removeFromParent();
	}

	/**
	 * @param rowValue
	 */
	public void setAccount(DataAccount dataAccount) {
		this.dataAccount = dataAccount;
		accountUsername.setText(dataAccount.username);
	}

	public void showAccountUsername(boolean show) {
		accountUsername.setVisible(show);
	}

	public void setButtonText(String text) {
		linkAccountBtn.setText(text);
	}

	public void resetButtonStatus() {
		linkAccountBtn.resetStatus();
	}

	public void setTitleText(String text) {
		title.setInnerText(text);
	}

	public void setSubtitleText(String text) {
		if (!fieldset.isOrHasChild(subtitle)) {
			fieldset.insertAfter(subtitle, title);
		}
		subtitle.setInnerText(text);
	}

	public void setTitleStyleName(String style) {
		if (!fieldset.isOrHasChild(title)) {
			fieldset.insertFirst(title);
		}
		title.setClassName(style);
	}

	public void setStatusLoading(String loadingText) {
		linkAccountBtn.setStatusLoading(loadingText);
	}

	public void setStatusSuccess(String successText) {
		linkAccountBtn.setStatusSuccess(successText);
	}

	public void setStatusSuccess(String successText, int hideTimeout) {
		linkAccountBtn.setStatusSuccess(successText, hideTimeout);
	}

	public void setStatusError() {
		linkAccountBtn.setStatusError();
	}

	public void setStatusError(String errorText) {
		linkAccountBtn.setStatusError(errorText);
	}

	/**
	 * On pressing key on form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "accountUsername", "password" })
	void onKeyDown(KeyDownEvent event) {
		accountUsername.hideNote();
		password.hideNote();
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			linkAccountBtn.click();
		}
	}

	@UiHandler("linkAccountBtn")
	void onLinkedAccountBtnClicked(ClickEvent ce) {
		dataAccount = new DataAccount();
		dataAccount.username = accountUsername.getText();
		dataAccount.password = password.getText();
		if (validate()) {
			setFormErrors();
			fireEvent(new LinkedAccountChangeEvent(dataAccount, dataAccount.id == null ? EVENT_TYPE.ADD : EVENT_TYPE.UPDATE));
		} else {
			setFormErrors();
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

		// Check fields constraints
		if (username == null || username.length() < 2) {
			accountUsernameError = FormHelper.ERROR_LINKEDACCOUNT_NAME_EMPTY;
			validated = false;
		} else if (username.length() > 255) {
			accountUsernameError = "Too long";
			validated = false;
		} else {
			accountUsernameError = null;
			validated = validated && true;
		}

		// The password constraints are relaxed here since they depends by the Store checking the password
		if (pswd == null || pswd.length() < 2) {
			passwordError = (accountUsername.isVisible() ? FormHelper.ERROR_LINKEDACCOUNT_PASSWORD_EMPTY : FormHelper.ERROR_LINKEDACCOUNT_UPDATE_PASSWORD_EMPTY);
			validated = false;
		} else if (pswd.length() > 64) {
			passwordError = "Too long";
			validated = false;
		} else {
			passwordError = null;
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
		Map<String, Object> properties = new HashMap<String, Object>();
		if (accountUsernameError != null) {
			accountUsername.showNote(accountUsernameError, true);
			properties.put("error_form_account_name", accountUsernameError);
		} else {
			accountUsername.hideNote();
		}
		if (passwordError != null) {
			password.showNote(passwordError, true);
			properties.put("error_form_password", passwordError);
		} else {
			password.hideNote();
		}

		if (!properties.isEmpty()) {
			MixpanelHelper.track(MixpanelHelper.Event.LINK_ACCOUNT_FAILURE, properties);
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

	public void clearPassword() {
		password.clear();
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
		if (!enabled) {
			password.setFocus(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.linkaccount.LinkableAccountFields#resetForm()
	 */
	@Override
	public void resetForm() {
		accountUsername.setText("");
		accountUsername.hideNote();
		password.hideNote();
		password.clear();
		setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.HasLinkedAccountChangeEventHandlers#addLinkedAccountChangeEventHander(io.reflection
	 * .app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler)
	 */
	@Override
	public HandlerRegistration addLinkedAccountChangeEventHander(LinkedAccountChangeEventHandler handler) {
		return addHandler(handler, LinkedAccountChangeEvent.TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		TooltipHelper.updateWhatsThisTooltip();
	}

}
