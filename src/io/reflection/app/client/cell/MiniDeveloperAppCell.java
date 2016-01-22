//
//  MiniDeveloperAppCell.java
//  storedata
//
//  Created by reflection on 15 Jan 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiRenderer;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Item;

/**
 * @author reflection
 *
 */
public class MiniDeveloperAppCell extends AbstractCell<Item> {

	@UiField
	AnchorElement	activeLink;
	@UiField
	SpanElement		inactiveLink;

	interface MiniDeveloperAppCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String ratingAsPercentage, SafeUri smallImage, SafeUri link);
	}

	private static MiniDeveloperAppCellRenderer RENDERER = GWT.create(MiniDeveloperAppCellRenderer.class);

	@Override
	public void render(Context context, Item value, SafeHtmlBuilder builder) {

		final SafeUri link = PageType.AppDetailsPage.asHref(value.internalId, "leaderboard", FilterController.get().getFilter().asItemFilterString());
		final SafeUri smallImage = UriUtils.fromString(value.smallImage == null ? "images/placeholder_app_icon_2x.png" : value.smallImage);
		String ratingAsPercentage = String.valueOf(value.rating);
		if (value.rating != null && value.rating > 0) {
			ratingAsPercentage = String.valueOf(((value.rating / 5) * 100));
		}

		RENDERER.render(builder, value.name, ratingAsPercentage, smallImage, link);
	}
}
