//
//  SendNotificationPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.page.Page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SendNotificationPage extends Page {

	private static SendNotificationPageUiBinder uiBinder = GWT.create(SendNotificationPageUiBinder.class);

	interface SendNotificationPageUiBinder extends UiBinder<Widget, SendNotificationPage> {}

	public SendNotificationPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
