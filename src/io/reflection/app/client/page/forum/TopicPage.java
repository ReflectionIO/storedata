//
//  TopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;
import io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.dataprovider.ForumMessageProvider;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumMessageCell;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class TopicPage extends Page implements NavigationEventHandler, GetTopicEventHandler {

	private static TopicPageUiBinder uiBinder = GWT.create(TopicPageUiBinder.class);

	protected final static String VIEW_ACTION_PARAMETER_VALUE = "view";
	protected final static int TOPIC_ID_PARAMETER_INDEX = 0;

	private Long topicId;

	interface TopicPageUiBinder extends UiBinder<Widget, TopicPage> {}

	@UiField HeadingElement title;
	@UiField UListElement notes;

	@UiField(provided = true) CellList<ForumMessage> messages = new CellList<ForumMessage>(new ForumMessageCell(), BootstrapGwtCellList.INSTANCE);

	@UiField Button post;

	@UiField HTMLPanel replyGroup;
	@UiField RichTextArea replyText;
	@UiField HTMLPanel replyNote;

	public TopicPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(GetTopicEventHandler.TYPE, TopicController.get(), this));
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

	@UiHandler("post")
	void postReplyClicked(ClickEvent event) {
		if (validate()) {
			ReplyController.get().addReply(topicId, replyText.getHTML());
		} else {

		}
	}

	private boolean validate() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (current != null && PageType.ForumTopicPageType.equals(current.getPage())) {
			if (current.getAction() != null && VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {

				String topicIdString;
				if ((topicIdString = current.getParameter(TOPIC_ID_PARAMETER_INDEX)) != null) {
					topicId = Long.valueOf(topicIdString);

					Topic topic = TopicController.get().getTopic(topicId);

					updateTopic(topic);
				}
			}
		}
	}

	private void updateTopic(Topic topic) {
		if (topic != null) {
			title.setInnerHTML(topic.title);
			notes.removeAllChildren();

			LIElement author = Document.get().createLIElement();
			author.setInnerHTML("Started " + FormattingHelper.getTimeSince(topic.created) + " by " + FormattingHelper.getUserName(topic.author));

			notes.appendChild(author);

			if (topic.numberOfReplies != null) {
				LIElement replies = Document.get().createLIElement();
				replies.setInnerHTML(topic.numberOfReplies.toString() + " replies");

				notes.appendChild(replies);
			}

			if (topic.lastReplier != null) {
				LIElement lastReplier = Document.get().createLIElement();
				lastReplier.setInnerHTML("Latest reply from " + FormattingHelper.getUserName(topic.lastReplier));

				notes.appendChild(lastReplier);
			}

			ForumMessageProvider dataProvider = new ForumMessageProvider(topic);
			dataProvider.addDataDisplay(messages);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler#getTopicSuccess(io.reflection.app.api.forum.shared.call.GetTopicRequest,
	 * io.reflection.app.api.forum.shared.call.GetTopicResponse)
	 */
	@Override
	public void getTopicSuccess(GetTopicRequest input, GetTopicResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			updateTopic(output.topic);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler#getTopicFailure(io.reflection.app.api.forum.shared.call.GetTopicRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getTopicFailure(GetTopicRequest input, Throwable caught) {

	}

}
