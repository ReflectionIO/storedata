//
//  MyAppsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 17 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.part.myapps.LinkedAccountsWaiting;
import io.reflection.app.client.part.myapps.MyAppsSidePanel;
import io.reflection.app.client.part.myapps.Overview;
import io.reflection.app.client.part.myapps.OverviewWaiting;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 *
 */
public class MyAppsPage extends Page {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}
	
	@UiField MyAppsSidePanel mMyAppsSidePanel;
	
	@UiField Overview mOverview;
	
	@UiField OverviewWaiting mOverviewWaiting;
	
	@UiField LinkedAccountsWaiting mLinkedAccountsWaiting;

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
