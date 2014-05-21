//
//  StoreWell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.page.part.MiniStore;
import io.reflection.app.datatypes.shared.Store;

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
public class StoreWell extends Composite {

	private static StoreWellUiBinder uiBinder = GWT.create(StoreWellUiBinder.class);

	interface StoreWellUiBinder extends UiBinder<Widget, StoreWell> {}

	@UiField Hyperlink mMore;

	@UiField HTMLPanel mMainStores;
	@UiField HTMLPanel mMoreStores;

	public StoreWell() {
		initWidget(uiBinder.createAndBindUi(this));

		List<Store> stores = StoreController.get().getStores();
		if (stores != null) {
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
	}

	@UiHandler("mMore")
	public void onMoreClicked(ClickEvent event) {
		// NOTE: we do not have enough stores for this yet
	}

}
