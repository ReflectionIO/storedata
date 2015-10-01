//
//  LeaderboardDownloadsCell.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 29 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMATTER;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.PageType;
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
public class LeaderboardDownloadsCell extends AbstractCell<Rank> {

	public LeaderboardDownloadsCell() {
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
			value = (rank.downloads != null ? SafeHtmlUtils.fromSafeConstant(WHOLE_NUMBER_FORMATTER.format(rank.downloads)) : SafeHtmlUtils
					.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
		} else if (SessionController.get().canSeePredictions()) {

			value = (rank.downloads != null ? SafeHtmlUtils.fromSafeConstant(WHOLE_NUMBER_FORMATTER.format(rank.downloads)) : SafeHtmlUtils
					.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
		} else {
			if (CalendarUtil.isSameDate(FilterHelper.getDaysAgo(2), FilterController.get().getEndDate())
					|| NavigationController.get().getCurrentPage().equals(PageType.HomePageType)) {
				if (position > 10) {
					value = SafeHtmlUtils.fromSafeConstant("<a style=\"cursor: pointer\" class=\"sign-up-link\">"
							+ (SessionController.get().isValidSession() ? "Link Account" : "Sign Up") + "</a>");
				} else {
					value = (rank.downloads != null ? SafeHtmlUtils.fromSafeConstant(WHOLE_NUMBER_FORMATTER.format(rank.downloads)) : SafeHtmlUtils
							.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
				}
			} else {
				value = SafeHtmlUtils.fromSafeConstant("<a style=\"cursor: pointer\" class=\"sign-up-link\">"
						+ (SessionController.get().isValidSession() ? "Link Account" : "Sign Up") + "</a>");
			}
		}
		if (value != null) {
			sb.append(value);
		}
	}

}
