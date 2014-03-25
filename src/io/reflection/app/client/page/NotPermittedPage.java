//
//  NotPermittedPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 25 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class NotPermittedPage extends Composite {

	private static NotPermittedPageUiBinder uiBinder = GWT.create(NotPermittedPageUiBinder.class);

	interface NotPermittedPageUiBinder extends UiBinder<Widget, NotPermittedPage> {}

	public NotPermittedPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
