//
//  LinkedAccount.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class LinkedAccount extends Composite {

	private static LinkedAccountUiBinder uiBinder = GWT.create(LinkedAccountUiBinder.class);

	interface LinkedAccountUiBinder extends UiBinder<Widget, LinkedAccount> {}

	public LinkedAccount() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
