//  
//  AdminJsonServlet.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin;

import io.reflection.app.api.admin.shared.call.GetUsersRequest;

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
		}
		return output;
	}
}