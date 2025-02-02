//
//  TopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;
import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler;
import io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler;
import io.reflection.app.client.DefaultEventBus;
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
import io.reflection.app.client.page.forum.part.BootstrapGwtTopicCellList;
import io.reflection.app.client.page.forum.part.ForumMessageCell;
import io.reflection.app.client.page.forum.part.ForumSummarySidePanel;
import io.reflection.app.client.page.forum.part.LockButton;
import io.reflection.app.client.page.forum.part.StickyButton;
import io.reflection.app.client.part.NumberedPager;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class TopicPage extends Page implements NavigationEventHandler, GetTopicEventHandler, AddReplyEventHandler {

	private static TopicPageUiBinder uiBinder = GWT.create(TopicPageUiBinder.class);

	protected final static int TOPIC_ID_PARAMETER_INDEX = 0;

	private Long topicId;

	interface TopicPageUiBinder extends UiBinder<Widget, TopicPage> {}

	@UiField HeadingElement topicTitle;
	@UiField HeadingElement forumTitle;

	@UiField UListElement notes;

	private ForumMessageCell cellPrototype = new ForumMessageCell();
	@UiField(provided = true) CellList<ForumMessage> messagesCellList = new CellList<ForumMessage>(cellPrototype, BootstrapGwtTopicCellList.INSTANCE);

	@UiField Button replyLink;
	@UiField Button post;

	@UiField HTMLPanel replyGroup;
	@UiField HTMLPanel replyNote;

	@UiField FormPanel replyForm;

	@UiField MarkdownEditor replyText;
	@UiField NumberedPager pager;

	@UiField HTMLPanel adminButtons;

	@UiField ForumSummarySidePanel forumSummarySidePanel;

	private Map<Long, String> editorTextMap = new HashMap<Long, String>();

	private ForumMessageProvider dataProvider;

	private Topic topic;

	private Integer startPagePost;

	private HandlerRegistration onLoadedHandler;

	private boolean isLocked;

	public TopicPage() {
		initWidget(uiBinder.createAndBindUi(this));

		messagesCellList.setPageSize(ServiceConstants.SHORT_STEP_VALUE);
		Image loadingIndicator = new Image(Images.INSTANCE.preloader());

		// not sure why this is needed here to centre, but can't see it used
		// elsewhere in code.
		loadingIndicator.getElement().getStyle().setDisplay(Display.BLOCK);
		loadingIndicator.getElement().getStyle().setProperty("marginLeft", "auto");
		loadingIndicator.getElement().getStyle().setProperty("marginRight", "auto");

		messagesCellList.setLoadingIndicator(loadingIndicator);
		messagesCellList.setEmptyListWidget(new HTMLPanel("No messages found!"));

		pager.setPageSize(ServiceConstants.SHORT_STEP_VALUE);

		cellPrototype.setMarkdownTextEditor(replyText);

	}

	private void addAdminButtons() {
		adminButtons.add(new StickyButton(topicId));
		adminButtons.add(new LockButton("Lock", topicId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetTopicEventHandler.TYPE, TopicController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(AddReplyEventHandler.TYPE, ReplyController.get(), this));

		registerOnMessagesLoadedHandler();

		if (dataProvider != null) {
			dataProvider.registerListeners();
		}

		reset();

	}

	protected void registerOnMessagesLoadedHandler() {
		if (onLoadedHandler == null) {
			onLoadedHandler = messagesCellList.addHandler(new LoadingStateChangeEvent.Handler() {

				@Override
				public void onLoadingStateChanged(LoadingStateChangeEvent event) {
					if (event.getLoadingState() == LoadingState.LOADED) {
						replyText.setVisible(true);
						pager.setVisible(true);
						replyForm.setVisible(!isLocked);
					}
				}
			}, LoadingStateChangeEvent.TYPE);
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

		onLoadedHandler.removeHandler();
		onLoadedHandler = null;

		reset();
	}

	/**
     * 
     */
	private void reset() {
		topicTitle.setInnerHTML("");
		post.setText("Post Response");

		if (dataProvider != null && dataProvider.getDataDisplays().size() > 0) {
			dataProvider.removeDataDisplay(messagesCellList);
		}

		messagesCellList.setVisibleRangeAndClearData(messagesCellList.getVisibleRange(), true);

		pager.setVisible(false);

		notes.removeAllChildren();

		editorTextMap.put(topicId, replyText.getText());
		replyText.reset();
		replyText.setVisible(false);
		replyForm.setVisible(false);

		// we shouldn't have to reset admin buttons because admin privledges should be the same.

		forumSummarySidePanel.reset();

		startPagePost = null;

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

	@UiHandler("replyLink")
	void focusReplyClicked(ClickEvent event) {
		post.getElement().scrollIntoView();
	}

	@UiHandler("post")
	void postReplyClicked(ClickEvent event) {
		if (validate()) {
			ReplyController.get().addReply(topicId, replyText.getText());
			post.setText("Posting...");
			post.setEnabled(false);
			replyText.setLoading(true);
		}
	}

	private boolean validate() {
		boolean isValid = false;
		// TODO should probably take into account quoted text
		if (replyText.getText().trim().length() > 0) {
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
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged (io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (current != null && PageType.ForumThreadPageType.equals(current.getPage())) {

			forumSummarySidePanel.redraw();
			replyText.setVisible(false);
			replyForm.setVisible(false);

			if (current.getAction() != null && NavigationController.VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {
				String topicIdString;
				if ((topicIdString = current.getParameter(TOPIC_ID_PARAMETER_INDEX)) != null) {
					topicId = Long.valueOf(topicIdString);
					topic = TopicController.get().getTopic(topicId);

					replyText.setText(editorTextMap.get(topicId));

					String param1 = current.getParameter(1);
					if (param1 != null && param1.contains("post")) {
						String param2 = current.getParameter(2);
						if (param2 != null) {
							final int post = Integer.parseInt(param2);
							startPagePost = post;

						}
					}

					updateTopic(topic);
					if (startPagePost != null) {
						focusPagerOnPost(startPagePost);
					}
				}

			}
		}
	}

	protected void focusPagerOnPost(final int post) {

		// This used to be the previous way we set the range on the display, via the pager like so
		// pager.setPageStart(firstOnPage);
		// However, this seems to be influenced by the previous replies the display/pager/dataprovider was bound too.
		// Exactly why, I'm not sure, so to be safe set the visible range on the display itself. (Each change like this seems to have knock on effects that
		// are difficult to predict without a complete understanding of AsyncDataProvider/CellList/Table).

		startPagePost = post - (post % ServiceConstants.SHORT_STEP_VALUE);

		messagesCellList.setVisibleRange(startPagePost, ServiceConstants.SHORT_STEP_VALUE);
	}

	/**
	 * is called when there are new changes to be applied to the topic this actually updates these changes in the ui
	 * 
	 * @param topic
	 */
	private void updateTopic(Topic topic) {
		if (topic != null) {

			String properties = "";

			isLocked = topic.locked != null && topic.locked.booleanValue();
			if (isLocked) {
				properties += "<i class=\"glyphicon glyphicon-lock\"></i> ";
			}

			if (topic.heat != null && topic.heat > 10) {
				properties += "<i class=\"glyphicon glyphicon-fire\"></i> ";
			}

			if (topic.sticky != null && topic.sticky.booleanValue()) {
				properties += "<i class=\"glyphicon glyphicon-pushpin\"></i> ";
			}

			topicTitle.setInnerHTML(properties + topic.title);

			updateNotes(topic);

			if (dataProvider != null) {
				dataProvider.unregisterListeners();

				if (dataProvider.getDataDisplays().size() > 0) {
					dataProvider.removeDataDisplay(messagesCellList);
				}
			}

			dataProvider = new ForumMessageProvider(topic);
			dataProvider.registerListeners();

			pager.setDisplay(messagesCellList); // bind the pager and the
			// display together.

			if (startPagePost == null) {
				startPagePost = 0;
			}
			// this needs to be called BEFORE the dataprovider is connected.
			// Connecting the data provider will trigger a rangeChange and we need the right page to be set for that.
			focusPagerOnPost(startPagePost.intValue());

			// at this point we will call an onRangeChanged, but we don't have a start post as per yet.
			dataProvider.addDataDisplay(messagesCellList);

			// necessary to pull data from the data provider?
			messagesCellList.redraw();

			// just note that the display is primary about what range it has
			// set.
			// The SimplePager is just a bound view on that data.
			// Manual jumping of ranges should thus be set on the display, not
			// on the pager.
			// in any event that would be better in case a different pager was
			// bound/detached or
			// something else happened during the lifecycle.

			if (isLocked) {
				post.getElement().getParentElement().getStyle().setDisplay(Display.NONE);
			} else {
				post.getElement().getParentElement().getStyle().clearDisplay();
			}
		}
	}

	interface TopicNotesTemplate extends SafeHtmlTemplates {
		TopicNotesTemplate INSTANCE = GWT.create(TopicNotesTemplate.class);

		@SafeHtmlTemplates.Template("<strong>Started by {0}</strong> <span>{1}</span>\n")
		SafeHtml descriptionStartedBy(String startedBy, String topicStartedTimeAgo);

		@SafeHtmlTemplates.Template("<strong>Replies</strong> <span>{0}</span>\n")
		SafeHtml descriptionReplies(String numberOfReplies);

		@SafeHtmlTemplates.Template("<strong>Last post by {0}</strong> <span>{1}</span>")
		SafeHtml descriptionLastPoster(String lastPoster, String lastPostTimeAgo);
	}

	protected void updateNotes(Topic topic) {

		notes.removeAllChildren();

		LIElement author = Document.get().createLIElement();
		author.setInnerSafeHtml(TopicNotesTemplate.INSTANCE.descriptionStartedBy(FormattingHelper.getUserName(topic.author),
				FormattingHelper.getTimeSince(topic.created)));
		notes.appendChild(author);

		if (topic.numberOfReplies != null) {
			LIElement replies = Document.get().createLIElement();
			replies.setInnerSafeHtml(TopicNotesTemplate.INSTANCE.descriptionReplies(topic.numberOfReplies.toString()));
			notes.appendChild(replies);
		}

		if (topic.lastReplier != null) {
			LIElement lastReplier = Document.get().createLIElement();
			lastReplier.setInnerSafeHtml(TopicNotesTemplate.INSTANCE.descriptionLastPoster(FormattingHelper.getUserName(topic.lastReplier),
					FormattingHelper.getTimeSince(topic.lastReplied)));
			notes.appendChild(lastReplier);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler# getTopicSuccess(io.reflection.app.api.forum.shared.call.GetTopicRequest,
	 * io.reflection.app.api.forum.shared.call.GetTopicResponse)
	 */
	@Override
	public void getTopicSuccess(GetTopicRequest input, GetTopicResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {

			if (TopicPage.this.topic == null) {
				if (SessionController.get().isAdmin()) {
					addAdminButtons();
				} else {
					adminButtons.removeFromParent();
				}
			}

			TopicPage.this.topic = output.topic;

			updateTopic(output.topic);
			forumSummarySidePanel.selectItem(output.topic.forum);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler# getTopicFailure(io.reflection.app.api.forum.shared.call.GetTopicRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getTopicFailure(GetTopicRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler# addReplySuccess(io.reflection.app.api.forum.shared.call.AddReplyRequest,
	 * io.reflection.app.api.forum.shared.call.AddReplyResponse)
	 */
	@Override
	public void addReplySuccess(AddReplyRequest input, AddReplyResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			post.setEnabled(true);
			post.setText("Post Response");
			replyText.setLoading(false);
			replyText.setText("");
			Topic topic2 = TopicController.get().getTopic(topicId);
			updateNotes(topic2);

			// note: It is api non-deterministic, (or specifically the order of handler registrations and treatment by the eventbus)
			// whether this addReply handler will get called first or the one in ForumMessageProvider.
			// that may be important depending on what you want to update in the handlers.

			messagesCellList.redraw();

			// numberOfReplies was already incremented by ReplyController, and since ForumMessages start at 0 it is the right number.
			focusPagerOnPost(topic2.numberOfReplies);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler# addReplyFailure(io.reflection.app.api.forum.shared.call.AddReplyRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void addReplyFailure(AddReplyRequest input, Throwable caught) {}

}
