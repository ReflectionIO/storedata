//
//  MiniAppCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.datatypes.shared.Item;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class MiniAppCell extends AbstractCell<Item> {

	interface MiniAppCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, String smallImage, String itemId);
	}

	private static MiniAppCellRenderer RENDERER = GWT.create(MiniAppCellRenderer.class);

	@Override
	public void render(Context context, Item value, SafeHtmlBuilder builder) {
		String name = value.name;

		if (name.length() > 20) {
			name = name.substring(0, 20);
			name += "...";
		}
		
		String creatorName = value.creatorName;
		
		if (creatorName.length() > 20) {
			creatorName = creatorName.substring(0, 20);
			creatorName += "...";
		}

		RENDERER.render(builder, name, creatorName, value.smallImage, value.externalId);
	}

}
