// 
//  DateRangePicker.java 
//  storedata 
// 
//  Created by William Shakour (billy1380) on 13 Jan 2014. 
//  Copyright © 2014 Reflection.io Ltd. All rights reserved. 
// 
package io.reflection.app.client.part;

import io.reflection.app.client.helper.FilterHelper;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

	public interface Style extends CssResource {
		String inline();

		String highlightRange();

		String fromHighlightRangeBoundary();

		String toHighlightRangeBoundary();
	}

	@UiField Style style;

	private List<Date> dates = new ArrayList<Date>();

	private static DateRangePickerUiBinder uiBinder = GWT.create(DateRangePickerUiBinder.class);

	interface DateRangePickerUiBinder extends UiBinder<Widget, DateRangePicker> {}

	DateRange mValue = new DateRange(); // initialize date range
	private LeafValueEditor<DateRange> mEditor;

	@UiField DatePicker mFromPicker;
	@UiField DatePicker mToPicker;

	/**
	 * Every operation between dates happens with dates set at noon, since the datepicker return dates at noon.
	 */
	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		// add styles so they don't trash existing styles
		mFromPicker.addStyleName(style.inline());
		mToPicker.addStyleName(style.inline());

		// initialize today and set it at midnight, since when pick the today date from DatePicker is set to noon, and the after function doesn't work
		// as expected in the morning

		mValue.setTo(FilterHelper.getToday());
		mToPicker.setValue(mValue.getTo(), false);

		Date oneMonthAgo = (Date) FilterHelper.getToday().clone();
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
				FilterHelper.disableOutOfRangeDates(mFromPicker, null, mToPicker.getValue());
				resetHighlightRangeFromPicker();
				resetHighlightRangeToPicker();
				highlightRange();
			}
		});
		mToPicker.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(mToPicker, mFromPicker.getValue(), FilterHelper.getToday());
				resetHighlightRangeFromPicker();
				resetHighlightRangeToPicker();
				highlightRange();
			}
		});

	}

	/**
	 * Function called when the user selects a date on the FROM datepicker
	 */
	@UiHandler("mFromPicker")
	void onChangedSelectedValueFrom(ValueChangeEvent<Date> event) {
		Date dateClicked = event.getValue();
		if (dateClicked.after(mToPicker.getValue())) {
			// Window.alert("Date not valid");
			mFromPicker.setValue(mValue.getFrom());
			mFromPicker.setCurrentMonth(mValue.getFrom());
		} else {
			mValue.setFrom(dateClicked);
			FilterHelper.disableOutOfRangeDates(mToPicker, mFromPicker.getValue(), FilterHelper.getToday());
			resetHighlightRangeFromPicker();
			resetHighlightRangeToPicker();
			highlightRange();
		}
	}

	/**
	 * Function called when the user selects a date on the TO Datepicker
	 */
	@UiHandler("mToPicker")
	void onChangedSelectedValueToOnPressFrom(ValueChangeEvent<Date> event) {
		Date dateClicked = event.getValue();
		if (dateClicked.before(mFromPicker.getValue()) || dateClicked.after(FilterHelper.getToday())) {
			// Window.alert("Date not valid");
			mToPicker.setValue(mValue.getTo());
			mToPicker.setCurrentMonth(mValue.getTo());
		} else {
			mValue.setTo(dateClicked);
			FilterHelper.disableOutOfRangeDates(mFromPicker, null, mToPicker.getValue());
			resetHighlightRangeFromPicker();
			resetHighlightRangeToPicker();
			highlightRange();
		}
	}

	/**
	 * Add highlighted style to current range in both Datepicker
	 */
	private void highlightRange() {
		if (mValue.getDaysBetween() == 0) {
			mFromPicker.addStyleToDates(style.highlightRange(), mValue.getFrom());
			mToPicker.addStyleToDates(style.highlightRange(), mValue.getFrom());
		} else {
			mFromPicker.addStyleToDates(style.fromHighlightRangeBoundary(), mValue.getFrom());
			mFromPicker.addStyleToDates(style.toHighlightRangeBoundary(), mValue.getTo());
			mToPicker.addStyleToDates(style.fromHighlightRangeBoundary(), mValue.getFrom());
			mToPicker.addStyleToDates(style.toHighlightRangeBoundary(), mValue.getTo());
			mFromPicker.addStyleToDates(style.highlightRange(), mValue.getDatesBetween());
			mToPicker.addStyleToDates(style.highlightRange(), mValue.getDatesBetween());
			dates.clear();
		}
	}

	/**
	 * Remove highlighted style from From Datepicker
	 */
	private void resetHighlightRangeFromPicker() {
		Date firstShownOnCalendar = CalendarUtil.copyDate(mFromPicker.getFirstDate());
		Date lastShownOnCalendar = CalendarUtil.copyDate(mFromPicker.getLastDate());

		while (firstShownOnCalendar.before(lastShownOnCalendar) || CalendarUtil.isSameDate(firstShownOnCalendar, lastShownOnCalendar)) {
			dates.add(CalendarUtil.copyDate(firstShownOnCalendar));
			CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}
		mFromPicker.removeStyleFromDates(style.highlightRange(), dates);
		mFromPicker.removeStyleFromDates(style.fromHighlightRangeBoundary(), dates);
		mFromPicker.removeStyleFromDates(style.toHighlightRangeBoundary(), dates);
		dates.clear();
	}

	/**
	 * Remove highlighted style from To Datepicker
	 */
	private void resetHighlightRangeToPicker() {
		Date firstShownOnCalendar = CalendarUtil.copyDate(mToPicker.getFirstDate());
		Date lastShownOnCalendar = CalendarUtil.copyDate(mToPicker.getLastDate());

		while (firstShownOnCalendar.before(lastShownOnCalendar) || CalendarUtil.isSameDate(firstShownOnCalendar, lastShownOnCalendar)) {
			dates.add(CalendarUtil.copyDate(firstShownOnCalendar));
			CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}
		mToPicker.removeStyleFromDates(style.highlightRange(), dates);
		mToPicker.removeStyleFromDates(style.fromHighlightRangeBoundary(), dates);
		mToPicker.removeStyleFromDates(style.toHighlightRangeBoundary(), dates);
		dates.clear();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateRange> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void setCurrentMonth(DateRange dateRange) {
		if (dateRange != null) {
			if (dateRange.getFrom() != null) {
				mFromPicker.setCurrentMonth(dateRange.getFrom());
			}

			if (dateRange.getTo() != null) {
				mToPicker.setCurrentMonth(dateRange.getTo());
			}
		}
	}

}