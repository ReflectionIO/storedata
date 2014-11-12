//
//  RankFilter.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.part;

import static io.reflection.app.client.controller.FilterController.DOWNLOADS_DAILY_DATA_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_DAILY_DATA_TYPE;
import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMAT_DD_MMM_YYYY;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.BootstrapGwtDatePicker;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * @author billy1380
 * 
 */
public class RankSidePanel extends Composite {

	private static RankSidePanelUiBinder uiBinder = GWT.create(RankSidePanelUiBinder.class);

	interface RankSidePanelUiBinder extends UiBinder<Widget, RankSidePanel> {}

	@UiField DateBox date;
	Date currentDate = FilterHelper.getToday();
	@UiField ListBox mAppStore;
	// @UiField ListBox mListType;
	@UiField ListBox mCountry;
	@UiField ListBox category;
	@UiField RadioButton mDailyDataRevenue;
	@UiField RadioButton mDailyDataDownloads;

	@UiField HTMLPanel dailyDataRadio;

	public RankSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();
		
		FilterHelper.addStores(mAppStore, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCountries(mCountry, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCategories(category, SessionController.get().isLoggedInUserAdmin());

		date.setFormat(new DateBox.DefaultFormat(DATE_FORMAT_DD_MMM_YYYY));
		date.getTextBox().setReadOnly(Boolean.TRUE);

		date.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableFutureDates(date.getDatePicker());
			}
		});

		updateFromFilter();
	}

	@UiHandler("mAppStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(mAppStore.getValue(mAppStore.getSelectedIndex()));
	}

	// @UiHandler("mListType")
	// void onListTypeValueChanged(ChangeEvent event) {
	// FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
	// }

	@UiHandler("mCountry")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(mCountry.getValue(mCountry.getSelectedIndex()));
	}

	@UiHandler("date")
	void onDateValueChanged(ValueChangeEvent<Date> event) {
		if (event.getValue().after(FilterHelper.getToday())) { // Restore previously selected date
			date.setValue(currentDate);
		} else {
			currentDate.setTime(date.getValue().getTime());
			FilterController fc = FilterController.get();
			fc.start();
			fc.setEndDate(event.getValue());
			Date startDate = fc.getEndDate();
			CalendarUtil.addDaysToDate(startDate, -30);
			fc.setStartDate(startDate);
			fc.commit();
		}
	}

	@UiHandler("category")
	void onCategoryValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(getCatgegory());
	}

	@UiHandler("mDailyDataRevenue")
	void onDailyDataRevenueSelected(ClickEvent event) {
		FilterController.get().setDailyData(REVENUE_DAILY_DATA_TYPE);
	}

	@UiHandler("mDailyDataDownloads")
	void onDailyDataDownloadsSelected(ClickEvent event) {
		FilterController.get().setDailyData(DOWNLOADS_DAILY_DATA_TYPE);
	}

	/**
	 * @return
	 */
	public String getStore() {
		return mAppStore.getItemText(mAppStore.getSelectedIndex());
	}

	/**
	 * @return
	 */
	public String getCountry() {
		return mCountry.getItemText(mCountry.getSelectedIndex());
	}

	public String getDisplayDate() {
		return getDisplayDate("d MMM");
	}

	public String getDisplayDate(String format) {
		Date date = getDate();
		return date == null ? "" : DateTimeFormat.getFormat(format).format(getDate());
	}

	public Long getCatgegory() {
		return Long.valueOf(category.getValue(category.getSelectedIndex()));
	}

	/**
	 * @return
	 */
	public Date getDate() {
		return date.getValue();
	}

	public void updateFromFilter() {
		FilterController fc = FilterController.get();

		mAppStore.setSelectedIndex(FormHelper.getItemIndex(mAppStore, fc.getFilter().getStoreA3Code()));
		long endTime = fc.getFilter().getEndTime().longValue();
		Date endDate = new Date(endTime);
		date.setValue(endDate);
		currentDate.setTime(endDate.getTime());
		mCountry.setSelectedIndex(FormHelper.getItemIndex(mCountry, fc.getFilter().getCountryA2Code()));
		category.setSelectedIndex(FormHelper.getItemIndex(category, fc.getFilter().getCategoryId().toString()));

		String dailyDataType = fc.getFilter().getDailyData();
		if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
			mDailyDataRevenue.setValue(true);
		} else {
			mDailyDataDownloads.setValue(true);
		}
	}

	public void setDataFilterVisible(boolean visible) {
		if (visible) {
			dailyDataRadio.setVisible(true);
		} else {
			dailyDataRadio.setVisible(false);
		}
	}

}
