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
import io.reflection.app.api.forum.shared.call.GetReplyRequest;
import io.reflection.app.api.forum.shared.call.GetReplyResponse;
import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler.AddReplyFailure;
import io.reflection.app.api.forum.shared.call.event.AddReplyEventHandler.AddReplySuccess;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesFailure;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesSuccess;
import io.reflection.app.api.forum.shared.call.event.GetReplyEventHandler;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler.UpdateReplyFailure;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler.UpdateReplySuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ReplyController implements ServiceConstants {
	private static ReplyController one = null;

	private ReplyThread thread;

	public static ReplyController get() {
		if (one == null) {
			one = new ReplyController();
		}

		return one;
	}

	/**
	 * @param topicId
	 * @param end
	 * @param start
	 */
	public void getReplies(Long topicId, int start, int end) {

		getThread(topicId).fetchReplies(topicId, start, end);
	}

	/**
	 * 
	 */
	public void reset() {
		thread = null;
	}

	/**
	 * @param replyId
	 * @param content
	 */
	public void updateReply(Long replyId, String content) {
		thread.updateReply(replyId, content);
	}

	/**
	 * @param replyId
	 * @param html
	 */
	public void addReply(Long replyId, String html) {
		thread.addReply(replyId, html);
	}

	public ReplyThread getThread(long topicId) {
		if (thread == null || thread.getTopicId() != topicId) {
			thread = new ReplyThread(this, topicId);
		}
		return thread;
	}

	/**
	 * The ReplyThread class is to act as a caching mechanism. Rather than resetting all the various members of this class to change threads, simply removing
	 * the current ReplyThread and reinstantiating will do. It also means we will be able to eventually cache across different threads seamlessly.
	 * 
	 * @author daniel
	 * 
	 */
	public static class ReplyThread implements ServiceConstants {

		private List<Reply> replies = new ArrayList<Reply>();
		/*
		 * This is to get a reply by it's id At the moment it's used in the EditTopicPage, but actually this could be using ForumMessage too TODO
		 */
		private HashMap<Integer, Reply> replyStore = new HashMap<Integer, Reply>();
		Long topicId;

		private SparseArray<ForumMessage> messagesLookup = new SparseArray<ForumMessage>();
		private ReplyController replyController;
		private Topic topic;

		/**
		 * @param replyController
		 * @param topicId
		 */
		public ReplyThread(ReplyController replyController, Long topicId) {
			this.replyController = replyController;
			this.topicId = topicId;
		}

		public Reply getReply(Long replyId) {
			Reply result = replyStore.get(replyId.intValue());
			if (result == null) {
				fetchReply(replyId);
			}
			return result;
		}

		/**
		 * This method doesn't need the topicId as an argument, but putting it here as the replystore is still thread based for now.
		 * 
		 * @param replyId
		 */
		private void fetchReply(Long replyId) {
			ForumService service = ServiceCreator.createForumService();
			final GetReplyRequest input = new GetReplyRequest();
			input.accessCode = ACCESS_CODE;
			input.session = SessionController.get().getSessionForApiCall();
			input.id = replyId;
			service.getReply(input, new AsyncCallback<GetReplyResponse>() {

				@Override
				public void onSuccess(GetReplyResponse output) {
					if (output.status == StatusType.StatusTypeSuccess) {
						if (output.reply != null) {
							replies.add(output.reply);
							replyStore.put(output.reply.id.intValue(), output.reply);
						}
					}

					DefaultEventBus.get().fireEventFromSource(new GetReplyEventHandler.GetReplySuccess(input, output), replyController);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new GetReplyEventHandler.GetReplyFailure(input, caught), replyController);
				}
			});
		}

		/**
		 * @return
		 */
		public Long getTopicId() {
			return topicId;
		}

		//
		// Writing down example of a two pages, 10 items per page to demonstrate numbering to avoid confusion.
		// That means 20 ForumMessages, of which the first is the topic message with 19 replies.
		//
		// First page: Visually 01 -> 10
		// Message Lookup ids 00 -> 09
		// fetch from server 01 -> 09 because 00 is the topic,
		// with server LIMIT ids 00 -> 08 (but we can fetch 10, no harm).
		//
		// so replies START = 00, but fill messageLookup first entry is 01 (not equal)
		//
		// Second page: Visually 11 -> 20
		// Message Lookup ids 10 -> 19
		// fetch from server 10 -> 19 because 00 is the topic
		// with server LIMIT ids 09 -> 18
		//
		// replies START = 10, AND fill messageLookup first entry is 10 (equal)
		//
		void fetchReplies(Long topicId2, final long start, final long count) {
			ForumService service = ServiceCreator.createForumService();

			final GetRepliesRequest input = new GetRepliesRequest();
			input.accessCode = ACCESS_CODE;
			input.session = SessionController.get().getSessionForApiCall();

			input.topic = new Topic();
			input.topic.id = topicId;

			final Pager pager = new Pager();
			pager.count = count;
			pager.start = start == 0 ? Long.valueOf(0) : Long.valueOf(start - 1);
			pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			pager.sortBy = "created";

			input.pager = pager;

			service.getReplies(input, new AsyncCallback<GetRepliesResponse>() {

				@Override
				public void onSuccess(GetRepliesResponse output) {
					if (output.status == StatusType.StatusTypeSuccess) {
						if (output.replies != null) {
							replies.addAll(output.replies);

							long i = start == 0 ? Long.valueOf(1) : Long.valueOf(start); // not equal on first page, see above
							for (Reply reply : output.replies) {
								Topic topic = input.topic;
								int replyId = reply.id.intValue();
								addForumMessage((int) i, reply, topic, replyId);
								i++;
							}
						}
					}

					DefaultEventBus.get().fireEventFromSource(new GetRepliesSuccess(input, output), replyController);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new GetRepliesFailure(input, caught), replyController);
				}
			});
		}

		/**
		 * 
		 * @param messageIndex
		 *            starts from zero, replies start from 1
		 * @param reply
		 * @param topic
		 * @param replyId
		 */
		protected void addForumMessage(int messageIndex, Reply reply, Topic topic, int replyId) {
			messagesLookup.put(messageIndex, new ForumMessage(topic, reply, messageIndex));
			replyStore.put(replyId, reply);
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
						Topic topic = TopicController.get().getTopic(ReplyThread.this.topicId);

						if (topic != null) {
							int numberOfReplies = topic.numberOfReplies.intValue();
							topic.numberOfReplies = Integer.valueOf(++numberOfReplies);
						}

						addForumMessage((int) topic.numberOfReplies, output.reply, topic, output.reply.id.intValue());
					}

					DefaultEventBus.get().fireEventFromSource(new AddReplySuccess(input, output), replyController);
				}

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new AddReplyFailure(input, caught), replyController);
				}
			});
		}

		public void updateReply(Long id, String content) {
			ForumService service = ServiceCreator.createForumService();

			final UpdateReplyRequest input = new UpdateReplyRequest();
			input.accessCode = ACCESS_CODE;

			input.session = SessionController.get().getSessionForApiCall();
			input.reply = replyStore.get(id.intValue());

			input.reply.id = id;
			input.reply.content = content;

			service.updateReply(input, new AsyncCallback<UpdateReplyResponse>() {

				@Override
				public void onFailure(Throwable caught) {
					DefaultEventBus.get().fireEventFromSource(new UpdateReplyFailure(input, caught), replyController);

				}

				@Override
				public void onSuccess(UpdateReplyResponse output) {
					if (output.status == StatusType.StatusTypeSuccess) {
						replyController.reset();
					}

					DefaultEventBus.get().fireEventFromSource(new UpdateReplySuccess(input, output), replyController);

				}
			});
		}

		/**
		 * @param topic
		 */
		public void setTopic(Topic topic) {
			this.topic = topic;
			messagesLookup.append(0, new ForumMessage(topic, null, 0));
		}

		/**
		 * @param start
		 *            from zero.
		 * @param end
		 *            (exclusive)
		 * @return
		 */
		public boolean hasRows(int start, int end) {
			boolean hasRows = true;
			// inefficient, but not noticeable for 10-100 rows per page.
			for (int i = start; i <= end && i <= topic.numberOfReplies; i++)
				if (messagesLookup.valueAt(i) == null) {
					hasRows = false;
					break;
				}
			return hasRows;
		}

		/**
		 * @return
		 */
		public long getCount() {
			return replyStore.size();
		}

		/**
		 * Since Replys and ForumMessages are so similar, it makes sense to manage the caching of them here in ReplyController and do the wrapping.
		 * 
		 * @param start
		 * @param end
		 * @return
		 */
		public List<ForumMessage> getMessages(int start) {
			ArrayList<ForumMessage> rows = new ArrayList<ForumMessage>();
			int i = start;
			ForumMessage message = messagesLookup.get(i);

			// this may be inefficient returning more than needed replies
			// but at least it should always work.
			while (message != null || i == start) {

				if (message != null) {
					rows.add(message);
				}
				i++;
				message = messagesLookup.get(i);
			}

			return rows;
		}

		/**
		 * @return
		 */
		public int getTotalCount() {
			return topic.numberOfReplies + 1;
		}

	}
}
