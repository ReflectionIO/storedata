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
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.RanksPage;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class AppRankCell extends AbstractCell<Rank> {

	interface AppRankCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, SafeUri smallImage, SafeUri link, SafeHtml dailyData, String displayDailyData);
	}

	private static AppRankCellRenderer RENDERER = GWT.create(AppRankCellRenderer.class);

	@Override
	public void render(Context context, Rank value, SafeHtmlBuilder builder) {

		Item item = ItemController.get().lookupItem(value.itemId);

		if (item == null) {
			item = new Item();
			item.internalId = value.itemId;
			item.name = value.itemId + "???";
			item.creatorName = "???";
			item.smallImage = "";
		}

		Filter filter = FilterController.get().getFilter();

		String dailyDataType = filter.getDailyData(), listType = FilterController.OVERALL_LIST_TYPE;

		SafeHtml dailyData;

		if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
			dailyData = SafeHtmlUtils.fromSafeConstant("<span class=\"icon-dollar\" style=\"padding-right: 6px;\"></span>"
					+ FormattingHelper.getCurrencySymbol(value.currency) + " " + value.revenue);
		} else {
			dailyData = SafeHtmlUtils.fromSafeConstant("<span class=\"icon-download-alt\" style=\"padding-right: 6px;\"></span>" + value.downloads.toString());
		}

		Stack s = NavigationController.get().getStack();
		if (s != null) {
			listType = s.getParameter(RanksPage.SELECTED_TAB_PARAMETER_INDEX);
		}

		SafeStyles display;
		if (FilterController.OVERALL_LIST_TYPE.equals(listType)) {
			switch (context.getColumn()) {
			case 1:
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.PAID_LIST_TYPE);
				break;
			case 2:
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.FREE_LIST_TYPE);
				break;
			case 3:
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.GROSSING_LIST_TYPE);
				break;
			}

			display = SafeStylesUtils.fromTrustedString("");
		} else {
			display = SafeStylesUtils.forDisplay(Display.NONE);
		}

		SafeUri link = PageType.ItemPageType.asHref("view", item.internalId, FilterController.RANKING_CHART_TYPE, filter.asItemFilterString());
		SafeUri smallImage = UriUtils.fromString(item.smallImage);

		RENDERER.render(builder, item.name, item.creatorName, smallImage, link, dailyData, display.asString());
	}
}
