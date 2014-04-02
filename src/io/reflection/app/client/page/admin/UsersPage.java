//
//  UsersPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class UsersPage extends Page {

	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);

	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField(provided = true) CellTable<User> mUsers = new CellTable<User>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		UserController.get().addDataDisplay(mUsers);
		mPager.setDisplay(mUsers);

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

		FieldUpdater<User, String> action = new FieldUpdater<User, String>() {

			@Override
			public void update(int index, User object, String value) {
				switch (value) {
				case "Make admin":
					UserController.get().makeAdmin(object.id);
					break;
				case "Add to beta":
					UserController.get().makeBeta(object.id);
					break;
				case "Delete":
					UserController.get().delete(object.id);
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
}
