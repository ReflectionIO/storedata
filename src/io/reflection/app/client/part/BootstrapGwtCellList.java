//
//  BootstrapGwtCellList.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 March 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtCellList extends CellList.Resources {
	public static final BootstrapGwtCellList INSTANCE = GWT.create(BootstrapGwtCellList.class);

	public interface BootstrapGwtCellListStyle extends Style {};

	@Source("../res/bootstrapgwtcelllist.css")
	Style cellListStyle();
};
