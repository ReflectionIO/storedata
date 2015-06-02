//
//  Error404Page.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 7 May 2015.
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
public class Error404Page extends Page {

	private static Error404PageUiBinder uiBinder = GWT.create(Error404PageUiBinder.class);

	interface Error404PageUiBinder extends UiBinder<Widget, Error404Page> {}

	public Error404Page() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
