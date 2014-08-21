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
import io.reflection.app.client.controller.EventController;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
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

    protected final static String VIEW_ACTION_PARAMETER_VALUE = "view";
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

    private ForumMessageProvider dataProvider;

    private Topic topic;

    private Integer startPage;

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

    @UiHandler("replyLink")
    void focusReplyClicked(ClickEvent event) {
        post.getElement().scrollIntoView();
    }

    @UiHandler("post")
    void postReplyClicked(ClickEvent event) {
        if (validate()) {
            ReplyController.get().addReply(topicId, replyText.getText());
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

            if (current.getAction() != null && VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {
                String topicIdString;
                if ((topicIdString = current.getParameter(TOPIC_ID_PARAMETER_INDEX)) != null) {
                    topicId = Long.valueOf(topicIdString);
                    topic = TopicController.get().getTopic(topicId);

                    String param1 = current.getParameter(1);
                    if (param1 != null && param1.contains("post")) {
                        String param2 = current.getParameter(2);
                        if (param2 != null) {
                            final int post = Integer.parseInt(param2);
                            startPage = post ;
                            
                        }
                    }
                    updateTopic(topic);
                    if (startPage != null)
                    {
                        focusPagerOnPost(startPage);
                    }
                }
            }
        }
        messagesCellList.redraw();
    }

    protected void focusPagerOnPost(final int post) {
        int firstOnPage = post - (post % ServiceConstants.SHORT_STEP_VALUE);
        pager.setPageStart(firstOnPage);
    }

    /**
     * is called when there are new changes to be applied to the topic this actually updates these changes in the ui
     * 
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

            topicTitle.setInnerHTML(properties + topic.title);
            forumTitle.setInnerText(topic.forum.title);

            updateNotes(topic);

            if (dataProvider != null) {
                dataProvider.unregisterListeners();
            }

            dataProvider = new ForumMessageProvider(topic);
            dataProvider.registerListeners();
            
            pager.setDisplay(messagesCellList); // bind the pager and the
            // display together.

            dataProvider.addDataDisplay(messagesCellList);
            if (startPage != null)
            {
                focusPagerOnPost(startPage.intValue());
            }

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

            replyForm.setVisible(!isLocked);

            if (isLocked) {
                post.getElement().getParentElement().getStyle().setDisplay(Display.NONE);
            } else {
                post.getElement().getParentElement().getStyle().clearDisplay();
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
     * @see io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler# getTopicSuccess(io.reflection.app.api.forum.shared.call.GetTopicRequest,
     * io.reflection.app.api.forum.shared.call.GetTopicResponse)
     */
    @Override
    public void getTopicSuccess(GetTopicRequest input, GetTopicResponse output) {
        if (output.status == StatusType.StatusTypeSuccess) {

            if (TopicPage.this.topic == null) {
                if (SessionController.get().isLoggedInUserAdmin()) {
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
            replyText.setText("");
            Topic topic2 = TopicController.get().getTopic(topicId);
            updateNotes(topic2);

            // note: It is api non-deterministic, (or specifically the order of handler registrations and treatment by the eventbus)
            // whether this addReply handler will get called first or the one in ForumMessageProvider.
            // that may be important depending on what you want to update in the handlers.

            messagesCellList.redraw();
            focusPagerOnPost(topic2.numberOfReplies + 1);
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
