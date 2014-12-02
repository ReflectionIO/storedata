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
	public String subject;
	public String shortBody;
	public String longBody;
	public NotificationStatusType status;
	public NotificationTypeType type;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonCause = cause == null ? JsonNull.INSTANCE : cause.toJson();
		object.add("cause", jsonCause);
		JsonElement jsonSubject = subject == null ? JsonNull.INSTANCE : new JsonPrimitive(subject);
		object.add("subject", jsonSubject);
		JsonElement jsonShortBody = shortBody == null ? JsonNull.INSTANCE : new JsonPrimitive(shortBody);
		object.add("shortBody", jsonShortBody);
		JsonElement jsonLongBody = longBody == null ? JsonNull.INSTANCE : new JsonPrimitive(longBody);
		object.add("longBody", jsonLongBody);
		JsonElement jsonStatus = status == null ? JsonNull.INSTANCE : new JsonPrimitive(status.toString());
		object.add("status", jsonStatus);
		JsonElement jsonType = type == null ? JsonNull.INSTANCE : new JsonPrimitive(type.toString());
		object.add("type", jsonType);
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
		if (jsonObject.has("subject")) {
			JsonElement jsonSubject = jsonObject.get("subject");
			if (jsonSubject != null) {
				subject = jsonSubject.getAsString();
			}
		}
		if (jsonObject.has("shortBody")) {
			JsonElement jsonShortBody = jsonObject.get("shortBody");
			if (jsonShortBody != null) {
				shortBody = jsonShortBody.getAsString();
			}
		}
		if (jsonObject.has("longBody")) {
			JsonElement jsonLongBody = jsonObject.get("longBody");
			if (jsonLongBody != null) {
				longBody = jsonLongBody.getAsString();
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
	}

	public Notification cause(EventSubscription cause) {
		this.cause = cause;
		return this;
	}

	public Notification subject(String subject) {
		this.subject = subject;
		return this;
	}

	public Notification shortBody(String shortBody) {
		this.shortBody = shortBody;
		return this;
	}

	public Notification longBody(String longBody) {
		this.longBody = longBody;
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
}