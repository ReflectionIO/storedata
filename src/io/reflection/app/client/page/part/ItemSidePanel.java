//
//  ItemSidePanel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ItemSidePanel extends Composite {

	private static ItemSidePanelUiBinder uiBinder = GWT.create(ItemSidePanelUiBinder.class);

	interface ItemSidePanelUiBinder extends UiBinder<Widget, ItemSidePanel> {}

	@UiField HeadingElement title;
	@UiField Image image;
	@UiField HeadingElement creatorName;

	@UiField SpanElement storeName;
	@UiField AnchorElement viewInStore;
	@UiField ParagraphElement price;

	@UiField Image spinnerLoader;
	private String iapDescription;

	public ItemSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setItem(Item item) {
		title.setInnerText(item.name);
		creatorName.setInnerText("By " + item.creatorName);

		if (item.largeImage != null) {
			image.setUrl(item.largeImage);
			image.removeStyleName(Styles.STYLES_INSTANCE.reflection().unknownAppLarge());
		} else {
			image.setUrl("");
			image.addStyleName(Styles.STYLES_INSTANCE.reflection().unknownAppLarge());
		}

		Store s = StoreController.get().getStore(item.source);
		storeName.setInnerHTML((s == null || s.name == null || s.name.length() == 0) ? item.source.toUpperCase() + " Store" : s.name);

		viewInStore.setHref(StoreController.get().getExternalUri(item));

		iapDescription = DataTypeHelper.itemIapState(item, " + In App Purchases", "", "");
	}

	public void setPrice(String currency, Float value) {
		if (currency != null && value != null) {
			setPriceInnerHTML(FormattingHelper.asPriceString(currency, value.floatValue()) + iapDescription);
		} else {
			setPriceInnerHTML("-");
		}
	}

	public void setPriceInnerHTML(String s) {
		if (s != null) {
			spinnerLoader.setVisible(false);
			price.setInnerHTML(s);
		} else {
			price.setInnerHTML("");
			spinnerLoader.setVisible(true);
		}
	}

}
