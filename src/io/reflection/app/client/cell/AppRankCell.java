//
//  MiniAppCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import static io.reflection.app.client.helper.FormattingHelper.*;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiRenderer;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.RanksPage;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

/**
 * @author billy1380
 *
 */
public class AppRankCell extends AbstractCell<Rank> {

	@UiField AnchorElement appLink;

	interface AppRankCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, SafeUri smallImage, SafeUri link, SafeHtml dailyData, String displayDailyData,
				String displayLink, String displayLinkText);
	}

	interface DailyDataTemplate extends SafeHtmlTemplates {
		DailyDataTemplate INSTANCE = GWT.create(DailyDataTemplate.class);

		@Template("<span class=\"{0}\" style=\"{1}\">{2}</span>")
		SafeHtml dailyData(String icon, String style, String value);
	}

	private static AppRankCellRenderer RENDERER = GWT.create(AppRankCellRenderer.class);

	@Override
	public void render(Context context, Rank rank, SafeHtmlBuilder builder) {

		String displayLink = "";
		String displayLinkText = "margin-bottom: 6px;display: block;font-weight: 700;line-height: 1.2;color: #363A45;text-overflow: ellipsis;overflow: hidden;";

		if (SessionController.get().isLoggedInUserAdmin()) {
			displayLinkText = "display:none";
		} else {
			displayLink = "display:none";
		}

		Item item = ItemController.get().lookupItem(rank.itemId);

		if (item == null) {
			item = new Item();
			item.internalId = rank.itemId;
			item.name = rank.itemId + "???";
			item.creatorName = "???";
			item.smallImage = "";
		}

		Filter filter = FilterController.get().getFilter();

		SafeHtml dailyData = SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
		SafeStyles displayDailyData = SafeStylesUtils.fromTrustedString("");

		// String dailyDataType = filter.getDailyData();
		String listType = FilterController.OVERALL_LIST_TYPE;

		// if (SessionController.get().isLoggedInUserAdmin()) {
		// if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
		// if (value.downloads != null && value.revenue != null) {
		// dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
		// + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
		// FormattingHelper.asWholeMoneyString(value.currency, value.revenue.floatValue()));
		// } else {
		// dailyData = SafeHtmlUtils.fromSafeConstant("-");
		// }
		// } else { // Downloads daily data type
		// if (value.downloads != null) {
		// dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
		// + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
		// WHOLE_NUMBER_FORMATTER.format(value.downloads.doubleValue()));
		// } else {
		// dailyData = SafeHtmlUtils.fromSafeConstant("-");
		// }
		// }
		// }

		Stack stack = NavigationController.get().getStack();
		if (stack != null) {
			listType = stack.getParameter(RanksPage.SELECTED_TAB_PARAMETER_INDEX);
		}

		if (PageType.HomePageType.equals(stack.getPage())) {
			displayDailyData = SafeStylesUtils.forDisplay(Display.NONE);
			dailyData = SafeHtmlUtils.fromSafeConstant("");
		} else {

			if (FilterController.OVERALL_LIST_TYPE.equals(listType)) {
				switch (context.getColumn()) {
				case 1:
					filter = Filter.parse(filter.asItemFilterString());
					filter.setListType(FilterController.PAID_LIST_TYPE);
					displayDailyData = SafeStylesUtils.fromTrustedString("");
					if (!SessionController.get().isLoggedInUserAdmin() && rank.position != null && rank.position.intValue() > 0
							&& rank.position.intValue() <= 5) {
						dailyData = SafeHtmlUtils.fromSafeConstant("<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud() + "\">&nbsp;</span><span class=\"js-tooltip "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().whatsThisTooltipIconStatic()
								+ "\" data-tooltip=\"We are upgrading our model to improve accuracy for the Top 5. It will be implemented soon.\"></span>");
					} else if (rank.downloads != null) {
						dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
								WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue()));
					} else if (!SessionController.get().isValidSession() && rank.position.intValue() > 10) {
						dailyData = SafeHtmlUtils.EMPTY_SAFE_HTML;
					}
					break;
				case 2:
					filter = Filter.parse(filter.asItemFilterString());
					filter.setListType(FilterController.FREE_LIST_TYPE);
					displayDailyData = SafeStylesUtils.fromTrustedString("");
					if (!SessionController.get().isLoggedInUserAdmin() && rank.position != null && rank.position.intValue() > 0
							&& rank.position.intValue() <= 5) {
						dailyData = SafeHtmlUtils.fromSafeConstant("<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud() + "\">&nbsp;</span><span class=\"js-tooltip "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().whatsThisTooltipIconStatic()
								+ "\" data-tooltip=\"We are upgrading our model to improve accuracy for the Top 5. It will be implemented soon.\"></span>");
					} else if (rank.downloads != null) {
						dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
								WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue()));
					} else if (!SessionController.get().isValidSession() && rank.position.intValue() > 10) {
						dailyData = SafeHtmlUtils.EMPTY_SAFE_HTML;
					}
					break;
				case 3:
					filter = Filter.parse(filter.asItemFilterString());
					filter.setListType(FilterController.GROSSING_LIST_TYPE);
					displayDailyData = SafeStylesUtils.fromTrustedString("");
					// if (!SessionController.get().isLoggedInUserAdmin()) {
					if (!SessionController.get().isLoggedInUserAdmin() && rank.grossingPosition != null && rank.grossingPosition.intValue() > 0
							&& rank.grossingPosition.intValue() <= 5) {
						dailyData = SafeHtmlUtils.fromSafeConstant("<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue() + "\">&nbsp;</span><span class=\"js-tooltip "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().whatsThisTooltipIconStatic()
								+ "\" data-tooltip=\"We are upgrading our model to improve accuracy for the Top 5. It will be implemented soon.\"></span>");
					} else if (rank.currency != null && rank.revenue != null) {
						dailyData = DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
								FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue()));
					} else if (!SessionController.get().isValidSession() && rank.grossingPosition.intValue() > 10) {
						dailyData = SafeHtmlUtils.EMPTY_SAFE_HTML;
					}
					// }
					break;
				}
			} else if (FilterController.FREE_LIST_TYPE.equals(listType)) {
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.FREE_LIST_TYPE);
				displayDailyData = SafeStylesUtils.forDisplay(Display.NONE);
				dailyData = SafeHtmlUtils.fromSafeConstant("");
			} else if (FilterController.PAID_LIST_TYPE.equals(listType)) {
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.PAID_LIST_TYPE);
				displayDailyData = SafeStylesUtils.forDisplay(Display.NONE);
				dailyData = SafeHtmlUtils.fromSafeConstant("");
			} else if (FilterController.GROSSING_LIST_TYPE.equals(listType)) {
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.GROSSING_LIST_TYPE);
				displayDailyData = SafeStylesUtils.forDisplay(Display.NONE);
				dailyData = SafeHtmlUtils.fromSafeConstant("");
			}

		}

		SafeUri link = (SessionController.get().isLoggedInUserAdmin() ? PageType.ItemPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
				item.internalId, FilterController.RANKING_CHART_TYPE, RanksPage.COMING_FROM_PARAMETER, filter.asItemFilterString()) : UriUtils
				.fromSafeConstant("#"));
		SafeUri smallImage = UriUtils.fromString(item.smallImage);

		RENDERER.render(builder, item.name, item.creatorName, smallImage, link, dailyData, displayDailyData.asString(), displayLink, displayLinkText);

	}
}
