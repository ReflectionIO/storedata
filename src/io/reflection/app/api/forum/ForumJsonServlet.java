//  
//  ForumJsonServlet.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum;

import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.DeleteReplyRequest;
import io.reflection.app.api.forum.shared.call.DeleteTopicRequest;
import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetReplyRequest;
import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicsRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateTopicRequest;

import com.google.gson.JsonObject;
import com.willshex.gson.json.service.server.JsonServlet;

@SuppressWarnings("serial")
public final class ForumJsonServlet extends JsonServlet {
	@Override
	protected String processAction(String action, JsonObject request) {
		String output = "null";
		Forum service = new Forum();
		if ("GetForums".equals(action)) {
			GetForumsRequest input = new GetForumsRequest();
			input.fromJson(request);
			output = service.getForums(input).toString();
		} else if ("GetTopics".equals(action)) {
			GetTopicsRequest input = new GetTopicsRequest();
			input.fromJson(request);
			output = service.getTopics(input).toString();
		} else if ("GetTopic".equals(action)) {
			GetTopicRequest input = new GetTopicRequest();
			input.fromJson(request);
			output = service.getTopic(input).toString();
		} else if ("GetReplies".equals(action)) {
			GetRepliesRequest input = new GetRepliesRequest();
			input.fromJson(request);
			output = service.getReplies(input).toString();
		} else if ("GetReply".equals(action)) {
			GetReplyRequest input = new GetReplyRequest();
			input.fromJson(request);
			output = service.getReply(input).toString();
		} else if ("CreateTopic".equals(action)) {
			CreateTopicRequest input = new CreateTopicRequest();
			input.fromJson(request);
			output = service.createTopic(input).toString();
		} else if ("AddReply".equals(action)) {
			AddReplyRequest input = new AddReplyRequest();
			input.fromJson(request);
			output = service.addReply(input).toString();
		} else if ("UpdateTopic".equals(action)) {
			UpdateTopicRequest input = new UpdateTopicRequest();
			input.fromJson(request);
			output = service.updateTopic(input).toString();
		} else if ("UpdateReply".equals(action)) {
			UpdateReplyRequest input = new UpdateReplyRequest();
			input.fromJson(request);
			output = service.updateReply(input).toString();
		} else if ("DeleteTopic".equals(action)) {
			DeleteTopicRequest input = new DeleteTopicRequest();
			input.fromJson(request);
			output = service.deleteTopic(input).toString();
		} else if ("DeleteReply".equals(action)) {
			DeleteReplyRequest input = new DeleteReplyRequest();
			input.fromJson(request);
			output = service.deleteReply(input).toString();
		}
		return output;
	}
}