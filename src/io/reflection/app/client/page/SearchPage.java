//
//  SearchPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;
import io.reflection.app.datatypes.shared.Item;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class SearchPage extends Page implements NavigationEventHandler, SearchForItemEventHandler {

	private static SearchPageUiBinder uiBinder = GWT.create(SearchPageUiBinder.class);

	interface SearchPageUiBinder extends UiBinder<Widget, SearchPage> {}

	@UiField AlertBox mAlertBox;

	private String mQuery;

	public SearchPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(SearchForItemEventHandler.TYPE, ItemController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (current != null && PageType.SearchPageType.equals(current.getPage())) {
			if ("query".equals(current.getAction()) && (mQuery = current.getParameter(0)) != null) {
				List<Item> items = ItemController.get().searchForItems(mQuery);

				if (items != null) {
					if (items.size() == 0) {
						AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.WarningAlertBoxType, true, "Search " + mQuery,
								" - No results found for search. Try another search term.", false).setVisible(true);
					} else {
						displaySearchResults(items);
					}
				} else {
					AlertBoxHelper
							.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Searching", " - This will only take a few seconds...", false)
							.setVisible(true);
				}
			}

		} else {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Search", " - We could not understand your search!", false)
					.setVisible(true);
		}

	}

	private void displaySearchResults(List<Item> items) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler#searchForItemSuccess(io.reflection.app.api.core.shared.call.SearchForItemRequest,
	 * io.reflection.app.api.core.shared.call.SearchForItemResponse)
	 */
	@Override
	public void searchForItemSuccess(SearchForItemRequest input, SearchForItemResponse output) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler#searchForItemFailure(io.reflection.app.api.core.shared.call.SearchForItemRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void searchForItemFailure(SearchForItemRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

}
