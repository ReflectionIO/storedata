//
//  ChangeDetailsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler;
import io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.dataprovider.UserRolesProvider;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ChangeDetailsPage extends Page implements NavigationEventHandler, ChangeUserDetailsEventHandler, UserPasswordChangedEventHandler,
		AssignRoleEventHandler, AssignPermissionEventHandler {

	private static ChangeDetailsPageUiBinder uiBinder = GWT.create(ChangeDetailsPageUiBinder.class);

	interface ChangeDetailsPageUiBinder extends UiBinder<Widget, ChangeDetailsPage> {}

	@UiField(provided = true) CellTable<Role> rolesTable = new CellTable<Role>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellTable<Permission> permissionsTable = new CellTable<Permission>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

	@UiField MyAccountSidePanel myAccountSidePanel;

	// Change Details
	@UiField Preloader preloaderDetails;

	@UiField TextBox mUsername;
	@UiField HTMLPanel mUsernameGroup;
	@UiField HTMLPanel mUsernameNote;
	private String mUsernameError;

	@UiField TextBox mForename;
	@UiField HTMLPanel mForenameGroup;
	@UiField HTMLPanel mForenameNote;
	private String mForenameError;

	@UiField TextBox mSurname;
	@UiField HTMLPanel mSurnameGroup;
	@UiField HTMLPanel mSurnameNote;
	private String mSurnameError;

	@UiField TextBox mCompany;
	@UiField HTMLPanel mCompanyGroup;
	@UiField HTMLPanel mCompanyNote;
	private String mCompanyError;

	@UiField AlertBox mAlertBox;

	@UiField Button mChangeDetails;

	// Change Password
	@UiField PasswordTextBox mPassword;
	@UiField HTMLPanel mPasswordGroup;
	@UiField HTMLPanel mPasswordNote;

	@UiField PasswordTextBox mNewPassword;
	@UiField PasswordTextBox mConfirmPassword;
	@UiField HTMLPanel mNewPasswordGroup;
	@UiField HTMLPanel mNewPasswordNote;

	@UiField Button mChangePassword;

	// Error definition during validation
	private String mPasswordError = null;
	private String mNewPasswordError = null;

	@UiField Preloader preloaderPassword;

	// User Roles
	@UiField TextBox addRoleTextbox;
	@UiField HTMLPanel addRoleGroup;
	@UiField HTMLPanel addRoleNote;
	// private String addRoleError;

	@UiField Button addRoleButton;

	// User Permissions
	@UiField TextBox addPermissionTextbox;
	@UiField HTMLPanel addPermissionGroup;
	@UiField HTMLPanel addPermissionNote;
	// private String addPermissionError;

	@UiField Button addPermissionButton;

	private User currentUser = SessionController.get().getLoggedInUser();
	private Long userId = Long.valueOf(NavigationController.get().getStack().getParameter(0)); // User Id from the URL

	public ChangeDetailsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addRoleColumns();
		addPermissionColumns();

		rolesTable.setEmptyTableWidget(new HTMLPanel("NO ROLES"));
		permissionsTable.setEmptyTableWidget(new HTMLPanel("NO PERMISSIONS"));
		rolesTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		permissionsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		User u = new User();
		u.id = userId;
		UserRolesProvider userRolesProvider = new UserRolesProvider(u); // TODO update when user changes
		userRolesProvider.addDataDisplay(rolesTable);
		// PermissionController.get().addDataDisplay(permissionsTable);

		mUsername.getElement().setAttribute("placeholder", "Email Address");
		mForename.getElement().setAttribute("placeholder", "First name");
		mSurname.getElement().setAttribute("placeholder", "Last name");
		mCompany.getElement().setAttribute("placeholder", "Company");

		mPassword.getElement().setAttribute("placeholder", "Current password");
		mNewPassword.getElement().setAttribute("placeholder", "New password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm new password");

		addRoleTextbox.getElement().setAttribute("placeholder", "ROL");
		addPermissionTextbox.getElement().setAttribute("placeholder", "PER");

	}

	private void addRoleColumns() {

		TextColumn<Role> roleInfo = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.code == null || object.name == null ? "-" : object.code + " - " + object.name;
			}

		};
		rolesTable.addColumn(roleInfo);

		if (SessionController.get().isLoggedInUserAdmin()) {
			Column<Role, SafeHtml> removeRole = new Column<Role, SafeHtml>(new SafeHtmlCell()) {

				@Override
				public SafeHtml getValue(Role object) {
					return SafeHtmlUtils.fromSafeConstant("ciao");
				}

			};
			rolesTable.addColumn(removeRole);
		}

	}

	private void addPermissionColumns() {

		TextColumn<Permission> permissionInfo = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.code == null || object.name == null ? "-" : object.code + " - " + object.name;
			}

		};
		permissionsTable.addColumn(permissionInfo);

		if (SessionController.get().isLoggedInUserAdmin()) {
			Column<Permission, SafeHtml> removePermission = new Column<Permission, SafeHtml>(new SafeHtmlCell()) {

				@Override
				public SafeHtml getValue(Permission object) {
					return SafeHtmlUtils.fromSafeConstant("ciao");
				}

			};
			permissionsTable.addColumn(removePermission);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(ChangeUserDetailsEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, SessionController.get(), this));

	}

	@UiHandler("mChangeDetails")
	void onChangeDetailsClicked(ClickEvent event) {
		if (validate()) {
			clearDetailsErrors();
			preloaderDetails.show();

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

	@UiHandler("mChangePassword")
	void onChangePassword(ClickEvent event) {

		if (validatePassword()) {
			clearPasswordErrors();
			// mForm.setVisible(false);
			preloaderPassword.show();

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - changing user password...", false)
					.setVisible(true);

			if (SessionController.get().isLoggedInUserAdmin()) {
				userId = Long.valueOf(NavigationController.get().getStack().getParameter(0));

				UserController.get().setPassword(userId, mNewPassword.getText());
			} else {
				SessionController.get().changePassword(mPassword.getText(), mNewPassword.getText());
			}
		} else {
			if (mPasswordError != null) {
				FormHelper.showNote(true, mPasswordGroup, mPasswordNote, mPasswordError);
			} else {
				FormHelper.hideNote(mPasswordGroup, mPasswordNote);
			}
			if (mNewPasswordError != null) {
				FormHelper.showNote(true, mNewPasswordGroup, mNewPasswordNote, mNewPasswordError);
			} else {
				FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
			}
		}
	}

	private void clearDetailsErrors() {
		FormHelper.hideNote(mUsernameGroup, mUsernameNote);
		FormHelper.hideNote(mForenameGroup, mForenameNote);
		FormHelper.hideNote(mSurnameGroup, mSurnameNote);
		FormHelper.hideNote(mCompanyGroup, mCompanyNote);
	}

	private void clearPasswordErrors() {
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);
		FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
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

	@UiHandler({ "mUsername", "mForename", "mSurname", "mCompany" })
	void onDetailsFieldsModified(KeyUpEvent event) {
		if (!mUsername.getText().equals(currentUser.username) || !mForename.getText().equals(currentUser.forename)
				|| !mSurname.getText().equals(currentUser.surname) || !mCompany.getText().equals(currentUser.company)) {
			mChangeDetails.setEnabled(true);
		} else {
			mChangeDetails.setEnabled(false);
		}
	}

	/**
	 * Fire the change password button when pressing the 'enter' key on one of the change password form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "mPassword", "mNewPassword", "mConfirmPassword" })
	void onEnterKeyPressChangePasswordFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			mChangePassword.click();
		}
	}

	@UiHandler({ "mPassword", "mNewPassword", "mConfirmPassword" })
	void onPasswordFieldsModified(KeyUpEvent event) {
		if (!SessionController.get().isLoggedInUserAdmin()) {
			if (!mPassword.getText().isEmpty() && !mNewPassword.getText().isEmpty() && !mConfirmPassword.getText().isEmpty()) {
				mChangePassword.setEnabled(true);
			} else {
				mChangePassword.setEnabled(false);
			}
		} else {
			if (!mNewPassword.getText().isEmpty() && !mConfirmPassword.getText().isEmpty()) {
				mChangePassword.setEnabled(true);
			} else {
				mChangePassword.setEnabled(false);
			}
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
		mUsername.setText(currentUser.username);
		mForename.setText(currentUser.forename);
		mSurname.setText(currentUser.surname);
		mCompany.setText(currentUser.company);
	}

	private void changedUser() {
		currentUser = null;

		User newUser = SessionController.get().getLoggedInUser();
		String paramUserId = NavigationController.get().getStack().getParameter(0);

		if (paramUserId == null || newUser == null) {
			currentUser = newUser;

			if (currentUser == null) {
				AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "No user found",
						" - it does not look like a user has been specified and no user is logged in!", false);
			}

		} else {
			Long userId = Long.valueOf(paramUserId);

			if (newUser != null && newUser.id.longValue() == userId.longValue()) {
				currentUser = newUser;
			} else {
				if (SessionController.get().isLoggedInUserAdmin()) {
					currentUser = UserController.get().getUser(userId);

					if (currentUser == null) {
						AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, true, "Please wait", " - loading user details...", false);
					}
				} else {
					AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Access denied",
							" - You tried to view user details that are not your own. This functionality is only accessible by administrators.", false);
				}
			}
		}

		if (currentUser != null) {
			preloaderDetails.hide();
			fillForm();
		} else {
			resetForm();
		}
		mAlertBox.setVisible(currentUser == null);

	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validate() {
		boolean validated = true;
		// Retrieve fields to validate
		String forename = mForename.getText();
		String surname = mSurname.getText();
		String company = mCompany.getText();
		String username = mUsername.getText();
		// Check fields constraints
		if (username == null || username.length() == 0) {
			mUsernameError = "Cannot be empty";
			validated = false;
		} else if (username.length() < 6) {
			mUsernameError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (username.length() > 255) {
			mUsernameError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(username)) {
			mUsernameError = "Invalid email address";
			validated = false;
		} else {
			mUsernameError = null;
			validated = validated && true;
		}

		if (forename == null || forename.length() == 0) {
			mForenameError = "Cannot be empty";
			validated = false;
		} else if (forename.length() < 2) {
			mForenameError = "Too short (minimum 2 characters)";
			validated = false;
		} else if (forename.length() > 30) {
			mForenameError = "Too long (maximum 30 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(forename)) {
			mForenameError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			mForenameError = null;
			validated = validated && true;
		}

		if (surname == null || surname.length() == 0) {
			mSurnameError = "Cannot be empty";
			validated = false;
		} else if (surname.length() < 2) {
			mSurnameError = "(minimum 2 characters)";
			validated = false;
		} else if (surname.length() > 30) {
			mSurnameError = "Too long (maximum 30 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(surname)) {
			mSurnameError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			mSurnameError = null;
			validated = validated && true;
		}

		if (company == null || company.length() == 0) {
			mCompanyError = "Cannot be empty";
			validated = false;
		} else if (company.length() < 2) {
			mCompanyError = "(minimum 2 characters)";
			validated = false;
		} else if (company.length() > 255) {
			mCompanyError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(company)) {
			mCompanyError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			mCompanyError = null;
			validated = validated && true;
		}

		return validated;
	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	boolean validatePassword() {
		boolean validated = true;
		// Retrieve fields to validate
		String newPassword = mNewPassword.getText();
		String confirmPassword = mConfirmPassword.getText();
		String password = mPassword.getText();
		// Check password constraints for normal user
		if (newPassword == null || newPassword.length() == 0) {
			mNewPasswordError = "Cannot be empty";
			validated = false;
		} else if (newPassword.length() < 6) {
			mNewPasswordError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (newPassword.length() > 64) {
			mNewPasswordError = "Too long (maximum 64 characters)";
			validated = false;
		} else if (!newPassword.equals(confirmPassword)) {
			mNewPasswordError = "Password and confirmation should match";
			validated = false;
		} else if (!FormHelper.isTrimmed(newPassword)) {
			mNewPasswordError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			mNewPasswordError = null;
			validated = validated && true;
		}
		// Check password constraints for not admin user
		if (!SessionController.get().isLoggedInUserAdmin()) {
			if (password == null || password.length() == 0) {
				mPasswordError = "Cannot be empty";
				validated = false;
			} else if (password.length() < 6) {
				mPasswordError = "Too short (minimum 6 characters)";
				validated = false;
			} else if (password.length() > 64) {
				mPasswordError = "Too long (maximum 64 characters)";
				validated = false;
			} else if (!FormHelper.isTrimmed(password)) {
				mPasswordError = "Whitespaces not allowed either before or after the string";
				validated = false;
			} else {
				mPasswordError = null;
				validated = validated && true;
			}
		}

		return validated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current != null && PageType.UsersPageType.equals(current.getPage()) && PageType.ChangeDetailsPageType.equals(current.getAction())) {

			mChangeDetails.setEnabled(false);
			mChangePassword.setEnabled(false);

			changedUser();

			myAccountSidePanel.setPersonalDetailsLinkActive();

			currentUser = SessionController.get().getLoggedInUser();

			if (currentUser != null) {
				myAccountSidePanel.getLinkedAccountsLink().setTargetHistoryToken(
						PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), currentUser.id.toString()));

				myAccountSidePanel.getCreatorNameLink().setInnerText(currentUser.company);

				myAccountSidePanel.getPersonalDetailsLink().setTargetHistoryToken(
						PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), currentUser.id.toString()));

			}

			String currentFilter = FilterController.get().asMyAppsFilterString();
			if (currentFilter != null && currentFilter.length() > 0) {
				if (currentUser != null) {
					myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
							PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), currentUser.id.toString(), FilterController.get()
									.asMyAppsFilterString()));
				}
			}

			resetPasswordForm();

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
			currentUser = SessionController.get().getLoggedInUser();

			myAccountSidePanel.getCreatorNameLink().setInnerText(currentUser.company);

			mChangeDetails.setEnabled(false);
			mForename.setFocus(true);
			mForename.setCursorPos(mForename.getText().length());
		} else {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:",
					"(" + output.error.code + ") " + output.error.message, true).setVisible(true);
		}

		preloaderDetails.hide();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsFailure(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, java.lang.Throwable)
	 */
	@Override
	public void changeUserDetailsFailure(ChangeUserDetailsRequest input, Throwable caught) {
		if (preloaderDetails.isVisible()) {
			Error error = FormHelper.convertToError(caught);

			AlertBoxHelper
					.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
					.setVisible(true);

			preloaderDetails.hide();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChanged(java.lang.Long)
	 */
	@Override
	public void userPasswordChanged(Long userId) {

		final String userIdString = userId.toString();

		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Password changed",
				" - " + (SessionController.get().isLoggedInUserAdmin() ? "user with id " + userIdString : "you") + " can now login with new password.", false)
				.setVisible(true);

		mChangePassword.setEnabled(false);
		resetPasswordForm();

		if (SessionController.get().isLoggedInUserAdmin()) {
			Timer t = new Timer() {

				@Override
				public void run() {
					PageType.UsersPageType.show();
				}
			};

			t.schedule(2000);
		} else {
			// TODO Inform user about success
		}
		preloaderPassword.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChangeFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userPasswordChangeFailed(Error error) {
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message, true)
				.setVisible(true);
		preloaderPassword.hide();
		resetPasswordForm();

	}

	private void resetPasswordForm() {

		mPassword.setText("");

		mNewPassword.setText("");
		mConfirmPassword.setText("");

		mPasswordGroup.setVisible(!SessionController.get().isLoggedInUserAdmin());

		FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		mAlertBox.setVisible(false);

		// mForm.setVisible(true);
		preloaderPassword.hide();

		if (SessionController.get().isLoggedInUserAdmin()) {
			mNewPassword.setFocus(true);
		} else {
			mPassword.setFocus(true);
		}
	}

	@UiHandler("addRoleButton")
	void onAddRoleButtonClicked(ClickEvent event) {
		String roleCode = addRoleTextbox.getValue();
		if (!roleCode.equals("") && roleCode.length() == 3) {
			roleCode = roleCode.toUpperCase();
			userId = Long.valueOf(NavigationController.get().getStack().getParameter(0));
			UserController.get().assignUserRoleId(userId, roleCode);
		}
	}

	@UiHandler("addPermissionButton")
	void onAddPermissionButtonClicked(ClickEvent event) {
		String permissionCode = addPermissionTextbox.getValue();
		if (!permissionCode.equals("") && permissionCode.length() == 3) {
			permissionCode = permissionCode.toUpperCase();
			userId = Long.valueOf(NavigationController.get().getStack().getParameter(0));
			UserController.get().assignUserPermissionId(userId, permissionCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleSuccess(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * io.reflection.app.api.admin.shared.call.AssignRoleResponse)
	 */
	@Override
	public void assignRoleSuccess(AssignRoleRequest input, AssignRoleResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			// UserController.get().fetchUserRoles();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleFailure(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void assignRoleFailure(AssignRoleRequest input, Throwable caught) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler#assignPermissionSuccess(io.reflection.app.api.admin.shared.call.
	 * AssignPermissionRequest, io.reflection.app.api.admin.shared.call.AssignPermissionResponse)
	 */
	@Override
	public void assignPermissionSuccess(AssignPermissionRequest input, AssignPermissionResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			// UserController.get().fetchUserRoles();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler#assignPermissionFailure(io.reflection.app.api.admin.shared.call.
	 * AssignPermissionRequest, java.lang.Throwable)
	 */
	@Override
	public void assignPermissionFailure(AssignPermissionRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

}
