//  
//  LookupJsonServlet.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.lookup;

import io.reflection.app.api.lookup.call.LookupApplicationRequest;

import com.google.gson.JsonObject;
import com.willshex.gson.json.service.server.JsonServlet;

@SuppressWarnings("serial")
public final class LookupJsonServlet extends JsonServlet {
	@Override
	protected String processAction(String action, JsonObject request) {
		String output = "null";
		Lookup service = new Lookup();
		if ("LookupApplication".equals(action)) {
			LookupApplicationRequest input = new LookupApplicationRequest();
			input.fromJson(request);
			output = service.lookupApplication(input).toString();
		}
		return output;
	}
}