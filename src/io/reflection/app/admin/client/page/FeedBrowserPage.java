//
//  FeedBrowserPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.ServiceController;
import io.reflection.app.admin.client.part.BootstrapGwtCellTable;
import io.reflection.app.admin.client.part.SimplePager;
import io.reflection.app.shared.datatypes.FeedFetch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class FeedBrowserPage extends Composite {

	private static FeedBrowserPageUiBinder uiBinder = GWT.create(FeedBrowserPageUiBinder.class);

	interface FeedBrowserPageUiBinder extends UiBinder<Widget, FeedBrowserPage> {}

	@UiField(provided = true) CellTable<FeedFetch> mIngestedFeeds = new CellTable<FeedFetch>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager mIngestedPager;
	
	@UiField(provided = true) CellTable<FeedFetch> mOutstandingFeeds = new CellTable<FeedFetch>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager mOutstandingPager;

	public FeedBrowserPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		mIngestedPager.setDisplay(mIngestedFeeds);
		mOutstandingPager.setDisplay(mOutstandingFeeds);
	}

}
