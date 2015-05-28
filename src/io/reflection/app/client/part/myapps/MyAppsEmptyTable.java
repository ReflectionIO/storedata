//
//  MyAppsEmptyTable.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 22 Jul 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.myapps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class MyAppsEmptyTable extends Composite implements HasClickHandlers {

	private static MyAppsEmptyTableUiBinder uiBinder = GWT.create(MyAppsEmptyTableUiBinder.class);

	interface MyAppsEmptyTableUiBinder extends UiBinder<Widget, MyAppsEmptyTable> {}

	@UiField Element title;
	@UiField Element body;
	@UiField Button button;

	public MyAppsEmptyTable() {
		initWidget(uiBinder.createAndBindUi(this));

		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				fireEvent(event);
			}
		});
	}

	public void setNoDataAccounts(boolean noDataAccounts) {
		title.setInnerText(noDataAccounts ? "Where Are My Apps?" : "This Account is App-less");
		body.setInnerText(noDataAccounts ? "You first need to link an app store account before your apps can be displayed."
				: "You don't currently have any apps in this linked account.");
		button.setText(noDataAccounts ? "Link an Account" : "Link another Account");
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
