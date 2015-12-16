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
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.LoadingButton.IResetStatusCallback;
import io.reflection.app.client.component.PasswordField;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.dataprovider.UserPermissionsProvider;
import io.reflection.app.client.dataprovider.UserRolesProvider;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.UserPasswordChangedEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
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

	@UiField LIElement accountSettingsItem;
	@UiField LIElement manageSubscriptionItem;
	@UiField LIElement usersItem;
	@UiField LIElement notificationsItem;
	@UiField SpanElement usersText;
	@UiField SpanElement notifText;

	@UiField InlineHyperlink accountSettingsLink;
	@UiField InlineHyperlink manageSubscriptionLink;
	@UiField InlineHyperlink usersLink;
	@UiField InlineHyperlink notificationsLink;

	@UiField TextField username;
	private String usernameError;

	@UiField TextField forename;
	private String forenameError;

	@UiField TextField surname;
	private String surnameError;

	@UiField TextField company;
	private String companyError;

	@UiField LoadingButton changeDetailsBtn;

	// Change Password
	@UiField PasswordField password;

	@UiField(provided = true) PasswordField newPassword = new PasswordField(true);
	@UiField PasswordField confirmPassword;

	@UiField LoadingButton changePasswordBtn;

	@UiField ParagraphElement changePasswordGeneralErrorParagraph;
	private String changePasswordGeneralErrorNote = "";

	// Error definition during validation
	private String passwordError = null;
	private String newPasswordError = null;

	@UiField DivElement userCredentialsPanel;
	// User Roles
	@UiField TextField addRole;
	private String addRoleError;

	@UiField HTMLPanel addRolePanel;
	@UiField LoadingButton addRoleBtn;

	// User Permissions
	@UiField TextField addPermission;
	private String addPermissionError;

	@UiField HTMLPanel addPermissionPanel;
	@UiField LoadingButton addPermissionBtn;

	private User currentUser; // User using the system
	private User editingUser; // User being edited
	private Long editingUserId; // User being edited ID

	private LoadingBar loadingBar = new LoadingBar(false);

	public ChangeDetailsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		User user = SessionController.get().getLoggedInUser();
		usersItem.removeFromParent();
		notificationsItem.removeFromParent();
		if (!SessionController.get().isAdmin()) {
			userCredentialsPanel.removeFromParent();
			// usersText.setInnerHTML("Users <span class=\"text-small\">coming soon</span>");
			// usersItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			// usersItem.getStyle().setCursor(Cursor.DEFAULT);
			// notifText.setInnerHTML("Manage Notifications <span class=\"text-small\">coming soon</span>");
			// notificationsItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			// notificationsItem.getStyle().setCursor(Cursor.DEFAULT);
			// usersLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			// notificationsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		} else {
			// if (user != null) {
			// notificationsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.NotificationsPageType.toString(),
			// user.id.toString()));
			// }
		}

		if (user != null) {
			manageSubscriptionLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ManageSubscriptionPageType.toString(),
					user.id.toString()));
		}

		addRoleColumns(SessionController.get().isAdmin());
		addPermissionColumns(SessionController.get().isAdmin());

		HTMLPanel emptyRowRoles = new HTMLPanel("-");
		HTMLPanel emptyRowPermissions = new HTMLPanel("-");
		emptyRowRoles.setHeight("23px");
		emptyRowPermissions.setHeight("23px");
		rolesTable.setEmptyTableWidget(emptyRowRoles);
		permissionsTable.setEmptyTableWidget(emptyRowPermissions);
		rolesTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		permissionsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		// Add click event to LI element so the event is fired when clicking on the whole tab
		Event.sinkEvents(accountSettingsItem, Event.ONCLICK);
		Event.sinkEvents(manageSubscriptionItem, Event.ONCLICK);
		// Event.sinkEvents(notificationsItem, Event.ONCLICK);
		// Event.sinkEvents(usersItem, Event.ONCLICK);
		Event.setEventListener(accountSettingsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(accountSettingsLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(manageSubscriptionItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(manageSubscriptionLink.getTargetHistoryToken());
				}
			}
		});
		// Event.setEventListener(notificationsItem, new EventListener() {
		//
		// @Override
		// public void onBrowserEvent(Event event) {
		// if (Event.ONCLICK == event.getTypeInt()) {
		// History.newItem(notificationsLink.getTargetHistoryToken());
		// }
		// }
		// });
		// Event.setEventListener(usersItem, new EventListener() {
		//
		// @Override
		// public void onBrowserEvent(Event event) {
		// if (Event.ONCLICK == event.getTypeInt()) {
		// History.newItem(usersLink.getTargetHistoryToken());
		// }
		// }
		// });
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

			Column<Role, String> removeRole = new Column<Role, String>(new StyledButtonCell(Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonLink())) {

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

			Column<Permission, String> removePermission = new Column<Permission, String>(new StyledButtonCell(Styles.STYLES_INSTANCE.reflectionMainStyle()
					.refButtonLink())) {

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

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(ChangeUserDetailsEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UserPasswordChangedEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetRolesAndPermissionsEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(AssignRoleEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(AssignPermissionEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(RevokeRoleEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(RevokePermissionEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetUserDetailsEventHandler.TYPE, UserController.get(), this));

		ResponsiveDesignHelper.makeTabsResponsive();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		loadingBar.reset();
	}

	private void setUserDetailsFormEnabled(boolean enabled) {
		username.setEnabled(enabled);
		forename.setEnabled(enabled);
		surname.setEnabled(enabled);
		company.setEnabled(enabled);
	}

	private void setChangePasswordFormEnabled(boolean enabled) {
		password.setEnabled(enabled);
		newPassword.setEnabled(enabled);
		confirmPassword.setEnabled(enabled);
	}

	@UiHandler("changeDetailsBtn")
	void onChangeDetailsClicked(ClickEvent event) {
		if (validateDetails()) {
			clearDetailsErrors();
			changeDetailsBtn.setStatusLoading("Changing details");
			setUserDetailsFormEnabled(false);
			SessionController.get().changeUserDetails(editingUserId, username.getText(), forename.getText(), surname.getText(), company.getText());
		} else {
			if (usernameError != null) {
				username.showNote(usernameError, true);
			} else {
				username.hideNote();
			}

			if (forenameError != null) {
				forename.showNote(forenameError, true);
			} else {
				forename.hideNote();
			}

			if (surnameError != null) {
				surname.showNote(surnameError, true);
			} else {
				surname.hideNote();
			}

			if (companyError != null) {
				company.showNote(companyError, true);
			} else {
				company.hideNote();
			}
		}
	}

	@UiHandler("changePasswordBtn")
	void onChangePassword(ClickEvent event) {

		if (validatePassword()) {
			clearPasswordErrors();
			changePasswordBtn.setStatusLoading("Changing password");
			setChangePasswordFormEnabled(false);
			if (SessionController.get().isAdmin()) {
				UserController.get().setPassword(editingUserId, newPassword.getText());
			} else {
				SessionController.get().changePassword(password.getText(), newPassword.getText());
			}
		} else {
			if (passwordError != null) {
				password.showNote(passwordError, true);
			} else {
				password.hideNote();
			}
			if (newPasswordError != null) {
				newPassword.showNote(newPasswordError, true);
				confirmPassword.showNote(newPasswordError, true);
			} else {
				newPassword.hideNote();
				confirmPassword.hideNote();
			}
			changePasswordGeneralErrorParagraph.setInnerText(changePasswordGeneralErrorNote);
			changePasswordBtn.setStatusError(changePasswordGeneralErrorNote.equals(FormHelper.ERROR_FORM_EMPTY_FIELDS) ? FormHelper.ERROR_BUTTON_INCOMPLETE
					: FormHelper.ERROR_BUTTON_WRONG);
			changePasswordGeneralErrorNote = "";
		}
	}

	@UiHandler("addRoleBtn")
	void onAddRoleButtonClicked(ClickEvent event) {

		if (validateRole()) {
			clearAddRoleErrors();
			addRoleBtn.setStatusLoading("Adding role ..");
			userRolesProvider.updateRowCount(0, false);
			UserController.get().assignUserRoleCode(editingUserId, addRole.getText().toUpperCase());

		} else {
			if (addRoleError != null) {
				addRole.showNote(addRoleError, true);
			} else {
				clearAddRoleErrors();
			}

		}

	}

	@UiHandler("addPermissionBtn")
	void onAddPermissionButtonClicked(ClickEvent event) {

		if (validatePermission()) {
			clearAddPermissionErrors();;
			addPermissionBtn.setStatusLoading("Adding permission ..");
			userPermissionsProvider.updateRowCount(0, false);
			UserController.get().assignUserPermissionCode(editingUserId, addPermission.getText().toUpperCase());

		} else {
			if (addPermissionError != null) {
				addPermission.showNote(addPermissionError, true);
			} else {
				clearAddPermissionErrors();
			}

		}

	}

	private void clearDetailsErrors() {
		username.hideNote();
		forename.hideNote();
		surname.hideNote();
		company.hideNote();
	}

	private void clearPasswordErrors() {
		password.hideNote();
		newPassword.hideNote();
		confirmPassword.hideNote();
		changePasswordGeneralErrorParagraph.setInnerText("");
	}

	private void clearAddRoleErrors() {
		addRole.hideNote();
	}

	private void clearAddPermissionErrors() {
		addPermission.hideNote();
	}

	/**
	 * Fire the change details button when pressing the 'enter' key on one of the change details form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "username", "forename", "surname", "company" })
	void onEnterKeyDownChangeDetailsFields(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			changeDetailsBtn.click();
		}
	}

	@UiHandler({ "username", "forename", "surname", "company" })
	void onDetailsFieldsModified(KeyUpEvent event) {
		if (changeDetailsBtn.isStatusDefault()) {
			if (!username.getText().equals(editingUser.username) || !forename.getText().equals(editingUser.forename)
					|| !surname.getText().equals(editingUser.surname) || !company.getText().equals(editingUser.company)) {
				changeDetailsBtn.setEnabled(true);
			} else {
				changeDetailsBtn.setEnabled(false);
			}
		}
	}

	/**
	 * Fire the change password button when pressing the 'enter' key on one of the change password form fields
	 * 
	 * @param event
	 */
	@UiHandler({ "password", "newPassword", "confirmPassword" })
	void onEnterKeyDownChangePasswordFields(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			changePasswordBtn.click();
		}
	}

	@UiHandler({ "password", "newPassword", "confirmPassword" })
	void onPasswordFieldsModified(KeyUpEvent event) {
		if (changePasswordBtn.isStatusDefault()) {
			if (!SessionController.get().isAdmin()) {
				if (!password.getText().isEmpty() && !newPassword.getText().isEmpty() && !confirmPassword.getText().isEmpty()) {
					changePasswordBtn.setEnabled(true);
				} else {
					changePasswordBtn.setEnabled(false);
				}
			} else {
				if (!newPassword.getText().isEmpty() && !confirmPassword.getText().isEmpty()) {
					changePasswordBtn.setEnabled(true);
				} else {
					changePasswordBtn.setEnabled(false);
				}
			}
		}
	}

	@UiHandler("addRole")
	void onEnterKeyDownAddRoleFields(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			addRoleBtn.click();
		}
	}

	@UiHandler("addPermission")
	void onEnterKeyDownAddPermissionFields(KeyDownEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			addPermissionBtn.click();
		}
	}

	private void resetForm() {
		username.setText("");
		forename.setText("");
		surname.setText("");
		company.setText("");

		clearDetailsErrors();
	}

	private void resetPasswordForm() {

		password.clear();
		newPassword.clear();
		confirmPassword.clear();

		password.setVisible(!SessionController.get().isAdmin());

		// preloaderPassword.hide();

		// if (SessionController.get().isLoggedInUserAdmin()) {
		// mNewPassword.setFocus(true);
		// } else {
		// mPassword.setFocus(true);
		// }
	}

	private void resetRoleForm() {
		addRole.setText("");
		clearAddRoleErrors();
	}

	private void resetPermissionForm() {
		addPermission.setText("");
		clearAddPermissionErrors();
	}

	private void fillDetailsForm(User u) {
		username.setText(u.username);
		forename.setText(u.forename);
		surname.setText(u.surname);
		company.setText(u.company);
	}

	/**
	 * Check if every field of the form is valid and return true
	 * 
	 * @return Boolean validated
	 */
	private boolean validateDetails() {
		boolean validated = true;
		// Retrieve fields to validate
		String forenameText = forename.getText();
		String surnameText = surname.getText();
		String companyText = company.getText();
		String usernameText = username.getText();
		// Check fields constraints
		if (usernameText == null || usernameText.length() == 0) {
			usernameError = FormHelper.ERROR_EMAIL_EMPTY;
			validated = false;
		} else if (usernameText.length() > 255) {
			usernameError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!usernameText.contains("@")) {
			usernameError = FormHelper.ERROR_EMAIL_MISSING_AT;
			validated = false;
		} else if (!FormHelper.isValidEmail(usernameText)) {
			usernameError = FormHelper.ERROR_EMAIL_WRONG;
			validated = false;
		} else {
			usernameError = null;
			validated = validated && true;
		}

		if (forenameText == null || forenameText.length() == 0) {
			forenameError = FormHelper.ERROR_FIRST_NAME_EMPTY;
			validated = false;
		} else if (forenameText.length() < 2) {
			forenameError = FormHelper.ERROR_NAME_SHORT;
			validated = false;
		} else if (forenameText.length() > 30) {
			forenameError = "Too long (maximum 30 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(forenameText)) {
			forenameError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			forenameError = null;
			validated = validated && true;
		}

		if (surnameText == null || surnameText.length() == 0) {
			surnameError = FormHelper.ERROR_LAST_NAME_EMPTY;
			validated = false;
		} else if (surnameText.length() < 2) {
			surnameError = FormHelper.ERROR_NAME_SHORT;
			validated = false;
		} else if (surnameText.length() > 30) {
			surnameError = "Too long (maximum 30 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(surnameText)) {
			surnameError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			surnameError = null;
			validated = validated && true;
		}

		if (companyText == null || companyText.length() == 0) {
			companyError = FormHelper.ERROR_COMPANY_EMPTY;
			validated = false;
		} else if (companyText.length() < 2) {
			companyError = FormHelper.ERROR_COMPANY_SHORT;
			validated = false;
		} else if (companyText.length() > 255) {
			companyError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isTrimmed(companyText)) {
			companyError = "Whitespaces not allowed either before or after the string";
			validated = false;
		} else {
			companyError = null;
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
		String newPasswordText = newPassword.getText();
		String confirmPasswordText = confirmPassword.getText();
		String passwordText = password.getText();
		// Check password constraints for normal user
		if (newPasswordText == null || newPasswordText.length() == 0) {
			newPasswordError = FormHelper.ERROR_PASSWORD_CREATE_EMPTY;
			changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
			validated = false;
		} else if (newPasswordText.length() < 6) {
			newPasswordError = FormHelper.ERROR_PASSWORD_CREATE_SHORT;
			changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (newPasswordText.length() > 64) {
			newPasswordError = "Too long (maximum 64 characters)";
			changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!newPasswordText.equals(confirmPasswordText)) {
			newPasswordError = FormHelper.ERROR_PASSWORD_CREATE_CONFIRMATION_MATCH;
			changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else if (!FormHelper.isTrimmed(newPasswordText)) {
			newPasswordError = "Whitespaces not allowed either before or after the string";
			changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
			validated = false;
		} else {
			newPasswordError = null;
			validated = validated && true;
		}
		// Check password constraints for not admin user
		if (!SessionController.get().isAdmin()) {
			if (passwordText == null || passwordText.length() == 0) {
				passwordError = FormHelper.ERROR_PASSWORD_LOGIN_EMPTY;
				changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_EMPTY_FIELDS;
				validated = false;
			} else if (passwordText.length() < 6) {
				passwordError = FormHelper.ERROR_PASSWORD_CREATE_SHORT;
				changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
				validated = false;
			} else if (passwordText.length() > 64) {
				passwordError = "Too long (maximum 64 characters)";
				changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
				validated = false;
			} else if (!FormHelper.isTrimmed(passwordText)) {
				passwordError = "Whitespaces not allowed either before or after the string";
				changePasswordGeneralErrorNote = FormHelper.ERROR_FORM_WRONG_FIELDS;
				validated = false;
			} else {
				passwordError = null;
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
		String roleText = addRole.getText();
		// Check fields constraints
		if (roleText == null || roleText.length() == 0) {
			addRoleError = "Cannot be empty";
			validated = false;
		} else if (roleText.length() != 3) {
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
		String permissionText = addPermission.getText();
		// Check fields constraints
		if (permissionText == null || permissionText.length() == 0) {
			addPermissionError = "Cannot be empty";
			validated = false;
		} else if (permissionText.length() != 3) {
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
		changeDetailsBtn.setEnabled(false);
		changePasswordBtn.setEnabled(false);

		if (isValidStack(current)) {
			accountSettingsLink.setTargetHistoryToken(current.toString());

			boolean editingUserChanged = editingUserId != Long.valueOf(current.getParameter(0));
			if (editingUserChanged) {
				resetForm();
				resetPasswordForm();
				resetRoleForm();
				resetPermissionForm();

				// Create and fetch Roles and Permissions providers
				editingUserId = Long.valueOf(current.getParameter(0)); // Update user to edit
				User dummyEditingUser = DataTypeHelper.createUser(editingUserId);
				userRolesProvider = new UserRolesProvider();
				userRolesProvider.addDataDisplay(rolesTable);
				userPermissionsProvider = new UserPermissionsProvider();
				userPermissionsProvider.addDataDisplay(permissionsTable);
				userRolesProvider.updateRowCount(0, false);
				userPermissionsProvider.updateRowCount(0, false);

				if (SessionController.get().isAdmin()) {
					UserController.get().fetchAdminRolesAndPermissions(dummyEditingUser);
					loadingBar.show("Getting credentials ..");
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
					editingUser = currentUser;
					fillDetailsForm(editingUser);
				} else if (SessionController.get().isAdmin()) {
					UserController.get().fetchUser(editingUserId);
				} else { // No access to this user
					userRolesProvider.updateRowCount(0, true);
					userPermissionsProvider.updateRowCount(0, true);
				}

			}

		} else {
			userRolesProvider.updateRowCount(0, true);
			userPermissionsProvider.updateRowCount(0, true);
		}
	}

	private boolean isValidStack(Stack current) {
		return (current != null && PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.ChangeDetailsPageType.equals(current.getAction()) && current.getParameter(0) != null && (current.getParameter(0).equals(
				currentUser.id.toString()) || SessionController.get().isAdmin()));
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

			changeDetailsBtn.setEnabled(false);
			forename.setFocus(true);
			changeDetailsBtn.setStatusSuccess("Details changed!", new IResetStatusCallback() {

				@Override
				public void onResetStatus() {
					setUserDetailsFormEnabled(true);
					changeDetailsBtn.setEnabled(false);
				}
			});
		} else {
			changeDetailsBtn.setStatusError(new IResetStatusCallback() {

				@Override
				public void onResetStatus() {
					fillDetailsForm(editingUser); // Restore previous values
					setUserDetailsFormEnabled(true);
					changeDetailsBtn.setEnabled(false);
				}
			});
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsFailure(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, java.lang.Throwable)
	 */
	@Override
	public void changeUserDetailsFailure(ChangeUserDetailsRequest input, Throwable caught) {
		changeDetailsBtn.setStatusError(new IResetStatusCallback() {

			@Override
			public void onResetStatus() {
				fillDetailsForm(editingUser); // Restore previous values
				setUserDetailsFormEnabled(true);
				changeDetailsBtn.setEnabled(false);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChanged(java.lang.Long)
	 */
	@Override
	public void userPasswordChanged(Long userId) {

		resetPasswordForm();
		setChangePasswordFormEnabled(true);
		changePasswordBtn.setStatusSuccess("Password changed!", new IResetStatusCallback() {

			@Override
			public void onResetStatus() {
				changePasswordBtn.setEnabled(false);
				if (SessionController.get().isAdmin()) {
					PageType.UsersPageType.show();
				}
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPasswordChangedEventHandler#userPasswordChangeFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userPasswordChangeFailed(Error error) {

		if (error.code.intValue() == ApiError.IncorrectPasswordForChange.getCode()) {
			passwordError = "Hang on...that's not your existing password, please try again";
		} else if (error.code.intValue() == ApiError.InvalidPasswordSameAsCurrent.getCode()) {
			newPasswordError = "Sorry, you can't use the same password, please enter a new one";
		}
		if (passwordError != null) {
			password.showNote(passwordError, true);
		} else {
			password.hideNote();
		}
		if (newPasswordError != null) {
			newPassword.showNote(newPasswordError, true);
		} else {
			newPassword.hideNote();
		}
		setChangePasswordFormEnabled(true);

		changePasswordBtn.setStatusError();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleSuccess(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * io.reflection.app.api.admin.shared.call.AssignRoleResponse)
	 */
	@Override
	public void assignRoleSuccess(AssignRoleRequest input, AssignRoleResponse output) {
		UserController.get().fetchAdminRolesAndPermissions(input.user);
		loadingBar.show("Getting credentials ..");
		if (output.status == StatusType.StatusTypeSuccess) {
			addRoleBtn.setStatusSuccess("Role added!");
		} else {
			addRoleBtn.setStatusError();
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
		UserController.get().fetchAdminRolesAndPermissions(input.user);
		loadingBar.show("Getting credentials ..");
		addRoleBtn.setStatusError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignPermissionEventHandler#assignPermissionSuccess(io.reflection.app.api.admin.shared.call.
	 * AssignPermissionRequest, io.reflection.app.api.admin.shared.call.AssignPermissionResponse)
	 */
	@Override
	public void assignPermissionSuccess(AssignPermissionRequest input, AssignPermissionResponse output) {
		UserController.get().fetchAdminRolesAndPermissions(input.user);
		loadingBar.show("Getting credentials ..");
		if (output.status == StatusType.StatusTypeSuccess) {
			addPermissionBtn.setStatusSuccess("Permission added!");
		} else {
			addPermissionBtn.setStatusError();
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
		UserController.get().fetchAdminRolesAndPermissions(input.user);
		loadingBar.show("Getting credentials ..");
		addPermissionBtn.setStatusError();
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
			loadingBar.hide(true);
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
			loadingBar.hide(false);
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
		loadingBar.hide(false);
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
			UserController.get().fetchAdminRolesAndPermissions(input.user);
			loadingBar.show("Getting credentials ..");
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
			UserController.get().fetchAdminRolesAndPermissions(input.user);
			loadingBar.show("Getting credentials ..");
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
			editingUser = output.user;
			fillDetailsForm(editingUser);

		} else {

		}

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

	}

}
