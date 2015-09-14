//
//  CareersPage.java
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
public class CareersPage extends Page {

	private static CareersPageUiBinder uiBinder = GWT.create(CareersPageUiBinder.class);

	interface CareersPageUiBinder extends UiBinder<Widget, CareersPage> {}

	public CareersPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
