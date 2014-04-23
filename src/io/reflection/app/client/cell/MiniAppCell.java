//
//  MiniAppCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.client.res.Styles;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class MiniAppCell extends AbstractCell<Item> {

	interface MiniAppCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, SafeUri smallImage, String styleName, SafeUri link);
	}

	private static MiniAppCellRenderer RENDERER = GWT.create(MiniAppCellRenderer.class);

	@Override
	public void render(Context context, Item value, SafeHtmlBuilder builder) {

		SafeUri link = PageType.ItemPageType.asHref("view", value.externalId == null ? value.internalId : value.externalId, FilterController.OVERALL_LIST_TYPE,
				FilterController.get().getFilter().asItemFilterString());
		SafeUri smallImage = UriUtils.fromString(value.smallImage == null ? "" : value.smallImage);

		RENDERER.render(builder, value.name, value.creatorName, smallImage, Styles.INSTANCE.reflection().unknownAppSmall(), link);
	}

}
