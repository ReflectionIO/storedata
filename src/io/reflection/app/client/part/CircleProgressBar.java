//
//  CircleProgressBar.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 *
 */
public class CircleProgressBar extends Composite {

	private static CircleProgressBarUiBinder uiBinder = GWT.create(CircleProgressBarUiBinder.class);

	interface CircleProgressBarUiBinder extends UiBinder<Widget, CircleProgressBar> {}

	public CircleProgressBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
