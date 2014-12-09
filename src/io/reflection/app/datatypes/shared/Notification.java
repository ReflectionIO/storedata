//  
//  Notification.java
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

public class Notification extends DataType {
	public EventSubscription cause;
	public String from;
	public String subject;
	public String body;
	public NotificationStatusType status;
	public NotificationTypeType type;
	public EventPriorityType priority;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonCause = cause == null ? JsonNull.INSTANCE : cause.toJson();
		object.add("cause", jsonCause);
		JsonElement jsonFrom = from == null ? JsonNull.INSTANCE : new JsonPrimitive(from);
		object.add("from", jsonFrom);
		JsonElement jsonSubject = subject == null ? JsonNull.INSTANCE : new JsonPrimitive(subject);
		object.add("subject", jsonSubject);
		JsonElement jsonBody = body == null ? JsonNull.INSTANCE : new JsonPrimitive(body);
		object.add("body", jsonBody);
		JsonElement jsonStatus = status == null ? JsonNull.INSTANCE : new JsonPrimitive(status.toString());
		object.add("status", jsonStatus);
		JsonElement jsonType = type == null ? JsonNull.INSTANCE : new JsonPrimitive(type.toString());
		object.add("type", jsonType);
		JsonElement jsonPriority = priority == null ? JsonNull.INSTANCE : new JsonPrimitive(priority.toString());
		object.add("priority", jsonPriority);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("cause")) {
			JsonElement jsonCause = jsonObject.get("cause");
			if (jsonCause != null) {
				cause = new EventSubscription();
				cause.fromJson(jsonCause.getAsJsonObject());
			}
		}
		if (jsonObject.has("from")) {
			JsonElement jsonFrom = jsonObject.get("from");
			if (jsonFrom != null) {
				from = jsonFrom.getAsString();
			}
		}
		if (jsonObject.has("subject")) {
			JsonElement jsonSubject = jsonObject.get("subject");
			if (jsonSubject != null) {
				subject = jsonSubject.getAsString();
			}
		}
		if (jsonObject.has("body")) {
			JsonElement jsonBody = jsonObject.get("body");
			if (jsonBody != null) {
				body = jsonBody.getAsString();
			}
		}
		if (jsonObject.has("status")) {
			JsonElement jsonStatus = jsonObject.get("status");
			if (jsonStatus != null) {
				status = NotificationStatusType.fromString(jsonStatus.getAsString());
			}
		}
		if (jsonObject.has("type")) {
			JsonElement jsonType = jsonObject.get("type");
			if (jsonType != null) {
				type = NotificationTypeType.fromString(jsonType.getAsString());
			}
		}
		if (jsonObject.has("priority")) {
			JsonElement jsonPriority = jsonObject.get("priority");
			if (jsonPriority != null) {
				priority = EventPriorityType.fromString(jsonPriority.getAsString());
			}
		}
	}

	public Notification cause(EventSubscription cause) {
		this.cause = cause;
		return this;
	}

	public Notification from(String from) {
		this.from = from;
		return this;
	}

	public Notification subject(String subject) {
		this.subject = subject;
		return this;
	}

	public Notification body(String body) {
		this.body = body;
		return this;
	}

	public Notification status(NotificationStatusType status) {
		this.status = status;
		return this;
	}

	public Notification type(NotificationTypeType type) {
		this.type = type;
		return this;
	}

	public Notification priority(EventPriorityType priority) {
		this.priority = priority;
		return this;
	}
}