//
//  MiniAppCell.java
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
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.RanksPage;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
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
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author billy1380
 *
 */
public class LeaderboardAppDetailsCell extends AbstractCell<Rank> {

	@UiField AnchorElement appLink;

	public LeaderboardAppDetailsCell() {
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
		if ("click".equals(event.getType())) {
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
		String displayLinkText = "margin-bottom: 6px;display: block;font-weight: 700;line-height: 1.2;color: #363A45;text-overflow: ellipsis;overflow: hidden;";

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
					if (SessionController.get().isAdmin()) {
						dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
								+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
								WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
					} else if (SessionController.get().canSeePredictions()) {
						dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
								+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
								WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
					} else {
						if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(2), FilterController.get().getEndDate())
								|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
							if (rank.position.intValue() > 10 && !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
								dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(
										Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
												+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(),
										"",
										SafeHtmlUtils.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link\">"
												+ (SessionController.get().isLoggedIn() ? "Link Account" : "Sign Up") + "</a>"));
							} else {
								dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle()
										.refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
										WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : SafeHtmlUtils
										.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
							}
						} else {
							String textValue = "";
							if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
								textValue = "Upgrade";
							} else if (SessionController.get().isLoggedIn()) {
								textValue = "Link Account";
							} else {
								textValue = "Sign Up";
							}
							dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
									+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
									SafeHtmlUtils.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link\">" + textValue + "</a>"));
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
								WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
					} else if (SessionController.get().canSeePredictions()) {
						dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore()
								+ " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
								WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
					} else {
						if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(2), FilterController.get().getEndDate())
								|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
							if (rank.position.intValue() > 10 && !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
								dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(
										Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
												+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(),
										"",
										SafeHtmlUtils.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link\">"
												+ (SessionController.get().isLoggedIn() ? "Link Account" : "Sign Up") + "</a>"));
							} else {
								dailyData = (rank.downloads != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle()
										.refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
										WHOLE_NUMBER_FORMATTER.format(rank.downloads.doubleValue())) : SafeHtmlUtils
										.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
							}
						} else {
							String textValue = "";
							if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
								textValue = "Upgrade";
							} else if (SessionController.get().isLoggedIn()) {
								textValue = "Link Account";
							} else {
								textValue = "Sign Up";
							}
							dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
									+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCloud(), "",
									SafeHtmlUtils.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link\">" + textValue + "</a>"));
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
								FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue())) : SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
					} else if (SessionController.get().canSeePredictions()) {
						dailyData = (rank.currency != null && rank.revenue != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE
								.reflectionMainStyle().refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
								FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue())) : SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
					} else {
						if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(2), FilterController.get().getEndDate())
								|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
							if (rank.grossingPosition.intValue() > 10
									&& !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
								dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(
										Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
												+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(),
										"",
										SafeHtmlUtils.fromSafeConstant("<a style=\"cursor: pointer\" class=\"sign-up-link\">"
												+ (SessionController.get().isLoggedIn() ? "Link Account" : "Sign Up") + "</a>"));
							} else {
								dailyData = (rank.currency != null && rank.revenue != null ? DailyDataTemplate.INSTANCE.dailyData(Styles.STYLES_INSTANCE
										.reflectionMainStyle().refIconBefore() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
										FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue())) : SafeHtmlUtils
										.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
							}
						} else {
							String textValue = "";
							if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
								textValue = "Upgrade";
							} else if (SessionController.get().isLoggedIn()) {
								textValue = "Link Account";
							} else {
								textValue = "Sign Up";
							}
							dailyData = DailyDataTemplateHtml.INSTANCE.dailyData(Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
									+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeRevenue(), "",
									SafeHtmlUtils.fromSafeConstant("<a style=\"cursor: pointer\" class=\"sign-up-link\">" + textValue + "</a>"));
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

		}

		SafeUri link = (SessionController.get().isAdmin() ? PageType.ItemPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, item.internalId,
				FilterController.RANKING_CHART_TYPE, RanksPage.COMING_FROM_PARAMETER, filter.asItemFilterString()) : UriUtils.fromSafeConstant("#"));
		SafeUri smallImage = UriUtils.fromString(item.smallImage);

		RENDERER.render(builder, item.name, item.creatorName, smallImage, link, dailyData, displayDailyData.asString(), displayLink, displayLinkText);

	}
}
