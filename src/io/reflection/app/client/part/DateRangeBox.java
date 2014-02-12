//
//  DateRangeBox.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Feb 2014.
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
public class DateRangeBox extends Composite {

	private static DateRangeBoxUiBinder uiBinder = GWT.create(DateRangeBoxUiBinder.class);

	interface DateRangeBoxUiBinder extends UiBinder<Widget, DateRangeBox> {}

	public DateRangeBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
