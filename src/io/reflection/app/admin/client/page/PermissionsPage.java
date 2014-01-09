//
//  PermissionsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.PermissionController;
import io.reflection.app.admin.client.controller.ServiceController;
import io.reflection.app.admin.client.part.BootstrapGwtCellTable;
import io.reflection.app.admin.client.part.SimplePager;
import io.reflection.app.shared.datatypes.Permission;

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
public class PermissionsPage extends Composite {

	private static PermissionsPageUiBinder uiBinder = GWT.create(PermissionsPageUiBinder.class);

	interface PermissionsPageUiBinder extends UiBinder<Widget, PermissionsPage> {}

	@UiField(provided = true) CellTable<Permission> mPermissions = new CellTable<Permission>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	public PermissionsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addPermissionColumns();

		PermissionController.get().addDataDisplay(mPermissions);
		mPager.setDisplay(mPermissions);
	}
	
	private void addPermissionColumns() {
		TextColumn<Permission> code = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.code == null ? "-" : object.code;
			}

		};

		TextHeader codeHeader = new TextHeader("Code");
		codeHeader.setHeaderStyleNames("col-md-1");
		mPermissions.addColumn(code, codeHeader);

		TextColumn<Permission> name = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.name;
			}

		};

		TextHeader nameHeader = new TextHeader("Name");
		nameHeader.setHeaderStyleNames("col-md-1");
		mPermissions.addColumn(name, nameHeader);

		TextColumn<Permission> description = new TextColumn<Permission>() {

			@Override
			public String getValue(Permission object) {
				return object.description;
			}

		};

		TextHeader descriptionHeader = new TextHeader("Description");
		descriptionHeader.setHeaderStyleNames("col-md-1");
		mPermissions.addColumn(description, descriptionHeader);
	}

}
