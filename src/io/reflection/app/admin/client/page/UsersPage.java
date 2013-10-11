//
//  UsersPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.ServiceController;
import io.reflection.app.admin.client.controller.UserController;
import io.reflection.app.admin.client.part.BootstrapGwtCellTable;
import io.reflection.app.admin.client.part.SimplePager;
import io.reflection.app.shared.datatypes.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class UsersPage extends Composite {

	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);

	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField(provided = true) CellTable<User> mUsers = new CellTable<User>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager mPager;

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

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

		TextColumn<User> email = new TextColumn<User>() {

			@Override
			public String getValue(User object) {
				return object.username;
			}
		};

		TextHeader emailHeader = new TextHeader("E-mail");
		emailHeader.setHeaderStyleNames("col-md-3");
		mUsers.addColumn(email, emailHeader);

		UserController.get().addDataDisplay(mUsers);
		mPager.setDisplay(mUsers);

	}

}
