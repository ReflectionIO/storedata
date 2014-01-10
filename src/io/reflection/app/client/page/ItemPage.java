//
//  ItemPage.java
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

public class ItemPage extends Composite implements NavigationEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	@UiField AlertBox mAlertBox;

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
	}

	@Override
	public void navigationChanged(Stack stack) {

		String view;
		if (stack != null && "item".equals(stack.getPage()) && "view".equals(stack.getAction()) && (view = stack.getParameter(0)) != null) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Item",
					" - Normally we would display items details for (" + view + ").", false).setVisible(true);
		} else {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
					.setVisible(true);
		}

	}

}
