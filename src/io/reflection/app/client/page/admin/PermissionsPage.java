//
//  PermissionsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.controller.PermissionController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Permission;

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
public class PermissionsPage extends Page {

	private static PermissionsPageUiBinder uiBinder = GWT.create(PermissionsPageUiBinder.class);

	interface PermissionsPageUiBinder extends UiBinder<Widget, PermissionsPage> {}

	@UiField(provided = true) CellTable<Permission> permissionTable = new CellTable<Permission>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	public PermissionsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addPermissionColumns();

		permissionTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		PermissionController.get().addDataDisplay(permissionTable);
		mPager.setDisplay(permissionTable);
	}

	private void addPermissionColumns() {
		TextColumn<Permission> code = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.code == null ? "-" : object.code;
			}

		};

		permissionTable.addColumn(code, new TextHeader("Code"));

		TextColumn<Permission> name = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.name;
			}

		};

		permissionTable.addColumn(name, new TextHeader("Name"));

		TextColumn<Permission> description = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.description;
			}

		};

		permissionTable.addColumn(description, new TextHeader("Description"));
	}

}
