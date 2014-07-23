//
//  MyAppsTopPanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 4 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import java.util.ArrayList;
import java.util.List;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.DateSelector;
import io.reflection.app.client.part.DateSelector.PresetDateRange;
import io.reflection.app.client.part.datatypes.DateRange;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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

	@UiField ListBox accountName;
	@UiField ListBox appStore;
	@UiField ListBox country;
	@UiField DateSelector dateSelector;

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public MyAppsTopPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		FilterHelper.addStores(appStore);
		FilterHelper.addCountries(country);

		List<PresetDateRange> dateSelectorPresetRanges = new ArrayList<PresetDateRange>();

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "1 wk";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getWeeksAgo(1), FilterHelper.getToday());
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "2 wks";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getWeeksAgo(2), FilterHelper.getToday());
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "4 wks";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getWeeksAgo(4), FilterHelper.getToday());
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "6 wks";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getWeeksAgo(6), FilterHelper.getToday());
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "8 wks";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getWeeksAgo(8), FilterHelper.getToday());
			}
		});

		dateSelector.addFixedRanges(dateSelectorPresetRanges);

		updateFromFilter();

	}

	@UiHandler("accountName")
	void onAccountNameChanged(ChangeEvent event) {
		FilterController.get().setLinkedAccount(Long.valueOf(accountName.getValue(accountName.getSelectedIndex())));
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
		FilterController fc = FilterController.get();

		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, fc.getFilter().getStoreA3Code()));
		DateRange range = new DateRange();

		range.setFrom(fc.getStartDate());
		range.setTo(fc.getEndDate());
		dateSelector.setValue(range);

		country.setSelectedIndex(FormHelper.getItemIndex(country, fc.getFilter().getCountryA2Code()));

		if (fc.getFilter().getLinkedAccountId() > 0) {
			accountName.setSelectedIndex(FormHelper.getItemIndex(accountName, fc.getFilter().getLinkedAccountId().toString()));
		}

	}

	@UiHandler("dateSelector")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();

		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
	}

	public void fillAccountNameList() {
		accountName.clear();
		FilterHelper.addLinkedAccounts(accountName);
	}

	/**
	 * Linked account filter
	 * 
	 * @param enabled
	 */
	public void setFilterAccountEnabled(boolean enabled) {
		accountName.setEnabled(enabled);
	}

	/**
	 * Excluded linked account filter
	 * 
	 * @param enabled
	 */
	public void setFiltersEnabled(boolean enabled) {
		appStore.setEnabled(enabled);
		country.setEnabled(enabled);
		dateSelector.setEnabled(enabled);
	}

}
