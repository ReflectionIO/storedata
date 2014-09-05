//
//  LeaderboardEmptyTable.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 5 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class LeaderboardEmptyTable extends Composite {

	private static LeaderboardEmptyTableUiBinder uiBinder = GWT.create(LeaderboardEmptyTableUiBinder.class);

	interface LeaderboardEmptyTableUiBinder extends UiBinder<Widget, LeaderboardEmptyTable> {}

	public LeaderboardEmptyTable() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
