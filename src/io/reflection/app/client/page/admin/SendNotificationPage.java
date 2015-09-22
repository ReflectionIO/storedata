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
import io.reflection.app.api.admin.shared.call.SendNotificationRequest;
import io.reflection.app.api.admin.shared.call.SendNotificationResponse;
import io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler;
import io.reflection.app.api.admin.shared.call.event.GetUsersEventHandler;
import io.reflection.app.api.admin.shared.call.event.SendNotificationEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NotificationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtSuggestBox;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventPriorityType;
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
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SendNotificationPage extends Page implements GetUsersEventHandler, GetEventsEventHandler, SendNotificationEventHandler {

	private static SendNotificationPageUiBinder uiBinder = GWT.create(SendNotificationPageUiBinder.class);

	interface SendNotificationPageUiBinder extends UiBinder<Widget, SendNotificationPage> {}

	@UiField(provided = true) SuggestBox eventSuggestBox = new SuggestBox(EventController.get().oracle());
	String eventError;

	@UiField(provided = true) SuggestBox userSuggestBox = new SuggestBox(UserController.get().oracle());
	String userError;

	@UiField(provided = true) Selector fromListBox = new Selector(false);
	String fromError;

	@UiField TextField subjectTextBox;
	String subjectError;

	@UiField MarkdownEditor bodyEditor;
	String bodyError;

	@UiField(provided = true) Selector priorityListBox = new Selector(false);
	// private String priorityError = null;

	@UiField Button buttonSend;

	private User user;
	private Event event;

	private Map<String, User> userLookup = new HashMap<String, User>();
	private Map<String, Event> eventLookup = new HashMap<String, Event>();

	public SendNotificationPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtSuggestBox.INSTANCE.styles().ensureInjected();

		addPriorities(priorityListBox);
	}

	/**
	 * @param listBox
	 */
	private void addPriorities(Selector listBox) {
		for (EventPriorityType p : EventPriorityType.values()) {
			listBox.addItem(p.toString(), p.toString());
		}
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
		register(DefaultEventBus.get().addHandlerToSource(SendNotificationEventHandler.TYPE, NotificationController.get(), this));

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

			priorityListBox.setSelectedIndex(FormHelper.getItemIndex(priorityListBox, event.priority.toString()));
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
			clearErrors();
			// preloader.show();
			NotificationController.get().sendNotification(event == null ? null : event.id, user.id, fromListBox.getSelectedValue(), subjectTextBox.getText(),
					bodyEditor.getText(), EventPriorityType.fromString(priorityListBox.getSelectedValue()));
		} else {
			// if (eventError != null) {
			// FormHelper.showNote(true, eventGroup, eventNote, eventError);
			// } else {
			// FormHelper.hideNote(eventGroup, eventNote);
			// }

			// if (userError != null) {
			// FormHelper.showNote(true, userGroup, userNote, userError);
			// } else {
			// FormHelper.hideNote(userGroup, userNote);
			// }

			// if (fromError != null) {
			// FormHelper.showNote(true, fromGroup, fromNote, fromError);
			// } else {
			// FormHelper.hideNote(fromGroup, fromNote);
			// }

			if (subjectError != null) {
				subjectTextBox.showNote(subjectError, true);
			} else {
				subjectTextBox.hideNote();
			}

			// if (bodyError != null) {
			// FormHelper.showNote(true, bodyGroup, bodyNote, bodyError);
			// } else {
			// FormHelper.hideNote(bodyGroup, bodyNote);
			// }

			// if (priorityError != null) {
			// FormHelper.showNote(true, priorityGroup, priorityNote, priorityError);
			// } else {
			// FormHelper.hideNote(priorityGroup, priorityNote);
			// }
		}
	}

	private boolean validate() {
		boolean validated = true;

		String eventText = eventSuggestBox.getText();
		String userText = userSuggestBox.getText();
		String fromText = fromListBox.getSelectedValue();
		String subjectText = subjectTextBox.getText();
		String bodyText = bodyEditor.getText();
		EventPriorityType priority = EventPriorityType.fromString(priorityListBox.getSelectedValue());

		if (event == null && !eventText.isEmpty()) {
			eventError = "Unrecognised event: try searching and selecting from the list";
			validated = false;
		} else {
			eventError = null;
			validated = validated && true;
		}

		if (user == null && !userText.isEmpty()) {
			userError = "Cannot be empty";
			validated = false;
		} else if (user == null) {
			userError = "Unrecognised user: try searching and selecting from the list";
			validated = false;
		} else {
			userError = null;
			validated = validated && true;
		}

		if (fromText.isEmpty()) {
			// this should never happen
			fromError = "Select a user from the list";
			validated = false;
		} else {
			fromError = null;
			validated = validated && true;
		}

		if (subjectText.isEmpty()) {
			subjectError = "Cannot be empty";
			validated = false;
		} else {
			subjectError = null;
			validated = validated && true;
		}

		if (bodyText.isEmpty()) {
			bodyError = "Cannot be empty";
			validated = false;
		} else {
			bodyError = null;
			validated = validated && true;
		}

		if (priority == null) {
			StringBuffer values = new StringBuffer();
			for (EventPriorityType priorityType : EventPriorityType.values()) {
				values.append(", ");
				values.append(priorityType.toString());
			}

			// priorityError = "Priority should be one of: " + values;
		} else {
			// priorityError = null;
			validated = validated && true;
		}

		return validated;
	}

	private void clearErrors() {
		// FormHelper.hideNote(userGroup, userNote);
		// FormHelper.hideNote(eventGroup, eventNote);
		// FormHelper.hideNote(fromGroup, fromNote);
		subjectTextBox.hideNote();
		// FormHelper.hideNote(bodyGroup, bodyNote);
		// FormHelper.hideNote(priorityGroup, priorityNote);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.SendNotificationEventHandler#sendNotificationSuccess(io.reflection.app.api.admin.shared.call.
	 * SendNotificationRequest, io.reflection.app.api.admin.shared.call.SendNotificationResponse)
	 */
	@Override
	public void sendNotificationSuccess(SendNotificationRequest input, SendNotificationResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			clearForm();
		} else if (output.status == StatusType.StatusTypeFailure) {

		}

		// preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.SendNotificationEventHandler#sendNotificationFailure(io.reflection.app.api.admin.shared.call.
	 * SendNotificationRequest, java.lang.Throwable)
	 */
	@Override
	public void sendNotificationFailure(SendNotificationRequest input, Throwable caught) {
		// preloader.hide();
	}

	private void clearForm() {
		clearErrors();

		event = null;
		user = null;

		eventSuggestBox.setText("");
		userSuggestBox.setText("");
		fromListBox.setSelectedIndex(0);
		subjectTextBox.setText("");
		bodyEditor.setText("");
		priorityListBox.setSelectedIndex(FormHelper.getItemIndex(priorityListBox, EventPriorityType.EventPriorityTypeNormal.toString()));
	}

}
