//  
//  AdminJsonServlet.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin;

import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetItemsRequest;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.SendEmailRequest;
import io.reflection.app.api.admin.shared.call.SetPasswordRequest;
import io.reflection.app.api.admin.shared.call.TriggerGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelRequest;
import io.reflection.app.api.admin.shared.call.TriggerPredictRequest;

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
		} else if ("TriggerPredict".equals(action)) {
			TriggerPredictRequest input = new TriggerPredictRequest();
			input.fromJson(request);
			output = service.triggerPredict(input).toString();
		} else if ("SetPassword".equals(action)) {
			SetPasswordRequest input = new SetPasswordRequest();
			input.fromJson(request);
			output = service.setPassword(input).toString();
		} else if ("AssignRole".equals(action)) {
			AssignRoleRequest input = new AssignRoleRequest();
			input.fromJson(request);
			output = service.assignRole(input).toString();
		} else if ("GetRoles".equals(action)) {
			GetRolesRequest input = new GetRolesRequest();
			input.fromJson(request);
			output = service.getRoles(input).toString();
		} else if ("GetPermissions".equals(action)) {
			GetPermissionsRequest input = new GetPermissionsRequest();
			input.fromJson(request);
			output = service.getPermissions(input).toString();
		} else if ("GetEmailTemplates".equals(action)) {
			GetEmailTemplatesRequest input = new GetEmailTemplatesRequest();
			input.fromJson(request);
			output = service.getEmailTemplates(input).toString();
		} else if ("SendEmail".equals(action)) {
			SendEmailRequest input = new SendEmailRequest();
			input.fromJson(request);
			output = service.sendEmail(input).toString();
		} else if ("GetItems".equals(action)) {
			GetItemsRequest input = new GetItemsRequest();
			input.fromJson(request);
			output = service.getItems(input).toString();
		}

		return output;
	}
}