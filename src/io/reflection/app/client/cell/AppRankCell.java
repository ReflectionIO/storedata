//
//  MiniAppCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import static io.reflection.app.client.controller.FilterController.REVENUE_DAILY_DATA_TYPE;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class AppRankCell extends AbstractCell<Rank> {

	interface AppRankCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, String smallImage, String itemId, String dailyData, String filter,
				String displayDailyData);
	}

	private static AppRankCellRenderer RENDERER = GWT.create(AppRankCellRenderer.class);

	@Override
	public void render(Context context, Rank value, SafeHtmlBuilder builder) {

		Item item = ItemController.get().lookupItem(value.itemId);

		String dailyDataType = FilterController.get().getDailyData(), dailyData;
		String display = "default";

		if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
			dailyData = FormattingHelper.getCurrencySymbol(value.currency) + " " + value.revenue;

		} else {
			dailyData = value.downloads.toString();
		}

		String mode = FilterController.get().getListType();

		if (FilterController.OVERALL_LIST_TYPE.equals(mode)) {
			switch (context.getColumn()) {
			case 1:
				mode = FilterController.PAID_LIST_TYPE;
				break;
			case 2:
				mode = FilterController.FREE_LIST_TYPE;
				break;
			case 3:
				mode = FilterController.GROSSING_LIST_TYPE;
				break;
			}
			display = "default";
		} else {
			display = "none";
		}

		RENDERER.render(builder, item.name, item.creatorName, item.smallImage, item.externalId, dailyData, FilterController.get().toItemFilterString(mode),
				display);
	}
}
