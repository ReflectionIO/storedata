//
//  HomePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}
	
	@UiField Button bottone6 = new Button();

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("bottone6")
	void cliccato6(ClickEvent event) {
		History.newItem("readytostart");
	}

}