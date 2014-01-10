//
//  BootstrapGwtCellTable.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.SimplePager;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtSimplePager extends SimplePager.Resources {
	public static final BootstrapGwtSimplePager INSTANCE = GWT.create(BootstrapGwtSimplePager.class);

	public interface BootstrapGwtSimplePagerStyle extends SimplePager.Style {};

	@Source("../res/bootstrapgwtsimplepager.css")
	BootstrapGwtSimplePagerStyle simplePagerStyle();
};
