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
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author billy1380
 * 
 */
public class DateRangePicker extends Composite implements HasValue<DateRange>, IsEditor<LeafValueEditor<DateRange>> {

	private static DateRangePickerUiBinder uiBinder = GWT.create(DateRangePickerUiBinder.class);

	interface DateRangePickerUiBinder extends UiBinder<Widget, DateRangePicker> {}

	private static Date today;

	DateRange mValue;
	private LeafValueEditor<DateRange> mEditor;

	@UiField DatePicker mFromPicker;
	@UiField DatePicker mToPicker;

	@SuppressWarnings("deprecation")
	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		// initialize today and set it just before midnight, since when pick the today date from DatePicker is set to noon, and the after function doesn't work
		// as expected in the morning
		today = new Date();
		today.setHours(23);
		today.setMinutes(59);
		today.setSeconds(59);

		// initialize date range
		mValue = new DateRange();

		Date fistDatePickerTo = new Date();
		fistDatePickerTo.setHours(12);
		mValue.setTo(fistDatePickerTo);
		mToPicker.setValue(mValue.getTo(), false);

		Date oneMonthAgo = new Date();
		oneMonthAgo.setHours(12);
		CalendarUtil.addMonthsToDate(oneMonthAgo, -1);
		mValue.setFrom(oneMonthAgo);
		mFromPicker.setValue(mValue.getFrom(), false);

		/**
		 * Function called when the DatePicker is refreshed, e.g. first load or every time the month changes
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

	@SuppressWarnings("deprecation")
	private void styleDatePickerTo() {
		enableDateRangeToPicker();

		Date firstShownOnCalendar = new Date(mToPicker.getFirstDate().toString());
		Date lastShownOnCalendar = new Date(mToPicker.getLastDate().toString());

		List<Date> dates = new ArrayList<Date>();

		// disable dates before the selected From date
		while ((firstShownOnCalendar.before(lastShownOnCalendar) || firstShownOnCalendar.equals(lastShownOnCalendar))
				&& firstShownOnCalendar.before(mValue.getFrom())) {

			dates.add(new Date(firstShownOnCalendar.toString()));
			CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}

		firstShownOnCalendar = new Date(mToPicker.getFirstDate().toString());

		// disable future dates
		while (lastShownOnCalendar.after(firstShownOnCalendar) && lastShownOnCalendar.after(today)) {

			dates.add(new Date(lastShownOnCalendar.toString()));
			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mToPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Function called when the user selects a date on the TO datepicker
	 */
	@UiHandler("mToPicker")
	void onChangedSelectedValueToOnPressFrom(ValueChangeEvent<Date> event) {
		if (event.getValue().before(mFromPicker.getValue()) || event.getValue().after(today)) {
			Window.alert("Date not valid");
		} else {

			mValue.setTo(event.getValue());

			styleDatePickerFrom();
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void styleDatePickerFrom() {
		enableDateRangeFromPicker();

		Date firstShownOnCalendar = new Date(mFromPicker.getFirstDate().toString());
		Date lastShownOnCalendar = new Date(mFromPicker.getLastDate().toString());

		List<Date> dates = new ArrayList<Date>();
		// disable dates after the selected To date
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || lastShownOnCalendar.equals(firstShownOnCalendar))
				&& lastShownOnCalendar.after(mValue.getTo())) {

			dates.add(new Date(lastShownOnCalendar.toString()));

			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mFromPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Enable every date in the showed month
	 */
	@SuppressWarnings("deprecation")
	private void enableDateRangeFromPicker() {
		List<Date> datesFromPicker = new ArrayList<Date>();

		Date firstShownOnCalendarFromPicker = new Date(mFromPicker.getFirstDate().toString());
		Date lastShownOnCalendarFromPicker = new Date(mFromPicker.getLastDate().toString());
		while (firstShownOnCalendarFromPicker.before(lastShownOnCalendarFromPicker) || firstShownOnCalendarFromPicker.equals(lastShownOnCalendarFromPicker)) {
			datesFromPicker.add(new Date(firstShownOnCalendarFromPicker.toString()));
			CalendarUtil.addDaysToDate(firstShownOnCalendarFromPicker, 1);
		}
		mFromPicker.setTransientEnabledOnDates(true, datesFromPicker);
	}

	/**
	 * Enable every date in the showed month
	 */
	@SuppressWarnings("deprecation")
	private void enableDateRangeToPicker() {
		List<Date> datesToPicker = new ArrayList<Date>();

		//Window.alert("firstShown: " + mToPicker.getFirstDate().toString() + "	-	lastShown: " + mToPicker.getLastDate().toString());

		Date firstShownOnCalendarToPicker = new Date(mToPicker.getFirstDate().toString());
		Date lastShownOnCalendarToPicker = new Date(mToPicker.getLastDate().toString());

		//Window.alert("firstShown: " + firstShownOnCalendarToPicker + "		lastShown: " + lastShownOnCalendarToPicker);

		while (firstShownOnCalendarToPicker.before(lastShownOnCalendarToPicker) || firstShownOnCalendarToPicker.equals(lastShownOnCalendarToPicker)) {
			datesToPicker.add(new Date(firstShownOnCalendarToPicker.toString()));
			CalendarUtil.addDaysToDate(firstShownOnCalendarToPicker, 1);
		}
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