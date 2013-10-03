//
//  AdminSystem.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client;

import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.part.Footer;
import io.reflection.app.admin.client.part.Header;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author billy1380
 * 
 */
public class AdminSystem extends ErrorHandlingEntryPoint {

	HTMLPanel mContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();

		mContainer = new HTMLPanel("");
		mContainer.getElement().setId("container");

		mContainer.add(new Header());

		NavigationController nav = NavigationController.get();
		mContainer.add(nav.getPageHolderPanel());

		nav.addRanksPage();

		mContainer.add(new Footer());

		RootPanel.get().add(mContainer);
		
		

	}

}
