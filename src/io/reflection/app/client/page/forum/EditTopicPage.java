//
//  EditTopicPage.java
//  storedata
//
//  Created by William Shakour (donsasikumar) on 19 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.UpdateTopicRequest;
import io.reflection.app.api.forum.shared.call.UpdateTopicResponse;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler;
import io.reflection.app.api.forum.shared.call.event.UpdateTopicEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumSummarySidePanel;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EditTopicPage extends Page implements NavigationEventHandler, UpdateReplyEventHandler, UpdateTopicEventHandler {

    private static EditTopicPageUiBinder uiBinder = GWT.create(EditTopicPageUiBinder.class);

    interface EditTopicPageUiBinder extends UiBinder<Widget, EditTopicPage> {}

    private static final int TOPIC_ID_PARAMETER_INDEX = 0;
    // private static final int SECONDARY_ACTION_PARAMETER_INDEX = 1;
    private static final int REPLY_ID_PARAMETER_INDEX = 2;

    private static final String VIEW_ACTION_NAME = "view";
    private static final String EDIT_ACTION_NAME = "edit";

    private Long topicId;
    private Topic topic;

    private Long replyId;
    private Reply reply;

    private String content;

    private boolean isTopic;

    @UiField MarkdownEditor editText;
    @UiField ForumSummarySidePanel forumSummarySidePanel;

    public EditTopicPage() {
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
        register(EventController.get().addHandlerToSource(UpdateReplyEventHandler.TYPE, ReplyController.get(), this));
        register(EventController.get().addHandlerToSource(UpdateTopicEventHandler.TYPE, TopicController.get(), this));
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        reset();
    }

   
    private void reset() {
        forumSummarySidePanel.reset();
        editText.reset();
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged (io.reflection.app.client.controller.NavigationController.Stack,
     * io.reflection.app.client.controller.NavigationController.Stack)
     */
    @Override
    public void navigationChanged(Stack previous, Stack current) {
        isTopic = false;

        if (current != null && PageType.ForumEditTopicPageType.equals(current.getPage())) {
            String action = current.getAction();
            // String secondaryAction = current.getParameter(SECONDARY_ACTION_PARAMETER_INDEX);

            String topicIdString = current.getParameter(TOPIC_ID_PARAMETER_INDEX);
            String selectedReplyId = current.getParameter(REPLY_ID_PARAMETER_INDEX);

            forumSummarySidePanel.redraw();

            if (topicIdString != null) {
                topicId = null;

                if (EDIT_ACTION_NAME.equals(action)) {
                    isTopic = true;
                }

                try {
                    topicId = Long.parseLong(topicIdString);

                    if (!isTopic) {
                        replyId = Long.parseLong(selectedReplyId);
                    }
                } catch (NumberFormatException e) {
                    new RuntimeException(e);
                }

                if (isTopic) {
                    topic = TopicController.get().getTopic(topicId);

                    if (topic != null) {
                        content = topic.content.toString();
                        show();
                    }
                } else {
                    if (replyId != null) {
                        reply = ReplyController.get().getThread(topicId).getReply(replyId);

                        if (reply != null) {
                            content = reply.content.toString();
                            show();
                        }
                    }
                }
            } else {
                // there really is no point... this looks like trying to add a topic which will never happen on this page
            }
        }
    }

    /**
     * @param reply
     */
    private void show() {
        editText.setFocus(true);
        Document.get().setScrollTop(editText.getAbsoluteTop());
        editText.setText(content);
    }

    @UiHandler("submit")
    void onSubmitClicked(ClickEvent e) {

        if (validate()) {
            if (isTopic) {
                topic.content = editText.getText();

                TopicController.get().updateTopic(topic);
            } else {
                if (replyId != null) {
                    ReplyController.get().getThread(topic.id).updateReply(replyId, editText.getText());
                } else {
                    ReplyController.get().getThread(topic.id).addReply(replyId, editText.getText());
                }
            }
        }
    }

    private boolean validate() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler #updateReplySuccess
     * (io.reflection.app.api.forum.shared.call.UpdateReplyRequest, io.reflection.app.api.forum.shared.call.UpdateReplyResponse)
     */
    @Override
    public void updateReplySuccess(UpdateReplyRequest input, UpdateReplyResponse output) {
        if (output.status == StatusType.StatusTypeSuccess && replyId != null) {
            PageType.ForumThreadPageType.show(VIEW_ACTION_NAME, topicId.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler #updateReplyFailure
     * (io.reflection.app.api.forum.shared.call.UpdateReplyRequest, java.lang.Throwable)
     */
    @Override
    public void updateReplyFailure(UpdateReplyRequest input, Throwable caught) {}

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateTopicEventHandler#updateTopicSuccess(io.reflection.app.api.forum.shared.call.UpdateTopicRequest,
     * io.reflection.app.api.forum.shared.call.UpdateTopicResponse)
     */
    @Override
    public void updateTopicSuccess(UpdateTopicRequest input, UpdateTopicResponse output) {
        if (output.status == StatusType.StatusTypeSuccess && topicId != null) {
            PageType.ForumThreadPageType.show(VIEW_ACTION_NAME, topicId.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateTopicEventHandler#updateTopicFailure(io.reflection.app.api.forum.shared.call.UpdateTopicRequest,
     * java.lang.Throwable)
     */
    @Override
    public void updateTopicFailure(UpdateTopicRequest input, Throwable caught) {}

}
