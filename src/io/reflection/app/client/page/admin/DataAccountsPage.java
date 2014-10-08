//
//  DataAccountsPage.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 7 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.controller.DataAccountController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class DataAccountsPage extends Page {

	private static DataAccountsPageUiBinder uiBinder = GWT.create(DataAccountsPageUiBinder.class);

	interface DataAccountsPageUiBinder extends UiBinder<Widget, DataAccountsPage> {}

	@UiField(provided = true) CellTable<DataAccount> dataAccountTable = new CellTable<DataAccount>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	public DataAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		dataAccountTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		DataAccountController.get().addDataDisplay(dataAccountTable);
		simplePager.setDisplay(dataAccountTable);
	}

	public void addColumns() {

		TextColumn<DataAccount> usernameColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return object.username;
			}

		};
		dataAccountTable.addColumn(usernameColumn, "Username");

	}
}
