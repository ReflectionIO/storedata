//
//  FeedBrowserPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FeedFetchController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.Breadcrumbs;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.FeedFetch;

import java.util.Map;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class FeedBrowserPage extends Page implements FilterEventHandler {

	private static FeedBrowserPageUiBinder uiBinder = GWT.create(FeedBrowserPageUiBinder.class);

	interface FeedBrowserPageUiBinder extends UiBinder<Widget, FeedBrowserPage> {}

	@UiField(provided = true) CellTable<FeedFetch> mFeeds = new CellTable<FeedFetch>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	@UiField ListBox mAppStore;
	@UiField ListBox mCountry;
	@UiField ListBox category;

	@UiField ListBox mListType;

	@UiField Breadcrumbs mBreadcrumbs;

	public FeedBrowserPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		FilterHelper.addStores(mAppStore);
		FilterHelper.addCountries(mCountry);
		FilterHelper.addCategories(category);

		// final SingleSelectionModel<FeedFetch> s = new SingleSelectionModel<FeedFetch>();
		// s.addSelectionChangeHandler(new Handler() {
		//
		// @Override
		// public void onSelectionChange(SelectionChangeEvent event) {
		// FeedFetch selected = s.getSelectedObject();
		//
		// if (selected != null) {
		// mIngest.removeStyleName("disabled");
		// mModel.removeStyleName("disabled");
		// mPredict.removeStyleName("disabled");
		// } else {
		// mIngest.addStyleName("disabled");
		// mModel.addStyleName("disabled");
		// mPredict.addStyleName("disabled");
		// }
		// }
		// });
		// mFeeds.setSelectionModel(s);

		FeedFetchController.get().addDataDisplay(mFeeds);
		mPager.setDisplay(mFeeds);

		refreshBreadcrumbs();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
	}

	/**
	 * 
	 */
	private void refreshBreadcrumbs() {
		mBreadcrumbs.clear();
		mBreadcrumbs.push(mAppStore.getItemText(mAppStore.getSelectedIndex()), mCountry.getItemText(mCountry.getSelectedIndex()),
				mListType.getItemText(mListType.getSelectedIndex()), "Feeds");
	}

	/**
	 * 
	 */
	private void createColumns() {

		mFeeds.addColumn(new TextColumn<FeedFetch>() {

			@Override
			public String getValue(FeedFetch object) {
				return object.id.toString();
			}
		}, "Id");

		mFeeds.addColumn(new TextColumn<FeedFetch>() {

			@Override
			public String getValue(FeedFetch object) {
				return object.code.toString();
			}
		}, "Code");

		mFeeds.addColumn(new TextColumn<FeedFetch>() {

			@Override
			public String getValue(FeedFetch object) {
				return DateTimeFormat.getFormat("yyyy-MM-dd a").format(object.date);
			}
		}, "Date");

		mFeeds.addColumn(new TextColumn<FeedFetch>() {

			@Override
			public String getValue(FeedFetch object) {
				return object.type;
			}
		}, "Type");

		FieldUpdater<FeedFetch, String> onClick = new FieldUpdater<FeedFetch, String>() {

			@Override
			public void update(int index, FeedFetch object, String value) {
				switch (value) {
				case "ingest":
					FeedFetchController.get().ingest(object.code);
					break;
				case "model":
					FeedFetchController.get().model(object.code);
					break;
				case "predict":
					FeedFetchController.get().predict(object.code);
					break;
				}
			}
		};

		StyledButtonCell prototype = new StyledButtonCell("btn", "btn-xs", "btn-default"); 

		Column<FeedFetch, String> ingest = new Column<FeedFetch, String>(prototype) {

			@Override
			public String getValue(FeedFetch object) {
				return "ingest";
			}

		};

		ingest.setFieldUpdater(onClick);

		Column<FeedFetch, String> model = new Column<FeedFetch, String>(prototype) {

			@Override
			public String getValue(FeedFetch object) {
				return "model";
			}

		};

		model.setFieldUpdater(onClick);

		Column<FeedFetch, String> predict = new Column<FeedFetch, String>(prototype) {

			@Override
			public String getValue(FeedFetch object) {
				return "predict";
			}

		};

		predict.setFieldUpdater(onClick);

		mFeeds.addColumn(ingest);
		mFeeds.addColumn(model);
		mFeeds.addColumn(predict);
	}

	@UiHandler("mAppStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(mAppStore.getValue(mAppStore.getSelectedIndex()));
	}

	@UiHandler("mListType")
	void onListTypeValueChanged(ChangeEvent event) {
		FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
	}

	@UiHandler("mCountry")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(mCountry.getValue(mCountry.getSelectedIndex()));
	}

	@UiHandler("category")
	void onCategoryValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(Long.valueOf(category.getValue(category.getSelectedIndex())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		refreshBreadcrumbs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		refreshBreadcrumbs();
	}
}
