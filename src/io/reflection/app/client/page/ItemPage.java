//
//  ItemPage.java
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
import io.reflection.app.client.part.ItemFilter;
import io.reflection.app.datatypes.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Composite implements NavigationEventHandler, SearchForItemEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	@UiField AlertBox mAlertBox;
	@UiField ItemFilter mFilter;

	String mItemExternalId;

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(SearchForItemEventHandler.TYPE, ItemController.get(), this);
	}

	@Override
	public void navigationChanged(Stack stack) {

		if (stack != null && "item".equals(stack.getPage())) {

			if ("view".equals(stack.getAction()) && (mItemExternalId = stack.getParameter(0)) != null) {

				Item item = null;

				if ((item = ItemController.get().lookupItem(mItemExternalId)) != null) {
					displayItemDetails(item);
				} else {
					AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Getting details", " - This will only take a few seconds...",
							false).setVisible(true);

					mFilter.setVisible(false);
				}

				// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Item",
				// " - Normally we would display items details for (" + view + ").", false).setVisible(true);
			} else {
				AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
						.setVisible(true);
			}

		}

	}

	private void displayItemDetails(Item item) {
		mAlertBox.setVisible(false);

		mFilter.setName(item.name);
		mFilter.setImage(item.largeImage);
		mFilter.setCreatorName(item.creatorName);
		
		mFilter.setVisible(true);
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
		boolean found = false;

		if (mItemExternalId != null && mItemExternalId.equals(input.query) && output.status == StatusType.StatusTypeSuccess) {

			// for now we don't lookup the item again... because it causes an infinite loop of lookup failure
			if (output.items != null) for (Item item : output.items) {
				if (mItemExternalId.equals(item.externalId)) {
					displayItemDetails(item);
					found = true;
					break;
				}
			}

		}

		if (!found) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
					.setVisible(true);
		}
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
		if (mItemExternalId != null && mItemExternalId.equals(input.query)) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We could not find the requrested item!", false)
					.setVisible(true);
		}
	}

}
