//
//  ChangeDetailsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import java.util.List;

import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.admin.shared.call.RevokePermissionRequest;
import io.reflection.app.api.admin.shared.call.RevokePermissionResponse;
import io.reflection.app.api.admin.shared.call.RevokeRoleRequest;
import io.reflection.app.api.admin.shared.call.RevokeRoleResponse;
import io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler;
import io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler;
import io.reflection.app.api.admin.shared.call.event.GetRolesAndPermissionsEventHandler;
import io.reflection.app.api.admin.shared.call.event.RevokePermissionEventHandler;
import io.reflection.app.api.admin.shared.call.event.RevokeRoleEventHandler;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.dataprovider.UserPermissionsProvider;
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
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
		AssignRoleEventHandler, AssignPermissionEventHandler, RevokeRoleEventHandler, RevokePermissionEventHandler, GetRolesAndPermissionsEventHandler,
		GetUserDetailsEventHandler {

	private static ChangeDetailsPageUiBinder uiBinder = GWT.create(ChangeDetailsPageUiBinder.class);

	interface ChangeDetailsPageUiBinder extends UiBinder<Widget, ChangeDetailsPage> {}

	UserRolesProvider userRolesProvider;
	UserPermissionsProvider userPermissionsProvider;
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
	private String addRoleError;

	@UiField HTMLPanel addRolePanel;
	@UiField Button addRoleButton;
	@UiField Preloader preloaderAddRole;

	// User Permissions
	@UiField TextBox addPermissionTextbox;
	@UiField HTMLPanel addPermissionGroup;
	@UiField HTMLPanel addPermissionNote;
	private String addPermissionError;

	@UiField HTMLPanel addPermissionPanel;
	@UiField Button addPermissionButton;
	@UiField Preloader preloaderAddPermission;

	private User currentUser; // User using the system
	private Long editingUserId; // User Id to Edit

	public ChangeDetailsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		if (SessionController.get().isLoggedInUserAdmin()) {
			addRolePanel.getParent().getElement().appendChild(addRolePanel.getElement());
			addPermissionPanel.getParent().getElement().appendChild(addPermissionPanel.getElement());
		} else {
			addRolePanel.removeFromParent();
			addPermissionPanel.removeFromParent();
		}

		addRoleColumns(SessionController.get().isLoggedInUserAdmin());
		addPermissionColumns(SessionController.get().isLoggedInUserAdmin());

		rolesTable.setEmptyTableWidget(new HTMLPanel("NO ROLES"));
		permissionsTable.setEmptyTableWidget(new HTMLPanel("NO PERMISSIONS"));
		rolesTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		permissionsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		addPlaceholders();

	}

	private void addRoleColumns(boolean isAdmin) {

		TextColumn<Role> roleInfo = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.code == null || object.name == null ? "-" : object.code + " - " + object.name;
			}

		};
		rolesTable.addColumn(roleInfo);

		if (isAdmin) {

			FieldUpdater<Role, String> actionRole = new FieldUpdater<Role, String>() {

				@Override
				public void update(int index, Role object, String value) {
					userRolesProvider.updateRowCount(0, false);
					UserController.get().revokeUserRole(editingUserId, object);
				}
			};

			StyledButtonCell prototype = new StyledButtonCell("btn", "btn-xs", "btn-default", "pull-right");
			Column<Role, String> removeRole = new Column<Role, String>(prototype) {

				@Override
				public String getValue(Role object) {
					return "Revoke role";
				}
			};
			removeRole.setFieldUpdater(actionRole);
			rolesTable.addColumn(removeRole);

		}

	}

	private void addPermissionColumns(boolean isAdmin) {

		TextColumn<Permission> permissionInfo = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.code == null || object.name == null ? "-" : object.code + " - " + object.name;
			}

		};
		permissionsTable.addColumn(permissionInfo);

		if (isAdmin) {

			FieldUpdater<Permission, String> actionPermission = new FieldUpdater<Permission, String>() {

				@Override
				public void update(int index, Permission object, String value) {
					userPermissionsProvider.updateRowCount(0, false);
					UserController.get().revokeUserPermission(editingUserId, object);
				}
			};

			StyledButtonCell prototype = new StyledButtonCell("btn", "btn-xs", "btn-default", "pull-right");
			Column<Permission, String> removePermission = new Column<Permission, String>(prototype) {

				@Override
				public String getValue(Permission object) {
					return "Revoke permission";
				}
			};
			removePermission.setFieldUpdater(actionPermission);
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
		register(EventController.get().addHandlerToSource(GetRolesAndPermissionsEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(AssignRoleEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(AssignPermissionEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(RevokeRoleEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(RevokePermissionEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(GetUserDetailsEventHandler.TYPE, UserController.get(), this));

	}

	@UiHandler("mChangeDetails")
	void onChangeDetailsClicked(ClickEvent event) {
		if (validateDetails()) {
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
			preloaderPassword.show();

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait", " - changing user password...", false)
					.setVisible(true);

			if (SessionController.get().isLoggedInUserAdmin()) {
				UserController.get().setPassword(editingUserId, mNewPassword.getText());
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

	@UiHandler("addRoleButton")
	void onAddRoleButtonClicked(ClickEvent event) {

		if (validateRole()) {
			clearAddRoleErrors();
			preloaderAddRole.show();
			userRolesProvider.updateRowCount(0, false);
			UserController.get().assignUserRoleId(editingUserId, addRoleTextbox.getText().toUpperCase());

		} else {
			if (addRoleError != null) {
				FormHelper.showNote(true, addRoleGroup, addRoleNote, addRoleError);
			} else {
				FormHelper.hideNote(addRoleGroup, addRoleNote);
			}

		}

	}

	@UiHandler("addPermissionButton")
	void onAddPermissionButtonClicked(ClickEvent event) {

		if (validatePermission()) {
			clearAddPermissionErrors();;
			preloaderAddPermission.show();
			userPermissionsProvider.updateRowCount(0, false);
			UserController.get().assignUserPermissionId(editingUserId, addPermissionTextbox.getText().toUpperCase());

		} else {
			if (addPermissionError != null) {
				FormHelper.showNote(true, addPermissionGroup, addPermissionNote, addPermissionError);
			} else {
				FormHelper.hideNote(addPermissionGroup, addPermissionNote);
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

	private void clearAddRoleErrors() {
		FormHelper.hideNote(addRoleGroup, addRoleNote);
	}

	private void clearAddPermissionErrors() {
		FormHelper.hideNote(addPermissionGroup, addPermissionNote);
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

	@UiHandler("addRoleTextbox")
	void onEnterKeyPressAddRoleFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			addRoleButton.click();
		}
	}

	@UiHandler("addPermissionTextbox")
	void onEnterKeyPressAddPermissionFields(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			addPermissionButton.click();
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
	}

	private void resetPasswordForm() {

		mPassword.setText("");

		mNewPassword.setText("");
		mConfirmPassword.setText("");

		mPasswordGroup.setVisible(!SessionController.get().isLoggedInUserAdmin());

		FormHelper.hideNote(mNewPasswordGroup, mNewPasswordNote);
		FormHelper.hideNote(mPasswordGroup, mPasswordNote);

		preloaderPassword.hide();

		// if (SessionController.get().isLoggedInUserAdmin()) {
		// mNewPassword.setFocus(true);
		// } else {
		// mPassword.setFocus(true);
		// }
	}

	private void resetRoleForm() {
		addRoleTextbox.setText("");
		FormHelper.hideNote(addRoleGroup, addRoleNote);
	}

	private void resetPermissionForm() {
		addPermissionTextbox.setText("");
		FormHelper.hideNote(addPermissionGroup, addPermissionNote);
	}

	private void fillDetailsForm(User u) {
		mUsername.setText(u.username);
		mForename.setText(u.forename);
		mSurname.setText(u.surname);
		mCompany.setText(u.company);
	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validateDetails() {
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

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validateRole() {
		boolean validated = true;
		// Retrieve fields to validate
		String role = addRoleTextbox.getText();
		// Check fields constraints
		if (role == null || role.length() == 0) {
			addRoleError = "Cannot be empty";
			validated = false;
		} else if (role.length() != 3) {
			addRoleError = "Must have 3 characters";
			validated = false;
		} else {
			addRoleError = null;
			validated = validated && true;
		}

		return validated;
	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validatePermission() {
		boolean validated = true;
		// Retrieve fields to validate
		String permission = addPermissionTextbox.getText();
		// Check fields constraints
		if (permission == null || permission.length() == 0) {
			addPermissionError = "Cannot be empty";
			validated = false;
		} else if (permission.length() != 3) {
			addPermissionError = "Must have 3 characters";
			validated = false;
		} else {
			addPermissionError = null;
			validated = validated && true;
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

		currentUser = SessionController.get().getLoggedInUser(); // Update user using the system
		mChangeDetails.setEnabled(false);
		mChangePassword.setEnabled(false);
		myAccountSidePanel.setPersonalDetailsLinkActive();

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

		resetForm();
		resetPasswordForm();
		resetRoleForm();
		resetPermissionForm();

		if (isValidStack(current)) {

			boolean editingUserChanged = editingUserId != Long.valueOf(current.getParameter(0));
			if (editingUserChanged) {

				// Create and fetch Roles and Permissions providers
				editingUserId = Long.valueOf(current.getParameter(0)); // Update user to edit
				User dummyEditingUser = DataTypeHelper.createUser(editingUserId);
				userRolesProvider = new UserRolesProvider();
				userRolesProvider.addDataDisplay(rolesTable);
				userPermissionsProvider = new UserPermissionsProvider();
				userPermissionsProvider.addDataDisplay(permissionsTable);
				userRolesProvider.updateRowCount(0, false);
				userPermissionsProvider.updateRowCount(0, false);

				if (SessionController.get().isLoggedInUserAdmin()) {
					UserController.get().fetchUserRolesAndPermissions(dummyEditingUser);
				} else {
					// If non admin, can retrieve only his own powers, so get from SessionController
					List<Role> currentUserRoles = SessionController.get().getLoggedInUser().roles;
					if (currentUserRoles != null) {
						userRolesProvider.updateRowData(0, currentUserRoles);
					} else {
						userRolesProvider.updateRowCount(0, true);
					}
					List<Permission> currentUserPermissions = SessionController.get().getLoggedInUser().permissions;
					if (currentUserPermissions != null) {
						userPermissionsProvider.updateRowData(0, currentUserPermissions);
					} else {
						userPermissionsProvider.updateRowCount(0, true);
					}
				}

				// Fill user details form
				if (currentUser.id.toString().equals(editingUserId.toString())) { // Current user is the same as in the stack parameter
					fillDetailsForm(currentUser);
				} else if (SessionController.get().isLoggedInUserAdmin()) {
					// User editingUser = UserController.get().getUser(editingUserId);
					// if (editingUser != null) { // User already retrieved
					// fillDetailsForm(editingUser);
					// } else { // Coming from a page refreshing
					preloaderDetails.show();
					UserController.get().fetchUser(editingUserId);
					// }
				} else { // No access to this user
					userRolesProvider.updateRowCount(0, true);
					userPermissionsProvider.updateRowCount(0, true);
				}
				mForename.setFocus(true);
				mForename.setCursorPos(mForename.getText().length());

			}

		} else {

			userRolesProvider.updateRowCount(0, true);
			userPermissionsProvider.updateRowCount(0, true);

		}
	}

	private boolean isValidStack(Stack current) {
		return (current != null && PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.ChangeDetailsPageType.equals(current.getAction()) && current.getParameter(0) != null && (current.getParameter(0).equals(
				currentUser.id.toString()) || SessionController.get().isLoggedInUserAdmin()));
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
					PageType.UsersPageType.show("view");
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

	private void addPlaceholders() {
		mUsername.getElement().setAttribute("placeholder", "Email Address");
		mForename.getElement().setAttribute("placeholder", "First name");
		mSurname.getElement().setAttribute("placeholder", "Last name");
		mCompany.getElement().setAttribute("placeholder", "Company");

		mPassword.getElement().setAttribute("placeholder", "Current password");
		mNewPassword.getElement().setAttribute("placeholder", "New password");
		mConfirmPassword.getElement().setAttribute("placeholder", "Confirm new password");

		addRoleTextbox.getElement().setAttribute("placeholder", "ROL");
		addPermissionTextbox.getElement().setAttribute("placeholder", "PRM");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleSuccess(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * io.reflection.app.api.admin.shared.call.AssignRoleResponse)
	 */
	@Override
	public void assignRoleSuccess(AssignRoleRequest input, AssignRoleResponse output) {
		UserController.get().fetchUserRolesAndPermissions(input.user);
		preloaderAddRole.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleFailure(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void assignRoleFailure(AssignRoleRequest input, Throwable caught) {
		UserController.get().fetchUserRolesAndPermissions(input.user);
		preloaderAddRole.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler#assignPermissionSuccess(io.reflection.app.api.admin.shared.call.
	 * AssignPermissionRequest, io.reflection.app.api.admin.shared.call.AssignPermissionResponse)
	 */
	@Override
	public void assignPermissionSuccess(AssignPermissionRequest input, AssignPermissionResponse output) {
		UserController.get().fetchUserRolesAndPermissions(input.user);
		preloaderAddPermission.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler#assignPermissionFailure(io.reflection.app.api.admin.shared.call.
	 * AssignPermissionRequest, java.lang.Throwable)
	 */
	@Override
	public void assignPermissionFailure(AssignPermissionRequest input, Throwable caught) {
		UserController.get().fetchUserRolesAndPermissions(input.user);
		preloaderAddPermission.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetRolesAndPermissionsEventHandler#getRolesAndPermissionsSuccess(io.reflection.app.api.admin.shared.call
	 * .GetRolesAndPermissionsRequest, io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsResponse)
	 */
	@Override
	public void getRolesAndPermissionsSuccess(GetRolesAndPermissionsRequest input, GetRolesAndPermissionsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			// Roles and Permissions must be retrieved with one call
			if (output.roles != null) {
				userRolesProvider.updateRowData(0, output.roles);
			} else {
				userRolesProvider.updateRowCount(0, true);
			}
			if (output.permissions != null) {
				userPermissionsProvider.updateRowData(0, output.permissions);
			} else {
				userPermissionsProvider.updateRowCount(0, true);
			}
		} else {
			userRolesProvider.updateRowCount(0, true);
			userPermissionsProvider.updateRowCount(0, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetRolesAndPermissionsEventHandler#getRolesAndPermissionsFailure(io.reflection.app.api.admin.shared.call
	 * .GetRolesAndPermissionsRequest, java.lang.Throwable)
	 */
	@Override
	public void getRolesAndPermissionsFailure(GetRolesAndPermissionsRequest input, Throwable caught) {
		userRolesProvider.updateRowCount(0, true);
		userPermissionsProvider.updateRowCount(0, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.RevokePermissionEventHandler#revokePermissionSuccess(io.reflection.app.api.admin.shared.call.
	 * RevokePermissionRequest, io.reflection.app.api.admin.shared.call.RevokePermissionResponse)
	 */
	@Override
	public void revokePermissionSuccess(RevokePermissionRequest input, RevokePermissionResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			UserController.get().fetchUserRolesAndPermissions(input.user);
		} else {
			List<Permission> currentUserPermissions = SessionController.get().getLoggedInUser().permissions;
			if (currentUserPermissions != null) {
				userPermissionsProvider.updateRowData(0, currentUserPermissions);
			} else {
				userPermissionsProvider.updateRowCount(0, true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.RevokePermissionEventHandler#revokePermissionFailure(io.reflection.app.api.admin.shared.call.
	 * RevokePermissionRequest, java.lang.Throwable)
	 */
	@Override
	public void revokePermissionFailure(RevokePermissionRequest input, Throwable caught) {
		List<Permission> currentUserPermissions = SessionController.get().getLoggedInUser().permissions;
		if (currentUserPermissions != null) {
			userPermissionsProvider.updateRowData(0, currentUserPermissions);
		} else {
			userPermissionsProvider.updateRowCount(0, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.RevokeRoleEventHandler#revokeRoleSuccess(io.reflection.app.api.admin.shared.call.RevokeRoleRequest,
	 * io.reflection.app.api.admin.shared.call.RevokeRoleResponse)
	 */
	@Override
	public void revokeRoleSuccess(RevokeRoleRequest input, RevokeRoleResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			UserController.get().fetchUserRolesAndPermissions(input.user);
		} else {
			List<Role> currentUserRoles = SessionController.get().getLoggedInUser().roles;
			if (currentUserRoles != null) {
				userRolesProvider.updateRowData(0, currentUserRoles);
			} else {
				userRolesProvider.updateRowCount(0, true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.RevokeRoleEventHandler#revokeRoleFailure(io.reflection.app.api.admin.shared.call.RevokeRoleRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void revokeRoleFailure(RevokeRoleRequest input, Throwable caught) {
		List<Role> currentUserRoles = SessionController.get().getLoggedInUser().roles;
		if (currentUserRoles != null) {
			userRolesProvider.updateRowData(0, currentUserRoles);
		} else {
			userRolesProvider.updateRowCount(0, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler#getUserDetailsSuccess(io.reflection.app.api.core.shared.call.GetUserDetailsRequest
	 * , io.reflection.app.api.core.shared.call.GetUserDetailsResponse)
	 */
	@Override
	public void getUserDetailsSuccess(GetUserDetailsRequest input, GetUserDetailsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.user != null) {
			fillDetailsForm(output.user);
		}
		preloaderDetails.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler#getUserDetailsFailure(io.reflection.app.api.core.shared.call.GetUserDetailsRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void getUserDetailsFailure(GetUserDetailsRequest input, Throwable caught) {
		preloaderDetails.hide();
	}

}
