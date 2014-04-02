//
//  MyAppsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.MyAppsController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;

import java.util.Date;
import java.util.Map;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.InlineHyperlink;
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
public class MyAppsPage extends Page implements FilterEventHandler, NavigationEventHandler {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	@UiField(provided = true) CellTable<MyApp> appsTable = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	// @UiField(provided = true) PageSizePager pager = new PageSizePager(ServiceConstants.STEP_VALUE);

	@UiField InlineHyperlink mLinkedAccountsLink;
	@UiField InlineHyperlink mMyAppsLink;

	@UiField ListBox appStore;
	@UiField ListBox country;
	@UiField DateBox date;
	@UiField RadioButton rangeDay;
	@UiField RadioButton rangeWeek;
	@UiField RadioButton rangeMonth;

	// Columns
	private TextColumn<MyApp> columnRank;
	private Column<MyApp, Item> columnAppDetails;
	private TextColumn<MyApp> columnPrice;
	private TextColumn<MyApp> columnDownloads;
	private TextColumn<MyApp> columnRevenue;
	private Column<MyApp, ImageResource> columnIap;

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		Styles.INSTANCE.reflection().ensureInjected();

		FilterHelper.addStores(appStore);
		FilterHelper.addCountries(country);

		FilterController.get().setListType(OVERALL_LIST_TYPE);
		date.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		Date d = FilterHelper.normalizeDate(new Date());
		date.setValue(d);
		updateFromFilter();

		createColumns();

		MyAppsController.get().addDataDisplay(appsTable);
		MyAppsController.get().getAllUserItems();

		// pager.setDisplay(appsTable);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));

		// boolean hasPermission = SessionController.get().loggedInUserHas(SessionController.);

		// pager.setVisible(hasPermission);
		// redirect.setVisible(!hasPermission);

		User user = SessionController.get().getLoggedInUser();

		if (user != null) {
			mLinkedAccountsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken("linkedaccounts", user.id.toString()));
			mMyAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken("myapps", user.id.toString()));
		}
	}

	/**
	 * 
	 */
	private void createColumns() {

		columnRank = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return (object.rank != null) ? object.rank.position.toString() : "-";
			}
		};
		appsTable.addColumn(columnRank, "Rank");

		columnAppDetails = new Column<MyApp, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(MyApp object) {
				return object.item;
			}
		};
		appsTable.addColumn(columnAppDetails, "App Details");

		columnPrice = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return object.item.price.toString();
			}
		};
		appsTable.addColumn(columnPrice, "Price");

		columnDownloads = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return (object.rank != null) ? object.rank.downloads.toString() : "-";
			}
		};
		appsTable.addColumn(columnDownloads, "Downloads");

		columnRevenue = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return (object.rank != null) ? object.rank.revenue.toString() : "-";
			}
		};
		appsTable.addColumn(columnRevenue, "Revenue");

		ImageResourceCell imgIapCell = new ImageResourceCell();
		columnIap = new Column<MyApp, ImageResource>(imgIapCell) {
			@Override
			public ImageResource getValue(MyApp object) {
				// if (object.item.properties.){ TODO IAP image
				return Images.INSTANCE.greenTick();
				// }else{
				// return Images.INSTANCE.;
				// }

			}
		};
		appsTable.addColumn(columnIap, "IAP");

	}

	@UiHandler("appStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(appStore.getValue(appStore.getSelectedIndex()));
	}

	@UiHandler("country")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(country.getValue(country.getSelectedIndex()));
	}

	@UiHandler("date")
	void onDateValueChanged(ValueChangeEvent<Date> event) {
		// TODO disable future dates
		updateFilterDate();
	}

	@UiHandler("rangeDay")
	void onRangeDayValueSelected(ClickEvent event) {
		FilterController.get().setStartDate(date.getValue());
	}

	@UiHandler("rangeWeek")
	void onRangeWeekValueSelected(ClickEvent event) {
		Date startDate = new Date(date.getValue().getTime());
		CalendarUtil.addDaysToDate(startDate, -7);
		FilterController.get().setStartDate(startDate);
	}

	@UiHandler("rangeMonth")
	void onRangeMonthValueSelected(ClickEvent event) {
		Date startDate = new Date(date.getValue().getTime());
		CalendarUtil.addMonthsToDate(startDate, -1);
		FilterController.get().setStartDate(startDate);
	}

	private void updateFromFilter() {
		FilterController.get().start();
		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, FilterController.get().getFilter().getStoreA3Code()));
		country.setSelectedIndex(FormHelper.getItemIndex(country, FilterController.get().getFilter().getCountryA2Code()));
		updateFilterDate();
		FilterController.get().commit();
	}

	public void setFiltersEnabled(boolean enable) {
		appStore.setEnabled(enable);
		country.setEnabled(enable);
		date.setEnabled(enable);
		rangeDay.setEnabled(enable);
		rangeWeek.setEnabled(enable);
		rangeMonth.setEnabled(enable);
	}

	private void updateFilterDate() {

		Date endDate = date.getValue();
		CalendarUtil.addDaysToDate(endDate, 1); // add 1 day to the EndDate because from DatePicker is set at midnight
		FilterController.get().setEndDate(endDate);

		if (rangeDay.getValue()) {
			FilterController.get().setStartDate(date.getValue());
		} else if (rangeWeek.getValue()) {
			Date startDate = new Date(date.getValue().getTime());
			CalendarUtil.addDaysToDate(startDate, -7);
			FilterController.get().setStartDate(startDate);
		} else {
			Date startDate = new Date(date.getValue().getTime());
			CalendarUtil.addMonthsToDate(startDate, -1);
			FilterController.get().setStartDate(startDate);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		if (!name.equals("enddate")) {
			appsTable.setVisibleRangeAndClearData(appsTable.getVisibleRange(), false);
			if (appsTable.getRowCount() < 1) {
				appsTable.setRowCount(1); // Force loading effect
			}
			MyAppsController.get().reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

	}

}
