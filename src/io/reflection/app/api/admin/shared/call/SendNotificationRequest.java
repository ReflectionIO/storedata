//  
//  SendNotificationRequest.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SendNotificationRequest extends Request {
	public Event event;
	public User toUser;
	public String toAddress;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonEvent = event == null ? JsonNull.INSTANCE : event.toJson();
		object.add("event", jsonEvent);
		JsonElement jsonToUser = toUser == null ? JsonNull.INSTANCE : toUser.toJson();
		object.add("toUser", jsonToUser);
		JsonElement jsonToAddress = toAddress == null ? JsonNull.INSTANCE : new JsonPrimitive(toAddress);
		object.add("toAddress", jsonToAddress);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("event")) {
			JsonElement jsonEvent = jsonObject.get("event");
			if (jsonEvent != null) {
				event = new Event();
				event.fromJson(jsonEvent.getAsJsonObject());
			}
		}
		if (jsonObject.has("toUser")) {
			JsonElement jsonToUser = jsonObject.get("toUser");
			if (jsonToUser != null) {
				toUser = new User();
				toUser.fromJson(jsonToUser.getAsJsonObject());
			}
		}
		if (jsonObject.has("toAddress")) {
			JsonElement jsonToAddress = jsonObject.get("toAddress");
			if (jsonToAddress != null) {
				toAddress = jsonToAddress.getAsString();
			}
		}
	}

	public SendNotificationRequest event(Event event) {
		this.event = event;
		return this;
	}

	public SendNotificationRequest toUser(User toUser) {
		this.toUser = toUser;
		return this;
	}

	public SendNotificationRequest toAddress(String toAddress) {
		this.toAddress = toAddress;
		return this;
	}
}