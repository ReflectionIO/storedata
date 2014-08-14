//
//  BootstrapGwtCellTable.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtCellTable extends CellTable.Resources {
	public static final BootstrapGwtCellTable INSTANCE = GWT.create(BootstrapGwtCellTable.class);

	public interface BootstrapGwtCellTableStyle extends CellTable.Style {};

	@Source("res/bootstrapgwtcelltable.css")
	BootstrapGwtCellTableStyle cellTableStyle();
};
