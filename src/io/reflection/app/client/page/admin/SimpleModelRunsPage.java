//
//  SimpleModelRunsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 20 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMAT_DD_MMM_YYYY;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SimpleModelRunController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.part.DateSelector;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.SimpleModelRun;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class SimpleModelRunsPage extends Page implements FilterEventHandler {

	private static SimpleModelRunsPageUiBinder uiBinder = GWT.create(SimpleModelRunsPageUiBinder.class);

	interface SimpleModelRunsPageUiBinder extends UiBinder<Widget, SimpleModelRunsPage> {}

	@UiField(provided = true) CellTable<SimpleModelRun> simpleModelRunTable = new CellTable<SimpleModelRun>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField ListBox listType;
	@UiField DateSelector dateSelector;
	@UiField ListBox country;
	@UiField ListBox appStore;
	@UiField ListBox category;

	public SimpleModelRunsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		addColumns();

		FilterHelper.addCountries(country, true);
		FilterHelper.addStores(appStore, true);
		FilterHelper.addCategories(category, true);

		simpleModelRunTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		simpleModelRunTable.setEmptyTableWidget(new HTMLPanel("<h6>No Simple Model Runs</h6>"));
		SimpleModelRunController.get().addDataDisplay(simpleModelRunTable);
		simplePager.setDisplay(simpleModelRunTable);

		dateSelector.addFixedRanges(FilterHelper.getAdminDateRanges());

		updateFromFilter();

	}

	private void addColumns() {

		TextColumn<SimpleModelRun> idColumn = new TextColumn<SimpleModelRun>() {

			@Override
			public String getValue(SimpleModelRun object) {
				return object.id.toString();
			}

		};
		simpleModelRunTable.addColumn(idColumn, "Id");

		TextColumn<SimpleModelRun> createdColumn = new TextColumn<SimpleModelRun>() {

			@Override
			public String getValue(SimpleModelRun object) {
				return DATE_FORMAT_DD_MMM_YYYY.format(object.created);
			}

		};
		simpleModelRunTable.addColumn(createdColumn, "Created");

		TextColumn<SimpleModelRun> aColumn = new TextColumn<SimpleModelRun>() {

			@Override
			public String getValue(SimpleModelRun object) {
				return object.a.toString();
			}

		};
		simpleModelRunTable.addColumn(aColumn, "a");

		TextColumn<SimpleModelRun> bColumn = new TextColumn<SimpleModelRun>() {

			@Override
			public String getValue(SimpleModelRun object) {
				return object.b.toString();
			}

		};
		simpleModelRunTable.addColumn(bColumn, "b");

		TextColumn<SimpleModelRun> feedFetchIdColumn = new TextColumn<SimpleModelRun>() {

			@Override
			public String getValue(SimpleModelRun object) {
				return object.feedFetch != null && object.feedFetch.id != null ? object.feedFetch.id.toString() : "-";
			}

		};
		simpleModelRunTable.addColumn(feedFetchIdColumn, "Feed Id");

		TextColumn<SimpleModelRun> feedDateColumn = new TextColumn<SimpleModelRun>() {

			@Override
			public String getValue(SimpleModelRun object) {
				return object.feedFetch != null && object.feedFetch.date != null ? DATE_FORMAT_DD_MMM_YYYY.format(object.feedFetch.date) : "-";
			}

		};
		simpleModelRunTable.addColumn(feedDateColumn, "Date");

	}

	private void updateFromFilter() {
		FilterController fc = FilterController.get();

		listType.setSelectedIndex(FormHelper.getItemIndex(listType, fc.getFilter().getListType()));

		DateRange range = new DateRange();
		range.setFrom(fc.getStartDate());
		range.setTo(fc.getEndDate());
		dateSelector.setValue(range);

		country.setSelectedIndex(FormHelper.getItemIndex(country, fc.getFilter().getCountryA2Code()));
		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, fc.getFilter().getStoreA3Code()));
		category.setSelectedIndex(FormHelper.getItemIndex(category, fc.getFilter().getCategoryId().toString()));
	}

	@UiHandler("listType")
	void onListTypeValueChanged(ChangeEvent event) {
		FilterController.get().setListType(listType.getValue(listType.getSelectedIndex()));
	}

	@UiHandler("dateSelector")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();
		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
	}

	@UiHandler("country")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(country.getValue(country.getSelectedIndex()));
	}

	@UiHandler("appStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(appStore.getValue(appStore.getSelectedIndex()));
	}

	@UiHandler("category")
	void onCategoryValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(Long.valueOf(category.getValue(category.getSelectedIndex())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		register(DefaultEventBus.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		if (NavigationController.get().getCurrentPage() == PageType.SimpleModelRunPageType) {
			SimpleModelRunController.get().reset();
			simplePager.setPageStart(0);
			SimpleModelRunController.get().fetchSimpleModelRuns();
			PageType.SimpleModelRunPageType.show(FilterController.get().asFeedFilterString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		if (NavigationController.get().getCurrentPage() == PageType.SimpleModelRunPageType) {
			SimpleModelRunController.get().reset();
			simplePager.setPageStart(0);
			SimpleModelRunController.get().fetchSimpleModelRuns();
			PageType.SimpleModelRunPageType.show(FilterController.get().asFeedFilterString());
		}
	}

}
