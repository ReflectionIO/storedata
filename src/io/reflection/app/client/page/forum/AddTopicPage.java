//
//  AddTopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicResponse;
import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler;
import io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumSummarySidePanel;
import io.reflection.app.client.part.MyAnchor;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.datatypes.shared.Forum;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class AddTopicPage extends Page implements CreateTopicEventHandler, GetForumsEventHandler, NavigationEventHandler {

	private static final int FORUM_ID_PARAMETER_INDEX = 0;
	@UiField TextBox title;
	@UiField TextBox tags;

	@UiField Selector forums;

	@UiField MarkdownEditor contentText;

	@UiField ForumSummarySidePanel forumSummarySidePanel;

	@UiField Button submit;

	@UiField DivElement prepareRow;
	@UiField DivElement doneRow;

	@UiField MyAnchor redirectAnchor;

	private static AddTopicPageUiBinder uiBinder = GWT.create(AddTopicPageUiBinder.class);
	private HandlerRegistration redirectToTopicHandler;
	private Long forumId;

	interface AddTopicPageUiBinder extends UiBinder<Widget, AddTopicPage> {}

	public AddTopicPage() {
		initWidget(uiBinder.createAndBindUi(this));

		title.getElement().setAttribute("placeholder", "Title");
		tags.getElement().setAttribute("placeholder", "Comma separated tags");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(CreateTopicEventHandler.TYPE, TopicController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetForumsEventHandler.TYPE, ForumController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));

		resetForm();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		resetForm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Reflection.io: Forum";
	}

	@UiHandler("submit")
	void onSubmit(ClickEvent e) {
		if (validate()) {
			Long forumId = null;

			submit.setText("Posting new Topic...");
			submit.setEnabled(false);
			contentText.setLoading(true);

			if (forums.getItemCount() > 0) {
				String forumIdString = forums.getSelectedValue();
				forumId = Long.valueOf(forumIdString);
			}

			if (forumId != null) {
				TopicController.get().createTopic(forumId, title.getText(), false, contentText.getText(), tags.getText());
			}
		}
	}

	private boolean validate() {
		return true;
	}

	private void resetForm() {

		title.setText("");
		contentText.setText("");
		tags.setText("");
		submit.setText("Post New Topic");
		submit.setEnabled(true);

		forumSummarySidePanel.redraw();

		forums.clear();
		FilterHelper.addForums(forums);

		prepareRow.getStyle().setDisplay(Display.BLOCK);
		doneRow.getStyle().setDisplay(Display.NONE);

		if (redirectToTopicHandler != null) {
			redirectToTopicHandler.removeHandler();
			redirectToTopicHandler = null;
		}

		contentText.setLoading(false);
		forumId = null;

		// hide errors and remove clear validation strings
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler#createTopicSuccess
	 * (io.reflection.app.api.forum.shared.call.CreateTopicRequest,io.reflection.app.api.forum.shared.call.CreateTopicResponse)
	 */
	@Override
	public void createTopicSuccess(CreateTopicRequest input, final CreateTopicResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			submit.setText("Submit");
			TopicController.get().reset();

			prepareRow.getStyle().setDisplay(Display.NONE);
			doneRow.getStyle().setDisplay(Display.BLOCK);

			redirectToTopicHandler = redirectAnchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					PageType.ForumThreadPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, output.topic.id.toString());
				}
			});

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler#createTopicFailure
	 * (io.reflection.app.api.forum.shared.call.CreateTopicRequest,java.lang.Throwable)
	 */
	@Override
	public void createTopicFailure(CreateTopicRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsSuccess
	 * (io.reflection.app.api.forum.shared.call.GetForumsRequest,io.reflection.app.api.forum.shared.call.GetForumsResponse)
	 */
	@Override
	public void getForumsSuccess(GetForumsRequest input, GetForumsResponse output) {
		forums.clear();

		FilterHelper.addForums(forums);

		setForumComboIndex();
	}

	protected void setForumComboIndex() {
		List<Forum> forumList = ForumController.get().getForums();

		int index = 0;
		for (int i = 0; i < forumList.size(); i++) {
			if (forumList.get(i).id.equals(forumId)) {
				index = i;
			}
		}

		forums.setSelectedIndex(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsFailure
	 * (io.reflection.app.api.forum.shared.call.GetForumsRequest,java.lang.Throwable)
	 */
	@Override
	public void getForumsFailure(GetForumsRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current != null && PageType.ForumTopicPageType.equals(current.getPage())) {
			String forumIdString = null;
			if ((forumIdString = current.getParameter(FORUM_ID_PARAMETER_INDEX)) != null) {
				forumId = Long.valueOf(forumIdString);
				setForumComboIndex();
			}
		}
	}
}
