//
//  TopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.client.ForumService;
import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;
import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler;
import io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.dataprovider.ForumMessageProvider;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumMessageCell;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.client.part.ReflectionProgressBar;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.client.part.text.RichTextToolbar;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class TopicPage extends Page implements NavigationEventHandler, GetTopicEventHandler, AddReplyEventHandler {

	private static TopicPageUiBinder uiBinder = GWT.create(TopicPageUiBinder.class);

	protected final static String VIEW_ACTION_PARAMETER_VALUE = "view";
	protected final static int TOPIC_ID_PARAMETER_INDEX = 0;

	private Long topicId;

	interface TopicPageUiBinder extends UiBinder<Widget, TopicPage> {}

	@UiField HeadingElement title;
	@UiField UListElement notes;

	private ForumMessageCell cellPrototype = new ForumMessageCell();
	@UiField(provided = true) CellList<ForumMessage> messages = new CellList<ForumMessage>(cellPrototype, BootstrapGwtCellList.INSTANCE);

	@UiField Button reply;
	@UiField Button post;

	@UiField FormPanel replyForm;

	@UiField HTMLPanel replyGroup;

	@UiField HTMLPanel replyNote;

	@UiField RichTextArea replyText;
	@UiField RichTextToolbar replyToolbar;

	@UiField SimplePager pager;
	
	@UiField HTMLPanel adminButtons ;

	private ForumMessageProvider dataProvider;

	private Button makeStickyButton;

	public TopicPage() {
		initWidget(uiBinder.createAndBindUi(this));

		ReflectionProgressBar progress = new ReflectionProgressBar();
		progress.getElement().getStyle().setTextAlign(TextAlign.CENTER);

		messages.setPageSize(ServiceConstants.SHORT_STEP_VALUE);
		messages.setLoadingIndicator(progress);
		messages.setEmptyListWidget(new HTMLPanel("No messages found!"));

		pager.setPageSize(ServiceConstants.SHORT_STEP_VALUE);

		replyToolbar.setRichText(replyText);
		cellPrototype.setRichText(replyText);
		
		if (SessionController.get().isLoggedInUserAdmin())
			addAdminButtons();
		else
			adminButtons.removeFromParent();

//		  Topic topic = TopicController.get().getTopic(topicId);
//		  dataProvider = new ForumMessageProvider(topic);
//		  dataProvider.registerListeners();
//		  
//		  dataProvider.addDataDisplay(messages);
		 
	}
	
	class StickyButton extends Button implements ClickHandler {
		public StickyButton(String name) {
			super(name);
			this.addClickHandler(this);
		}

		
		@Override
		public void onClick(ClickEvent event) {
			TopicController.get().setSticky(topicId, true);
		}
		
	}
	
	class LockButton extends Button implements ClickHandler {
		public LockButton(String name) {
			super(name);
			this.addClickHandler(this);
		}

		
		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
	private void addAdminButtons() {	
		adminButtons.add(new StickyButton("Make Sticky"));
		adminButtons.add(new LockButton("Lock"));

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
		register(EventController.get().addHandlerToSource(AddReplyEventHandler.TYPE, ReplyController.get(), this));

		if (dataProvider != null) {
			dataProvider.registerListeners();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		if (dataProvider != null) {
			dataProvider.unregisterListeners();
		}
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

	@UiHandler("reply")
	void focusReplyClicked(ClickEvent event) {
		replyText.setFocus(true);
		Document.get().setScrollTop(replyText.getAbsoluteTop());
	}

	@UiHandler("post")
	void postReplyClicked(ClickEvent event) {
		if (validate()) {
			ReplyController.get().addReply(topicId, replyText.getHTML());
		}
	}

	private boolean validate() {
		boolean isValid = false;
		// TODO should probably take into account quoted text
		if (replyText.getHTML().trim().length() > 0) {
			FormHelper.hideNote(replyGroup, replyNote);
			isValid = true;
		} else {
			FormHelper.showNote(true, replyGroup, replyNote, "No reply text");
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (current != null && PageType.ForumThreadPageType.equals(current.getPage())) {
			if (current.getAction() != null && VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {
				String topicIdString;
				if ((topicIdString = current.getParameter(TOPIC_ID_PARAMETER_INDEX)) != null) {
					topicId = Long.valueOf(topicIdString);
					Topic topic = TopicController.get().getTopic(topicId);
					updateTopic(topic);
					// replyText.setHTML("<div class=\"quoting\">" + topic.author.forename + " scribbled " + topic.content.toString() + "</div>");
				}
			}
		}
	}

	/**
	 * is called when there are new changes to be applied to the topic
	 * this actually updates these changes in the ui
	 * @param topic
	 */
	private void updateTopic(Topic topic) {
		if (topic != null) {

			String properties = "";

			boolean isLocked = topic.locked != null && topic.locked.booleanValue();
			if (isLocked) {
				properties += "<i class=\"glyphicon glyphicon-lock\"></i> ";
			}

			if (topic.heat != null && topic.heat > 10) {
				properties += "<i class=\"glyphicon glyphicon-fire\"></i> ";
			}

			if (topic.sticky != null && topic.sticky.booleanValue()) {
				properties += "<i class=\"glyphicon glyphicon-pushpin\"></i> ";
			}

			title.setInnerHTML(properties + topic.title);
			
			
			updateNotes(topic);

			if (dataProvider != null) {
				dataProvider.unregisterListeners();
			}

			dataProvider = new ForumMessageProvider(topic);
			dataProvider.registerListeners();

			dataProvider.addDataDisplay(messages);
			pager.setDisplay(messages);

			replyForm.setVisible(!isLocked);

			if (isLocked) {
				reply.getElement().getParentElement().getStyle().setDisplay(Display.NONE);
			} else {
				reply.getElement().getParentElement().getStyle().clearDisplay();
			}
		}
	}

	protected void updateNotes(Topic topic) {
		notes.removeAllChildren();

		LIElement author = Document.get().createLIElement();
		author.setInnerHTML("Started " + FormattingHelper.getTimeSince(topic.created) + " by " + FormattingHelper.getUserLongName(topic.author));

		notes.appendChild(author);

		if (topic.numberOfReplies != null) {
			LIElement replies = Document.get().createLIElement();
			replies.setInnerHTML(topic.numberOfReplies.toString() + " replies");

			notes.appendChild(replies);
		}

		if (topic.lastReplier != null) {
			LIElement lastReplier = Document.get().createLIElement();
			lastReplier.setInnerHTML("Latest reply from " + FormattingHelper.getUserLongName(topic.lastReplier));

			notes.appendChild(lastReplier);
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
	public void getTopicFailure(GetTopicRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler#addReplySuccess(io.reflection.app.api.forum.shared.call.AddReplyRequest,
	 * io.reflection.app.api.forum.shared.call.AddReplyResponse)
	 */
	@Override
	public void addReplySuccess(AddReplyRequest input, AddReplyResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			replyText.setText("");
			updateNotes(TopicController.get().getTopic(topicId));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler#addReplyFailure(io.reflection.app.api.forum.shared.call.AddReplyRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void addReplyFailure(AddReplyRequest input, Throwable caught) {}

}
