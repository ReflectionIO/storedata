//  
//  LoginResponse.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Session;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.willshex.gson.json.service.shared.Response;

public class LoginResponse extends Response {
	public Session session;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonSession = session == null ? JsonNull.INSTANCE : session.toJson();
		object.add("session", jsonSession);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("session")) {
			JsonElement jsonSession = jsonObject.get("session");
			if (jsonSession != null) {
				session = new Session();
				session.fromJson(jsonSession.getAsJsonObject());
			}
		}
	}
}