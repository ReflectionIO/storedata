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
import io.reflection.app.datatypes.shared.Notification;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class SendNotificationRequest extends Request {
	public Notification notification;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonNotification = notification == null ? JsonNull.INSTANCE : notification.toJson();
		object.add("notification", jsonNotification);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("notification")) {
			JsonElement jsonNotification = jsonObject.get("notification");
			if (jsonNotification != null) {
				notification = new Notification();
				notification.fromJson(jsonNotification.getAsJsonObject());
			}
		}
	}

	public SendNotificationRequest notification(Notification notification) {
		this.notification = notification;
		return this;
	}
}