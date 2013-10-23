//  
//  AdminJsonServlet.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin;

import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.TriggerGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelRequest;

import com.google.gson.JsonObject;
import com.willshex.gson.json.service.server.JsonServlet;

@SuppressWarnings("serial")
public final class AdminJsonServlet extends JsonServlet {
	@Override
	protected String processAction(String action, JsonObject request) {
		String output = "null";
		Admin service = new Admin();
		if ("GetUsers".equals(action)) {
			GetUsersRequest input = new GetUsersRequest();
			input.fromJson(request);
			output = service.getUsers(input).toString();
		} else if ("GetUsersCount".equals(action)) {
			GetUsersCountRequest input = new GetUsersCountRequest();
			input.fromJson(request);
			output = service.getUsersCount(input).toString();
		} else if ("GetModelOutcome".equals(action)) {
			GetModelOutcomeRequest input = new GetModelOutcomeRequest();
			input.fromJson(request);
			output = service.getModelOutcome(input).toString();
		} else if ("GetFeedFetches".equals(action)) {
			GetFeedFetchesRequest input = new GetFeedFetchesRequest();
			input.fromJson(request);
			output = service.getFeedFetches(input).toString();
		} else if ("TriggerGather".equals(action)) {
			TriggerGatherRequest input = new TriggerGatherRequest();
			input.fromJson(request);
			output = service.triggerGather(input).toString();
		} else if ("TriggerIngest".equals(action)) {
			TriggerIngestRequest input = new TriggerIngestRequest();
			input.fromJson(request);
			output = service.triggerIngest(input).toString();
		} else if ("TriggerModel".equals(action)) {
			TriggerModelRequest input = new TriggerModelRequest();
			input.fromJson(request);
			output = service.triggerModel(input).toString();
		}
		return output;
	}
}