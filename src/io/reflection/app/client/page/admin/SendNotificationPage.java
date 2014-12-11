//
//  SendNotificationPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.text.MarkdownEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SendNotificationPage extends Page {

	private static SendNotificationPageUiBinder uiBinder = GWT.create(SendNotificationPageUiBinder.class);

	interface SendNotificationPageUiBinder extends UiBinder<Widget, SendNotificationPage> {}

	@UiField Preloader preloader;
	@UiField HTMLPanel eventGroup;
	@UiField SuggestBox eventTextBox;
	@UiField HTMLPanel eventNote;

	@UiField(provided = true) SuggestBox userSuggestBox = new SuggestBox(UserController.get().oracle());

	@UiField HTMLPanel userGroup;
	@UiField HTMLPanel userNote;

	@UiField HTMLPanel fromGroup;
	@UiField ListBox fromListBox;
	@UiField HTMLPanel fromNote;
	@UiField HTMLPanel subjectGroup;
	@UiField TextBox subjectTextBox;
	@UiField HTMLPanel subjectNote;
	@UiField HTMLPanel bodyGroup;
	@UiField MarkdownEditor bodyEditor;
	@UiField HTMLPanel bodyNote;
	@UiField Button buttonSend;

	public SendNotificationPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		addFrom();
		super.onAttach();
	}

	private void addFrom() {
		fromListBox.clear();

		String username = SessionController.get().getLoggedInUser().username;
		fromListBox.addItem("Me (" + username + ")", username);
		fromListBox.addItem("Beta (beta@reflection.io)", "beta@reflection.io");
		fromListBox.addItem("Hello (hello@reflection.io)", "hello@reflection.io");
	}

}
