//
//  AboutPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 11 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AboutPage extends Page {

	private static AboutPageUiBinder uiBinder = GWT.create(AboutPageUiBinder.class);

	interface AboutPageUiBinder extends UiBinder<Widget, AboutPage> {}

	public AboutPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
