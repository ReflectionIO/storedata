//
//  LoggedInHomePage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 21 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class LoggedInHomePage extends Page {

	private static LoggedInHomePageUiBinder uiBinder = GWT.create(LoggedInHomePageUiBinder.class);

	interface LoggedInHomePageUiBinder extends UiBinder<Widget, LoggedInHomePage> {}

	@UiField SpanElement userName;

	public LoggedInHomePage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		User user = SessionController.get().getLoggedInUser();
		if (user != null) {
			userName.setInnerText(user.forename);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

	}
}
