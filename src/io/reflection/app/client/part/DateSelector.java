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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.HTMLPanel;
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

	public interface PresetDateRange {
		String getName();

		DateRange getDateRange();
	}

	private List<PresetDateRange> fixedRanges = null;
	private HashMap<PresetDateRange, Anchor> lookupFixedDateRangeAnchor = new HashMap<PresetDateRange, Anchor>();
	List<HandlerRegistration> fixedRangeRegistrations = new ArrayList<HandlerRegistration>();

	interface DateSelectorStyle extends CssResource {
		String highlight();

		String preset();

		String disable();
	}

	@UiField DateSelectorStyle style;

	@UiField DateBox dateBoxFrom;
	@UiField DateBox dateBoxTo;
	@UiField HTMLPanel fixedRangesPanel;
	@UiField Button applyDateRange;
	@UiField SpanElement icon;
	@UiField SpanElement separator;

	private DateRange dateRange = new DateRange();

	public DateSelector() {
		initWidget(uiBinder.createAndBindUi(this));

		DateTimeFormat dtf = DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_DD_MMM_YYYY);
		dateBoxFrom.setFormat(new DateBox.DefaultFormat(dtf));
		dateBoxFrom.getTextBox().setReadOnly(Boolean.TRUE);
		dateBoxTo.setFormat(new DateBox.DefaultFormat(dtf));
		dateBoxTo.getTextBox().setReadOnly(Boolean.TRUE);

		setDateRange(FilterHelper.getWeeksAgo(4), FilterHelper.getToday());

		// Disable out of range dates
		dateBoxFrom.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBoxFrom.getDatePicker(), FilterHelper.getDaysAgo(60), dateBoxTo.getValue());
			}
		});
		dateBoxTo.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBoxTo.getDatePicker(), dateBoxFrom.getValue(), FilterHelper.getToday());
			}
		});

	}

	public void addFixedRange(PresetDateRange fixedRange) {
		if (fixedRange != null) {
			if (fixedRanges == null) {
				fixedRanges = new ArrayList<PresetDateRange>();
			}

			fixedRanges.add(fixedRange);

			showRanges();
		}
	}

	public void addFixedRanges(List<PresetDateRange> fixedRanges) {
		if (fixedRanges != null && fixedRanges.size() > 0) {
			if (this.fixedRanges == null) {
				this.fixedRanges = new ArrayList<PresetDateRange>();
			}

			this.fixedRanges.addAll(fixedRanges);

			showRanges();
		}
	}

	private void showRanges() {
		for (HandlerRegistration registration : fixedRangeRegistrations) {
			registration.removeHandler();
		}
		fixedRangeRegistrations.clear();
		fixedRangesPanel.clear();
		lookupFixedDateRangeAnchor.clear();
		for (final PresetDateRange fixedRange : fixedRanges) {
			final Anchor fixedRangeLink = new Anchor(fixedRange.getName());
			fixedRangeLink.getElement().addClassName(style.preset());
			lookupFixedDateRangeAnchor.put(fixedRange, fixedRangeLink);
			fixedRangeRegistrations.add(fixedRangeLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (fixedRangeLink.isEnabled()) {
						setValue(fixedRange.getDateRange(), Boolean.TRUE);
					}
				}
			}));
			fixedRangesPanel.add(fixedRangeLink);
		}
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
		if (event.getValue().after(dateBoxTo.getValue()) || event.getValue().before(FilterHelper.getDaysAgo(60))) {
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
		if (event.getValue().before(dateBoxFrom.getValue()) || event.getValue().after(FilterHelper.getToday())) {
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
		if (fixedRanges != null && dateBoxTo.getValue().equals(FilterHelper.getToday())) {
			for (PresetDateRange fixedRange : fixedRanges) {
				if (dateBoxFrom.getValue().equals(fixedRange.getDateRange().getFrom())) {
					link = lookupFixedDateRangeAnchor.get(fixedRange);
					break;
				}
			}
		}
		return link;
	}

	/**
	 * Clear highlight style for all links
	 */
	private void clearLinkHighlight() {
		if (fixedRanges != null) {
			for (PresetDateRange fixedRange : fixedRanges) {
				lookupFixedDateRangeAnchor.get(fixedRange).getElement().removeClassName(style.highlight());
			}
		}
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
		setDateRange(value.getFrom(), value.getTo()); // Update date range
	}

	private void setValue(Date from, Date to, boolean fireEvents) {
		DateRange dr = new DateRange();
		dr.setFrom(from);
		dr.setTo(to);
		setValue(dr, fireEvents);
	}

	public void setEnabled(boolean enabled) {
		dateBoxFrom.setEnabled(enabled);
		dateBoxTo.setEnabled(enabled);
		applyDateRange.setEnabled(enabled);
		if (enabled) {
			icon.removeClassName(style.disable());
			separator.removeClassName(style.disable());
		} else {
			separator.addClassName(style.disable());
			icon.addClassName(style.disable());
		}
		if (fixedRanges != null) {
			for (PresetDateRange fixedRange : fixedRanges) {
				if (enabled) {
					lookupFixedDateRangeAnchor.get(fixedRange).getElement().removeClassName(style.disable());
				} else {
					lookupFixedDateRangeAnchor.get(fixedRange).getElement().addClassName(style.disable());
				}
				lookupFixedDateRangeAnchor.get(fixedRange).setEnabled(enabled);
			}
		}
	}

}
