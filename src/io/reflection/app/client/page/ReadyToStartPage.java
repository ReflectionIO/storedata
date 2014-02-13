//
//  ReadyToStartPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 11 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class ReadyToStartPage extends Composite {

	private static ReadyToStartPageUiBinder uiBinder = GWT.create(ReadyToStartPageUiBinder.class);

	interface ReadyToStartPageUiBinder extends UiBinder<Widget, ReadyToStartPage> {}

	@UiField InlineHyperlink mLeaderboard;

	public ReadyToStartPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Redirect to Rank Page when user clicks on 'Leaderboard' link
	 * 
	 * @param event
	 */
	@UiHandler("mLeaderboard")
	void onLeaderboardClick(ClickEvent event) {
		History.newItem("ranks");
	}

}
