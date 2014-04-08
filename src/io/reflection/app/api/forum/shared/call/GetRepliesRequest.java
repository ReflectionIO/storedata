//  
//  GetRepliesRequest.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Topic;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetRepliesRequest extends Request {
	public Topic topic;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonTopic = topic == null ? JsonNull.INSTANCE : topic.toJson();
		object.add("topic", jsonTopic);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
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
		if (jsonObject.has("pager")) {
			JsonElement jsonPager = jsonObject.get("pager");
			if (jsonPager != null) {
				pager = new Pager();
				pager.fromJson(jsonPager.getAsJsonObject());
			}
		}
	}
}