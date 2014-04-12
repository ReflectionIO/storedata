//
//  DateRangeBox.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.DateRangeChangeEvent;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author billy1380
 * 
 */
public class DateRangeBox extends Composite implements HasEnabled, HasValue<DateRange>, IsEditor<LeafValueEditor<DateRange>> {

	public static final String DATE_RANGE_SPLITTER = " to ";

	public static class DefaultFormat implements Format {

		private final DateTimeFormat dateTimeFormat;

		public DefaultFormat() {
			dateTimeFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
		}
		
		public DefaultFormat(DateTimeFormat dateTimeFormat) {
			this.dateTimeFormat = dateTimeFormat;
		}

		public String format(DateRangeBox box, DateRange dateRange) {
			if (dateRange == null) {
				return "";
			} else {
				return dateTimeFormat.format(dateRange.getFrom()) + DATE_RANGE_SPLITTER + dateTimeFormat.format(dateRange.getTo());
			}
		}

		public DateTimeFormat getDateTimeFormat() {
			return dateTimeFormat;
		}

		@SuppressWarnings("deprecation")
		public DateRange parse(DateRangeBox dateRangeBox, String dateRangeText, boolean reportError) {
			DateRange dateRange = null;

			Date from = null, to = null;

			if (dateRangeText != null && dateRangeText.length() > 0) {
				String[] dateRangeParts = dateRangeText.split(DATE_RANGE_SPLITTER);

				if (dateRangeParts != null && dateRangeParts.length == 2) {
					try {
						from = dateTimeFormat.parse(dateRangeParts[0]);
					} catch (IllegalArgumentException exception) {
						try {
							from = new Date(dateRangeParts[0]);
							from = dateTimeFormat.parse(dateTimeFormat.format(from));
						} catch (IllegalArgumentException e) {
							if (reportError) {
								dateRangeBox.addStyleName(DATE_BOX_FORMAT_ERROR);
							}
							return null;
						}
					}

					try {
						to = dateTimeFormat.parse(dateRangeParts[1]);
					} catch (IllegalArgumentException exception) {
						try {
							to = new Date(dateRangeParts[1]);
							to = dateTimeFormat.parse(dateTimeFormat.format(to));
						} catch (IllegalArgumentException e) {
							if (reportError) {
								dateRangeBox.addStyleName(DATE_BOX_FORMAT_ERROR);
							}
							return null;
						}
					}

					if (from != null && to != null) {
						dateRange = new DateRange();
						dateRange.setFrom(from);
						dateRange.setTo(to);
					}
				}
			}

			return dateRange;
		}

		public void reset(DateRangeBox dateRangeBox, boolean abandon) {
			dateRangeBox.removeStyleName(DATE_BOX_FORMAT_ERROR);
		}
	}

	public interface Format {

		String format(DateRangeBox dateRangeBox, DateRange dateRange);

		DateRange parse(DateRangeBox dateRangeBox, String text, boolean reportError);

		void reset(DateRangeBox dateBox, boolean abandon);
	}

	private class DateRangeBoxHandler implements ValueChangeHandler<DateRange>, FocusHandler, BlurHandler, ClickHandler, KeyDownHandler,
			CloseHandler<PopupPanel> {

		public void onBlur(BlurEvent event) {
			if (isDateRangePickerShowing() == false) {
				updateDateRangeFromTextBox();
			}
		}

		public void onClick(ClickEvent event) {
			showDateRangePicker();
		}

		public void onClose(CloseEvent<PopupPanel> event) {
			// If we are not closing because we have picked a new value, make sure the
			// current value is updated.
			if (allowDPShow) {
				updateDateRangeFromTextBox();
			}
		}

		public void onFocus(FocusEvent event) {
			if (allowDPShow && isDateRangePickerShowing() == false) {
				showDateRangePicker();
			}
		}

		public void onKeyDown(KeyDownEvent event) {
			switch (event.getNativeKeyCode()) {
			case KeyCodes.KEY_ENTER:
			case KeyCodes.KEY_TAB:
				updateDateRangeFromTextBox();
				// Deliberate fall through
			case KeyCodes.KEY_ESCAPE:
			case KeyCodes.KEY_UP:
				hideDatePicker();
				break;
			case KeyCodes.KEY_DOWN:
				showDateRangePicker();
				break;
			}
		}

		public void onValueChange(ValueChangeEvent<DateRange> event) {
			setValue(parseDateRange(false), normalize(event.getValue()), true, true);
			hideDatePicker();
			preventDatePickerPopup();
			box.setFocus(true);
		}

		private DateRange normalize(DateRange dateRange) {
			DateRangeBox dateRangeBox = DateRangeBox.this;
			return getFormat().parse(dateRangeBox, getFormat().format(dateRangeBox, dateRange), false);
		}
	}

