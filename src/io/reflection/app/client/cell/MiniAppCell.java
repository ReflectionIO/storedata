//
//  MiniAppCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.page.MyAppsPage;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Item;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class MiniAppCell extends AbstractCell<Item> {

	@UiField AnchorElement activeLink;
	@UiField SpanElement inactiveLink;

	interface MiniAppCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, SafeUri smallImage, SafeUri link);
	}

	private static MiniAppCellRenderer RENDERER = GWT.create(MiniAppCellRenderer.class);

	@Override
	public void render(Context context, Item value, SafeHtmlBuilder builder) {

		SafeUri link = PageType.ItemPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, value.internalId, FilterController.DOWNLOADS_CHART_TYPE,
				MyAppsPage.COMING_FROM_PARAMETER, FilterController.get().getFilter().asItemFilterString());
		SafeUri smallImage = UriUtils.fromString(value.smallImage == null ? "images/placeholder_app_icon_2x.png" : value.smallImage);

		RENDERER.render(builder, value.name, value.creatorName, smallImage, link);
	}
}
