//
//  ForgotPasswordPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
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
public class ForgotPasswordPage extends Composite {

	private static ForgotPasswordPageUiBinder uiBinder = GWT.create(ForgotPasswordPageUiBinder.class);

	interface ForgotPasswordPageUiBinder extends UiBinder<Widget, ForgotPasswordPage> {}

	public ForgotPasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
