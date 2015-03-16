//
//  PanelRightSearch.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class PanelRightSearch extends Composite {

	private static PanelRightSearchUiBinder uiBinder = GWT.create(PanelRightSearchUiBinder.class);

	interface PanelRightSearchUiBinder extends UiBinder<Widget, PanelRightSearch> {}

	@UiField DivElement panelOverlay;

	public PanelRightSearch() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public DivElement getPanelOverlay() {
		return panelOverlay;
	}

}
