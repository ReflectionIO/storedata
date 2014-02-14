//
//  RankFilter.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FormHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * @author billy1380
 * 
 */
public class RankSidePanel extends Composite {

	private static RankSidePanelUiBinder uiBinder = GWT.create(RankSidePanelUiBinder.class);

	interface RankSidePanelUiBinder extends UiBinder<Widget, RankSidePanel> {}

	@UiField DateBox mDate;
	@UiField ListBox mAppStore;
	// @UiField ListBox mListType;
	@UiField ListBox mCountry;
	@UiField ListBox category;

	public RankSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		mDate.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));

		final List<Date> dates = new ArrayList<Date>();

		mDate.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				Date start = event.getStart();
				Date end = event.getEnd();
				Date now = new Date();

				dates.clear();
				Date curr = start;
				while (curr.getTime() <= end.getTime()) {
					if (curr.getTime() > now.getTime() || curr.getTime() < 1356998401000l) {
						dates.add(curr);
					}

					curr = new Date(curr.getTime() + 24l * 1000l * 60l * 60l);
				}

				mDate.getDatePicker().setTransientEnabledOnDates(false, dates);
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

	@UiHandler("mDate")
	void onDateValueChanged(ValueChangeEvent<Date> event) {
		FilterController.get().start();
		FilterController.get().setEndDate(mDate.getValue());
		Date startDate = new Date(mDate.getValue().getTime());
		CalendarUtil.addDaysToDate(startDate, -10);
		FilterController.get().setStartDate(startDate);
		FilterController.get().commit();
	}

	@UiHandler("category")
	void onCategoryValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(getCatgegory());
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
		return mDate.getValue();
	}

	private void updateFromFilter() {
		FilterController.get().start();
		mAppStore.setSelectedIndex(FormHelper.getItemIndex(mAppStore, FilterController.get().getStoreA3Code()));
		mDate.setValue(FilterController.get().getEndDate());
		mCountry.setSelectedIndex(FormHelper.getItemIndex(mCountry, FilterController.get().getCountry().a2Code));
		category.setSelectedIndex(FormHelper.getItemIndex(category, FilterController.get().getCategory().id.toString()));
		FilterController.get().commit();
	}

}
