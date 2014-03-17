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
import io.reflection.app.datatypes.shared.Tag;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CreatePostRequest extends Request {
	public List<Tag> tags;
	public String title;
	public String description;
	public String content;
	public Boolean publish;
	public Boolean visible;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonTags = JsonNull.INSTANCE;
		if (tags != null) {
			jsonTags = new JsonArray();
			for (int i = 0; i < tags.size(); i++) {
				JsonElement jsonTagsItem = tags.get(i) == null ? JsonNull.INSTANCE : tags.get(i).toJson();
				((JsonArray) jsonTags).add(jsonTagsItem);
			}
		}
		object.add("tags", jsonTags);
		JsonElement jsonTitle = title == null ? JsonNull.INSTANCE : new JsonPrimitive(title);
		object.add("title", jsonTitle);
		JsonElement jsonDescription = description == null ? JsonNull.INSTANCE : new JsonPrimitive(description);
		object.add("description", jsonDescription);
		JsonElement jsonContent = content == null ? JsonNull.INSTANCE : new JsonPrimitive(content);
		object.add("content", jsonContent);
		JsonElement jsonPublish = publish == null ? JsonNull.INSTANCE : new JsonPrimitive(publish);
		object.add("publish", jsonPublish);
		JsonElement jsonVisible = visible == null ? JsonNull.INSTANCE : new JsonPrimitive(visible);
		object.add("visible", jsonVisible);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("tags")) {
			JsonElement jsonTags = jsonObject.get("tags");
			if (jsonTags != null) {
				tags = new ArrayList<Tag>();
				Tag item = null;
				for (int i = 0; i < jsonTags.getAsJsonArray().size(); i++) {
					if (jsonTags.getAsJsonArray().get(i) != null) {
						(item = new Tag()).fromJson(jsonTags.getAsJsonArray().get(i).getAsJsonObject());
						tags.add(item);
					}
				}
			}
		}

		if (jsonObject.has("title")) {
			JsonElement jsonTitle = jsonObject.get("title");
			if (jsonTitle != null) {
				title = jsonTitle.getAsString();
			}
		}
		if (jsonObject.has("description")) {
			JsonElement jsonDescription = jsonObject.get("description");
			if (jsonDescription != null) {
				description = jsonDescription.getAsString();
			}
		}
		if (jsonObject.has("content")) {
			JsonElement jsonContent = jsonObject.get("content");
			if (jsonContent != null) {
				content = jsonContent.getAsString();
			}
		}
		if (jsonObject.has("publish")) {
			JsonElement jsonPublish = jsonObject.get("publish");
			if (jsonPublish != null) {
				publish = Boolean.valueOf(jsonPublish.getAsBoolean());
			}
		}
		if (jsonObject.has("visible")) {
			JsonElement jsonVisible = jsonObject.get("visible");
			if (jsonVisible != null) {
				visible = Boolean.valueOf(jsonVisible.getAsBoolean());
			}
		}
	}
}