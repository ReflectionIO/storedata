//
//  ItemsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ItemsPage extends Page {

	private static ItemsPageUiBinder uiBinder = GWT.create(ItemsPageUiBinder.class);

	interface ItemsPageUiBinder extends UiBinder<Widget, ItemsPage> {}

	@UiField(provided = true) CellTable<Item> items = new CellTable<Item>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField TextField searchField;
	private String query = "";

	public ItemsPage() {
		initWidget(uiBinder.createAndBindUi(this));
		addColumns();

		items.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		items.setEmptyTableWidget(new HTMLPanel("No Items found!"));
		ItemController.get().addDataDisplay(items);
		simplePager.setDisplay(items);

	}

	private void addColumns() {
		TextColumn<Item> id = new TextColumn<Item>() {

			@Override
			public String getValue(Item object) {
				return object.id.toString() + " e[" + object.externalId + "] i[" + object.internalId + "]";
			}

		};

		items.addColumn(id, new TextHeader("Id"));

		TextColumn<Item> name = new TextColumn<Item>() {

			@Override
			public String getValue(Item object) {
				return object.name;
			}

		};

		items.addColumn(name, new TextHeader("Name"));

		TextColumn<Item> creator = new TextColumn<Item>() {

			@Override
			public String getValue(Item object) {
				return object.creatorName;
			}

		};

		items.addColumn(creator, new TextHeader("Creator"));

		Column<Item, String> small = new Column<Item, String>(new ImageCell()) {

			@Override
			public String getValue(Item object) {
				return object.smallImage;
			}
		};

		items.addColumn(small, new TextHeader("S"));

		Column<Item, String> medium = new Column<Item, String>(new ImageCell()) {

			@Override
			public String getValue(Item object) {
				return object.mediumImage;
			}
		};

		items.addColumn(medium, new TextHeader("M"));

		Column<Item, String> large = new Column<Item, String>(new ImageCell()) {

			@Override
			public String getValue(Item object) {
				return object.largeImage;
			}
		};

		items.addColumn(large, new TextHeader("L"));

		Column<Item, SafeHtml> columnIap = new Column<Item, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeMinus() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(Item object) {
				return (object != null) ? SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object, IAP_YES_HTML, IAP_NO_HTML, IAP_DONT_KNOW_HTML))
						: SafeHtmlUtils.fromSafeConstant("-");
			}

		};
		items.addColumn(columnIap, "IAP");
	}

	@UiHandler("searchField")
	void onKeyPressed(KeyUpEvent event) {
		if (!searchField.getText().equals(query)) {
			query = searchField.getText();
			simplePager.setPageStart(0);
			ItemController.get().reset();
			ItemController.get().fetchItems(query);
		}
	}

}
