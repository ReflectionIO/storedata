//
//  AppDetailsAndPredictionCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.cell;

import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMATTER;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.HomePage;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.RanksPage;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author billy1380
 *
 */
public class AppDetailsAndPredictionCell extends AbstractCell<Rank> {

	private SafeHtml noDataQuestionMark = SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding "
			+ Styles.STYLES_INSTANCE.reflectionMainStyle().whatsThisTooltipIconStatic() + "\" data-tooltip=\"No data available\"></span>");
	private SafeHtml signUpLink = SafeHtmlUtils
			.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link js-tooltip js-tooltip--right\" data-tooltip=\"Sign up and link your app store account to see this data\">Sign Up</a>");
	private SafeHtml linkAccountLink = SafeHtmlUtils
			.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link js-tooltip js-tooltip--right\" data-tooltip=\"Link your app store account to see this data\">Link Account</a>");
	private SafeHtml upgradeLink = SafeHtmlUtils
			.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link js-tooltip js-tooltip--right\" data-tooltip=\"Upgrade to Developer Premium to see historical data\">Upgrade</a>");

	public AppDetailsAndPredictionCell() {
		super("click");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object,
	 * com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
	 */
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Rank value, NativeEvent event, ValueUpdater<Rank> valueUpdater) {
		// Handle the click event.
		if (BrowserEvents.CLICK.equals(event.getType())) {
			Element clickedElem = Element.as(event.getEventTarget());
			if (clickedElem.getTagName().equalsIgnoreCase("A") && !clickedElem.getAttribute("href").startsWith("#!item/view/")) {
				valueUpdater.update(value);
			}
		}
	}

	interface AppRankCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String name, String creatorName, SafeUri smallImage, SafeUri link, SafeHtml dailyData, String displayDailyData,
				String displayLink, String displayLinkText);
	}

	interface DailyDataTemplate extends SafeHtmlTemplates {
		DailyDataTemplate INSTANCE = GWT.create(DailyDataTemplate.class);

		@Template("<span class=\"{0}\" style=\"{1}\">{2}</span>")
		SafeHtml dailyData(String icon, String style, String value);
	}

	interface DailyDataTemplateHtml extends SafeHtmlTemplates {
		DailyDataTemplateHtml INSTANCE = GWT.create(DailyDataTemplateHtml.class);

		@Template("<span class=\"{0}\" style=\"{1}\">{2}</span>")
		SafeHtml dailyData(String icon, String style, SafeHtml value);
	}

	private static AppRankCellRenderer RENDERER = GWT.create(AppRankCellRenderer.class);

	@Override
	public void render(Context context, Rank rank, SafeHtmlBuilder builder) {

		String displayLink = "";
		String displayLinkText = "";

		if (SessionController.get().isAdmin()) {
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

		SafeHtml dailyData = noDataQuestionMark;
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

		if (NavigationController.get().getCurrentPage() == null || NavigationController.get().getCurrentPage() == PageType.HomePageType) {
			listType = HomePage.getSelectedTab(); // Home page leaderboard
		} else {
			if (NavigationController.get().getStack() != null) {
				listType = NavigationController.get().getStack().getParameter(RanksPage.SELECTED_TAB_PARAMETER_INDEX);
			}
		}

		if (FilterController.OVERALL_LIST_TYPE.equals(listType)) {
			switch (context.getColumn()) {
			case 1:
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.PAID_LIST_TYPE);
				displayDailyData = SafeStylesUtils.fromTrustedString("");
				if (SessionController.get().isAdmin()) {
					dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
							+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
							WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : noDataQuestionMark);
				} else if (SessionController.get().canSeePredictions()) {
					dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
							+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
							WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : noDataQuestionMark);
				} else {
					if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(FilterHelper.DEFAULT_LEADERBOARD_LAG_DAYS), FilterController.get().getEndDate())
							|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
						if (rank.position.intValue() > 10 && !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
							dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
									+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
									SessionController.get().isLoggedIn() ? linkAccountLink : signUpLink);
						} else {
							dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle()
									.refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
									WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : noDataQuestionMark);
						}
					} else {
						SafeHtml linkValue = null;
						if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
							linkValue = upgradeLink;
						} else if (SessionController.get().isLoggedIn()) {
							linkValue = linkAccountLink;
						} else {
							linkValue = signUpLink;
						}
						dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "", linkValue);
					}
				}
				break;
			case 2:
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.FREE_LIST_TYPE);
				displayDailyData = SafeStylesUtils.fromTrustedString("");
				if (SessionController.get().isAdmin()) {
					dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
							+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
							WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : noDataQuestionMark);
				} else if (SessionController.get().canSeePredictions()) {
					dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
							+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
							WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : noDataQuestionMark);
				} else {
					if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(FilterHelper.DEFAULT_LEADERBOARD_LAG_DAYS), FilterController.get().getEndDate())
							|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
						if (rank.position.intValue() > 10 && !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
							dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
									+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
									SessionController.get().isLoggedIn() ? linkAccountLink : signUpLink);
						} else {
							dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle()
									.refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
									WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : noDataQuestionMark);
						}
					} else {
						SafeHtml linkValue = null;
						if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
							linkValue = upgradeLink;
						} else if (SessionController.get().isLoggedIn()) {
							linkValue = linkAccountLink;
						} else {
							linkValue = signUpLink;
						}
						dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "", linkValue);
					}
				}
				break;
			case 3:
				filter = Filter.parse(filter.asItemFilterString());
				filter.setListType(FilterController.GROSSING_LIST_TYPE);
				displayDailyData = SafeStylesUtils.fromTrustedString("");
				if (SessionController.get().isAdmin()) {
					dailyData = (rank.currency != null && rank.revenue != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE
							.reflectionMainStyle().refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
							FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue())) : noDataQuestionMark);
				} else if (SessionController.get().canSeePredictions()) {
					dailyData = (rank.currency != null && rank.revenue != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE
							.reflectionMainStyle().refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
							FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue())) : noDataQuestionMark);
				} else {
					if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(FilterHelper.DEFAULT_LEADERBOARD_LAG_DAYS), FilterController.get().getEndDate())
							|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
						if (rank.grossingPosition.intValue() > 10
								&& !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
							dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
									+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
									SessionController.get().isLoggedIn() ? linkAccountLink : signUpLink);
						} else {
							dailyData = (rank.currency != null && rank.revenue != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE
									.reflectionMainStyle().refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
									FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue())) : noDataQuestionMark);
						}
					} else {
						SafeHtml linkValue = null;
						if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
							linkValue = upgradeLink;
						} else if (SessionController.get().isLoggedIn()) {
							linkValue = linkAccountLink;
						} else {
							linkValue = signUpLink;
						}
						dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "", linkValue);
					}
				}
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

		SafeUri link = (SessionController.get().isAdmin() ? PageType.ItemPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, item.internalId,
				FilterController.RANKING_CHART_TYPE, RanksPage.COMING_FROM_PARAMETER, filter.asItemFilterString()) : UriUtils.fromSafeConstant("#"));
		SafeUri smallImage = UriUtils.fromString(item.smallImage);

		RENDERER.render(builder, item.name, item.creatorName, smallImage, link, dailyData, displayDailyData.asString(), displayLink, displayLinkText);

	}
}
