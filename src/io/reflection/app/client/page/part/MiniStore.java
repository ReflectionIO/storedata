//
//  MiniStore.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.part;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.datatypes.shared.Store;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class MiniStore extends Composite {

	private static MiniStoreUiBinder uiBinder = GWT.create(MiniStoreUiBinder.class);

	interface MiniStoreUiBinder extends UiBinder<Widget, MiniStore> {}

	private Store mStore;

	@UiField InlineHyperlink mName;

	public MiniStore() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * @param store
	 */
	public MiniStore(Store store) {
		this();

		mStore = store;

		mName.setText(mStore.name);
		// FIXME: this is not going to work... modally or not
		mName.setTargetHistoryToken(NavigationController.get().getCurrentPage().asTargetHistoryToken(mStore.a3Code));
	}

}
