//  
//  DeleteEventSubscriptionsRequest.java
//  reflection.io
//
//  Created by William Shakour on December 22, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.EventSubscription;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class DeleteEventSubscriptionsRequest extends Request {
	public List<EventSubscription> eventSubscriptions;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonEventSubscriptions = JsonNull.INSTANCE;
		if (eventSubscriptions != null) {
			jsonEventSubscriptions = new JsonArray();
			for (int i = 0; i < eventSubscriptions.size(); i++) {
				JsonElement jsonEventSubscriptionsItem = eventSubscriptions.get(i) == null ? JsonNull.INSTANCE : eventSubscriptions.get(i).toJson();
				((JsonArray) jsonEventSubscriptions).add(jsonEventSubscriptionsItem);
			}
		}
		object.add("eventSubscriptions", jsonEventSubscriptions);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("eventSubscriptions")) {
			JsonElement jsonEventSubscriptions = jsonObject.get("eventSubscriptions");
			if (jsonEventSubscriptions != null) {
				eventSubscriptions = new ArrayList<EventSubscription>();
				EventSubscription item = null;
				for (int i = 0; i < jsonEventSubscriptions.getAsJsonArray().size(); i++) {
					if (jsonEventSubscriptions.getAsJsonArray().get(i) != null) {
						(item = new EventSubscription()).fromJson(jsonEventSubscriptions.getAsJsonArray().get(i).getAsJsonObject());
						eventSubscriptions.add(item);
					}
				}
			}
		}

	}

	public DeleteEventSubscriptionsRequest eventSubscriptions(List<EventSubscription> eventSubscriptions) {
		this.eventSubscriptions = eventSubscriptions;
		return this;
	}
}