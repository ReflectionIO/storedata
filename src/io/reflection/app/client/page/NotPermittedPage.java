//
//  NotPermittedPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 25 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class NotPermittedPage extends Page implements NavigationEventHandler {

	private static NotPermittedPageUiBinder uiBinder = GWT.create(NotPermittedPageUiBinder.class);

	interface NotPermittedPageUiBinder extends UiBinder<Widget, NotPermittedPage> {}

	@UiField InlineHyperlink back;
	@UiField SpanElement page;

	public NotPermittedPage() {
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current.hasPrevious()) {
			back.setTargetHistoryToken(current.getPrevious().toString());
			page.setInnerText(current.getAction() == null ? "unknown" : current.getAction());
		} else {
			back.setTargetHistoryToken(PageType.HomePageType.toString());
		}
	}

}
