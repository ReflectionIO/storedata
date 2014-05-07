//
//  ReplyController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.forum.client.ForumService;
import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;
import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler.AddReplyFailure;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler.AddReplySuccess;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesFailure;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ReplyController implements ServiceConstants {

	private static ReplyController one = null;

	public static ReplyController get() {
		if (one == null) {
			one = new ReplyController();
		}

		return one;
	}

	private Long topicId;
	private Pager pager;

	private void fetchReplies() {
		ForumService service = ServiceCreator.createForumService();

		final GetRepliesRequest input = new GetRepliesRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.topic = new Topic();
		input.topic.id = topicId;

		if (pager == null) {
			pager = new Pager();
			pager.count = SHORT_STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			pager.sortBy = "created";
		}
		input.pager = pager;

		service.getReplies(input, new AsyncCallback<GetRepliesResponse>() {

			@Override
			public void onSuccess(GetRepliesResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.pager != null) {
						pager = output.pager;
					}
				}

				EventController.get().fireEventFromSource(new GetRepliesSuccess(input, output), ReplyController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetRepliesFailure(input, caught), ReplyController.this);
			}
		});
	}

	/**
	 * @param topicId
	 */
	public void getReplies(Long topicId) {

		if (topicId != this.topicId) {
			reset();

			this.topicId = topicId;
		}

		fetchReplies();
	}

	public void reset() {
		topicId = null;
		pager = null;
	}

	public void addReply(Long topicId, String replyContent) {
		ForumService service = ServiceCreator.createForumService();

		final AddReplyRequest input = new AddReplyRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.reply = new Reply();
		input.reply.author = SessionController.get().getLoggedInUser();
		input.reply.content = replyContent;

		input.reply.topic = new Topic();
		input.reply.topic.id = topicId;

		service.addReply(input, new AsyncCallback<AddReplyResponse>() {

			@Override
			public void onSuccess(AddReplyResponse output) {

				if (output.status == StatusType.StatusTypeSuccess) {
					if (pager != null && pager.totalCount != null) {
						long totalCount = pager.totalCount.longValue();
						pager.totalCount = Long.valueOf(totalCount++);
					}

					Topic topic = TopicController.get().getTopic(ReplyController.this.topicId);

					if (topic != null) {
						int numberOfReplies = topic.numberOfReplies.intValue();
						topic.numberOfReplies = Integer.valueOf(numberOfReplies++);
					}
				}

				EventController.get().fireEventFromSource(new AddReplySuccess(input, output), ReplyController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new AddReplyFailure(input, caught), ReplyController.this);
			}
		});
	}
}
