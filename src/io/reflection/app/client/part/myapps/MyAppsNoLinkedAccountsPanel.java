//
//  MyAppsNoLinkedAccountsPanel.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.myapps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class MyAppsNoLinkedAccountsPanel extends Composite implements HasClickHandlers {

	private static MyAppsNoLinkedAccountsPanelUiBinder uiBinder = GWT.create(MyAppsNoLinkedAccountsPanelUiBinder.class);

	interface MyAppsNoLinkedAccountsPanelUiBinder extends UiBinder<Widget, MyAppsNoLinkedAccountsPanel> {}

	@UiField Button button;

	public MyAppsNoLinkedAccountsPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				fireEvent(event);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}

}
