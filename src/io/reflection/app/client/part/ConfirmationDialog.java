//
//  ConfirmationDialog.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

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

	Long parameter = null;

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
	}

	public Button getCancelButton() {
		return cancel;
	}

	public Button getDeleteButton() {
		return delete;
	}

	public void setParameter(Long typeParameter) {
		parameter = typeParameter;
	}

	public Long getParameter() {
		return parameter;
	}

	public void reset() {
		parameter = null;
		this.hide();
	}

}
