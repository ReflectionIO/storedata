//
//  BootstrapGwtDataGrid.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;

/**
 * @author William Shakour (billy1380)
 *
 */
public interface BootstrapGwtDataGrid extends DataGrid.Resources {
	public static final BootstrapGwtDataGrid INSTANCE = GWT.create(BootstrapGwtDataGrid.class);

	public interface BootstrapGwtDataGridStyle extends DataGrid.Style {};

	@Source("res/bootstrapgwtdatagrid.gss")
	BootstrapGwtDataGridStyle dataGridStyle();
}
