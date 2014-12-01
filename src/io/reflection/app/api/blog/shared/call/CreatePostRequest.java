//  
//  CreatePostRequest.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Post;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CreatePostRequest extends Request {
	public Post post;
	public Boolean publish;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonPost = post == null ? JsonNull.INSTANCE : post.toJson();
		object.add("post", jsonPost);
		JsonElement jsonPublish = publish == null ? JsonNull.INSTANCE : new JsonPrimitive(publish);
		object.add("publish", jsonPublish);
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
		if (jsonObject.has("publish")) {
			JsonElement jsonPublish = jsonObject.get("publish");
			if (jsonPublish != null) {
				publish = Boolean.valueOf(jsonPublish.getAsBoolean());
			}
		}
	}

	public CreatePostRequest post(Post post) {
		this.post = post;
		return this;
	}

	public CreatePostRequest publish(boolean publish) {
		this.publish = publish;
		return this;
	}
}