//
//  LeaderboardRevenueCell.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 29 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Rank;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class LeaderboardRevenueCell extends AbstractCell<Rank> {

	private SafeHtml noDataQuestionMark = SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding "
			+ Styles.STYLES_INSTANCE.reflectionMainStyle().whatsThisTooltipIconStatic() + "\" data-tooltip=\"No data available\"></span>");

	public LeaderboardRevenueCell() {
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
			if (parent.getFirstChildElement().isOrHasChild(clickedElem) && clickedElem.getTagName().equalsIgnoreCase("A")) {
				valueUpdater.update(value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Rank rank, SafeHtmlBuilder sb) {
		SafeHtml value;
		int position = (rank.position.intValue() > 0 ? rank.position.intValue() : rank.grossingPosition.intValue());
		if (SessionController.get().isAdmin()) {
			value = (rank.currency != null && rank.revenue != null ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(rank.currency,
					rank.revenue.floatValue())) : noDataQuestionMark);
		} else if (SessionController.get().canSeePredictions()) {
			value = (rank.currency != null && rank.revenue != null ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(rank.currency,
					rank.revenue.floatValue())) : noDataQuestionMark);
		} else {
			if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(2), FilterController.get().getEndDate())
					|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
				if (position > 10 && !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
					value = SafeHtmlUtils.fromSafeConstant("<a style=\"cursor: pointer\" class=\"sign-up-link\">"
							+ (SessionController.get().isLoggedIn() ? "Link Account" : "Sign Up") + "</a>");
				} else {
					value = (rank.currency != null && rank.revenue != null ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(rank.currency,
							rank.revenue.floatValue())) : noDataQuestionMark);
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
				value = SafeHtmlUtils.fromSafeConstant("<a style=\"cursor: pointer\" class=\"sign-up-link\">" + textValue + "</a>");
			}
		}
		if (value != null) {
			sb.append(value);
		}
	}

}
