//
//  StoreWell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.event.GetStoresEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.datatypes.shared.Store;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class StoreWell extends Composite implements GetStoresEventHandler {

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

		EventController.get().addHandlerToSource(GetStoresEventHandler.TYPE, StoreController.get(), this);

		StoreController.get().fetchAllStores();

	}

	@UiHandler("mMore")
	public void onMoreClicked(ClickEvent event) {
		// NOTE: we do not have enough stores for this yet
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetStoresEventHandler#getStoresSuccess(io.reflection.app.api.core.shared.call.GetStoresRequest,
	 * io.reflection.app.api.core.shared.call.GetStoresResponse)
	 */
	@Override
	public void getStoresSuccess(GetStoresRequest input, GetStoresResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.stores != null) {
			mMainStores.clear();

			int i = 0;
			HTMLPanel row = null;
			for (Store store : output.stores) {
				if (i++ % 4 == 0) {
					row = new HTMLPanel("");
					row.setStyleName("row");
					mMainStores.add(row);
				}

				row.add(new MiniStore(store));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetStoresEventHandler#getStoresFailure(io.reflection.app.api.core.shared.call.GetStoresRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getStoresFailure(GetStoresRequest input, Throwable caught) {}

}
