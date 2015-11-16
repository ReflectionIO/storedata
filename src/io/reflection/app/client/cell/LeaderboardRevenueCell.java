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
import com.google.gwt.dom.client.BrowserEvents;
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

	private SafeHtml noDataQuestionMark = SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip js-tooltip--info tooltip--info js-tooltip--right js-tooltip--right--no-pointer-padding "
			+ Styles.STYLES_INSTANCE.reflectionMainStyle().tooltipInfo() + "\" data-tooltip=\"No data available\"></span>");
	private SafeHtml signUpLink = SafeHtmlUtils
			.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link js-tooltip js-tooltip--right\" data-tooltip=\"Sign up and link your app store account to see this data\">Sign Up</a>");
	private SafeHtml linkAccountLink = SafeHtmlUtils
			.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link js-tooltip js-tooltip--right\" data-tooltip=\"Link your app store account to see this data\">Link Account</a>");
	private SafeHtml upgradeLink = SafeHtmlUtils
			.fromTrustedString("<a style=\"cursor: pointer\" class=\"sign-up-link js-tooltip js-tooltip--right\" data-tooltip=\"Upgrade to Developer Premium to see historical data\">Upgrade</a>");

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
		if (BrowserEvents.CLICK.equals(event.getType())) {
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
			if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(3), FilterController.get().getEndDate())
					|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
				if (position > 10 && !(SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount())) {
					value = SessionController.get().isLoggedIn() ? linkAccountLink : signUpLink;
				} else {
					value = (rank.currency != null && rank.revenue != null ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(rank.currency,
							rank.revenue.floatValue())) : noDataQuestionMark);
				}
			} else {
				if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
					value = upgradeLink;
				} else if (SessionController.get().isLoggedIn()) {
					value = linkAccountLink;
				} else {
					value = signUpLink;
				}
			}
		}
		if (value != null) {
			sb.append(value);
		}
	}

}
