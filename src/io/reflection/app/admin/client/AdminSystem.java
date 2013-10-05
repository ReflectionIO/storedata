//
//  AdminSystem.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client;

import io.reflection.app.admin.client.controller.NavigationController;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author billy1380
 * 
 */
public class AdminSystem extends ErrorHandlingEntryPoint {

	private HTMLPanel mContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();

		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				NavigationController.get().addPage(event.getValue());
			}
		});

		makeContainer();

		// add header
		mContainer.add(NavigationController.get().getHeader());

		// add page area
		mContainer.add(NavigationController.get().getPageHolderPanel());

		// add footer
		mContainer.add(NavigationController.get().getFooter());

		History.fireCurrentHistoryState();

	}

	private void makeContainer() {
		mContainer = new HTMLPanel("");
		mContainer.getElement().setId("container");
		RootPanel.get().add(mContainer);
	}

}
