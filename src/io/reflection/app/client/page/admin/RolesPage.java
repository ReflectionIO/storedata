//
//  RolesPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.controller.RoleController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Role;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class RolesPage extends Page {

	private static RolesPageUiBinder uiBinder = GWT.create(RolesPageUiBinder.class);

	interface RolesPageUiBinder extends UiBinder<Widget, RolesPage> {}

	@UiField(provided = true) CellTable<Role> roleTable = new CellTable<Role>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	public RolesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addRoleColumns();

		roleTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		RoleController.get().addDataDisplay(roleTable);
		mPager.setDisplay(roleTable);
	}

	private void addRoleColumns() {
		TextColumn<Role> code = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.code == null ? "-" : object.code;
			}

		};

		roleTable.addColumn(code, new TextHeader("Code"));

		TextColumn<Role> name = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.name;
			}

		};

		roleTable.addColumn(name, new TextHeader("Name"));

		TextColumn<Role> description = new TextColumn<Role>() {

			@Override
			public String getValue(Role object) {
				return object.description;
			}

		};

		roleTable.addColumn(description, new TextHeader("Description"));

	}

}
