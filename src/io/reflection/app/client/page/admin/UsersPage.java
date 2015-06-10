//
//  UsersPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY_HH_MM;
import io.reflection.app.api.admin.shared.call.DeleteUserRequest;
import io.reflection.app.api.admin.shared.call.DeleteUserResponse;
import io.reflection.app.api.admin.shared.call.DeleteUsersRequest;
import io.reflection.app.api.admin.shared.call.DeleteUsersResponse;
import io.reflection.app.api.admin.shared.call.event.DeleteUserEventHandler;
import io.reflection.app.api.admin.shared.call.event.DeleteUsersEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.component.PopupDialog;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
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

	@UiField PopupDialog deleteUserDialog;
	@UiField Button cancelDelete;
	@UiField Anchor closeDeleteDialog;
	@UiField LoadingButton confirmDelete;

	@UiField Button deleteTestUsers;
	@UiField TextField queryTextBox;
	private String query = "";

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		deleteUserDialog.removeFromParent();

		createColumns();

		usersTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		usersTable.setEmptyTableWidget(new HTMLPanel("No Users found!"));
		simplePager.setDisplay(usersTable);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		queryTextBox.setText(UserController.get().getQuery());
		UserController.get().addDataDisplay(usersTable);

		register(DefaultEventBus.get().addHandlerToSource(DeleteUserEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(DeleteUsersEventHandler.TYPE, UserController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		UserController.get().removeDataDisplay(usersTable);
		super.onDetach();
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

		TextColumn<User> lastLoginColumn = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return (object.lastLoggedIn == null) ? "-" : DATE_FORMATTER_DD_MMM_YYYY_HH_MM.format(object.lastLoggedIn);
			}

		};
		usersTable.addColumn(lastLoginColumn, "Last login");

		Column<User, SafeHtml> changeDetails = new Column<User, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(User object) {
				return SafeHtmlUtils.fromTrustedString("<a class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonFunctionSmall() + "\" href=\""
						+ PageType.UsersPageType.asHref("changedetails", object.id.toString()).asString() + "\">Edit</a>");
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
					deleteUserDialog.setParameter(object.id);
					deleteUserDialog.center();
					break;
				}
			}
		};

		StyledButtonCell prototypeTestAndBeta = new StyledButtonCell(Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonFunctionSmall());

		Column<User, String> makeTest = new Column<User, String>(prototypeTestAndBeta) {

			@Override
			public String getValue(User object) {
				return "Make test";
			}
		};
		makeTest.setFieldUpdater(action);

		Column<User, String> addToBeta = new Column<User, String>(prototypeTestAndBeta) {

			@Override
			public String getValue(User object) {
				return "Add to beta";
			}
		};
		addToBeta.setFieldUpdater(action);

		Column<User, String> delete = new Column<User, String>(new StyledButtonCell(Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonLink() + " "
				+ Styles.STYLES_INSTANCE.reflectionMainStyle().warningText())) {

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

	@UiHandler("confirmDelete")
	void onConfirmDeleteClicked(ClickEvent event) {
		UserController.get().deleteUser(deleteUserDialog.getParameter());
		confirmDelete.setStatusLoading("Deleting..");
	}

	@UiHandler("cancelDelete")
	void onCancelDeleteClicked(ClickEvent event) {
		deleteUserDialog.hide();
	}

	@UiHandler("closeDeleteDialog")
	void onCloseDeleteDialogClicked(ClickEvent event) {
		event.preventDefault();
		deleteUserDialog.hide();
	}

	@UiHandler("deleteTestUsers")
	void onDeleteTestUsersClick(ClickEvent event) {
		UserController.get().deleteTestUsers();
	}

	@UiHandler("queryTextBox")
	void onKeyPressed(KeyUpEvent event) {
		if (!queryTextBox.getText().equals(query)) {
			query = queryTextBox.getText();
			simplePager.setPageStart(0);
			UserController.get().reset();
			UserController.get().fetchUsers(query);
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
			queryTextBox.setText("");
			simplePager.setPageStart(0);
			confirmDelete.resetStatus();
			deleteUserDialog.hide();
		} else {
			confirmDelete.setStatusError();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.DeleteUserEventHandler#deleteUserFailure(io.reflection.app.api.blog.shared.call.DeleteUserRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void deleteUserFailure(DeleteUserRequest input, Throwable caught) {
		confirmDelete.setStatusError();

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
			queryTextBox.setText("");
			simplePager.setPageStart(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.DeleteUsersEventHandler#deleteUsersFailure(io.reflection.app.api.admin.shared.call.DeleteUsersRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void deleteUsersFailure(DeleteUsersRequest input, Throwable caught) {}

}
