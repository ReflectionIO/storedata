//  
//  AddEventRequest.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Event;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class AddEventRequest extends Request {
	public Event Event;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonEvent = Event == null ? JsonNull.INSTANCE : Event.toJson();
		object.add("Event", jsonEvent);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("Event")) {
			JsonElement jsonEvent = jsonObject.get("Event");
			if (jsonEvent != null) {
				Event = new Event();
				Event.fromJson(jsonEvent.getAsJsonObject());
			}
		}
	}

	public AddEventRequest Event(Event Event) {
		this.Event = Event;
		return this;
	}
}