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
import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY;
import io.reflection.app.client.component.FormFieldSelect;
import io.reflection.app.client.component.FormRadioButton;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author billy1380
 * 
 */
public class RankSidePanel extends Composite {

	private static RankSidePanelUiBinder uiBinder = GWT.create(RankSidePanelUiBinder.class);

	interface RankSidePanelUiBinder extends UiBinder<Widget, RankSidePanel> {}

	@UiField(provided = true) DateBox dateBox = new DateBox(new DatePicker(), null, new DateBox.DefaultFormat(DATE_FORMATTER_DD_MMM_YYYY));
	Date currentDate = FilterHelper.getToday();
	@UiField FormFieldSelect appStoreListBox;
	// @UiField ListBox mListType;
	@UiField FormFieldSelect countryListBox;
	@UiField FormFieldSelect categoryListBox;
	@UiField FormRadioButton dailyDataRevenue;
	@UiField FormRadioButton dailyDataDownloads;

	@UiField HTMLPanel dailyDataRadio;

	public RankSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		if (!SessionController.get().isLoggedInUserAdmin()) {
			dailyDataRadio.removeFromParent();
		}

		dateBox.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableFutureDates(dateBox.getDatePicker());
			}
		});

		FilterHelper.addStores(appStoreListBox, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCountries(countryListBox, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCategories(categoryListBox, SessionController.get().isLoggedInUserAdmin());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		updateFromFilter();
	}

	@UiHandler("appStoreListBox")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(appStoreListBox.getValue(appStoreListBox.getSelectedIndex()));
	}

	// @UiHandler("mListType")
	// void onListTypeValueChanged(ChangeEvent event) {
	// FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
	// }

	@UiHandler("countryListBox")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(countryListBox.getValue(countryListBox.getSelectedIndex()));
	}

	@UiHandler("dateBox")
	void onDateValueChanged(ValueChangeEvent<Date> event) {
		if (event.getValue().after(FilterHelper.getToday())) { // Restore previously selected date
			dateBox.setValue(currentDate);
		} else {
			currentDate.setTime(dateBox.getValue().getTime());
			FilterController fc = FilterController.get();
			fc.start();
			fc.setEndDate(event.getValue());
			Date startDate = fc.getEndDate();
			CalendarUtil.addDaysToDate(startDate, -30);
			fc.setStartDate(startDate);
			fc.commit();
		}
	}

	@UiHandler("categoryListBox")
	void onCategoryListBoxValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(getCatgegory());
	}

	@UiHandler("dailyDataRevenue")
	void onDailyDataRevenueSelected(ClickEvent event) {
		FilterController.get().setDailyData(REVENUE_DAILY_DATA_TYPE);
	}

	@UiHandler("dailyDataDownloads")
	void onDailyDataDownloadsSelected(ClickEvent event) {
		FilterController.get().setDailyData(DOWNLOADS_DAILY_DATA_TYPE);
	}

	/**
	 * @return
	 */
	public String getStore() {
		return appStoreListBox.getItemText(appStoreListBox.getSelectedIndex());
	}

	/**
	 * @return
	 */
	public String getCountry() {
		return countryListBox.getItemText(countryListBox.getSelectedIndex());
	}

	public String getDisplayDate() {
		return getDisplayDate("d MMM");
	}

	public String getDisplayDate(String format) {
		Date date = getDate();
		return date == null ? "" : DateTimeFormat.getFormat(format).format(getDate());
	}

	public Long getCatgegory() {
		return Long.valueOf(categoryListBox.getValue(categoryListBox.getSelectedIndex()));
	}

	/**
	 * @return
	 */
	public Date getDate() {
		return dateBox.getValue();
	}

	public void updateFromFilter() {
		FilterController fc = FilterController.get();

		long endTime = fc.getFilter().getEndTime().longValue();
		Date endDate = new Date(endTime);
		dateBox.setValue(endDate);
		currentDate.setTime(endDate.getTime());
		if (SessionController.get().isLoggedInUserAdmin()) {
			appStoreListBox.setSelectedIndex(FormHelper.getItemIndex(appStoreListBox, fc.getFilter().getStoreA3Code()));
			countryListBox.setSelectedIndex(FormHelper.getItemIndex(countryListBox, fc.getFilter().getCountryA2Code()));
			categoryListBox.setSelectedIndex(FormHelper.getItemIndex(categoryListBox, fc.getFilter().getCategoryId().toString()));
		} else {
			appStoreListBox.setSelectedIndex(0);
			countryListBox.setSelectedIndex(0);
			categoryListBox.setSelectedIndex(0);
		}

		String dailyDataType = fc.getFilter().getDailyData();
		if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
			dailyDataRevenue.setValue(true);
		} else {
			dailyDataDownloads.setValue(true);
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
