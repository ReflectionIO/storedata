//
//  ItemTopPanel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 29 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;

import java.util.Date;

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
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * @author billy1380
 * 
 */
public class ItemTopPanel extends Composite {

	private static ItemTopPanelUiBinder uiBinder = GWT.create(ItemTopPanelUiBinder.class);

	interface ItemTopPanelUiBinder extends UiBinder<Widget, ItemTopPanel> {}

	@UiField DateRangeBox mDateRange;
	@UiField ListBox mAppStore;
	// @UiField ListBox mListType;
	@UiField ListBox mCountry;

	public ItemTopPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		FilterHelper.addStores(mAppStore);
		FilterHelper.addCountries(mCountry);

		mDateRange.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		mDateRange.setValue(new Date());

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

	@UiHandler("mDateRange")
	void onDateValueChanged(ValueChangeEvent<Date> event) {
		FilterController.get().start();
		FilterController.get().setEndDate(mDateRange.getValue());
		Date startDate = new Date(mDateRange.getValue().getTime());
		CalendarUtil.addDaysToDate(startDate, -30);
		FilterController.get().setStartDate(startDate);
		FilterController.get().commit();
	}

	private void updateFromFilter() {
		FilterController.get().start();
		mAppStore.setSelectedIndex(FormHelper.getItemIndex(mAppStore, FilterController.get().getStoreA3Code()));
		mDateRange.setValue(FilterController.get().getEndDate());
		mCountry.setSelectedIndex(FormHelper.getItemIndex(mCountry, FilterController.get().getCountry().a2Code));
		FilterController.get().commit();
	}

}
