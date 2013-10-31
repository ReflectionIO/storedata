//
//  FeedBrowserPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.FeedFetchController;
import io.reflection.app.admin.client.controller.FilterController;
import io.reflection.app.admin.client.controller.ServiceController;
import io.reflection.app.admin.client.part.BootstrapGwtCellTable;
import io.reflection.app.admin.client.part.SimplePager;
import io.reflection.app.shared.datatypes.FeedFetch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author billy1380
 * 
 */
public class FeedBrowserPage extends Composite {

	private static FeedBrowserPageUiBinder uiBinder = GWT.create(FeedBrowserPageUiBinder.class);

	interface FeedBrowserPageUiBinder extends UiBinder<Widget, FeedBrowserPage> {}

	@UiField(provided = true) CellTable<FeedFetch> mFeeds = new CellTable<FeedFetch>(ServiceController.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager mPager = new SimplePager(false, false);

	@UiField ListBox mAppStore;
	@UiField ListBox mListType;
	@UiField ListBox mCountry;
	
	@UiField Button mIngest;
	@UiField Button mModel;
	@UiField Button mPredict;

	public FeedBrowserPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addFeedColumns();

		final SingleSelectionModel<FeedFetch> s = new SingleSelectionModel<FeedFetch>();
		s.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				FeedFetch selected = s.getSelectedObject();

				mIngest.setEnabled(selected != null);
				mModel.setEnabled(selected != null);
				mPredict.setEnabled(selected != null);
			}
		});
		mFeeds.setSelectionModel(s);

		FeedFetchController.get().addDataDisplay(mFeeds);
		mPager.setDisplay(mFeeds);

		FilterController.get().start();
		FilterController.get().setStore(mAppStore.getValue(mAppStore.getSelectedIndex()));
		FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
		FilterController.get().setCountry(mCountry.getValue(mCountry.getSelectedIndex()));
		FilterController.get().commit();
	}

	/**
	 * 
	 */
	private void addFeedColumns() {
		
		mFeeds.addColumn(new TextColumn<FeedFetch>() {

			@Override
			public String getValue(FeedFetch object) {
				return object.code;
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

	
	@UiHandler("mIngest")
	void onIngestClicked(ClickEvent event) {
		@SuppressWarnings("unchecked")
		FeedFetch selected = ((SingleSelectionModel<FeedFetch>)mFeeds.getSelectionModel()).getSelectedObject();
		
		if (selected != null) {
			FeedFetchController.get().ingest(selected.code);
		}
	}
	
	@UiHandler("mModel")
	void onModelClicked(ClickEvent event) {
		@SuppressWarnings("unchecked")
		FeedFetch selected = ((SingleSelectionModel<FeedFetch>)mFeeds.getSelectionModel()).getSelectedObject();
		
		if (selected != null) {
			FeedFetchController.get().model(selected.code);
		}
	}
	
	@UiHandler("mPredict")
	void onPredictClicked(ClickEvent event) {
		@SuppressWarnings("unchecked")
		FeedFetch selected = ((SingleSelectionModel<FeedFetch>)mFeeds.getSelectionModel()).getSelectedObject();
		
		if (selected != null) {
			FeedFetchController.get().predict(selected.code);
		}
	}
}
