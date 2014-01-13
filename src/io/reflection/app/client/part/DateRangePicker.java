//
//  DateRangePicker.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.part.datatypes.DateRange;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class DateRangePicker extends Composite implements HasValue<DateRange> {

	private static DateRangePickerUiBinder uiBinder = GWT.create(DateRangePickerUiBinder.class);

	interface DateRangePickerUiBinder extends UiBinder<Widget, DateRangePicker> {}

	private DateRange mValue;

	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateRange> handler) {
		return null;
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

}