	private static final String DATE_BOX_FORMAT_ERROR = "dateBoxFormatError";

	public static final String DEFAULT_STYLENAME = "gwt-DateRangeBox";
	private static final DefaultFormat DEFAULT_FORMAT = GWT.create(DefaultFormat.class);

	private final PopupPanel popup;
	private final TextBox box = new TextBox();
	private final DateRangePicker picker;
	private LeafValueEditor<DateRange> editor;
	private Format format;
	private boolean allowDPShow = true;
	private boolean fireNullValues = false;

	public DateRangeBox() {
		this(new DateRangePicker(), null, DEFAULT_FORMAT);
	}

	public DateRangeBox(DateRangePicker picker, DateRange dateRange, Format format) {
		this.picker = picker;
		this.popup = new PopupPanel(true);
		assert format != null : "You may not construct a date range box with a null format";
		this.format = format;

		popup.addAutoHidePartner(box.getElement());
		popup.setWidget(picker);
		popup.setStyleName("dateBoxPopup");

		initWidget(box);
		setStyleName(DEFAULT_STYLENAME);

		DateRangeBoxHandler handler = new DateRangeBoxHandler();
		picker.addValueChangeHandler(handler);
		box.addFocusHandler(handler);
		box.addBlurHandler(handler);
		box.addClickHandler(handler);
		box.addKeyDownHandler(handler);
		box.setDirectionEstimator(false);
		popup.addCloseHandler(handler);
		setValue(dateRange);
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateRange> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public LeafValueEditor<DateRange> asEditor() {
		if (editor == null) {
			editor = TakesValueEditor.of(this);
		}
		return editor;
	}

	public int getCursorPos() {
		return box.getCursorPos();
	}

	public DateRangePicker getDateRangePicker() {
		return picker;
	}

	public boolean getFireNullValues() {
		return fireNullValues;
	}

	public Format getFormat() {
		return this.format;
	}

	public int getTabIndex() {
		return box.getTabIndex();
	}

	public TextBox getTextBox() {
		return box;
	}

	public DateRange getValue() {
		return parseDateRange(true);
	}

	public void hideDatePicker() {
		popup.hide();
	}

	public boolean isDateRangePickerShowing() {
		return popup.isShowing();
	}

	public boolean isEnabled() {
		return box.isEnabled();
	}

	public void setAccessKey(char key) {
		box.setAccessKey(key);
	}

	public void setEnabled(boolean enabled) {
		box.setEnabled(enabled);
	}

	public void setFireNullValues(boolean fireNullValues) {
		this.fireNullValues = fireNullValues;
	}

	public void setFocus(boolean focused) {
		box.setFocus(focused);
	}

	public void setFormat(Format format) {
		assert format != null : "A Date box may not have a null format";
		if (this.format != format) {
			DateRange date = getValue();

			// This call lets the formatter do whatever other clean up is required to
			// switch formatters.
			//
			this.format.reset(this, true);

			// Now update the format and show the current date using the new format.
			this.format = format;
			setValue(date);
		}
	}

	public void setTabIndex(int index) {
		box.setTabIndex(index);
	}

	public void setValue(DateRange dateRange) {
		setValue(dateRange, false);
	}

	public void setValue(DateRange dateRange, boolean fireEvents) {
		setValue(picker.getValue(), dateRange, fireEvents, true);
	}

	public void showDateRangePicker() {
		DateRange current = parseDateRange(false);
		if (current == null) {
			current = new DateRange();
		}
		picker.setCurrentMonth(current);
		popup.showRelativeTo(this);
	}

	private DateRange parseDateRange(boolean reportError) {
		if (reportError) {
			getFormat().reset(this, false);
		}
		String text = box.getText().trim();
		return getFormat().parse(this, text, reportError);
	}

	private void preventDatePickerPopup() {
		allowDPShow = false;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				allowDPShow = true;
			}
		});
	}

	private void setValue(DateRange oldDateRange, DateRange dateRange, boolean fireEvents, boolean updateText) {
		if (dateRange != null) {
			picker.setCurrentMonth(dateRange);
		}
		picker.setValue(dateRange, false);

		if (updateText) {
			format.reset(this, false);
			box.setText(getFormat().format(this, dateRange));
		}

		if (fireEvents) {
			DateRangeChangeEvent.fireIfNotEqualDateRanges(this, oldDateRange, dateRange);
		}
	}

	private void updateDateRangeFromTextBox() {
		DateRange parsedDateRange = parseDateRange(true);
		if (fireNullValues || (parsedDateRange != null)) {
			setValue(picker.getValue(), parsedDateRange, true, false);
		}
	}
}
