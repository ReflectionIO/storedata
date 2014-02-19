//
//  ThankYouRegisterPanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 19 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.register;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class ThankYouRegisterPanel extends Composite {

	private static ThankYouRegisterPanelUiBinder uiBinder = GWT.create(ThankYouRegisterPanelUiBinder.class);

	interface ThankYouRegisterPanelUiBinder extends UiBinder<Widget, ThankYouRegisterPanel> {}

	public ThankYouRegisterPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
