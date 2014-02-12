//
//  RolesPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.RoleController;
import io.reflection.app.client.controller.ServiceController;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.Role;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class RolesPage extends Page {

	private static RolesPageUiBinder uiBinder = GWT.create(RolesPageUiBinder.class);

	interface RolesPageUiBinder extends UiBinder<Widget, RolesPage> {}

	@UiField(provided = true) CellTable<Role> mRoles = new CellTable<Role>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	public RolesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addRoleColumns();

		RoleController.get().addDataDisplay(mRoles);
		mPager.setDisplay(mRoles);
	}

	private void addRoleColumns() {
		TextColumn<Role> code = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.code == null ? "-" : object.code;
			}

		};

		TextHeader codeHeader = new TextHeader("Code");
		codeHeader.setHeaderStyleNames("col-md-1");
		mRoles.addColumn(code, codeHeader);

		TextColumn<Role> name = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.name;
			}

		};

		TextHeader nameHeader = new TextHeader("Name");
		nameHeader.setHeaderStyleNames("col-md-1");
		mRoles.addColumn(name, nameHeader);

		TextColumn<Role> description = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.description;
			}

		};

		TextHeader descriptionHeader = new TextHeader("Description");
		descriptionHeader.setHeaderStyleNames("col-md-1");
		mRoles.addColumn(description, descriptionHeader);

	}

}
