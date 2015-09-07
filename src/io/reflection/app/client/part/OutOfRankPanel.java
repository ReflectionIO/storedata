//
//  OutOfRankPanel.java
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
public class OutOfRankPanel extends Composite {

	private static OutOfRankPanelUiBinder uiBinder = GWT.create(OutOfRankPanelUiBinder.class);

	interface OutOfRankPanelUiBinder extends UiBinder<Widget, OutOfRankPanel> {}

	public OutOfRankPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
