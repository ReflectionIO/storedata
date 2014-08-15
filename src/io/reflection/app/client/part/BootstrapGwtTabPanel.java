//
//  BootstrapGwtDatePicker.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtTabPanel extends ClientBundle {
	public interface BootstrapGwtTabPanelStyles extends CssResource {}

	public static final BootstrapGwtTabPanel INSTANCE = GWT.create(BootstrapGwtTabPanel.class);

	@Source("res/bootstrapgwttabpanel.css")
	BootstrapGwtTabPanelStyles styles();
}
