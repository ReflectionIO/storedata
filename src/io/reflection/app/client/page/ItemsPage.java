//
//  ItemsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.ServiceController;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.Item;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.core.client.GWT;
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
public class ItemsPage extends Page {

	private static ItemsPageUiBinder uiBinder = GWT.create(ItemsPageUiBinder.class);

	interface ItemsPageUiBinder extends UiBinder<Widget, ItemsPage> {}

	@UiField(provided = true) CellTable<Item> items = new CellTable<Item>(ServiceController.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager pager = new SimplePager(false, false);

	public ItemsPage() {
		initWidget(uiBinder.createAndBindUi(this));
		addColumns();

		ItemController.get().addDataDisplay(items);
		pager.setDisplay(items);
	}

	private void addColumns() {
		TextColumn<Item> id = new TextColumn<Item>() {

			@Override
			public String getValue(Item object) {
				return object.id.toString() + " e[" + object.externalId + "] i[" + object.internalId + "]";
			}

		};

		TextHeader idHeader = new TextHeader("Id");
		idHeader.setHeaderStyleNames("col-md-1");
		items.addColumn(id, idHeader);

		TextColumn<Item> name = new TextColumn<Item>() {

			@Override
			public String getValue(Item object) {
				return object.name;
			}

		};

		TextHeader nameHeader = new TextHeader("Name");
		items.addColumn(name, nameHeader);

		TextColumn<Item> creator = new TextColumn<Item>() {

			@Override
			public String getValue(Item object) {
				return object.creatorName;
			}

		};

		TextHeader creatorHeader = new TextHeader("Creator");
		items.addColumn(creator, creatorHeader);

		Column<Item, String> small = new Column<Item, String>(new ImageCell()) {

			@Override
			public String getValue(Item object) {
				return object.smallImage;
			}
		};

		TextHeader smallHeader = new TextHeader("S");
		items.addColumn(small, smallHeader);

		Column<Item, String> medium = new Column<Item, String>(new ImageCell()) {

			@Override
			public String getValue(Item object) {
				return object.mediumImage;
			}
		};

		TextHeader mediumHeader = new TextHeader("M");
		items.addColumn(medium, mediumHeader);

		Column<Item, String> large = new Column<Item, String>(new ImageCell()) {

			@Override
			public String getValue(Item object) {
				return object.largeImage;
			}
		};

		TextHeader largeHeader = new TextHeader("L");
		items.addColumn(large, largeHeader);
	}

}
