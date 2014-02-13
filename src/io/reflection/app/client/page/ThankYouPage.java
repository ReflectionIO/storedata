//
//  ThankYouPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 10 Feb 2014.
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
public class ThankYouPage extends Page {

	private static ThankYouPageUiBinder uiBinder = GWT.create(ThankYouPageUiBinder.class);

	interface ThankYouPageUiBinder extends UiBinder<Widget, ThankYouPage> {}

	public ThankYouPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
