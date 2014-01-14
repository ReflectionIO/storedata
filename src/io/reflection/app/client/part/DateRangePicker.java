//
//  DateRangePicker.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.part.datatypes.DateRange;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author billy1380
 * 
 */
public class DateRangePicker extends Composite implements HasValue<DateRange>, IsEditor<LeafValueEditor<DateRange>>, ValueChangeHandler<Date> {

	private static DateRangePickerUiBinder uiBinder = GWT.create(DateRangePickerUiBinder.class);

	interface DateRangePickerUiBinder extends UiBinder<Widget, DateRangePicker> {}

	private DateRange mValue;
	private LeafValueEditor<DateRange> mEditor;

	@UiField DatePicker mFromPicker;
	@UiField DatePicker mToPicker;

	public DateRangePicker() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		mFromPicker.addValueChangeHandler(this);
		mToPicker.addValueChangeHandler(this);
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
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<Date> event) {
		if (event.getSource() == mFromPicker) {
			Window.alert("From picker");
		} else if (event.getSource() == mToPicker) {
			Window.alert("To picker");
		}

	}

}
