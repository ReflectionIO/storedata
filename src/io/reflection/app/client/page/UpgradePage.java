//
//  UpgradePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class UpgradePage extends Page {

	private static UpgradePageUiBinder uiBinder = GWT.create(UpgradePageUiBinder.class);

	interface UpgradePageUiBinder extends UiBinder<Widget, UpgradePage> {}

	public UpgradePage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
