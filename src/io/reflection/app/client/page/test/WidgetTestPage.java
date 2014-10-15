//
//  WidgetTestPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 31 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.test;

import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.DateRangeBox;
import io.reflection.app.client.part.DateRangePicker;
import io.reflection.app.client.part.DateRangePicker.FixedDateRange;
import io.reflection.app.client.part.DateSelector;
import io.reflection.app.client.part.datatypes.DateRange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class WidgetTestPage extends Page {

	private static WidgetTestPageUiBinder uiBinder = GWT.create(WidgetTestPageUiBinder.class);

	interface WidgetTestPageUiBinder extends UiBinder<Widget, WidgetTestPage> {}

	@UiField DateSelector dateSelector;
	@UiField DateRangePicker picker1;
	@UiField DateRangeBox dateRange;

	public WidgetTestPage() {
		initWidget(uiBinder.createAndBindUi(this));

		dateSelector.addFixedRanges(FilterHelper.getDefaultDateRanges());

		picker1.addFixedRange(new FixedDateRange() {

			@Override
			public String getName() {
				return "Today";
			}

			@Override
			public DateRange getDateRange() {
				DateRange r = new DateRange();

				r.setFrom(FilterHelper.normalizeDate(new Date()));
				Date to = r.getFrom();
				r.setTo(to);
				return r;
			}
		});

		List<FixedDateRange> ranges = new ArrayList<FixedDateRange>();
		ranges.add(new FixedDateRange() {

			@Override
			public String getName() {
				return "Today";
			}

			@Override
			public DateRange getDateRange() {
				DateRange r = new DateRange();
				r.setFrom(FilterHelper.getToday());
				r.setTo(r.getFrom());
				return r;
			}
		});

		ranges.add(new FixedDateRange() {

			@Override
			public String getName() {
				return "Last Week";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRangeFromToday(-7);
			}
		});

		ranges.add(new FixedDateRange() {

			@Override
			public String getName() {
				return "Last 30 Days";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRangeFromToday(-30);
			}
		});

		dateRange.getDateRangePicker().addFixedRanges(ranges);
	}

}
