//
//  MiniStore.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.shared.datatypes.Store;

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

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
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
		mName.setTargetHistoryToken(NavigationController.get().getCurrentPage() + "/" + mStore.a3Code);
	}

}
