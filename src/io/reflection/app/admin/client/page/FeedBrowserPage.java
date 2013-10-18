//
//  FeedBrowserPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.FilterController;
import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.controller.NavigationController.Stack;
import io.reflection.app.admin.client.controller.ServiceController;
import io.reflection.app.admin.client.handler.FilterEventHandler;
import io.reflection.app.admin.client.handler.NavigationEventHandler;
import io.reflection.app.admin.client.part.BootstrapGwtCellTable;
import io.reflection.app.admin.client.part.SimplePager;
import io.reflection.app.shared.datatypes.FeedFetch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class FeedBrowserPage extends Composite implements NavigationEventHandler, FilterEventHandler {

	private static FeedBrowserPageUiBinder uiBinder = GWT.create(FeedBrowserPageUiBinder.class);

	interface FeedBrowserPageUiBinder extends UiBinder<Widget, FeedBrowserPage> {}

	@UiField(provided = true) CellTable<FeedFetch> mIngestedFeeds = new CellTable<FeedFetch>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager mIngestedPager;

	@UiField(provided = true) CellTable<FeedFetch> mOutstandingFeeds = new CellTable<FeedFetch>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager mOutstandingPager;

	@UiField ListBox mAppStore;
	@UiField ListBox mListType;
	@UiField ListBox mCountry;

	public FeedBrowserPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mIngestedPager.setDisplay(mIngestedFeeds);
		mOutstandingPager.setDisplay(mOutstandingFeeds);

		EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);

		FilterController.get().setStore(mAppStore.getValue(mAppStore.getSelectedIndex()));
		FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
		FilterController.get().setCountry(mCountry.getValue(mCountry.getSelectedIndex()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.admin.client.event.NavigationChanged.Handler#navigationChanged(io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.event.FilterChanged.Handler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {

		Window.alert(name + " changed to " + currentValue);
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
}
