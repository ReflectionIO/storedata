//
//  DateRangeChangeEvent.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

/**
 * @author billy1380
 *
 */
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HasHandlers;

public class DateRangeChangeEvent extends ValueChangeEvent<DateRange> {

	public static <S extends HasValueChangeHandlers<DateRange> & HasHandlers> void fireIfNotEqualDateRanges(S source, DateRange oldValue, DateRange newValue) {
		if (ValueChangeEvent.shouldFire(source, oldValue, newValue)) {
			source.fireEvent(new DateRangeChangeEvent(newValue));
		}
	}

	protected DateRangeChangeEvent(DateRange value) {
		super(DateRange.copy(value));
	}

	@Override
	public DateRange getValue() {
		return DateRange.copy(super.getValue());
	}
}
