//
//  MyAppsTopPanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 4 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.DateRangeBox;
import io.reflection.app.client.part.DateRangeBox.DefaultFormat;
import io.reflection.app.client.part.DateRangePicker.FixedDateRange;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsTopPanel extends Composite {

	private static MyAppsTopPanelUiBinder uiBinder = GWT.create(MyAppsTopPanelUiBinder.class);

	interface MyAppsTopPanelUiBinder extends UiBinder<Widget, MyAppsTopPanel> {}

	@UiField ListBox appStore;
	@UiField ListBox country;
	@UiField DateRangeBox dateRangeBox;

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public MyAppsTopPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		FilterHelper.addStores(appStore);
		FilterHelper.addCountries(country);

		dateRangeBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT)));

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

		dateRangeBox.getDateRangePicker().addFixedRanges(ranges);
		dateRangeBox.setValue(FilterHelper.createRangeFromToday(-30));

		updateFromFilter();

	}

	@UiHandler("appStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(appStore.getValue(appStore.getSelectedIndex()));
	}

	@UiHandler("country")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(country.getValue(country.getSelectedIndex()));
	}

	public void updateFromFilter() {
		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, FilterController.get().getFilter().getStoreA3Code()));
		DateRange range = new DateRange();

		range.setFrom(FilterController.get().getStartDate());
		range.setTo(FilterController.get().getEndDate());

		dateRangeBox.setValue(range);
		country.setSelectedIndex(FormHelper.getItemIndex(country, FilterController.get().getFilter().getCountryA2Code()));
	}

	@UiHandler("dateRangeBox")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();

		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
	}

}
