//
//  OverviewWaiting.java
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
public class OverviewWaiting extends Composite {

	private static OverviewWaitingUiBinder uiBinder = GWT.create(OverviewWaitingUiBinder.class);

	interface OverviewWaitingUiBinder extends UiBinder<Widget, OverviewWaiting> {}

	
	public OverviewWaiting() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
