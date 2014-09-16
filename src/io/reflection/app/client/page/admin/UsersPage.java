//
//  UsersPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsResponse;
import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetRolesResponse;
import io.reflection.app.api.admin.shared.call.event.GetPermissionsEventHandler;
import io.reflection.app.api.admin.shared.call.event.GetRolesEventHandler;
import io.reflection.app.api.blog.shared.call.DeleteUserRequest;
import io.reflection.app.api.blog.shared.call.DeleteUserResponse;
import io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.PermissionController;
import io.reflection.app.client.controller.RoleController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ConfirmationDialog;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class UsersPage extends Page implements DeleteUserEventHandler, GetRolesEventHandler, GetPermissionsEventHandler {

	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);

	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField(provided = true) CellTable<User> mUsers = new CellTable<User>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	private ConfirmationDialog confirmationDialog;
	@UiField Preloader preloader;

	private ListBox roles = new ListBox();
	private ListBox permissions = new ListBox();

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mUsers.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		RoleController.get().fetchRoles();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(DeleteUserEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(GetRolesEventHandler.TYPE, RoleController.get(), this));
		register(EventController.get().addHandlerToSource(GetPermissionsEventHandler.TYPE, PermissionController.get(), this));
	}

	private void createColumns() {
		TextColumn<User> name = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return FormattingHelper.getUserName(object);
			}

		};

		TextHeader nameHeader = new TextHeader("Name");
		nameHeader.setHeaderStyleNames("col-md-1");
		mUsers.addColumn(name, nameHeader);

		TextColumn<User> company = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return object.company;
			}

		};

		TextHeader companyHeader = new TextHeader("Company");
		companyHeader.setHeaderStyleNames("col-md-1");
		mUsers.addColumn(company, companyHeader);

		Column<User, SafeHtml> email = new Column<User, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(User object) {
				String s = SafeHtmlUtils.htmlEscape(object.username);

				return SafeHtmlUtils.fromTrustedString("<a href=\"mailto:" + s + "\">" + s + "</a>");
			}
		};

		TextHeader emailHeader = new TextHeader("E-mail");
		emailHeader.setHeaderStyleNames("col-md-3");
		mUsers.addColumn(email, emailHeader);

		SafeHtmlCell prototype = new SafeHtmlCell();
		Column<User, SafeHtml> assignPassword = new Column<User, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(User object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.UsersPageType.asHref("changepassword", object.id.toString()).asString()
						+ "\" class=\"btn btn-xs btn-default\">Assign password</a>");
			}
		};

		Column<User, SafeHtml> changeDetails = new Column<User, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(User object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.UsersPageType.asHref("changedetails", object.id.toString()).asString()
						+ "\" class=\"btn btn-xs btn-default\">Change details</a>");
			}
		};

		Column<User, SafeHtml> changeRole = new Column<User, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(User object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.UsersPageType.asHref("changepassword", object.id.toString()).asString()
						+ "\" class=\"btn btn-xs btn-default\">Change role</a> " + roles);
			}
		};

		Column<User, SafeHtml> changePermission = new Column<User, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(User object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.UsersPageType.asHref("changepassword", object.id.toString()).asString()
						+ "\" class=\"btn btn-xs btn-default\">Change permission</a> " + permissions);
			}
		};

		FieldUpdater<User, String> action = new FieldUpdater<User, String>() {

			@Override
			public void update(int index, final User object, String value) {
				switch (value) {
				case "Make admin":
					UserController.get().makeAdmin(object.id);
					break;
				case "Add to beta":
					UserController.get().makeBeta(object.id);
					break;
				case "Delete":
					confirmationDialog = new ConfirmationDialog("Delete " + object.forename + " " + object.surname + "\n" + object.company,
							"Are you sure you want to remove " + object.username + " ?");
					confirmationDialog.center();
					confirmationDialog.setParameter(object.id);

					confirmationDialog.getCancelButton().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							confirmationDialog.reset();
						}
					});

					confirmationDialog.getDeleteButton().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							preloader.show();
							UserController.get().deleteUser(object.id);
							confirmationDialog.reset();
						}
					});

					break;
				}
			}
		};

		StyledButtonCell prototype1 = new StyledButtonCell("btn", "btn-xs", "btn-default");
		Column<User, String> makeAdmin = new Column<User, String>(prototype1) {

			@Override
			public String getValue(User object) {
				return "Make admin";
			}
		};
		makeAdmin.setFieldUpdater(action);

		Column<User, String> addToBeta = new Column<User, String>(prototype1) {

			@Override
			public String getValue(User object) {
				return "Add to beta";
			}
		};
		addToBeta.setFieldUpdater(action);

		StyledButtonCell prototype2 = new StyledButtonCell("btn", "btn-xs", "btn-danger");
		Column<User, String> delete = new Column<User, String>(prototype2) {

			@Override
			public String getValue(User object) {
				return "Delete";
			}
		};
		delete.setFieldUpdater(action);

		mUsers.addColumn(assignPassword);
		mUsers.addColumn(changeDetails);
		mUsers.addColumn(changeRole);
		mUsers.addColumn(changePermission);
		mUsers.addColumn(makeAdmin);
		mUsers.addColumn(addToBeta);
		mUsers.addColumn(delete);
	}

	//
	// String userId = mStack.getParameter(0);
	// String roleName = mStack.getParameter(1);
	//
	// // TODO: this should not really be here (and the navigation controller should probably not be responsible for actions)
	// if (userId != null) {
	// if (roleName.equalsIgnoreCase("admin")) {
	// UserController.get().makeAdmin(Long.valueOf(userId));
	// } else if (roleName.equals("beta")) {
	// UserController.get().makeBeta(Long.valueOf(userId));
	// }
	// }
	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler#deleteUserSuccess(io.reflection.app.api.blog.shared.call.DeleteUserRequest,
	 * io.reflection.app.api.blog.shared.call.DeleteUserResponse)
	 */
	@Override
	public void deleteUserSuccess(DeleteUserRequest input, DeleteUserResponse output) {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler#deleteUserFailure(io.reflection.app.api.blog.shared.call.DeleteUserRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void deleteUserFailure(DeleteUserRequest input, Throwable caught) {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetRolesEventHandler#getRolesSuccess(io.reflection.app.api.admin.shared.call.GetRolesRequest,
	 * io.reflection.app.api.admin.shared.call.GetRolesResponse)
	 */
	@Override
	public void getRolesSuccess(GetRolesRequest input, GetRolesResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			for (Role r : output.roles) {
				roles.addItem(r.name);
			}
			PermissionController.get().fetchPermissions();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetRolesEventHandler#getRolesFailure(io.reflection.app.api.admin.shared.call.GetRolesRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getRolesFailure(GetRolesRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetPermissionsEventHandler#getPermissionsSuccess(io.reflection.app.api.admin.shared.call.GetPermissionsRequest
	 * , io.reflection.app.api.admin.shared.call.GetPermissionsResponse)
	 */
	@Override
	public void getPermissionsSuccess(GetPermissionsRequest input, GetPermissionsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			for (Permission p : output.permissions) {
				permissions.addItem(p.name);
			}
			createColumns();
			UserController.get().addDataDisplay(mUsers);
			mPager.setDisplay(mUsers);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetPermissionsEventHandler#getPermissionsFailure(io.reflection.app.api.admin.shared.call.GetPermissionsRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void getPermissionsFailure(GetPermissionsRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

}
