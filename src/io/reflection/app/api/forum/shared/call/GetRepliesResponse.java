//  
//  GetRepliesResponse.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Reply;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.reflection.app.api.shared.datatypes.Response;

public class GetRepliesResponse extends Response {
	public List<Reply> replies;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonReplies = JsonNull.INSTANCE;
		if (replies != null) {
			jsonReplies = new JsonArray();
			for (int i = 0; i < replies.size(); i++) {
				JsonElement jsonRepliesItem = replies.get(i) == null ? JsonNull.INSTANCE : replies.get(i).toJson();
				((JsonArray) jsonReplies).add(jsonRepliesItem);
			}
		}
		object.add("replies", jsonReplies);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("replies")) {
			JsonElement jsonReplies = jsonObject.get("replies");
			if (jsonReplies != null) {
				replies = new ArrayList<Reply>();
				Reply item = null;
				for (int i = 0; i < jsonReplies.getAsJsonArray().size(); i++) {
					if (jsonReplies.getAsJsonArray().get(i) != null) {
						(item = new Reply()).fromJson(jsonReplies.getAsJsonArray().get(i).getAsJsonObject());
						replies.add(item);
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

	public GetRepliesResponse replies(List<Reply> replies) {
		this.replies = replies;
		return this;
	}

	public GetRepliesResponse pager(Pager pager) {
		this.pager = pager;
		return this;
	}
}