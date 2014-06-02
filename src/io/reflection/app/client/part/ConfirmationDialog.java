//
//  ConfirmationDialog.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author stefanocapuzzi
 * 
 */
public class ConfirmationDialog extends PopupPanel {
	HTMLPanel dialogWidget = new HTMLPanel("");
	HTMLPanel textPanel = new HTMLPanel("<h1></h1><p></p>");
	HTMLPanel buttonsPanel = new HTMLPanel("");
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete <i class=\"glyphicon glyphicon-remove-circle\"></i>");

	public ConfirmationDialog(String title, String text) {
		super();
		this.hide(true);
		this.setGlassEnabled(true);
		this.setModal(true);
		this.setWidget(dialogWidget);
		textPanel.getElement().getElementsByTagName("h1").getItem(0).setInnerText(title);
		textPanel.getElement().getElementsByTagName("p").getItem(0).setInnerText(text);
		buttonsPanel.add(cancel);
		buttonsPanel.add(delete);
		dialogWidget.add(textPanel);
		dialogWidget.add(buttonsPanel);
		cancel.getElement().addClassName("btn btn-cancel");
		delete.getElement().addClassName("btn btn-delete");

		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ConfirmationDialog.this.hide();
			}
		});

		delete.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ConfirmationDialog.this.hide();
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

	}

	public Button getDeleteButton() {
		return delete;
	}

}
