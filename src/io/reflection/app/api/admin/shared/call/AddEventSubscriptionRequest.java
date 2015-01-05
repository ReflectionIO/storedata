//  
//  AddEventSubscriptionRequest.java
//  reflection.io
//
//  Created by William Shakour on December 22, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.EventSubscription;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class AddEventSubscriptionRequest extends Request {
	public EventSubscription eventSubscription;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonEventSubscription = eventSubscription == null ? JsonNull.INSTANCE : eventSubscription.toJson();
		object.add("eventSubscription", jsonEventSubscription);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("eventSubscription")) {
			JsonElement jsonEventSubscription = jsonObject.get("eventSubscription");
			if (jsonEventSubscription != null) {
				eventSubscription = new EventSubscription();
				eventSubscription.fromJson(jsonEventSubscription.getAsJsonObject());
			}
		}
	}

	public AddEventSubscriptionRequest eventSubscription(EventSubscription eventSubscription) {
		this.eventSubscription = eventSubscription;
		return this;
	}
}