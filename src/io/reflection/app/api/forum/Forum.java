//  
//  Forum.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum;

import static io.reflection.app.api.PagerHelper.updatePager;
import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;
import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicResponse;
import io.reflection.app.api.forum.shared.call.DeleteReplyRequest;
import io.reflection.app.api.forum.shared.call.DeleteReplyResponse;
import io.reflection.app.api.forum.shared.call.DeleteTopicRequest;
import io.reflection.app.api.forum.shared.call.DeleteTopicResponse;
import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;
import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;
import io.reflection.app.api.forum.shared.call.GetTopicsRequest;
import io.reflection.app.api.forum.shared.call.GetTopicsResponse;
import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.UpdateTopicRequest;
import io.reflection.app.api.forum.shared.call.UpdateTopicResponse;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.service.forum.ForumServiceProvider;
import io.reflection.app.service.reply.ReplyServiceProvider;
import io.reflection.app.service.topic.TopicServiceProvider;

import java.util.logging.Logger;

import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.shared.StatusType;

public final class Forum extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Forum.class.getName());

	public GetForumsResponse getForums(GetForumsRequest input) {
		LOG.finer("Entering getForums");
		GetForumsResponse output = new GetForumsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetForumsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.pager = ValidationHelper.validatePager(input.pager, "input.pager");

			output.forums = ForumServiceProvider.provide().getForums(input.pager);
			output.pager = input.pager;
			updatePager(output.pager, output.forums, input.pager.totalCount == null ? ForumServiceProvider.provide().getForumsCount() : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getForums");
		return output;
	}

	public GetTopicsResponse getTopics(GetTopicsRequest input) {
		LOG.finer("Entering getTopics");
		GetTopicsResponse output = new GetTopicsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetTopicsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.pager = ValidationHelper.validatePager(input.pager, "input.pager");

			input.forum = ValidationHelper.validateForum(input.forum, "input.forum");

			output.topics = TopicServiceProvider.provide().getTopics(input.forum, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.topics, input.pager.totalCount == null ? TopicServiceProvider.provide().getTopicsCount(input.forum) : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getTopics");
		return output;
	}

	public GetTopicResponse getTopic(GetTopicRequest input) {
		LOG.finer("Entering getTopic");
		GetTopicResponse output = new GetTopicResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetTopicsRequest: input"));

			if (input.id == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Long: input.id"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			output.topic = TopicServiceProvider.provide().getTopic(input.id);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getTopic");
		return output;
	}

	public GetRepliesResponse getReplies(GetRepliesRequest input) {
		LOG.finer("Entering getReplies");
		GetRepliesResponse output = new GetRepliesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetRepliesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.pager = ValidationHelper.validatePager(input.pager, "input.pager");

			input.topic = ValidationHelper.validateExistingTopic(input.topic, "input.topic");

			output.replies = ReplyServiceProvider.provide().getReplies(input.topic, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.replies, input.pager.totalCount == null ? ReplyServiceProvider.provide().getRepliesCount(input.topic) : null);

			output.status = StatusType.StatusTypeSuccess;

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getReplies");
		return output;
	}

	public CreateTopicResponse createTopic(CreateTopicRequest input) {
		LOG.finer("Entering createTopic");
		CreateTopicResponse output = new CreateTopicResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("CreateTopicRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.topic.author = input.session.user;

			input.topic = ValidationHelper.validateNewTopic(input.topic, "input.topic");

			output.topic = TopicServiceProvider.provide().addTopic(input.topic);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting createTopic");
		return output;
	}

	public AddReplyResponse addReply(AddReplyRequest input) {
		LOG.finer("Entering addReply");
		AddReplyResponse output = new AddReplyResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("AddReplyRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.reply = ValidationHelper.validateNewReply(input.reply, "input.reply");

			output.reply = ReplyServiceProvider.provide().addReply(input.reply);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting addReply");
		return output;
	}

	public UpdateTopicResponse updateTopic(UpdateTopicRequest input) {
		LOG.finer("Entering updateTopic");
		UpdateTopicResponse output = new UpdateTopicResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("UpdateTopicRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.topic = ValidationHelper.validateExistingTopic(input.topic, "input.topic");

			TopicServiceProvider.provide().updateTopic(input.topic);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting updateTopic");
		return output;
	}

	public UpdateReplyResponse updateReply(UpdateReplyRequest input) {
		LOG.finer("Entering updateReply");
		UpdateReplyResponse output = new UpdateReplyResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("UpdateReplyRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.reply = ValidationHelper.validateExistingReply(input.reply, "input.reply");

			ReplyServiceProvider.provide().updateReply(input.reply);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting updateReply");
		return output;
	}

	public DeleteTopicResponse deleteTopic(DeleteTopicRequest input) {
		LOG.finer("Entering deleteTopic");
		DeleteTopicResponse output = new DeleteTopicResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("DeleteTopicRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.topic = ValidationHelper.validateExistingTopic(input.topic, "input.topic");

			TopicServiceProvider.provide().deleteTopic(input.topic);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteTopic");
		return output;
	}

	public DeleteReplyResponse deleteReply(DeleteReplyRequest input) {
		LOG.finer("Entering deleteReply");
		DeleteReplyResponse output = new DeleteReplyResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("DeleteReplyRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(Long.valueOf(1)));

			input.reply = ValidationHelper.validateExistingReply(input.reply, "input.reply");

			ReplyServiceProvider.provide().deleteReply(input.reply);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteReply");
		return output;
	}
}