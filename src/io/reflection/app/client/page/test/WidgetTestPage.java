//
//  WidgetTestPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 31 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.test;

import io.reflection.app.client.page.Page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class WidgetTestPage extends Page {

	private static WidgetTestPageUiBinder uiBinder = GWT.create(WidgetTestPageUiBinder.class);

	interface WidgetTestPageUiBinder extends UiBinder<Widget, WidgetTestPage> {}

	public WidgetTestPage() {
		initWidget(uiBinder.createAndBindUi(this));

	}
}
