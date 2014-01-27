//
//  ItemFilter.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class ItemFilter extends Composite {

	private static ItemFilterUiBinder uiBinder = GWT.create(ItemFilterUiBinder.class);

	interface ItemFilterUiBinder extends UiBinder<Widget, ItemFilter> {}

	public ItemFilter() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
