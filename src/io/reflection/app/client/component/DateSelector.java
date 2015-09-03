//
//  DateSelector.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 23 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.DateRangeChangeEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author stefanocapuzzi
 * 
 */
public class DateSelector extends Composite implements HasValue<DateRange> {

	private static DateSelectorUiBinder uiBinder = GWT.create(DateSelectorUiBinder.class);

	interface DateSelectorUiBinder extends UiBinder<Widget, DateSelector> {}

	public interface PresetDateRange {
		String getName();

		DateRange getDateRange();
	}

	private List<PresetDateRange> presetDateRangeList = null;
	private HashMap<String, PresetDateRange> lookupValuePresetDateRange = new HashMap<String, PresetDateRange>();

	@UiField FormDateBox dateBoxFrom;
	@UiField FormDateBox dateBoxTo;
	@UiField Selector selectFixedRange;
	@UiField Button applyDateRange;
	@UiField DivElement calendarContainer;

	private Date disableBefore;

	private DateRange dateRange = new DateRange();

	public DateSelector(final Date disableBefore) {
		initWidget(uiBinder.createAndBindUi(this));

		this.disableBefore = disableBefore;

		setDateRange(FilterHelper.getDaysAgo(29), FilterHelper.getDaysAgo(2));

		// Disable out of range dates
		dateBoxFrom.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBoxFrom.getDatePicker(), disableBefore, dateBoxTo.getValue());
			}
		});
		dateBoxTo.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBoxTo.getDatePicker(), dateBoxFrom.getValue(),
						(SessionController.get().isLoggedInUserAdmin() ? FilterHelper.getToday() : FilterHelper.getDaysAgo(2)));
			}
		});
	}

	public DateSelector() {
		this(null);
	}

	public void addFixedRange(PresetDateRange fixedRange) {
		if (fixedRange != null) {
			if (presetDateRangeList == null) {
				presetDateRangeList = new ArrayList<PresetDateRange>();
			}

			presetDateRangeList.add(fixedRange);

			showRanges();
		}
	}

	public void addFixedRanges(List<PresetDateRange> fixedRanges) {
		if (fixedRanges != null && fixedRanges.size() > 0) {
			if (this.presetDateRangeList == null) {
				this.presetDateRangeList = new ArrayList<PresetDateRange>();
			}

			this.presetDateRangeList.addAll(fixedRanges);

			showRanges();
		}
	}

	private void showRanges() {
		lookupValuePresetDateRange.clear();
		selectFixedRange.clear();
		for (final PresetDateRange fixedRange : presetDateRangeList) {
			selectFixedRange.addItem(fixedRange.getName(), fixedRange.getName());
			lookupValuePresetDateRange.put(fixedRange.getName(), fixedRange);
		}
	}

	@UiHandler("selectFixedRange")
	void onCountryValueChanged(ChangeEvent event) {
		setValue(lookupValuePresetDateRange.get(selectFixedRange.getSelectedValue()).getDateRange(), true);
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

	public void setEnabled(boolean enabled) {
		dateBoxFrom.setEnabled(enabled);
		dateBoxTo.setEnabled(enabled);
		selectFixedRange.setEnabled(enabled);
		if (enabled) {
			applyDateRange.setEnabled(!CalendarUtil.isSameDate(dateBoxFrom.getValue(), dateRange.getFrom())
					|| !CalendarUtil.isSameDate(dateBoxTo.getValue(), dateRange.getTo()));
		} else {
			applyDateRange.setEnabled(false);
		}
	}

	private boolean isPresetRange(DateRange dateRange) {
		boolean isPresetRange = false;
		for (PresetDateRange pdr : presetDateRangeList) {
			if (pdr.getDateRange().equals(dateRange)) {
				isPresetRange = true;
				break;
			}
		}
		return isPresetRange;
	}

	private String getDateRangeValue(DateRange dateRange) {
		String value = null;
		for (PresetDateRange pdr : presetDateRangeList) {
			if (pdr.getDateRange().equals(dateRange)) {
				value = pdr.getName();
			}
		}
		return value;
	}

	public void setTooltip(String text) {
		calendarContainer.addClassName("js-tooltip");
		calendarContainer.setAttribute("data-tooltip", text);
	}

	/**
	 * Check if out of range clicked
	 * 
	 * @param event
	 */
	@UiHandler("dateBoxFrom")
	void onChangedSelectedFrom(ValueChangeEvent<Date> event) {
		if (disableBefore != null) {
			if (event.getValue().after(dateBoxTo.getValue()) || event.getValue().before(disableBefore)) {
				dateBoxFrom.setValue(dateRange.getFrom());
			} else {
				applyDateRange.setEnabled(!CalendarUtil.isSameDate(dateBoxFrom.getValue(), dateRange.getFrom())
						|| !CalendarUtil.isSameDate(dateBoxTo.getValue(), dateRange.getTo()));
			}
		} else {
			if (event.getValue().after(dateBoxTo.getValue())) {
				dateBoxFrom.setValue(dateRange.getFrom());
			} else {
				applyDateRange.setEnabled(!CalendarUtil.isSameDate(dateBoxFrom.getValue(), dateRange.getFrom())
						|| !CalendarUtil.isSameDate(dateBoxTo.getValue(), dateRange.getTo()));
			}
		}
	}

	/**
	 * Check if out of range clicked
	 * 
	 * @param event
	 */
	@UiHandler("dateBoxTo")
	void onChangedSelectedTo(ValueChangeEvent<Date> event) {
		if (event.getValue().before(dateBoxFrom.getValue()) || event.getValue().after(FilterHelper.getToday())) {
			dateBoxTo.setValue(dateRange.getTo());
		} else {
			applyDateRange.setEnabled(!CalendarUtil.isSameDate(dateBoxFrom.getValue(), dateRange.getFrom())
					|| !CalendarUtil.isSameDate(dateBoxTo.getValue(), dateRange.getTo()));
		}
	}

	/**
	 * Fire custom date selected
	 * 
	 * @param event
	 */
	@UiHandler("applyDateRange")
	void onApplyDateRangeButtonClicked(ClickEvent event) {
		setValue(dateBoxFrom.getValue(), dateBoxTo.getValue(), true);
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

	private void setValue(Date from, Date to, boolean fireEvents) {
		DateRange dr = new DateRange();
		dr.setFrom(from);
		dr.setTo(to);
		setValue(dr, fireEvents);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(DateRange value) {
		setValue(value, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	@Override
	public void setValue(DateRange value, boolean fireEvents) {

		if (!FilterHelper.equalDate(value.getFrom(), dateBoxFrom.getValue()) || !FilterHelper.equalDate(value.getTo(), dateBoxTo.getValue())) {
			setDateBoxes(value.getFrom(), value.getTo());
		}

		applyDateRange.setEnabled(false);

		if (isPresetRange(value)) {
			if (getDateRangeValue(value) != null) {
				selectFixedRange.setSelectedIndex(selectFixedRange.getValueIndex(getDateRangeValue(value)), false);
			}
		} else {
			selectFixedRange.selectDefault();
		}

		if (fireEvents) {
			DateRangeChangeEvent.fireIfNotEqualDateRanges(this, dateRange, value);
		}

		setDateRange(value.getFrom(), value.getTo()); // Update date range
	}

}
