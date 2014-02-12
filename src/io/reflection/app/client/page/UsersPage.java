//
//  UsersPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.ServiceController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.User;

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
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author billy1380
 * 
 */
public class UsersPage extends Page {

	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);

	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField(provided = true) CellTable<User> mUsers = new CellTable<User>(ServiceController.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	@UiField InlineHyperlink mAssignPassword;
	@UiField InlineHyperlink mMakeAdmin;
	@UiField InlineHyperlink mChangeDetails;

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addUserColumns();

		final SingleSelectionModel<User> s = new SingleSelectionModel<User>();
		s.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				User selected = s.getSelectedObject();

				if (selected != null) {
					mAssignPassword.removeStyleName("disabled");
					mMakeAdmin.removeStyleName("disabled");
					mChangeDetails.removeStyleName("disabled");
				} else {
					mAssignPassword.addStyleName("disabled");
					mMakeAdmin.addStyleName("disabled");
					mChangeDetails.addStyleName("disabled");
				}

				if (selected != null) {
					mAssignPassword.setTargetHistoryToken("users/changepassword/" + selected.id.toString());
				}

				if (selected != null) {
					mMakeAdmin.setTargetHistoryToken("users/assignrole/" + selected.id.toString() + "/admin");
				}

				if (selected != null) {
					mChangeDetails.setTargetHistoryToken("users/changedetails/" + selected.id.toString());
				}

			}
		});
		mUsers.setSelectionModel(s);

		UserController.get().addDataDisplay(mUsers);
		mPager.setDisplay(mUsers);

	}

	private void addUserColumns() {
		TextColumn<User> name = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return object.forename + " " + object.surname;
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
	}

}
