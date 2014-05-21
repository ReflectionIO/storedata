//
//  LinkedAccountsWaiting.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 21 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.myapps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class LinkedAccountsWaiting extends Composite {

	private static LinkedAccountsWaitingUiBinder uiBinder = GWT.create(LinkedAccountsWaitingUiBinder.class);

	interface LinkedAccountsWaitingUiBinder extends UiBinder<Widget, LinkedAccountsWaiting> {}

	public LinkedAccountsWaiting() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
