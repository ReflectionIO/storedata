//
//  UsersPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.DeleteUsersRequest;
import io.reflection.app.api.admin.shared.call.DeleteUsersResponse;
import io.reflection.app.api.admin.shared.call.event.DeleteUsersEventHandler;
import io.reflection.app.api.blog.shared.call.DeleteUserRequest;
import io.reflection.app.api.blog.shared.call.DeleteUserResponse;
import io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ConfirmationDialog;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class UsersPage extends Page implements DeleteUserEventHandler, DeleteUsersEventHandler {

	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);

	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField(provided = true) CellTable<User> usersTable = new CellTable<User>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	private ConfirmationDialog confirmationDialog;
	@UiField Preloader preloader;
	@UiField Button deleteTestUsers;
	@UiField TextBox queryTextBox;
	private String query = "";

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		usersTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		usersTable.setEmptyTableWidget(new HTMLPanel("No Users found!"));
		UserController.get().addDataDisplay(usersTable);
		simplePager.setDisplay(usersTable);

		queryTextBox.getElement().setAttribute("placeholder", "Find a user");

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
		register(EventController.get().addHandlerToSource(DeleteUsersEventHandler.TYPE, UserController.get(), this));
	}

	private void createColumns() {

		TextColumn<User> id = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return object.id.toString();
			}

		};
		usersTable.addColumn(id, "Id");

		TextColumn<User> name = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return FormattingHelper.getUserName(object);
			}

		};
		usersTable.addColumn(name, "Name");

		TextColumn<User> company = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return object.company;
			}

		};
		usersTable.addColumn(company, "Company");

		Column<User, SafeHtml> email = new Column<User, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(User object) {
				String s = SafeHtmlUtils.htmlEscape(object.username);

				return SafeHtmlUtils.fromTrustedString("<a href=\"mailto:" + s + "\">" + s + "</a>");
			}
		};
		usersTable.addColumn(email, "E-mail");

		final DateTimeFormat dtf = DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_DD_MMM_YYYY_HH_MM);
		TextColumn<User> lastLoginColumn = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return (object.lastLoggedIn == null) ? "-" : dtf.format(object.lastLoggedIn);
			}

		};
		usersTable.addColumn(lastLoginColumn, "Last login");

		SafeHtmlCell prototype = new SafeHtmlCell();

		Column<User, SafeHtml> changeDetails = new Column<User, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(User object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.UsersPageType.asHref("changedetails", object.id.toString()).asString()
						+ "\">Edit</a>");
			}
		};

		FieldUpdater<User, String> action = new FieldUpdater<User, String>() {

			@Override
			public void update(int index, final User object, String value) {
				switch (value) {
				case "Make test":
					UserController.get().assignUserRoleId(object.id, "TST");
					break;
				case "Add to beta":
					UserController.get().assignUserRoleId(object.id, "BT1");
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

		Column<User, String> makeTest = new Column<User, String>(prototype1) {

			@Override
			public String getValue(User object) {
				return "Make test";
			}
		};
		makeTest.setFieldUpdater(action);

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

		usersTable.addColumn(changeDetails);
		usersTable.addColumn(makeTest);
		usersTable.addColumn(addToBeta);
		usersTable.addColumn(delete);
	}

	@UiHandler("deleteTestUsers")
	void onDeleteTestUsersClick(ClickEvent event) {
		confirmationDialog = new ConfirmationDialog("Delete test user", "Are you sure you want to remove all test users ?");
		confirmationDialog.center();

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
				UserController.get().deleteTestUsers();
				confirmationDialog.reset();
			}
		});
	}

	@UiHandler("queryTextBox")
	void onKeyPressed(KeyUpEvent event) {
		if (!queryTextBox.getText().equals(query)) {
			query = queryTextBox.getText();
			simplePager.setPageStart(0);
			UserController.get().updateRowCount(0, false);
			UserController.get().reset();
			if (query.length() >= 1) { // Execute search
				UserController.get().fetchUsersQuery(query);
			} else { // Get all users
				UserController.get().fetchUsers();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler#deleteUserSuccess(io.reflection.app.api.blog.shared.call.DeleteUserRequest,
	 * io.reflection.app.api.blog.shared.call.DeleteUserResponse)
	 */
	@Override
	public void deleteUserSuccess(DeleteUserRequest input, DeleteUserResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			simplePager.setPageStart(0);
			UserController.get().reset();
			UserController.get().updateRowCount(0, false);
			UserController.get().fetchUsers();
		}
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
	 * @see io.reflection.app.api.admin.shared.call.event.DeleteUsersEventHandler#deleteUsersSuccess(io.reflection.app.api.admin.shared.call.DeleteUsersRequest,
	 * io.reflection.app.api.admin.shared.call.DeleteUsersResponse)
	 */
	@Override
	public void deleteUsersSuccess(DeleteUsersRequest input, DeleteUsersResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			simplePager.setPageStart(0);
			UserController.get().reset();
			UserController.get().updateRowCount(0, false);
			UserController.get().fetchUsers();
		}
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.DeleteUsersEventHandler#deleteUsersFailure(io.reflection.app.api.admin.shared.call.DeleteUsersRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void deleteUsersFailure(DeleteUsersRequest input, Throwable caught) {
		preloader.hide();
	}

}
