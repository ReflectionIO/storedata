//
//  ItemTopPanel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 29 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import java.util.ArrayList;
import java.util.List;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
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
 * @author billy1380
 * 
 */
public class ItemTopPanel extends Composite {

	private static ItemTopPanelUiBinder uiBinder = GWT.create(ItemTopPanelUiBinder.class);

	interface ItemTopPanelUiBinder extends UiBinder<Widget, ItemTopPanel> {}

	@UiField DateSelector dateSelector;
	@UiField ListBox mAppStore;
	// @UiField ListBox mListType;
	@UiField ListBox mCountry;

	public ItemTopPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		FilterHelper.addStores(mAppStore);
		FilterHelper.addCountries(mCountry);

		List<PresetDateRange> dateSelectorPresetRanges = new ArrayList<PresetDateRange>();

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "1 Week";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getFixedDate(FilterHelper.ONE_WEEK_AGO_PARAM), FilterHelper.getFixedDate(FilterHelper.TODAY_PARAM));
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "1 Month";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getFixedDate(FilterHelper.ONE_MONTH_AGO_PARAM),
						FilterHelper.getFixedDate(FilterHelper.TODAY_PARAM));
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "3 Months";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getFixedDate(FilterHelper.THREE_MONTHS_AGO_PARAM),
						FilterHelper.getFixedDate(FilterHelper.TODAY_PARAM));
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "6 Months";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getFixedDate(FilterHelper.SIX_MONTHS_AGO_PARAM),
						FilterHelper.getFixedDate(FilterHelper.TODAY_PARAM));
			}
		});

		dateSelectorPresetRanges.add(new PresetDateRange() {

			@Override
			public String getName() {
				return "1 Year";
			}

			@Override
			public DateRange getDateRange() {
				return FilterHelper.createRange(FilterHelper.getFixedDate(FilterHelper.ONE_YEAR_AGO_PARAM), FilterHelper.getFixedDate(FilterHelper.TODAY_PARAM));
			}
		});

		dateSelector.addFixedRanges(dateSelectorPresetRanges);

		updateFromFilter();
	}

	@UiHandler("mAppStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(mAppStore.getValue(mAppStore.getSelectedIndex()));
	}

	@UiHandler("mCountry")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(mCountry.getValue(mCountry.getSelectedIndex()));
	}

	@UiHandler("dateSelector")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();

		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
	}

	public void updateFromFilter() {
		FilterController fc = FilterController.get();

		mAppStore.setSelectedIndex(FormHelper.getItemIndex(mAppStore, fc.getFilter().getStoreA3Code()));
		DateRange range = new DateRange();

		range.setFrom(fc.getStartDate());
		range.setTo(fc.getEndDate());
		dateSelector.setValue(range);

		mCountry.setSelectedIndex(FormHelper.getItemIndex(mCountry, fc.getFilter().getCountryA2Code()));
	}

}
