//
//  MyAppsLinkedAccountsPage.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 7 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsLinkedAccountsPage extends Page {

	private static MyAppsLinkedAccountsPageUiBinder uiBinder = GWT.create(MyAppsLinkedAccountsPageUiBinder.class);

	interface MyAppsLinkedAccountsPageUiBinder extends UiBinder<Widget, MyAppsLinkedAccountsPage> {}

	public MyAppsLinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
