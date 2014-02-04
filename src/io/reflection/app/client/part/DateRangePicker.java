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

	DateRange mValue = new DateRange(); // initialize date range
	private LeafValueEditor<DateRange> mEditor;

	@UiField DatePicker mFromPicker;
	@UiField DatePicker mToPicker;

	private static Date today = new Date();

	/**
	 * Every operation between dates happens with dates set at noon, since the datepicker return dates at noon.
	 */
	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		// initialize today and set it at midnight, since when pick the today date from DatePicker is set to noon, and the after function doesn't work
		// as expected in the morning
		today = setDateAtNoon(today);

		mValue.setTo(today);
		mToPicker.setValue(mValue.getTo(), false);

		Date oneMonthAgo = (Date) today.clone();
		CalendarUtil.addMonthsToDate(oneMonthAgo, -1);
		mValue.setFrom(oneMonthAgo);
		mFromPicker.setValue(mValue.getFrom(), false);
		
		mFromPicker.setCurrentMonth(oneMonthAgo); // Show the current selected month
		
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

	/**
	 * Set the date at noon, being sure the milliseconds are set at zero.
	 * 
	 * @param date Date to be set at noon
	 * @return temp The new date set at noon 
	 */
	@SuppressWarnings("deprecation")
	private Date setDateAtNoon(Date date) {
		Date newDate = new Date(0L);
		newDate.setDate(date.getDate());
		newDate.setMonth(date.getMonth());
		newDate.setYear(date.getYear());
		newDate.setHours(0);
		return newDate;
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
		Date dateClicked = setDateAtNoon(event.getValue());
		if (dateClicked.after(setDateAtNoon(mToPicker.getValue()))) {
			Window.alert("Date not valid");
		} else {

			mValue.setFrom(dateClicked);

			styleDatePickerTo();
		}
	}

	private void styleDatePickerTo() {
		enableDateRangeToPicker();

		Date firstShownOnCalendar = setDateAtNoon(mToPicker.getFirstDate());
		Date lastShownOnCalendar = setDateAtNoon(mToPicker.getLastDate());

		List<Date> dates = new ArrayList<Date>();

		// disable dates before the selected From date
		while ((firstShownOnCalendar.before(lastShownOnCalendar) || firstShownOnCalendar.equals(lastShownOnCalendar))
				&& firstShownOnCalendar.before(mValue.getFrom())) {

			dates.add(setDateAtNoon(firstShownOnCalendar));
			CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}

		firstShownOnCalendar = setDateAtNoon(mToPicker.getFirstDate());

		// disable future dates
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || firstShownOnCalendar.equals(lastShownOnCalendar)) && lastShownOnCalendar.after(today)) {

			dates.add(setDateAtNoon(lastShownOnCalendar));
			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mToPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Function called when the user selects a date on the TO Datepicker
	 */
	@UiHandler("mToPicker")
	void onChangedSelectedValueToOnPressFrom(ValueChangeEvent<Date> event) {
		Date dateClicked = setDateAtNoon(event.getValue());
		if (dateClicked.before(setDateAtNoon(mFromPicker.getValue())) || dateClicked.after(today)) {
			Window.alert("Date not valid");
		} else {

			mValue.setTo(dateClicked);

			styleDatePickerFrom();
		}
	}

	/**
	 * 
	 */
	private void styleDatePickerFrom() {
		enableDateRangeFromPicker();

		Date firstShownOnCalendar = setDateAtNoon(mFromPicker.getFirstDate());
		Date lastShownOnCalendar = setDateAtNoon(mFromPicker.getLastDate());

		List<Date> dates = new ArrayList<Date>();
		
		// disable dates after the selected To date
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || lastShownOnCalendar.equals(firstShownOnCalendar))
				&& lastShownOnCalendar.after(mValue.getTo())) {

			dates.add(setDateAtNoon(lastShownOnCalendar));

			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mFromPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Enable every date in the showed month
	 */
	private void enableDateRangeFromPicker() {
		List<Date> datesFromPicker = new ArrayList<Date>();

		Date firstShownOnCalendarFromPicker = setDateAtNoon(mFromPicker.getFirstDate());
		Date lastShownOnCalendarFromPicker = setDateAtNoon(mFromPicker.getLastDate());
		while (firstShownOnCalendarFromPicker.before(lastShownOnCalendarFromPicker) || firstShownOnCalendarFromPicker.equals(lastShownOnCalendarFromPicker)) {
			datesFromPicker.add(setDateAtNoon(firstShownOnCalendarFromPicker));
			CalendarUtil.addDaysToDate(firstShownOnCalendarFromPicker, 1);
		}
		mFromPicker.setTransientEnabledOnDates(true, datesFromPicker);
	}

	/**
	 * Enable every date in the showed month
	 */
	private void enableDateRangeToPicker() {
		List<Date> datesToPicker = new ArrayList<Date>();

		// Window.alert("firstShown: " + mToPicker.getFirstDate().toString() + "	-	lastShown: " + mToPicker.getLastDate().toString());

		Date firstShownOnCalendarToPicker = setDateAtNoon(mToPicker.getFirstDate());
		Date lastShownOnCalendarToPicker = setDateAtNoon(mToPicker.getLastDate());

		// Window.alert("firstShown: " + firstShownOnCalendarToPicker + "		lastShown: " + lastShownOnCalendarToPicker);

		while (firstShownOnCalendarToPicker.before(lastShownOnCalendarToPicker) || firstShownOnCalendarToPicker.equals(lastShownOnCalendarToPicker)) {
			datesToPicker.add(setDateAtNoon(firstShownOnCalendarToPicker));
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