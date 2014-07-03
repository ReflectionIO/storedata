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
import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler.AddReplyFailure;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler.AddReplySuccess;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesFailure;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesSuccess;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler.UpdateReplyFailure;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler.UpdateReplySuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ReplyController extends AsyncDataProvider<Reply> implements ServiceConstants {

	private List<Reply> replies = new ArrayList<Reply>();
	private HashMap<Long,Reply> replyStore= new HashMap<Long,Reply>();
	private Long topicId;
	private long count = 0;
	private Pager pager;
	private SparseArray<Reply> replyLookup = null;

	private static ReplyController one = null;

	public static ReplyController get() {
		if (one == null) {
			one = new ReplyController();
		}

		return one;
	}

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
					if (output.replies != null) {
						replies.addAll(output.replies);

						if (replyLookup == null) {
							replyLookup = new SparseArray<Reply>();
						}

						for (Reply reply : output.replies) {
							replyLookup.put(reply.id.intValue(), reply);
							replyStore.put(reply.id, reply);
						}
					}

					if (output.pager != null) {
						pager = output.pager;

						if (pager.totalCount != null) {
							count = pager.totalCount.longValue();
						}
					}

					updateRowCount((int) count, true);
					updateRowData(
							input.pager.start.intValue(),
							replies.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
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


	public Reply getReply(Long replyId) {
		return replyStore.get(replyId);
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

	public void updateReply(Long id, String content) {
		ForumService service = ServiceCreator.createForumService();

		final UpdateReplyRequest input = new UpdateReplyRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.reply = replyLookup.get(id.intValue());

		input.reply.id = id;
		input.reply.content = content;

		service.updateReply(input, new AsyncCallback<UpdateReplyResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new UpdateReplyFailure(input, caught), ReplyController.this);

			}

			@Override
			public void onSuccess(UpdateReplyResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					reset();
				}

				EventController.get().fireEventFromSource(new UpdateReplySuccess(input, output), ReplyController.this);

			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Reply> display) {
		// TODO Auto-generated method stub

	}
}
