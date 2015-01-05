//  
//  UpdateNotificationsRequest.java
//  reflection.io
//
//  Created by William Shakour on December 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Notification;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class UpdateNotificationsRequest extends Request {
	public List<Notification> notifications;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonNotifications = JsonNull.INSTANCE;
		if (notifications != null) {
			jsonNotifications = new JsonArray();
			for (int i = 0; i < notifications.size(); i++) {
				JsonElement jsonNotificationsItem = notifications.get(i) == null ? JsonNull.INSTANCE : notifications.get(i).toJson();
				((JsonArray) jsonNotifications).add(jsonNotificationsItem);
			}
		}
		object.add("notifications", jsonNotifications);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("notifications")) {
			JsonElement jsonNotifications = jsonObject.get("notifications");
			if (jsonNotifications != null) {
				notifications = new ArrayList<Notification>();
				Notification item = null;
				for (int i = 0; i < jsonNotifications.getAsJsonArray().size(); i++) {
					if (jsonNotifications.getAsJsonArray().get(i) != null) {
						(item = new Notification()).fromJson(jsonNotifications.getAsJsonArray().get(i).getAsJsonObject());
						notifications.add(item);
					}
				}
			}
		}

	}

	public UpdateNotificationsRequest notifications(List<Notification> notifications) {
		this.notifications = notifications;
		return this;
	}
}