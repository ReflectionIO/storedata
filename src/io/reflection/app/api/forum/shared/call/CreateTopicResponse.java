//  
//  CreateTopicResponse.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.datatypes.shared.Topic;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.reflection.app.api.shared.datatypes.Response;

public class CreateTopicResponse extends Response {
	public Topic topic;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonTopic = topic == null ? JsonNull.INSTANCE : topic.toJson();
		object.add("topic", jsonTopic);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("topic")) {
			JsonElement jsonTopic = jsonObject.get("topic");
			if (jsonTopic != null) {
				topic = new Topic();
				topic.fromJson(jsonTopic.getAsJsonObject());
			}
		}
	}

	public CreateTopicResponse topic(Topic topic) {
		this.topic = topic;
		return this;
	}
}