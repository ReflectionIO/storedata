//
//  ForumMessageProvider.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.dataprovider;

import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.datatypes.shared.Reply;
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
public class ForumMessageProvider extends AsyncDataProvider<ForumMessage> implements GetRepliesEventHandler {

	private List<ForumMessage> rows = null;
	private Topic topic;
	private HandlerRegistration registration;

	public ForumMessageProvider(Topic topic) {
		rows = new ArrayList<ForumMessage>();
		rows.add(new ForumMessage(this.topic = topic));
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

		if (rows == null || end > rows.size()) {
			registerListener();
			ReplyController.get().getReplies(topic.id);
		} else {
			updateRowData(start, rows.subList(start, end));
		}
	}

	/**
	 * 
	 */
	private void registerListener() {
		if (registration != null) {
			unregisterListener();
		}

		registration = EventController.get().addHandlerToSource(GetRepliesEventHandler.TYPE, ReplyController.get(), this);
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
			if (output.replies != null) {
				for (Reply reply : output.replies) {
					rows.add(new ForumMessage(reply));
				}
			}

			int start = input.pager.start.intValue() == 0 ? 0 : input.pager.start.intValue() + 1;

			updateRowCount(rows.size(), false);
			updateRowData(start, rows.subList(start, Math.min(start + input.pager.count.intValue(), rows.size())));
		}

		unregisterListener();
	}

	/**
	 * 
	 */
	private void unregisterListener() {
		if (registration != null) {
			registration.removeHandler();
			registration = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler#getRepliesFailure(io.reflection.app.api.forum.shared.call.GetRepliesRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getRepliesFailure(GetRepliesRequest input, Throwable caught) {

	}

}
