//
//  SearchPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SearchPage extends Composite implements NavigationEventHandler {

	private static SearchPageUiBinder uiBinder = GWT.create(SearchPageUiBinder.class);

	interface SearchPageUiBinder extends UiBinder<Widget, SearchPage> {}

	@UiField AlertBox mAlertBox;

	public SearchPage() {
		initWidget(uiBinder.createAndBindUi(this));

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {

		String query;
		if (stack != null && "search".equals(stack.getPage()) && "query".equals(stack.getAction()) && (query = stack.getParameter(0)) != null) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Search",
					" - Normally we would display items found for search query (" + query + ").", false).setVisible(true);
		} else {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Search", " - We could not understand your search!", false)
					.setVisible(true);
		}

	}

}
