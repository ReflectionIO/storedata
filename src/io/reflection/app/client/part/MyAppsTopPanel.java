//
//  MyAppsTopPanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 4 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsTopPanel extends Composite {

	private static MyAppsTopPanelUiBinder uiBinder = GWT.create(MyAppsTopPanelUiBinder.class);

	interface MyAppsTopPanelUiBinder extends UiBinder<Widget, MyAppsTopPanel> {}

	@UiField ListBox appStore;
	@UiField ListBox country;
	@UiField DateBox dateBox;
	@UiField RadioButton rangeDay;
	@UiField RadioButton rangeWeek;
	@UiField RadioButton rangeMonth;

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public MyAppsTopPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		FilterHelper.addStores(appStore);
		FilterHelper.addCountries(country);

		dateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		Date d = FilterHelper.normalizeDate(new Date());
		dateBox.setValue(d);
		dateBox.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableFutureDates(dateBox.getDatePicker());
			}
		});

		dateBox.getDatePicker().addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				updateFilterDate();
			}
		});

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

	@UiHandler("rangeDay")
	void onRangeDayValueSelected(ClickEvent event) {
		FilterController.get().setStartDate(dateBox.getValue());
	}

	@UiHandler("rangeWeek")
	void onRangeWeekValueSelected(ClickEvent event) {
		Date startDate = CalendarUtil.copyDate(dateBox.getValue());
		CalendarUtil.addDaysToDate(startDate, -7);
		FilterController.get().setStartDate(startDate);
	}

	@UiHandler("rangeMonth")
	void onRangeMonthValueSelected(ClickEvent event) {
		Date startDate = CalendarUtil.copyDate(dateBox.getValue());
		CalendarUtil.addMonthsToDate(startDate, -1);
		FilterController.get().setStartDate(startDate);
	}

	public void updateFromFilter() {
		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, FilterController.get().getFilter().getStoreA3Code()));
		country.setSelectedIndex(FormHelper.getItemIndex(country, FilterController.get().getFilter().getCountryA2Code()));
		updateFilterDate();
	}

	private void updateFilterDate() {

		Date endDate = dateBox.getValue();
		CalendarUtil.addDaysToDate(endDate, 1); // add 1 day to the EndDate because from DatePicker is set at midnight
		FilterController.get().setEndDate(endDate);

		FilterController.get().start();
		if (rangeDay.getValue()) {
			FilterController.get().setStartDate(dateBox.getValue());
		} else if (rangeWeek.getValue()) {
			Date startDate = CalendarUtil.copyDate(dateBox.getValue());
			CalendarUtil.addDaysToDate(startDate, -7);
			FilterController.get().setStartDate(startDate);
		} else {
			Date startDate = CalendarUtil.copyDate(dateBox.getValue());
			CalendarUtil.addMonthsToDate(startDate, -1);
			FilterController.get().setStartDate(startDate);
		}
		FilterController.get().commit();
	}

}
