//
//  ReplyController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.forum.client.ForumService;
import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesFailure;
import io.reflection.app.api.forum.shared.call.event.GetRepliesEventHandler.GetRepliesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
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
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
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

}
