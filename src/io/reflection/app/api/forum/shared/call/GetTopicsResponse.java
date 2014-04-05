//  
//  GetTopicsResponse.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.datatypes.shared.Post;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.willshex.gson.json.service.shared.Response;

public class GetTopicsResponse extends Response {
	public Post post;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonPost = post == null ? JsonNull.INSTANCE : post.toJson();
		object.add("post", jsonPost);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("post")) {
			JsonElement jsonPost = jsonObject.get("post");
			if (jsonPost != null) {
				post = new Post();
				post.fromJson(jsonPost.getAsJsonObject());
			}
		}
	}
}