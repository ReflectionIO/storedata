//
//  LinkedAccountsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.helper.AlertBoxHelper;
import io.reflection.app.admin.client.part.AlertBox;
import io.reflection.app.admin.client.part.AlertBox.AlertBoxType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountsPage extends Composite {

	private static LinkedAccountsPageUiBinder uiBinder = GWT.create(LinkedAccountsPageUiBinder.class);

	interface LinkedAccountsPageUiBinder extends UiBinder<Widget, LinkedAccountsPage> {}

	@UiField AlertBox mAlertBox;

	@UiField InlineHyperlink mIosMacLink;
	@UiField InlineHyperlink mPlayLink;
	@UiField InlineHyperlink mAmazonLink;
	@UiField InlineHyperlink mWindowsPhoneLink;

	@UiField FormPanel mForm;

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, false, "None", " - You currently have no linked accounts.", false).setVisible(
				true);

		addSoonTag(mPlayLink);

		mIosMacLink.setTargetHistoryToken("users/linkedaccounts/" + NavigationController.get().getStack().getParameter(0) + "/iosmac");

		mForm.setVisible(true);
	}

	/**
	 * @param link
	 */
	private void addSoonTag(InlineHyperlink link) {
		SpanElement s = DOM.createSpan().cast();
		s.setInnerText("Coming Soon!");
		s.addClassName("label");
		s.addClassName("label-danger");
		s.getStyle().setMarginLeft(10.0, Unit.PX);
		link.getElement().appendChild(s);
	}

}
