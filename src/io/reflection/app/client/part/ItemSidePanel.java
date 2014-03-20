//
//  ItemSidePanel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Store;

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

	@UiField HeadingElement mTitle;
	@UiField Image mImage;
	@UiField HeadingElement mCreatorName;

	@UiField SpanElement storeName;
	@UiField AnchorElement viewInStore;
	@UiField ParagraphElement price;

	public ItemSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setItem(Item item) {
		mTitle.setInnerText(item.name);
		mCreatorName.setInnerText("By " + item.creatorName);
		mImage.setUrl(item.largeImage);

		Store s = StoreController.get().getStore(item.source);
		storeName.setInnerHTML((s == null || s.name == null || s.name.length() == 0) ? item.source.toUpperCase() + " Store" : s.name);

		viewInStore.setHref(StoreController.get().getExternalUri(item));

		price.setInnerHTML(item.price == 0 ? "Free" : FormattingHelper.getCurrencySymbol(item.currency) + " " + item.price);
	}

}
