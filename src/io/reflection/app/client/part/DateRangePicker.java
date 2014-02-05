// 
//  DateRangePicker.java 
//  storedata 
// 
//  Created by William Shakour (billy1380) on 13 Jan 2014. 
//  Copyright © 2014 Reflection.io Ltd. All rights reserved. 
// 
package io.reflection.app.client.part;

import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.res.Styles.ReflectionStyles;

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

	@UiField ReflectionStyles style;

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
		today = normalizeDate(today);

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
				highlightRange();
			}
		});
		mToPicker.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				styleDatePickerTo();
				highlightRange();
			}
		});

	}

	/**
	 * Normalize the date being sure the milliseconds are set at zero.
	 * 
	 * @param date
	 *            Date to be set at noon
	 * @return temp The new date set at noon
	 */
	@SuppressWarnings("deprecation")
	private Date normalizeDate(Date date) {
		Date newDate = new Date(0L);
		newDate.setDate(date.getDate());
		newDate.setMonth(date.getMonth());
		newDate.setYear(date.getYear());
		newDate.setHours(0);
		return newDate;
	}

	/**
	 * Add highlighted style to current range in both Datepicker
	 */
	private void highlightRange() {
		mFromPicker.addStyleToDates(style.highlightRangeBoundaries(), mValue.getFrom());
		mFromPicker.addStyleToDates(style.highlightRangeBoundaries(), mValue.getTo());
		mToPicker.addStyleToDates(style.highlightRangeBoundaries(), mValue.getFrom());
		mToPicker.addStyleToDates(style.highlightRangeBoundaries(), mValue.getTo());

		Date highlightDate = normalizeDate(mValue.getFrom());
		CalendarUtil.addDaysToDate(highlightDate, 1);
		while (highlightDate.before(mValue.getTo())) {

			mFromPicker.addStyleToDates(style.highlightRange(), highlightDate);
			mToPicker.addStyleToDates(style.highlightRange(), highlightDate);
			CalendarUtil.addDaysToDate(highlightDate, 1);
		}
	}

	/**
	 * Remove highlighted style from From Datepicker
	 */
	private void resetHighlightRangeFromPicker() {
		Date firstShownOnCalendarToPicker = normalizeDate(mFromPicker.getFirstDate());
		Date lastShownOnCalendarToPicker = normalizeDate(mFromPicker.getLastDate());

		while (firstShownOnCalendarToPicker.before(lastShownOnCalendarToPicker) || firstShownOnCalendarToPicker.equals(lastShownOnCalendarToPicker)) {
			mFromPicker.removeStyleFromDates(style.highlightRange(), firstShownOnCalendarToPicker);
			mFromPicker.removeStyleFromDates(style.highlightRangeBoundaries(), firstShownOnCalendarToPicker);
			CalendarUtil.addDaysToDate(firstShownOnCalendarToPicker, 1);
		}
	}

	/**
	 * Remove highlighted style from To Datepicker
	 */
	private void resetHighlightRangeToPicker() {
		Date firstShownOnCalendarToPicker = normalizeDate(mToPicker.getFirstDate());
		Date lastShownOnCalendarToPicker = normalizeDate(mToPicker.getLastDate());

		while (firstShownOnCalendarToPicker.before(lastShownOnCalendarToPicker) || firstShownOnCalendarToPicker.equals(lastShownOnCalendarToPicker)) {
			mToPicker.removeStyleFromDates(style.highlightRange(), firstShownOnCalendarToPicker);
			mToPicker.removeStyleFromDates(style.highlightRangeBoundaries(), firstShownOnCalendarToPicker);
			CalendarUtil.addDaysToDate(firstShownOnCalendarToPicker, 1);
		}
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
		Date dateClicked = normalizeDate(event.getValue());
		if (dateClicked.after(normalizeDate(mToPicker.getValue()))) {
			Window.alert("Date not valid");
			mFromPicker.setValue(normalizeDate(mValue.getFrom()));
			mFromPicker.setCurrentMonth(mValue.getFrom());

		} else {

			mValue.setFrom(dateClicked);
			styleDatePickerTo();
			resetHighlightRangeFromPicker();
			resetHighlightRangeToPicker();

			highlightRange();
		}
	}

	/**
	 * Style Datepicker To
	 */
	private void styleDatePickerTo() {
		enableDateRangeToPicker();

		Date firstShownOnCalendar = normalizeDate(mToPicker.getFirstDate());
		Date lastShownOnCalendar = normalizeDate(mToPicker.getLastDate());

		List<Date> dates = new ArrayList<Date>();

		// disable dates before the selected From date
		while ((firstShownOnCalendar.before(lastShownOnCalendar) || firstShownOnCalendar.equals(lastShownOnCalendar))
				&& firstShownOnCalendar.before(mValue.getFrom())) {

			dates.add(normalizeDate(firstShownOnCalendar));
			CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}

		firstShownOnCalendar = normalizeDate(mToPicker.getFirstDate());

		// disable future dates
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || firstShownOnCalendar.equals(lastShownOnCalendar)) && lastShownOnCalendar.after(today)) {

			dates.add(normalizeDate(lastShownOnCalendar));
			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mToPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Function called when the user selects a date on the TO Datepicker
	 */
	@UiHandler("mToPicker")
	void onChangedSelectedValueToOnPressFrom(ValueChangeEvent<Date> event) {
		Date dateClicked = normalizeDate(event.getValue());
		if (dateClicked.before(normalizeDate(mFromPicker.getValue())) || dateClicked.after(today)) {
			Window.alert("Date not valid");
			mToPicker.setValue(normalizeDate(mValue.getTo()));
			mToPicker.setCurrentMonth(mValue.getTo());
		} else {
			mValue.setTo(dateClicked);
			styleDatePickerFrom();
			resetHighlightRangeFromPicker();
			resetHighlightRangeToPicker();

			highlightRange();
		}
	}

	/**
	 * Style Datepicker From
	 */
	private void styleDatePickerFrom() {
		enableDateRangeFromPicker();

		Date firstShownOnCalendar = normalizeDate(mFromPicker.getFirstDate());
		Date lastShownOnCalendar = normalizeDate(mFromPicker.getLastDate());

		List<Date> dates = new ArrayList<Date>();

		// disable dates after the selected To date
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || lastShownOnCalendar.equals(firstShownOnCalendar))
				&& lastShownOnCalendar.after(mValue.getTo())) {

			dates.add(normalizeDate(lastShownOnCalendar));
			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}

		mFromPicker.setTransientEnabledOnDates(false, dates);

	}

	/**
	 * Enable every date in the showed month
	 */
	private void enableDateRangeFromPicker() {
		List<Date> dates = new ArrayList<Date>();

		Date firstShownOnCalendarFromPicker = normalizeDate(mFromPicker.getFirstDate());
		Date lastShownOnCalendarFromPicker = normalizeDate(mFromPicker.getLastDate());
		while (firstShownOnCalendarFromPicker.before(lastShownOnCalendarFromPicker) || firstShownOnCalendarFromPicker.equals(lastShownOnCalendarFromPicker)) {
			dates.add(normalizeDate(firstShownOnCalendarFromPicker));
			CalendarUtil.addDaysToDate(firstShownOnCalendarFromPicker, 1);
		}
		mFromPicker.setTransientEnabledOnDates(true, dates);
		resetHighlightRangeFromPicker();
	}

	/**
	 * Enable every date in the showed month
	 */
	private void enableDateRangeToPicker() {
		List<Date> dates = new ArrayList<Date>();

		Date firstShownOnCalendarToPicker = normalizeDate(mToPicker.getFirstDate());
		Date lastShownOnCalendarToPicker = normalizeDate(mToPicker.getLastDate());

		while (firstShownOnCalendarToPicker.before(lastShownOnCalendarToPicker) || firstShownOnCalendarToPicker.equals(lastShownOnCalendarToPicker)) {
			dates.add(normalizeDate(firstShownOnCalendarToPicker));
			CalendarUtil.addDaysToDate(firstShownOnCalendarToPicker, 1);
		}
		mToPicker.setTransientEnabledOnDates(true, dates);
		resetHighlightRangeToPicker();
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