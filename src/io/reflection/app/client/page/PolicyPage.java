//
//  PolicyPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class PolicyPage extends Page {

	private static PolicyPageUiBinder uiBinder = GWT.create(PolicyPageUiBinder.class);

	interface PolicyPageUiBinder extends UiBinder<Widget, PolicyPage> {}

	public PolicyPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
