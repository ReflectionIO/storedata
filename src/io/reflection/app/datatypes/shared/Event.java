//  
//  Event.java
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

public class Event extends DataType {
	public EventTypeType type;
	public String code;
	public String name;
	public String description;
	public EventPriorityType priority;
	public String subject;
	public String shortBody;
	public String longBody;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonType = type == null ? JsonNull.INSTANCE : new JsonPrimitive(type.toString());
		object.add("type", jsonType);
		JsonElement jsonCode = code == null ? JsonNull.INSTANCE : new JsonPrimitive(code);
		object.add("code", jsonCode);
		JsonElement jsonName = name == null ? JsonNull.INSTANCE : new JsonPrimitive(name);
		object.add("name", jsonName);
		JsonElement jsonDescription = description == null ? JsonNull.INSTANCE : new JsonPrimitive(description);
		object.add("description", jsonDescription);
		JsonElement jsonPriority = priority == null ? JsonNull.INSTANCE : new JsonPrimitive(priority.toString());
		object.add("priority", jsonPriority);
		JsonElement jsonSubject = subject == null ? JsonNull.INSTANCE : new JsonPrimitive(subject);
		object.add("subject", jsonSubject);
		JsonElement jsonShortBody = shortBody == null ? JsonNull.INSTANCE : new JsonPrimitive(shortBody);
		object.add("shortBody", jsonShortBody);
		JsonElement jsonLongBody = longBody == null ? JsonNull.INSTANCE : new JsonPrimitive(longBody);
		object.add("longBody", jsonLongBody);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("type")) {
			JsonElement jsonType = jsonObject.get("type");
			if (jsonType != null) {
				type = EventTypeType.fromString(jsonType.getAsString());
			}
		}
		if (jsonObject.has("code")) {
			JsonElement jsonCode = jsonObject.get("code");
			if (jsonCode != null) {
				code = jsonCode.getAsString();
			}
		}
		if (jsonObject.has("name")) {
			JsonElement jsonName = jsonObject.get("name");
			if (jsonName != null) {
				name = jsonName.getAsString();
			}
		}
		if (jsonObject.has("description")) {
			JsonElement jsonDescription = jsonObject.get("description");
			if (jsonDescription != null) {
				description = jsonDescription.getAsString();
			}
		}
		if (jsonObject.has("priority")) {
			JsonElement jsonPriority = jsonObject.get("priority");
			if (jsonPriority != null) {
				priority = EventPriorityType.fromString(jsonPriority.getAsString());
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
	}

	public Event type(EventTypeType type) {
		this.type = type;
		return this;
	}

	public Event code(String code) {
		this.code = code;
		return this;
	}

	public Event name(String name) {
		this.name = name;
		return this;
	}

	public Event description(String description) {
		this.description = description;
		return this;
	}

	public Event priority(EventPriorityType priority) {
		this.priority = priority;
		return this;
	}

	public Event subject(String subject) {
		this.subject = subject;
		return this;
	}

	public Event shortBody(String shortBody) {
		this.shortBody = shortBody;
		return this;
	}

	public Event longBody(String longBody) {
		this.longBody = longBody;
		return this;
	}
}