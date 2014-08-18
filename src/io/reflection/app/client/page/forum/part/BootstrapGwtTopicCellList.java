//
//  BootstrapGwtTopicCellList.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Aug 2014.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtTopicCellList extends CellList.Resources {
	public static final BootstrapGwtTopicCellList INSTANCE = GWT.create(BootstrapGwtTopicCellList.class);

	public interface BootstrapGwtTopicCellListStyle extends Style {};

	@Source("res/bootstrapgwttopiccelllist.css")
	BootstrapGwtTopicCellListStyle cellListStyle();

};
