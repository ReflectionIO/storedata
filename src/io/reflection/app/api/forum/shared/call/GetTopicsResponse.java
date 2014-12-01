//  
//  GetTopicsResponse.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Topic;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.reflection.app.api.shared.datatypes.Response;

public class GetTopicsResponse extends Response {
	public List<Topic> topics;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonTopics = JsonNull.INSTANCE;
		if (topics != null) {
			jsonTopics = new JsonArray();
			for (int i = 0; i < topics.size(); i++) {
				JsonElement jsonTopicsItem = topics.get(i) == null ? JsonNull.INSTANCE : topics.get(i).toJson();
				((JsonArray) jsonTopics).add(jsonTopicsItem);
			}
		}
		object.add("topics", jsonTopics);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("topics")) {
			JsonElement jsonTopics = jsonObject.get("topics");
			if (jsonTopics != null) {
				topics = new ArrayList<Topic>();
				Topic item = null;
				for (int i = 0; i < jsonTopics.getAsJsonArray().size(); i++) {
					if (jsonTopics.getAsJsonArray().get(i) != null) {
						(item = new Topic()).fromJson(jsonTopics.getAsJsonArray().get(i).getAsJsonObject());
						topics.add(item);
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

	public GetTopicsResponse topics(List<Topic> topics) {
		this.topics = topics;
		return this;
	}

	public GetTopicsResponse pager(Pager pager) {
		this.pager = pager;
		return this;
	}
}