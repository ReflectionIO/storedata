//  
//  EventSubscription.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class EventSubscription extends DataType {
	public User user;
	public Event event;
	public User eavesDropping;
	public EventPriorityType email;
	public EventPriorityType text;
	public EventPriorityType push;
	public EventPriorityType notificationCenter;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonUser = user == null ? JsonNull.INSTANCE : user.toJson();
		object.add("user", jsonUser);
		JsonElement jsonEvent = event == null ? JsonNull.INSTANCE : event.toJson();
		object.add("event", jsonEvent);
		JsonElement jsonEavesDropping = eavesDropping == null ? JsonNull.INSTANCE : eavesDropping.toJson();
		object.add("eavesDropping", jsonEavesDropping);
		JsonElement jsonEmail = email == null ? JsonNull.INSTANCE : new JsonPrimitive(email.toString());
		object.add("email", jsonEmail);
		JsonElement jsonText = text == null ? JsonNull.INSTANCE : new JsonPrimitive(text.toString());
		object.add("text", jsonText);
		JsonElement jsonPush = push == null ? JsonNull.INSTANCE : new JsonPrimitive(push.toString());
		object.add("push", jsonPush);
		JsonElement jsonNotificationCenter = notificationCenter == null ? JsonNull.INSTANCE : new JsonPrimitive(notificationCenter.toString());
		object.add("notificationCenter", jsonNotificationCenter);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("user")) {
			JsonElement jsonUser = jsonObject.get("user");
			if (jsonUser != null) {
				user = new User();
				user.fromJson(jsonUser.getAsJsonObject());
			}
		}
		if (jsonObject.has("event")) {
			JsonElement jsonEvent = jsonObject.get("event");
			if (jsonEvent != null) {
				event = new Event();
				event.fromJson(jsonEvent.getAsJsonObject());
			}
		}
		if (jsonObject.has("eavesDropping")) {
			JsonElement jsonEavesDropping = jsonObject.get("eavesDropping");
			if (jsonEavesDropping != null) {
				eavesDropping = new User();
				eavesDropping.fromJson(jsonEavesDropping.getAsJsonObject());
			}
		}
		if (jsonObject.has("email")) {
			JsonElement jsonEmail = jsonObject.get("email");
			if (jsonEmail != null) {
				email = EventPriorityType.fromString(jsonEmail.getAsString());
			}
		}
		if (jsonObject.has("text")) {
			JsonElement jsonText = jsonObject.get("text");
			if (jsonText != null) {
				text = EventPriorityType.fromString(jsonText.getAsString());
			}
		}
		if (jsonObject.has("push")) {
			JsonElement jsonPush = jsonObject.get("push");
			if (jsonPush != null) {
				push = EventPriorityType.fromString(jsonPush.getAsString());
			}
		}
		if (jsonObject.has("notificationCenter")) {
			JsonElement jsonNotificationCenter = jsonObject.get("notificationCenter");
			if (jsonNotificationCenter != null) {
				notificationCenter = EventPriorityType.fromString(jsonNotificationCenter.getAsString());
			}
		}
	}

	public EventSubscription user(User user) {
		this.user = user;
		return this;
	}

	public EventSubscription event(Event event) {
		this.event = event;
		return this;
	}

	public EventSubscription eavesDropping(User eavesDropping) {
		this.eavesDropping = eavesDropping;
		return this;
	}

	public EventSubscription email(EventPriorityType email) {
		this.email = email;
		return this;
	}

	public EventSubscription text(EventPriorityType text) {
		this.text = text;
		return this;
	}

	public EventSubscription push(EventPriorityType push) {
		this.push = push;
		return this;
	}

	public EventSubscription notificationCenter(EventPriorityType notificationCenter) {
		this.notificationCenter = notificationCenter;
		return this;
	}
}