//
//  TopicController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.forum.client.ForumService;
import io.reflection.app.api.forum.shared.call.AlterTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicResponse;
import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;
import io.reflection.app.api.forum.shared.call.GetTopicsRequest;
import io.reflection.app.api.forum.shared.call.GetTopicsResponse;
import io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler.CreateTopicFailure;
import io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler.CreateTopicSuccess;
import io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler.GetTopicFailure;
import io.reflection.app.api.forum.shared.call.event.GetTopicEventHandler.GetTopicSuccess;
import io.reflection.app.api.forum.shared.call.event.GetTopicsEventHandler.GetTopicsFailure;
import io.reflection.app.api.forum.shared.call.event.GetTopicsEventHandler.GetTopicsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.SparseArray;
import io.reflection.app.shared.util.TagHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class TopicController extends AsyncDataProvider<Topic> implements ServiceConstants {

	private List<Topic> topics = new ArrayList<Topic>();
	private long count = 0;
	private Pager pager;
	private SparseArray<Topic> topicLookup = null;

	private Long forumId;

	private static TopicController one = null;

	public static TopicController get() {
		if (one == null) {
			one = new TopicController();
		}

		return one;
	}

	private void fetchTopics() {

		if (forumId != null) {
			ForumService service = ServiceCreator.createForumService();

			final GetTopicsRequest input = new GetTopicsRequest();
			input.accessCode = ACCESS_CODE;

			input.session = SessionController.get().getSessionForApiCall();

			input.forum = new Forum();
			input.forum.id = forumId;

			if (pager == null) {
				pager = new Pager();
				pager.count = SHORT_STEP;
				pager.start = Long.valueOf(0);
				pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}
			input.pager = pager;

			service.getTopics(input, new AsyncCallback<GetTopicsResponse>() {

				@Override
				public void onSuccess(GetTopicsResponse output) {
					if (output.status == StatusType.StatusTypeSuccess) {
						if (output.topics != null) {
							topics.addAll(output.topics);

							if (topicLookup == null) {
								topicLookup = new SparseArray<Topic>();
							}

							for (Topic topic : output.topics) {
								topicLookup.put(topic.id.intValue(), topic);
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
								topics.subList(input.pager.start.intValue(),
										Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
					}

					EventController.get().fireEventFromSource(new GetTopicsSuccess(input, output), TopicController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					EventController.get().fireEventFromSource(new GetTopicsFailure(input, caught), TopicController.this);
				}
			});
		}

	}

	private void fetchTopic(Long topicId) {
		ForumService service = ServiceCreator.createForumService();

		final GetTopicRequest input = new GetTopicRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.id = topicId;

		service.getTopic(input, new AsyncCallback<GetTopicResponse>() {

			@Override
			public void onSuccess(GetTopicResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.topic != null) {
						if (topicLookup == null) {
							topicLookup = new SparseArray<Topic>();
						}

						topicLookup.put(output.topic.id.intValue(), output.topic);
					}
				}

				EventController.get().fireEventFromSource(new GetTopicSuccess(input, output), TopicController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetTopicFailure(input, caught), TopicController.this);
			}
		});
	}

	public List<Topic> getTopics(Long forumId) {
		if (this.forumId == null || this.forumId != forumId) {
			this.forumId = forumId;

			reset();
		}

		if (pager == null) {
			fetchTopics();
		}

		return topics;
	}

	public long getTopicsCount() {
		return count;
	}

	public boolean hasTopics() {
		return pager != null || topics.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Topic> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > topics.size()) {
			fetchTopics();
		}

		updateRowData(start, topics.size() == 0 ? topics : topics.subList(start, Math.min(topics.size(), end)));
	}

	/**
	 * 
	 * @param id
	 * @param title
	 * @param visible
	 * @param commentsEnabled
	 * @param description
	 * @param content
	 * @param publish
	 * @param tags
	 */
	public void updateTopic(Long id, String title, Boolean visible, Boolean commentsEnabled, String description, String content, Boolean publish, String tags) {
		// ForumService service = ServiceCreator.createForumService();
		//
		// final UpdateTopicRequest input = new UpdateTopicRequest();
		// input.accessCode = ACCESS_CODE;
		//
		// input.session = SessionController.get().getSessionForApiCall();
		// input.topic = topicLookup.get(id.intValue());
		//
		// input.topic.title = title;
		// input.topic.description = description;
		// input.topic.content = content;
		//
		// input.publish = publish;
		//
		// input.topic.visible = visible;
		// input.topic.commentsEnabled = commentsEnabled;
		//
		// input.topic.tags = TagHelper.convertToTagList(tags);
		//
		// service.updateTopic(input, new AsyncCallback<UpdateTopicResponse>() {
		//
		// @Override
		// public void onSuccess(UpdateTopicResponse output) {
		// if (output.status == StatusType.StatusTypeSuccess) {
		// reset();
		// }
		//
		// EventController.get().fireEventFromSource(new UpdateTopicSuccess(input, output), TopicController.this);
		// }
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// EventController.get().fireEventFromSource(new UpdateTopicFailure(input, caught), TopicController.this);
		// }
		// });
	}

	/**
	 * 
	 * @param forumId
	 * @param title
	 * @param sticky
	 * @param visible
	 * @param commentsEnabled
	 * @param description
	 * @param content
	 * @param publish
	 * @param tags
	 */
	public void createTopic(Long forumId, String title, Boolean sticky, String content, String tags) {
		ForumService service = ServiceCreator.createForumService();

		final CreateTopicRequest input = new CreateTopicRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.topic = new Topic();

		input.topic.title = title;
		input.topic.content = content;

		input.topic.forum = new Forum();
		input.topic.forum.id = this.forumId = forumId;

		input.topic.tags = TagHelper.convertToTagList(tags);

		input.topic.sticky = sticky;

		service.createTopic(input, new AsyncCallback<CreateTopicResponse>() {

			@Override
			public void onSuccess(CreateTopicResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

				}

				EventController.get().fireEventFromSource(new CreateTopicSuccess(input, output), TopicController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new CreateTopicFailure(input, caught), TopicController.this);
			}
		});
	}

	public void reset() {
		pager = null;
		topics.clear();

		updateRowData(0, topics);
		updateRowCount(0, false);

		fetchTopics();
	}

	/**
	 * @param id
	 * @return
	 */
	public Topic getTopic(Long id) {
		Topic topic = null;

		if (topicLookup != null) {
			topic = topicLookup.get(id.intValue());
		}

		if (topic == null) {
			fetchTopic(id);
		}

		return topic;
	}

	public Topic getTopicPart(Long id) {
		Topic topic = null;

		if (topicLookup != null) {
			topic = topicLookup.get(id.intValue());
		}

		return topic;
	}

	public void deleteTopic(Long topicId) {
		// ForumService service = ServiceCreator.createForumService();
		//
		// final DeleteTopicRequest input = new DeleteTopicRequest();
		// input.accessCode = ACCESS_CODE;
		//
		// input.session = SessionController.get().getSessionForApiCall();
		//
		// input.topic = new Topic();
		// input.topic.id = topicId;
		//
		// service.deleteTopic(input, new AsyncCallback<DeleteTopicResponse>() {
		//
		// @Override
		// public void onSuccess(DeleteTopicResponse output) {
		// if (output.status == StatusType.StatusTypeSuccess) {}
		//
		// EventController.get().fireEventFromSource(new DeleteTopicSuccess(input, output), TopicController.this);
		// }
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// EventController.get().fireEventFromSource(new DeleteTopicFailure(input, caught), TopicController.this);
		// }
		// });
	}

	/**
	 * @return
	 */
	public Long getForumId() {
		return forumId;
	}

	/**
	 * @param topicId
	 * @param b
	 */
	public void setSticky(Long topicId, boolean makeSticky) {
		ForumService service = ServiceCreator.createForumService();

		final AlterTopicRequest input = new AlterTopicRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.topic = new Topic();
		
		input.topic.id = topicId ;
		input.topic.sticky = makeSticky;

//		service.alterTopic(input, new AsyncCallback<CreateTopicResponse>() {
//
//			@Override
//			public void onSuccess(CreateTopicResponse output) {
//				if (output.status == StatusType.StatusTypeSuccess) {
//
//				}
//
//				EventController.get().fireEventFromSource(new CreateTopicSuccess(input, output), TopicController.this);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				EventController.get().fireEventFromSource(new CreateTopicFailure(input, caught), TopicController.this);
//			}
//		});
		
	}

}
