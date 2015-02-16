//  
//  GetEventsResponse.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.Event;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetEventsResponse extends Response {

	public List<Event> events;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonEvents = JsonNull.INSTANCE;
		if (events != null) {
			jsonEvents = new JsonArray();
			for (int i = 0; i < events.size(); i++) {
				JsonElement jsonEventsItem = events.get(i) == null ? JsonNull.INSTANCE : events.get(i).toJson();
				((JsonArray) jsonEvents).add(jsonEventsItem);
			}
		}
		object.add("events", jsonEvents);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("events")) {
			JsonElement jsonEvents = jsonObject.get("events");
			if (jsonEvents != null) {
				events = new ArrayList<Event>();
				Event item = null;
				for (int i = 0; i < jsonEvents.getAsJsonArray().size(); i++) {
					if (jsonEvents.getAsJsonArray().get(i) != null) {
						(item = new Event()).fromJson(jsonEvents.getAsJsonArray().get(i).getAsJsonObject());
						events.add(item);
					}
				}
			}
		}

		if (jsonObject.has("pager")) {
			JsonElement jsonPager = jsonObject.get("pager");
			if (jsonPager != null) {
				pager = new Pager();
				pager.fromJson(jsonPager.getAsJsonObject());
			}
		}
	}

	public GetEventsResponse events(List<Event> events) {
		this.events = events;
		return this;
	}

	public GetEventsResponse pager(Pager pager) {
		this.pager = pager;
		return this;
	}
}