//
//  PressPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 14 Sep 2015.
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
public class PressPage extends Page {

	private static PressPageUiBinder uiBinder = GWT.create(PressPageUiBinder.class);

	interface PressPageUiBinder extends UiBinder<Widget, PressPage> {}

	public PressPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
