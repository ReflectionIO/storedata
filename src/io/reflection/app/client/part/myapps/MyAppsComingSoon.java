//
//  MyAppsComingSoon.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 5 Jun 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.myapps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class MyAppsComingSoon extends Composite {

	private static MyAppsComingSoonUiBinder uiBinder = GWT.create(MyAppsComingSoonUiBinder.class);

	interface MyAppsComingSoonUiBinder extends UiBinder<Widget, MyAppsComingSoon> {}

	public MyAppsComingSoon() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
