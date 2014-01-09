//
//  StoreWell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.StoreController;
import io.reflection.app.admin.client.handler.StoresEventHandler;
import io.reflection.app.shared.datatypes.Store;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class StoreWell extends Composite implements StoresEventHandler {

	private static StoreWellUiBinder uiBinder = GWT.create(StoreWellUiBinder.class);

	interface StoreWellUiBinder extends UiBinder<Widget, StoreWell> {}

	@UiField Hyperlink mMore;
	@UiField HTMLPanel mMainStores;
	@UiField HTMLPanel mMoreStores;

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public StoreWell() {
		initWidget(uiBinder.createAndBindUi(this));

		EventController.get().addHandlerToSource(StoresEventHandler.TYPE, StoreController.get(), this);

		StoreController.get().fetchAllStores();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.event.handler.ReceivedStoresEventHandler#receivedStores(java.util.List)
	 */
	@Override
	public void receivedStores(List<Store> stores) {
		mMainStores.clear();

		int i = 0;
		HTMLPanel row = null;
		for (Store store : stores) {
			if (i++ % 4 == 0) {
				row = new HTMLPanel("");
				row.setStyleName("row");
				mMainStores.add(row);
			}

			row.add(new MiniStore(store));
		}
	}

	@UiHandler("mMore")
	public void onMoreClicked(ClickEvent event) {
		// NOTE: we do not have enough stores for this yet
	}

}
