//
//  LoginRegisterPanel.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 3 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 *
 */
public class LoginRegisterPanel extends Composite {

	private static LoginRegisterPanelUiBinder uiBinder = GWT.create(LoginRegisterPanelUiBinder.class);

	interface LoginRegisterPanelUiBinder extends UiBinder<Widget, LoginRegisterPanel> {}

	public LoginRegisterPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
