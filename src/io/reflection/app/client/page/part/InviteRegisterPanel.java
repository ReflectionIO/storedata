//
//  InviteRegisterPanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 16 Jun 2014.
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
public class InviteRegisterPanel extends Composite {

	private static InviteRegisterPanelUiBinder uiBinder = GWT.create(InviteRegisterPanelUiBinder.class);

	interface InviteRegisterPanelUiBinder extends UiBinder<Widget, InviteRegisterPanel> {}

	public InviteRegisterPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
