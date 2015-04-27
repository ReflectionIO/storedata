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
import io.reflection.app.client.part.datatypes.DateRangeChangeEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
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

	public interface FixedDateRange {

		String getName();

		DateRange getDateRange();

	}

	@UiField Style style;

	private List<Date> dates = new ArrayList<Date>();
	private List<FixedDateRange> fixedRanges = null;

	private static DateRangePickerUiBinder uiBinder = GWT.create(DateRangePickerUiBinder.class);

	interface DateRangePickerUiBinder extends UiBinder<Widget, DateRangePicker> {}

	DateRange mValue = new DateRange(); // initialize date range
	private LeafValueEditor<DateRange> mEditor;

	@UiField DatePicker mFromPicker;
	@UiField DatePicker mToPicker;

	@UiField HTMLPanel ranges;
	@UiField Button apply;
	@UiField Button cancel;

	@UiField DivElement buttons;

	private Anchor activeFixedRange;
	private Anchor customFixedRange;

	List<HandlerRegistration> fixedRangeRegistrations = new ArrayList<HandlerRegistration>();

	/**
	 * Every operation between dates happens with dates set at noon, since the datepicker return dates at noon.
	 */
	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));

		// add styles so they don't trash existing styles
		mFromPicker.addStyleName(style.inline());
		mToPicker.addStyleName(style.inline());

		// initialize today and set it at midnight, since when pick the today date from DatePicker is set to noon, and the after function doesn't work
		// as expected in the morning

		mValue.setTo(FilterHelper.getToday());
		mToPicker.setValue(mValue.getTo(), false);

		mValue.setFrom(FilterHelper.getMonthsAgo(1));
		mFromPicker.setValue(mValue.getFrom(), false);

		mFromPicker.setCurrentMonth(FilterHelper.getMonthsAgo(1)); // Show the current selected month

		/**
		 * Function called when the DatePicker is refreshed, e.g. first load or every time the month changes
		 */
		mFromPicker.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				resetHighlightRangeFromPicker();
				resetHighlightRangeToPicker();
				highlightRange();
			}
		});

		mToPicker.addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
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
		setFromDate(event.getValue(), true);

	}

	private void setFromDate(Date date, boolean fireEvents) {
		if (!date.after(mToPicker.getValue())) {
			mValue.setFrom(date);
		}

		mFromPicker.setValue(mValue.getFrom(), fireEvents);
		mFromPicker.setCurrentMonth(mValue.getFrom());

		resetHighlightRangeFromPicker();
		resetHighlightRangeToPicker();
		highlightRange();
	}

	/**
	 * Function called when the user selects a date on the TO Datepicker
	 */
	@UiHandler("mToPicker")
	void onChangedSelectedValueToOnPressFrom(ValueChangeEvent<Date> event) {
		Date dateClicked = event.getValue();
		setToDate(dateClicked, true);
	}

	private void setToDate(Date date, boolean fireEvents) {
		if (!date.before(mFromPicker.getValue())) {
			mValue.setTo(date);
		}

		mToPicker.setValue(mValue.getTo(), fireEvents);
		mToPicker.setCurrentMonth(mValue.getTo());

		resetHighlightRangeFromPicker();
		resetHighlightRangeToPicker();
		highlightRange();
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
		if (value != null) {
			boolean fromChanged = false, toChanged = false;
			Date fromDate = null, toDate = null;

			if (value.getFrom() != null) {
				fromDate = mValue.getFrom();
				setFromDate(value.getFrom(), fireEvents);
				fromChanged = true;
			}

			if (value.getTo() != null) {
				toDate = mValue.getTo();
				setToDate(value.getTo(), fireEvents);
				toChanged = true;
			}

			if (fromChanged && toChanged && fireEvents) {
				DateRange original = new DateRange();
				original.setFrom(fromDate);
				original.setTo(toDate);
				DateRangeChangeEvent.fireIfNotEqualDateRanges(this, original, mValue);
			}
		}
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

	public DatePicker getFromDatePicker() {
		return mFromPicker;
	}

	public DatePicker getToDatePicker() {
		return mToPicker;
	}

	public void addFixedRange(FixedDateRange fixedRange) {
		if (fixedRange != null) {
			if (fixedRanges == null) {
				fixedRanges = new ArrayList<FixedDateRange>();
			}

			fixedRanges.add(fixedRange);

			showRanges();
		}
	}

	public void addFixedRanges(List<FixedDateRange> fixedRanges) {
		if (fixedRanges != null && fixedRanges.size() > 0) {
			if (this.fixedRanges == null) {
				this.fixedRanges = new ArrayList<FixedDateRange>();
			}

			this.fixedRanges.addAll(fixedRanges);

			showRanges();
		}

	}

	public void clearFixedDateRanges() {
		if (fixedRanges != null) {
			fixedRanges.clear();

			showRanges();
		}
	}

	public void removeFixedDateRange(FixedDateRange fixedRange) {

		if (fixedRange != null && fixedRanges != null) {
			fixedRanges.remove(fixedRange);

			showRanges();
		}
	}

	private void highlightFixedRange(Anchor source) {
		boolean isActive = source.getStyleName().contains("active");

		if (!isActive) {
			if (source == customFixedRange) {
				mFromPicker.setVisible(true);
				mToPicker.setVisible(true);
				buttons.getStyle().clearDisplay();
			} else {
				mFromPicker.setVisible(false);
				mToPicker.setVisible(false);
				buttons.getStyle().setDisplay(Display.NONE);
			}

			if (activeFixedRange != null) {
				activeFixedRange.removeStyleName("active");
			}

			source.addStyleName("active");
			activeFixedRange = source;
		}
	}

	private void showRanges() {

		for (HandlerRegistration registration : fixedRangeRegistrations) {
			registration.removeHandler();
		}

		fixedRangeRegistrations.clear();
		ranges.clear();

		Anchor fixedRangeButton;
		for (final FixedDateRange fixedRange : fixedRanges) {
			fixedRangeButton = new Anchor(fixedRange.getName());
			fixedRangeButton.addStyleName("list-group-item");
			fixedRangeRegistrations.add(fixedRangeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					DateRange r = fixedRange.getDateRange();

					if (r != null) {
						DateRangePicker.this.setValue(r, true);

						highlightFixedRange((Anchor) event.getSource());
					}
				}

			}));

			ranges.add(fixedRangeButton);
		}

		if (ranges.getWidgetCount() > 0) {
			customFixedRange = new Anchor("Custom");
			customFixedRange.addStyleName("list-group-item");
			fixedRangeRegistrations.add(customFixedRange.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					highlightFixedRange((Anchor) event.getSource());
				}
			}));
			ranges.add(customFixedRange);
			buttons.getStyle().setDisplay(Display.NONE);
			mFromPicker.setVisible(false);
			mToPicker.setVisible(false);
		}
	}

	@UiHandler("apply")
	void onApplyClicked(ClickEvent event) {

	}

	@UiHandler("cancel")
	void onCancelClicked(ClickEvent event) {

	}

}