//
//  ReflectionProgressBar.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Mar 2014.
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
public class ReflectionProgressBar extends Composite {

	private static ReflectionProgressBarUiBinder uiBinder = GWT.create(ReflectionProgressBarUiBinder.class);

	interface ReflectionProgressBarUiBinder extends UiBinder<Widget, ReflectionProgressBar> {}

	public ReflectionProgressBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
