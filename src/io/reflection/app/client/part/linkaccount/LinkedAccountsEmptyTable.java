//
//  LinkedAccountsEmptyTable.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 22 Jul 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.linkaccount;

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
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class LinkedAccountsEmptyTable extends Composite implements HasClickHandlers {

    private static LinkedAccountsEmptyTableUiBinder uiBinder = GWT.create(LinkedAccountsEmptyTableUiBinder.class);

    interface LinkedAccountsEmptyTableUiBinder extends UiBinder<Widget, LinkedAccountsEmptyTable> {}

	@UiField Button button;

    public LinkedAccountsEmptyTable() {
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
