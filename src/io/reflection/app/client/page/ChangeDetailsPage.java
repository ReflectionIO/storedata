//
//  ChangeDetailsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ChangeDetailsPage extends Composite implements NavigationEventHandler, ChangeUserDetailsEventHandler {

	private static ChangeDetailsPageUiBinder uiBinder = GWT.create(ChangeDetailsPageUiBinder.class);

	interface ChangeDetailsPageUiBinder extends UiBinder<Widget, ChangeDetailsPage> {}

	@UiField TextBox mUsername;
	@UiField HTMLPanel mUsernameGroup;
	@UiField HTMLPanel mUsernameNote;
	String mUsernameError;

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

	@UiField AlertBox mAlertBox;

	@UiField FormPanel mForm;

	@UiField Button mChangeDetails;

	private User mUser;

	public ChangeDetailsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mUsername.getElement().setAttribute("placeholder", "Email Address");
		mForename.getElement().setAttribute("placeholder", "First name");
		mSurname.getElement().setAttribute("placeholder", "Last name");
		mCompany.getElement().setAttribute("placeholder", "Company");

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);

		EventController.get().addHandlerToSource(ChangeUserDetailsEventHandler.TYPE, SessionController.get(), this);

	}

	@UiHandler("mChangeDetails")
	void onChangeDetailsClicked(ClickEvent event) {
		if (validate()) {
			mForm.setVisible(false);

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - changing user details...", false).setVisible(true);

			SessionController.get().changeUserDetails(mUsername.getText(), mForename.getText(), mSurname.getText(), mCompany.getText());
		} else {
			if (mUsernameError != null) {
				FormHelper.showNote(true, mUsernameGroup, mUsernameNote, mUsernameError);
			} else {
				FormHelper.hideNote(mUsernameGroup, mUsernameNote);
			}

			if (mForenameError != null) {
				FormHelper.showNote(true, mForenameGroup, mForenameNote, mForenameError);
			} else {
				FormHelper.hideNote(mForenameGroup, mForenameNote);
			}

			if (mSurnameError != null) {
				FormHelper.showNote(true, mSurnameGroup, mSurnameNote, mSurnameError);
			} else {
				FormHelper.hideNote(mSurnameGroup, mSurnameNote);
			}

			if (mCompanyError != null) {
				FormHelper.showNote(true, mCompanyGroup, mCompanyNote, mCompanyError);
			} else {
				FormHelper.hideNote(mCompanyGroup, mCompanyNote);
			}
		}
	}

	/**
	 * Fire the change details button when pressing the 'enter' key on one of the change details form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mUsername", "mForename", "mSurname", "mCompany" })
	void onEnterKeyPressChangeDetailsFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mChangeDetails.click();
		}
	}

	private void resetForm() {
		mUsername.setText("");
		mForename.setText("");
		mSurname.setText("");
		mCompany.setText("");

		FormHelper.hideNote(mUsernameGroup, mUsernameNote);
		FormHelper.hideNote(mForenameGroup, mForenameNote);
		FormHelper.hideNote(mSurnameGroup, mSurnameNote);
		FormHelper.hideNote(mCompanyGroup, mCompanyNote);

		mAlertBox.setVisible(false);
	}

	private void fillForm() {
		mUsername.setText(mUser.username);
		mForename.setText(mUser.forename);
		mSurname.setText(mUser.surname);
		mCompany.setText(mUser.company);
	}

	private void changedUser() {
		mUser = null;

		User user = SessionController.get().getLoggedInUser();
		String paramUserId = NavigationController.get().getStack().getParameter(0);

		if (paramUserId == null || user == null) {
			mUser = user;

			if (mUser == null) {
				AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "No user found",
						" - it does not look like a user has been specified and no user is logged in!", false);
			}

		} else {
			Long userId = Long.valueOf(paramUserId);

			if (user != null && user.id.longValue() == userId.longValue()) {
				mUser = user;
			} else {
				if (SessionController.get().isLoggedInUserAdmin()) {
					mUser = UserController.get().getUser(userId);

					if (mUser == null) {
						AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, true, "Please wait", " - loading user details...", false);
					}
				} else {
					AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Access denied",
							" - You tried to view user details that are not your own. This functionality is only accessible by administrators.", false);
				}
			}
		}

		mForm.setVisible(mUser != null);
		mAlertBox.setVisible(mUser == null);

		if (mUser == null) {
			resetForm();
		} else {
			fillForm();
		}
	}

	private boolean validate() {

		mUsernameError = "Error";
		mForenameError = "Error";
		mSurnameError = "Error";
		mCompanyError = "Error";

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged (io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if (stack != null && "users".equals(stack.getPage()) && "changedetails".equals(stack.getAction())) {
			changedUser();

			mForename.setFocus(true);
			mForename.setCursorPos(mForename.getText().length());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsSuccess(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse)
	 */
	@Override
	public void changeUserDetailsSuccess(ChangeUserDetailsRequest input, ChangeUserDetailsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Details changed", " - user details successfully changed", true)
					.setVisible(true);
		} else {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:",
					"(" + output.error.code + ") " + output.error.message, true).setVisible(true);
		}

		mForm.setVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsFailure(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, java.lang.Throwable)
	 */
	@Override
	public void changeUserDetailsFailure(ChangeUserDetailsRequest input, Throwable caught) {
		if (!mForm.isVisible()) {
			Error error = FormHelper.convertToError(caught);

			AlertBoxHelper
					.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
					.setVisible(true);

			mForm.setVisible(true);
		}

	}

}
