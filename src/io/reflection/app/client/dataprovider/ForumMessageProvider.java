//
//  ForumMessageProvider.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.dataprovider;

import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;
import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;
import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.UpdateTopicRequest;
import io.reflection.app.api.forum.shared.call.UpdateTopicResponse;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler;
import io.reflection.app.api.forum.shared.call.event.UpdateTopicEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.controller.ReplyController.ReplyThread;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.datatypes.shared.Topic;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForumMessageProvider extends AsyncDataProvider<ForumMessage> implements GetRepliesEventHandler, AddReplyEventHandler, UpdateTopicEventHandler,
        UpdateReplyEventHandler {

    private Topic topic;
    private List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();
    private int start;
    private int count;

    public ForumMessageProvider(Topic topic) {
        ReplyController.get(topic.id).setTopic(topic);
        this.topic = topic;
    }

    long getTotalCount() {
        return ReplyController.get(topic.id).getCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
     */
    @Override
    protected void onRangeChanged(HasData<ForumMessage> display) {
        Range r = display.getVisibleRange();

        int start = r.getStart();
        int end = start + r.getLength();

        ReplyThread thread = ReplyController.get(topic.id);

        if (thread.hasRows(start, end)) {
            updateRowData(start, thread.getMessages(start, end));
        } else {
            ReplyController.get().getReplies(topic.id, start, end);

            // this causes the spinner until we get the rows and update the display properly.
            display.setVisibleRangeAndClearData(display.getVisibleRange(), false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler#getRepliesSuccess(io.reflection.app.api.forum.shared.call.GetRepliesRequest,
     * io.reflection.app.api.forum.shared.call.GetRepliesResponse)
     */
    @Override
    public void getRepliesSuccess(GetRepliesRequest input, GetRepliesResponse output) {
        if (output.status == StatusType.StatusTypeSuccess) {

            start = input.pager.start.intValue() == 0 ? 0 : input.pager.start.intValue() + 1;
            count = input.pager.count.intValue();

            updateRows(input.topic.id);
        }
    }

    protected void updateRows(long topicId) {
        ReplyThread thread = ReplyController.get(topicId);

        // note this will not trigger a redraw of the table if the content of the cells
        // has changed but the range data hasn't.
        List<ForumMessage> messages = thread.getMessages(start, start + count + 1);
        updateRowCount(ReplyController.get(topic.id).getTotalCount(), true);
        updateRowData(start, messages);
    }

    public void registerListeners() {
        registrations.add(EventController.get().addHandlerToSource(GetRepliesEventHandler.TYPE, ReplyController.get(), this));
        registrations.add(EventController.get().addHandlerToSource(AddReplyEventHandler.TYPE, ReplyController.get(), this));
        registrations.add(EventController.get().addHandlerToSource(UpdateTopicEventHandler.TYPE, TopicController.get(), this));
        registrations.add(EventController.get().addHandlerToSource(UpdateReplyEventHandler.TYPE, ReplyController.get(), this));
    }

    public void unregisterListeners() {
        for (HandlerRegistration registration : this.registrations) {
            registration.removeHandler();
        }

        registrations.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler#getRepliesFailure(io.reflection.app.api.forum.shared.call.GetRepliesRequest,
     * java.lang.Throwable)
     */
    @Override
    public void getRepliesFailure(GetRepliesRequest input, Throwable caught) {}

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler#updateReplySuccess(io.reflection.app.api.forum.shared.call.UpdateReplyRequest,
     * io.reflection.app.api.forum.shared.call.UpdateReplyResponse)
     */
    @Override
    public void updateReplySuccess(UpdateReplyRequest input, UpdateReplyResponse output) {
        updateRows(input.reply.topic.id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler#updateReplyFailure(io.reflection.app.api.forum.shared.call.UpdateReplyRequest,
     * java.lang.Throwable)
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
        updateRows(input.topic.id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.UpdateTopicEventHandler#updateTopicFailure(io.reflection.app.api.forum.shared.call.UpdateTopicRequest,
     * java.lang.Throwable)
     */
    @Override
    public void updateTopicFailure(UpdateTopicRequest input, Throwable caught) {}

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler#addReplySuccess(io.reflection.app.api.forum.shared.call.AddReplyRequest,
     * io.reflection.app.api.forum.shared.call.AddReplyResponse)
     */
    @Override
    public void addReplySuccess(AddReplyRequest input, AddReplyResponse output) {

        updateRows(input.reply.topic.id);
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
