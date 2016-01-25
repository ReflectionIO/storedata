//
//  NoDataPanel.java
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
public class NoDataPanel extends Composite {

	private static NoDataPanelUiBinder uiBinder = GWT.create(NoDataPanelUiBinder.class);

	interface NoDataPanelUiBinder extends UiBinder<Widget, NoDataPanel> {}

	public NoDataPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
