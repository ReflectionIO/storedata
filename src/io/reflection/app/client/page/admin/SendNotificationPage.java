//
//  SendNotificationPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.GetEventsRequest;
import io.reflection.app.api.admin.shared.call.GetEventsResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler;
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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
public class SendNotificationPage extends Page implements GetUsersEventHandler, GetEventsEventHandler {

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
		register(DefaultEventBus.get().addHandlerToSource(GetEventsEventHandler.TYPE, EventController.get(), this));

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
	void onUserSuggestBoxSelectionEvent(SelectionEvent<Suggestion> e) {
		user = userLookup.get(e.getSelectedItem().getReplacementString());
		UserController.get().reset();

		if (event != null) {
			subjectTextBox.setText(fillUserDetails(subjectTextBox.getText()));
			bodyEditor.setText(fillUserDetails(bodyEditor.getText()));
		}
	}

	@UiHandler("eventSuggestBox")
	void onEventSuggestBoxSelectionEvent(SelectionEvent<Suggestion> e) {
		event = eventLookup.get(e.getSelectedItem().getReplacementString());
		EventController.get().reset();

		if (event != null) {
			subjectTextBox.setText(SafeHtmlUtils.fromString(fillUserDetails(event.subject)).asString());
			bodyEditor.setText(SafeHtmlUtils.fromString(fillUserDetails(event.longBody)).asString());
		}
	}

	/**
	 * @param text
	 * @return
	 */
	private String fillUserDetails(String text) {
		String filled = text;
		if (user != null) {
			filled = filled.replaceAll("\\$\\{user\\.forename\\}", user.forename);
			filled = filled.replaceAll("\\$\\{user\\.surname\\}", user.surname);
			filled = filled.replaceAll("\\$\\{user\\.username\\}", user.username);
		}

		return filled;
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
				userLookup.put(UserController.get().getOracleUserDescription(user), user);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler#getEventsSuccess(io.reflection.app.api.admin.shared.call.GetEventsRequest,
	 * io.reflection.app.api.admin.shared.call.GetEventsResponse)
	 */
	@Override
	public void getEventsSuccess(GetEventsRequest input, GetEventsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.events != null && output.events.size() > 0) {
			eventLookup.clear();

			for (Event event : output.events) {
				eventLookup.put(EventController.get().getOracleEventDescription(event), event);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler#getEventsFailure(io.reflection.app.api.admin.shared.call.GetEventsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getEventsFailure(GetEventsRequest input, Throwable caught) {}

}
