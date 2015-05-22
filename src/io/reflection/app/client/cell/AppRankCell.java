//
//  MiniAppCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import static io.reflection.app.client.controller.FilterController.REVENUE_DAILY_DATA_TYPE;
import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMATTER;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.RanksPage;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
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

	private boolean showAllPredictions;
	private boolean useFilter = true;
	private String currency = null;

	interface DailyDataTemplate extends SafeHtmlTemplates {
		DailyDataTemplate INSTANCE = GWT.create(DailyDataTemplate.class);

		@Template("<span class=\"{0}\" style=\"{1}\">{2}</span>")
		SafeHtml dailyData(String icon, String style, String value);
	}

	public AppRankCell(boolean showAllPredictions) {
		this.showAllPredictions = showAllPredictions;
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

		SafeHtml dailyData = null;
		SafeStyles display = null;

		if (useFilter) {
			String dailyDataType = filter.getDailyData(), listType = FilterController.OVERALL_LIST_TYPE;

			if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType) && showAllPredictions) {
				if (value.downloads != null && value.revenue != null) {
					dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
							+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
							FormattingHelper.asWholeMoneyString(value.currency, showAllPredictions ? value.revenue.floatValue() : 0.0f));
				} else {
					dailyData = SafeHtmlUtils.fromSafeConstant("-");
				}
			} else {
				if (value.downloads != null) {
					dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
							+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
							WHOLE_NUMBER_FORMATTER.format(value.downloads.doubleValue()));
				} else {
					dailyData = SafeHtmlUtils.fromSafeConstant("-");
				}
			}

			Stack s = NavigationController.get().getStack();
			if (s != null) {
				listType = s.getParameter(RanksPage.SELECTED_TAB_PARAMETER_INDEX);
			}

			if (FilterController.OVERALL_LIST_TYPE.equals(listType)) {
				switch (context.getColumn()) {
				case 1:
					filter = Filter.parse(filter.asItemFilterString());
					filter.setListType(FilterController.FREE_LIST_TYPE);
					display = SafeStylesUtils.fromTrustedString("");
					break;
				case 2:
					filter = Filter.parse(filter.asItemFilterString());
					filter.setListType(FilterController.PAID_LIST_TYPE);
					display = showAllPredictions ? SafeStylesUtils.fromTrustedString("") : SafeStylesUtils.forDisplay(Display.NONE);
					break;
				case 3:
					filter = Filter.parse(filter.asItemFilterString());
					filter.setListType(FilterController.GROSSING_LIST_TYPE);
					display = showAllPredictions ? SafeStylesUtils.fromTrustedString("") : SafeStylesUtils.forDisplay(Display.NONE);
					break;
				}
			} else if (FilterController.FREE_LIST_TYPE.equals(listType)) {
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.FREE_LIST_TYPE);
				display = SafeStylesUtils.forDisplay(Display.NONE);
			} else if (FilterController.PAID_LIST_TYPE.equals(listType)) {
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.PAID_LIST_TYPE);
				display = SafeStylesUtils.forDisplay(Display.NONE);
			} else if (FilterController.GROSSING_LIST_TYPE.equals(listType)) {
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.GROSSING_LIST_TYPE);
				display = SafeStylesUtils.forDisplay(Display.NONE);
			}
		} else {
			display = showAllPredictions ? SafeStylesUtils.fromTrustedString("") : SafeStylesUtils.forDisplay(Display.NONE);

			if (value.revenue != null) {
				dailyData = DailyDataTemplate.INSTANCE.dailyData("icon-dollar", "padding-right: 6px", FormattingHelper.asWholeMoneyString(
						value.currency == null ? currency : value.currency, showAllPredictions ? value.revenue.floatValue() : 0.0f));
			} else if (value.downloads != null) {
				dailyData = DailyDataTemplate.INSTANCE.dailyData("icon-download-alt", "padding-right: 6px",
						WHOLE_NUMBER_FORMATTER.format(value.downloads.doubleValue()));
			} else {
				dailyData = SafeHtmlUtils.fromSafeConstant("-");
			}
		}

		SafeUri link = PageType.ItemPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, item.internalId, FilterController.RANKING_CHART_TYPE,
				RanksPage.COMING_FROM_PARAMETER, filter.asItemFilterString());
		SafeUri smallImage = UriUtils.fromString(item.smallImage);

		RENDERER.render(builder, item.name, item.creatorName, smallImage, link, dailyData, display.asString());
	}

	public AppRankCell useFilter(boolean value) {
		useFilter = value;
		return this;
	}

	public AppRankCell currency(String value) {
		currency = value;
		return this;
	}
}
