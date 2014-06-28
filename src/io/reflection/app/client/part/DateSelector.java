//
//  DateSelector.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 23 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.DateRangeChangeEvent;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * @author stefanocapuzzi
 * 
 */
public class DateSelector extends Composite implements HasValue<DateRange> {

	private static DateSelectorUiBinder uiBinder = GWT.create(DateSelectorUiBinder.class);

	interface DateSelectorUiBinder extends UiBinder<Widget, DateSelector> {}

	interface DateSelectorStyle extends CssResource {
		String highlight();
	}

	@UiField DateSelectorStyle style;

	@UiField DateBox dateBoxFrom;
	@UiField DateBox dateBoxTo;
	@UiField Anchor oneWeekLink;
	@UiField Anchor oneMonthLink;
	@UiField Anchor threeMonthsLink;
	@UiField Anchor sixMonthsLink;
	@UiField Anchor oneYearLink;
	@UiField Button applyDateRange;

	private DateRange dateRange = new DateRange();

	public DateSelector() {
		initWidget(uiBinder.createAndBindUi(this));

		DateTimeFormat dtf = DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_2);
		dateBoxFrom.setFormat(new DateBox.DefaultFormat(dtf));
		dateBoxFrom.getTextBox().setReadOnly(Boolean.TRUE);
		dateBoxTo.setFormat(new DateBox.DefaultFormat(dtf));
		dateBoxTo.getTextBox().setReadOnly(Boolean.TRUE);

		setDateRange(FilterHelper.getCommonDate(FilterHelper.ONE_MONTH_AGO_PARAM), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM));

		// Disable out of range dates
		dateBoxFrom.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBoxFrom.getDatePicker(), null, dateBoxTo.getValue());
			}
		});
		dateBoxTo.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBoxTo.getDatePicker(), dateBoxFrom.getValue(), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM));
			}
		});

	}

	/**
	 * Set the date range
	 * 
	 * @param from
	 * @param to
	 */
	private void setDateRange(Date from, Date to) {
		dateRange.setFrom(from);
		dateRange.setTo(to);
	}

	/**
	 * Set programmatically the value of the DateBoxes
	 * 
	 * @param from
	 * @param to
	 */
	private void setDateBoxes(Date from, Date to) {
		dateBoxFrom.setValue(from);
		dateBoxTo.setValue(to);
	}

	/**
	 * Check if out of range clicked
	 * 
	 * @param event
	 */
	@UiHandler("dateBoxFrom")
	void onChangedSelectedFrom(ValueChangeEvent<Date> event) {
		if (event.getValue().after(dateBoxTo.getValue())) {
			dateBoxFrom.setValue(dateRange.getFrom());
		}
	}

	/**
	 * Check if out of range clicked
	 * 
	 * @param event
	 */
	@UiHandler("dateBoxTo")
	void onChangedSelectedTo(ValueChangeEvent<Date> event) {
		if (event.getValue().before(dateBoxFrom.getValue()) || event.getValue().after(FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM))) {
			dateBoxTo.setValue(dateRange.getTo());
		}
	}

	/**
	 * Fire custom date selected
	 * 
	 * @param event
	 */
	@UiHandler("applyDateRange")
	void onApplyDateRangeButtonClicked(ClickEvent event) {
		setValue(dateBoxFrom.getValue(), dateBoxTo.getValue(), Boolean.TRUE);
	}

	@UiHandler("oneWeekLink")
	void onSevenDaysLinkClicked(ClickEvent event) {
		setValue(FilterHelper.getCommonDate(FilterHelper.ONE_WEEK_AGO_PARAM), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM), Boolean.TRUE);
	}

	@UiHandler("oneMonthLink")
	void onOneMonthLinkClicked(ClickEvent event) {
		setValue(FilterHelper.getCommonDate(FilterHelper.ONE_MONTH_AGO_PARAM), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM), Boolean.TRUE);
	}

	@UiHandler("threeMonthsLink")
	void onThreeMonthsLinkClicked(ClickEvent event) {
		setValue(FilterHelper.getCommonDate(FilterHelper.SIX_MONTHS_AGO_PARAM), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM), Boolean.TRUE);
	}

	@UiHandler("sixMonthsLink")
	void onSixMonthsLinkClicked(ClickEvent event) {
		setValue(FilterHelper.getCommonDate(FilterHelper.SIX_MONTHS_AGO_PARAM), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM), Boolean.TRUE);
	}

	@UiHandler("oneYearLink")
	void onOneYearLinkClicked(ClickEvent event) {
		setValue(FilterHelper.getCommonDate(FilterHelper.ONE_YEAR_AGO_PARAM), FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM), Boolean.TRUE);
	}

	/**
	 * Highlight link changing the style
	 */
	private void highlightLink() {
		clearLinkHighlight();
		Anchor link = lookForDefaultLink();
		if (link != null) {
			link.getElement().addClassName(style.highlight());
		}
	}

	private Anchor lookForDefaultLink() {
		Anchor link = null;
		if (dateBoxTo.getValue().equals(FilterHelper.getCommonDate(FilterHelper.TODAY_PARAM))) {
			if (dateBoxFrom.getValue().equals(FilterHelper.getCommonDate(FilterHelper.ONE_WEEK_AGO_PARAM))) {
				link = oneWeekLink;
			} else if (dateBoxFrom.getValue().equals(FilterHelper.getCommonDate(FilterHelper.ONE_MONTH_AGO_PARAM))) {
				link = oneMonthLink;
			} else if (dateBoxFrom.getValue().equals(FilterHelper.getCommonDate(FilterHelper.THREE_MONTHS_AGO_PARAM))) {
				link = threeMonthsLink;
			} else if (dateBoxFrom.getValue().equals(FilterHelper.getCommonDate(FilterHelper.SIX_MONTHS_AGO_PARAM))) {
				link = sixMonthsLink;
			} else if (dateBoxFrom.getValue().equals(FilterHelper.getCommonDate(FilterHelper.ONE_YEAR_AGO_PARAM))) {
				link = oneYearLink;
			}
		}
		return link;
	}

	/**
	 * Clear highlight style for all links
	 */
	private void clearLinkHighlight() {
		oneWeekLink.getElement().removeClassName(style.highlight());
		oneMonthLink.getElement().removeClassName(style.highlight());
		threeMonthsLink.getElement().removeClassName(style.highlight());
		sixMonthsLink.getElement().removeClassName(style.highlight());
		oneYearLink.getElement().removeClassName(style.highlight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateRange> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	@Override
	public DateRange getValue() {
		return dateRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(DateRange value) {
		setValue(value, Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	@Override
	public void setValue(DateRange value, boolean fireEvents) {

		if (!value.getFrom().equals(dateBoxFrom.getValue()) || !value.getTo().equals(dateBoxTo.getValue())) {
			setDateBoxes(value.getFrom(), value.getTo());
		}
		highlightLink();

		if (fireEvents) {
			DateRangeChangeEvent.fireIfNotEqualDateRanges(this, dateRange, value);
		}
		setDateBoxes(value.getFrom(), value.getTo());
	}

	private void setValue(Date from, Date to, boolean fireEvents) {
		DateRange dr = new DateRange();
		dr.setFrom(from);
		dr.setTo(to);
		setValue(dr, fireEvents);
	}

}
