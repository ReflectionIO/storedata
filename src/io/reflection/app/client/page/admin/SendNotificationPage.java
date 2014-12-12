//
//  SendNotificationPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.event.GetUsersEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtSuggestBox;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.User;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SendNotificationPage extends Page implements GetUsersEventHandler {

	private static SendNotificationPageUiBinder uiBinder = GWT.create(SendNotificationPageUiBinder.class);

	interface SendNotificationPageUiBinder extends UiBinder<Widget, SendNotificationPage> {}

	@UiField Preloader preloader;

	@UiField(provided = true) SuggestBox eventSuggestBox = new SuggestBox(EventController.get().oracle());
	@UiField HTMLPanel eventGroup;
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

	private User user;
	private Event event;

	private Map<String, User> userLookup = new HashMap<String, User>();
	private Map<String, Event> eventLookup = new HashMap<String, Event>();

	public SendNotificationPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtSuggestBox.INSTANCE.styles().ensureInjected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		addFrom();

		register(DefaultEventBus.get().addHandlerToSource(GetUsersEventHandler.TYPE, UserController.get(), this));

		super.onAttach();
	}

	private void addFrom() {
		fromListBox.clear();

		String username = SessionController.get().getLoggedInUser().username;
		fromListBox.addItem("Me (" + username + ")", username);
		fromListBox.addItem("Beta (beta@reflection.io)", "beta@reflection.io");
		fromListBox.addItem("Hello (hello@reflection.io)", "hello@reflection.io");
	}

	@UiHandler("userSuggestBox")
	void onUserSuggestBoxSSelectionEvent(SelectionEvent<Suggestion> e) {
		user = userLookup.get(e.getSelectedItem().getReplacementString());
		UserController.get().reset();
	}

	@UiHandler("eventSuggestBox")
	void onEventSuggestBoxSSelectionEvent(SelectionEvent<Suggestion> e) {
		event = eventLookup.get(e.getSelectedItem().getReplacementString());
		EventController.get().reset();
	}

	@UiHandler("buttonSend")
	void onButtonSendClicked(ClickEvent e) {
		if (validate()) {

		}
	}

	private boolean validate() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetUsersEventHandler#getUsersSuccess(io.reflection.app.api.admin.shared.call.GetUsersRequest,
	 * io.reflection.app.api.admin.shared.call.GetUsersResponse)
	 */
	@Override
	public void getUsersSuccess(GetUsersRequest input, GetUsersResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.users != null && output.users.size() > 0) {
			userLookup.clear();

			for (User user : output.users) {
				userLookup.put(user.id.toString() + ":" + user.username, user);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetUsersEventHandler#getUsersFailure(io.reflection.app.api.admin.shared.call.GetUsersRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getUsersFailure(GetUsersRequest input, Throwable caught) {}

}
