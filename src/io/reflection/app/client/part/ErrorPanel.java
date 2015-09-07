//
//  ErrorPanel.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ErrorPanel extends Composite {

	private static ErrorPanelUiBinder uiBinder = GWT.create(ErrorPanelUiBinder.class);

	interface ErrorPanelUiBinder extends UiBinder<Widget, ErrorPanel> {}

	public ErrorPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
