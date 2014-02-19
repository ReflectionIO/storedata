//
//  MyAppsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 17 Feb 2014.
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
public class MyAppsPage extends Page {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
