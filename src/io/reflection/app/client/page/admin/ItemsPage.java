//
//  ItemsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ItemsPage extends Page {

	private static ItemsPageUiBinder uiBinder = GWT.create(ItemsPageUiBinder.class);

	interface ItemsPageUiBinder extends UiBinder<Widget, ItemsPage> {}

	interface ItemsPageStyle extends CssResource {

		String green();

		String silver();
	}

	@UiField ItemsPageStyle style;

	@UiField(provided = true) CellTable<Item> items = new CellTable<Item>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField TextBox queryTextBox;
	private String query = "";

	public ItemsPage() {
		initWidget(uiBinder.createAndBindUi(this));
		addColumns();

		items.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		items.setEmptyTableWidget(new HTMLPanel("No Items found!"));
		ItemController.get().addDataDisplay(items);
		simplePager.setDisplay(items);
		queryTextBox.getElement().setAttribute("placeholder", "Find an item");
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

		Column<Item, SafeHtml> columnIap = new Column<Item, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"icon-help " + style.silver() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"icon-ok " + style.green() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(Item object) {
				return (object != null) ? SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object, IAP_YES_HTML, IAP_NO_HTML, IAP_DONT_KNOW_HTML))
						: SafeHtmlUtils.fromSafeConstant("-");
			}

		};
		items.addColumn(columnIap, "IAP");
	}

	@UiHandler("queryTextBox")
	void onKeyPressed(KeyUpEvent event) {
		if (!queryTextBox.getText().equals(query)) {
			query = queryTextBox.getText();
			simplePager.setPageStart(0);
			ItemController.get().reset();
			ItemController.get().fetchItems(query);
		}
	}

}
