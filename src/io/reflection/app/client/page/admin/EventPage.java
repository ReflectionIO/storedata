//
//  EventPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import static io.reflection.app.client.helper.FormattingHelper.convertEventPriorityToIcon;
import io.reflection.app.api.admin.shared.call.GetEventsRequest;
import io.reflection.app.api.admin.shared.call.GetEventsResponse;
import io.reflection.app.api.admin.shared.call.UpdateEventRequest;
import io.reflection.app.api.admin.shared.call.UpdateEventResponse;
import io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler;
import io.reflection.app.api.admin.shared.call.event.UpdateEventEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.helpers.NotificationHelper;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EventPage extends Page implements NavigationEventHandler, GetEventsEventHandler, UpdateEventEventHandler {

	public static final int EVENT_ID_PARAMETER = 0;
	public static final int FORMAT_PLAIN_TEXT_INDEX = 0;
	public static final int FORMAT_HTML_INDEX = 1;

	private static EventPageUiBinder uiBinder = GWT.create(EventPageUiBinder.class);

	interface EventPageUiBinder extends UiBinder<Widget, EventPage> {}

	@UiField(provided = true) CellTable<Event> events = new CellTable<Event>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	private Event event;

	TextArea textArea;

	@UiField HTMLPanel editEventPanel;

	@UiField TextField codeTextBox;
	// private String codeError = null;

	@UiField TextField descriptionTextBox;
	// private String descriptionError = null;

	@UiField TextField nameTextBox;
	// private String nameError = null;

	@UiField TextField subjectTextBox;
	// private String subjectError = null;

	@UiField MarkdownEditor longBodyEditor;
	// private String longBodyError = null;

	@UiField TextArea shortBodyTextArea;
	// private String shortBodyError = null;

	@UiField(provided = true) Selector priorityListBox = new Selector(false);

	@UiField Button addForenameBtn;
	@UiField Button addSurnameBtn;
	@UiField Button addUsernameBtn;
	@UiField Button addCompanyBtn;
	@UiField Button addLinkBtn;

	@UiField Button buttonUpdate;

	public EventPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		events.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		EventController.get().addDataDisplay(events);
		simplePager.setDisplay(events);

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

	public void addColumns() {
		TextColumn<Event> codeColumn = new TextColumn<Event>() {

			@Override
			public String getValue(Event object) {
				return object.code.toString();
			}

		};

		events.addColumn(codeColumn, "Code");

		Column<Event, SafeHtml> columnPriority = new Column<Event, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(Event object) {
				return convertEventPriorityToIcon(object.priority);
			}
		};
		events.addColumn(columnPriority);

		TextColumn<Event> nameColumn = new TextColumn<Event>() {

			@Override
			public String getValue(Event object) {
				return object.name;
			}

		};
		events.addColumn(nameColumn, "Name");

		TextColumn<Event> typeColumn = new TextColumn<Event>() {

			@Override
			public String getValue(Event object) {
				return object.type.toString();
			}

		};

		events.addColumn(typeColumn, "Type");

		TextColumn<Event> descriptionColumn = new TextColumn<Event>() {

			@Override
			public String getValue(Event object) {
				return object.description;
			}

		};

		events.addColumn(descriptionColumn, "Description");

		Column<Event, SafeHtml> editColumn = new Column<Event, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Event object) {
				String id = object.id.toString();
				return SafeHtmlUtils.fromTrustedString("<a class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonFunctionSmall() + "\" href=\""
						+ PageType.EventsPageType.asHref(NavigationController.EDIT_ACTION_PARAMETER_VALUE, id).asString() + "\">Edit</a>");
			}
		};
		events.addColumn(editColumn);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		String actionParameter = current.getAction();
		String typeParameter = current.getParameter(EVENT_ID_PARAMETER);

		if (isValidEditStack(actionParameter, typeParameter)) {
			clearErrors();
			event = EventController.get().getEvent(Long.valueOf(typeParameter));
			editEventPanel.setVisible(true);

			codeTextBox.setText(event.code);
			nameTextBox.setText(event.name);
			descriptionTextBox.setText(event.description);
			subjectTextBox.setText(event.subject);
			shortBodyTextArea.setText(event.shortBody);
			longBodyEditor.getTextArea().setText(event.longBody);
			priorityListBox.setSelectedIndex(FormHelper.getItemIndex(priorityListBox, (event.priority == null ? EventPriorityType.EventPriorityTypeNormal
					: event.priority).toString()));

		} else {
			editEventPanel.setVisible(false);
			PageType.EventsPageType.show();
		}
	}

	private boolean isValidEditStack(String actionParameter, String typeParameter) {
		return (actionParameter != null && typeParameter != null && NavigationController.EDIT_ACTION_PARAMETER_VALUE.equals(actionParameter)
				&& typeParameter.matches("[0-9]+") && EventController.get().getEvent(Long.valueOf(typeParameter)) != null) ? true : false;
	}

	@UiHandler("addForenameBtn")
	void onAddForenameClicked(ClickEvent ce) {
		appendCode(NotificationHelper.USER_FORENAME_CODE);
	}

	@UiHandler("addSurnameBtn")
	void onAddSurnameClicked(ClickEvent ce) {
		appendCode(NotificationHelper.USER_SURNAME_CODE);
	}

	@UiHandler("addUsernameBtn")
	void onAddUsernameClicked(ClickEvent ce) {
		appendCode(NotificationHelper.USER_USERNAME_CODE);
	}

	@UiHandler("addCompanyBtn")
	void onAddCompanyClicked(ClickEvent ce) {
		appendCode(NotificationHelper.USER_COMPANY_CODE);
	}

	@UiHandler("addLinkBtn")
	void onAddLinkClicked(ClickEvent ce) {
		appendCode(NotificationHelper.LINK_CODE);
	}

	/**
	 * Append embedded email code at the end of the current TextArea text
	 * 
	 * @param code
	 */
	private void appendCode(String code) {
		if (textArea != null) {
			textArea.setText(textArea.getText() + code);
			textArea.setCursorPos(textArea.getText().length());
		}
	}

	@UiHandler({ "longBodyEditor", "shortBodyTextArea" })
	void onFocus(FocusEvent fe) {
		if (fe.getSource() instanceof TextArea) {
			textArea = (TextArea) fe.getSource();
		} else if (fe.getSource() instanceof MarkdownEditor) {
			textArea = ((MarkdownEditor) fe.getSource()).getTextArea();
		}
	}

	@UiHandler({ "longBodyEditor", "shortBodyTextArea" })
	void onBlur(BlurEvent be) {
		textArea = null;
	}

	@UiHandler("buttonUpdate")
	void onUpdateClicked(ClickEvent ce) {
		if (validate()) {
			clearErrors();
			// preloader.show();

			event.subject = subjectTextBox.getText();
			event.id = Long.valueOf(NavigationController.get().getStack().getParameter(EVENT_ID_PARAMETER));
			event.shortBody = shortBodyTextArea.getText();
			event.longBody = longBodyEditor.getText();
			event.name = nameTextBox.getText();
			event.description = descriptionTextBox.getText();
			event.code = codeTextBox.getText();
			event.priority = EventPriorityType.fromString(priorityListBox.getSelectedValue());

			EventController.get().updateEvent(event);
		} else {
			// if (codeError != null) {
			// FormHelper.showNote(true, codeGroup, codeNote, codeError);
			// } else {
			// FormHelper.hideNote(codeGroup, codeNote);
			// }
			//
			// if (nameError != null) {
			// FormHelper.showNote(true, nameGroup, nameNote, nameError);
			// } else {
			// FormHelper.hideNote(nameGroup, nameNote);
			// }
			//
			// if (descriptionError != null) {
			// FormHelper.showNote(true, descriptionGroup, descriptionNote, descriptionError);
			// } else {
			// FormHelper.hideNote(descriptionGroup, descriptionNote);
			// }
			//
			// if (subjectError != null) {
			// FormHelper.showNote(true, subjectGroup, subjectNote, subjectError);
			// } else {
			// FormHelper.hideNote(subjectGroup, subjectNote);
			// }
			//
			// if (shortBodyError != null) {
			// FormHelper.showNote(true, shortBodyGroup, shortBodyNote, shortBodyError);
			// } else {
			// FormHelper.hideNote(shortBodyGroup, shortBodyNote);
			// }
			//
			// if (longBodyError != null) {
			// FormHelper.showNote(true, longBodyGroup, longBodyNote, longBodyError);
			// } else {
			// FormHelper.hideNote(longBodyGroup, longBodyNote);
			// }
			//
			// if (priorityError != null) {
			// FormHelper.showNote(true, priorityGroup, priorityNote, priorityError);
			// } else {
			// FormHelper.hideNote(priorityGroup, priorityNote);
			// }
		}
	}

	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		// String code = codeTextBox.getText();
		// String name = nameTextBox.getText();
		// String description = descriptionTextBox.getText();
		// String subject = subjectTextBox.getText();
		// String shortBody = shortBodyTextArea.getText();
		// String longBody = longBodyEditor.getText();
		EventPriorityType priority = EventPriorityType.fromString(priorityListBox.getSelectedValue());

		// if (code == null || code.length() == 0) {
		// codeError = "Cannot be empty";
		// validated = false;
		// } else {
		// codeError = null;
		// validated = validated && true;
		// }
		//
		// if (name == null || name.length() == 0) {
		// nameError = "Cannot be empty";
		// validated = false;
		// } else {
		// nameError = null;
		// validated = validated && true;
		// }
		//
		// if (description == null || description.length() == 0) {
		// descriptionError = "Cannot be empty";
		// validated = false;
		// } else {
		// descriptionError = null;
		// validated = validated && true;
		// }
		//
		// if (subject == null || subject.length() == 0) {
		// subjectError = "Cannot be empty";
		// validated = false;
		// } else {
		// subjectError = null;
		// validated = validated && true;
		// }

		// if (shortBody == null || shortBody.length() == 0) {
		// shortBodyError = "Cannot be empty";
		// validated = false;
		// } else {
		// shortBodyError = null;
		// validated = validated && true;
		// }
		//
		// if (longBody == null || longBody.length() == 0) {
		// longBodyError = "Cannot be empty";
		// validated = false;
		// } else {
		// longBodyError = null;
		// validated = validated && true;
		// }

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
		// FormHelper.hideNote(codeGroup, codeNote);
		// FormHelper.hideNote(nameGroup, nameNote);
		// FormHelper.hideNote(descriptionGroup, descriptionNote);
		// FormHelper.hideNote(subjectGroup, subjectNote);
		// FormHelper.hideNote(shortBodyGroup, shortBodyNote);
		// FormHelper.hideNote(longBodyGroup, longBodyNote);
		// FormHelper.hideNote(priorityGroup, priorityNote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetEventsEventHandler.TYPE, EventController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UpdateEventEventHandler.TYPE, EventController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler#getEventsSuccess(io.reflection.app.api.admin.shared.call. GetEventsRequest,
	 * io.reflection.app.api.admin.shared.call.GetEventsResponse)
	 */
	@Override
	public void getEventsSuccess(GetEventsRequest input, GetEventsResponse output) {
		simplePager.setVisible(output.status == StatusType.StatusTypeSuccess);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler#getEventsFailure(io.reflection.app.api.admin.shared.call. GetEventsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getEventsFailure(GetEventsRequest input, Throwable caught) {
		simplePager.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.UpdateEventEventHandler#updateEventSuccess(io.reflection.app.api.admin.shared.call.
	 * UpdateEventRequest, io.reflection.app.api.admin.shared.call.UpdateEventResponse)
	 */
	@Override
	public void updateEventSuccess(UpdateEventRequest input, UpdateEventResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			events.redraw();

			PageType.EventsPageType.show();
		}

		// preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.UpdateEventEventHandler#updateEventFailure(io.reflection.app.api.admin.shared.call.
	 * UpdateEventRequest, java.lang.Throwable)
	 */
	@Override
	public void updateEventFailure(UpdateEventRequest input, Throwable caught) {
		// preloader.hide();
	}

}
