// 
//  DateRangePicker.java 
//  storedata 
// 
//  Created by William Shakour (billy1380) on 13 Jan 2014. 
//  Copyright © 2014 Reflection.io Ltd. All rights reserved. 
// 
package io.reflection.app.client.part;

import io.reflection.app.client.part.datatypes.DateRange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author billy1380
 * 
 */
public class DateRangePicker extends Composite implements HasValue<DateRange>, IsEditor<LeafValueEditor<DateRange>> {

	private static DateRangePickerUiBinder uiBinder = GWT.create(DateRangePickerUiBinder.class);

	interface DateRangePickerUiBinder extends UiBinder<Widget, DateRangePicker> {}

	DateRange mValue;
	private LeafValueEditor<DateRange> mEditor;

	@UiField DatePicker mFromPicker;
	@UiField DatePicker mToPicker;

	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		// initialize date range
		mValue = new DateRange();

		mValue.setTo(new Date());
		mToPicker.setValue(mValue.getTo(), false);

		Date oneWeekAgo = new Date();
		com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(oneWeekAgo, -7);
		mValue.setFrom(oneWeekAgo);
		mFromPicker.setValue(mValue.getFrom(), false);

		/**
		 * Function called when the datepicker is refreshed, e.g. first load or every time the month changes
		 */
		mFromPicker.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {

				styleDatePickerFrom();
			}
		});

		mToPicker.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				styleDatePickerTo();

			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	@Override
	public DateRange getValue() {

		if (mValue == null) {
			mValue = new DateRange();
		}

		return mValue;
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.editor.client.IsEditor#asEditor()
	 */
	@Override
	public LeafValueEditor<DateRange> asEditor() {
		if (mEditor == null) {
			mEditor = TakesValueEditor.of(this);
		}
		return mEditor;
	}

	/**
	 * Function called when the user selects a date on the FROM datepicker
	 */
	@UiHandler("mFromPicker")
	void onChangedSelectedValueFrom(ValueChangeEvent<Date> event) {

		if (event.getValue().after(mToPicker.getValue())) {
			Window.alert("Date not valid");
		} else {

			mValue.setFrom(event.getValue());

			styleDatePickerTo();
		}
	}

	private void styleDatePickerTo() {
		enableDateRangeToPicker();

		Date firstShownOnCalendar = new Date(mToPicker.getFirstDate().toString());
		Date lastShownOnCalendar = new Date(mToPicker.getLastDate().toString());
		com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(lastShownOnCalendar, 1);

		List<Date> dates = new ArrayList<Date>();

		while (firstShownOnCalendar.before(lastShownOnCalendar) && firstShownOnCalendar.before(mValue.getFrom())) {

			dates.add(new Date(firstShownOnCalendar.toString()));
			com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}

		mToPicker.setTransientEnabledOnDates(false, dates);

		// mToPicker.addTransientStyleToDates(styleName, dates);

	}

	/**
	 * Function called when the user selects a date on the TO datepicker
	 */
	@UiHandler("mToPicker")
	void onChangedSelectedValueToOnPressFrom(ValueChangeEvent<Date> event) {
		if (event.getValue().before(mFromPicker.getValue())) {
			Window.alert("Date not valid");
		} else {

			mValue.setTo(event.getValue());

			styleDatePickerFrom();
		}
	}

	/**
	 * 
	 */
	private void styleDatePickerFrom() {
		enableDateRangeFromPicker();

		Date firstShownOnCalendar = new Date(mFromPicker.getFirstDate().toString());
		com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(firstShownOnCalendar, -1);
		Date lastShownOnCalendar = new Date(mFromPicker.getLastDate().toString());

		List<Date> dates = new ArrayList<Date>();

		while (lastShownOnCalendar.after(firstShownOnCalendar) && lastShownOnCalendar.after(mValue.getTo())) {

			dates.add(new Date(lastShownOnCalendar.toString()));

			com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mFromPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Enable every date in the showed month
	 */
	private void enableDateRangeFromPicker() {
		List<Date> datesFromPicker = new ArrayList<Date>();

		Date firstShownOnCalendarFromPicker = new Date(mFromPicker.getFirstDate().toString());
		Date lastShownOnCalendarFromPicker = new Date(mFromPicker.getLastDate().toString());
		while (firstShownOnCalendarFromPicker.before(lastShownOnCalendarFromPicker)) {
			datesFromPicker.add(new Date(firstShownOnCalendarFromPicker.toString()));
			com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(firstShownOnCalendarFromPicker, 1);
		}
		datesFromPicker.add(lastShownOnCalendarFromPicker);
		mFromPicker.setTransientEnabledOnDates(true, datesFromPicker);
	}

	/**
	 * Enable every date in the showed month
	 */
	private void enableDateRangeToPicker() {
		List<Date> datesToPicker = new ArrayList<Date>();
		Date firstShownOnCalendarToPicker = new Date(mToPicker.getFirstDate().toString());
		Date lastShownOnCalendarToPicker = new Date(mToPicker.getLastDate().toString());
		while (firstShownOnCalendarToPicker.before(lastShownOnCalendarToPicker)) {
			datesToPicker.add(new Date(firstShownOnCalendarToPicker.toString()));
			com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate(firstShownOnCalendarToPicker, 1);
		}
		datesToPicker.add(lastShownOnCalendarToPicker);
		mToPicker.setTransientEnabledOnDates(true, datesToPicker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateRange> handler) {
		// TODO Auto-generated method stub
		return null;
	}

}