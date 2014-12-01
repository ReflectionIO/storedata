//  
//  GetPostsRequest.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Request;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetPostsRequest extends Request {
	public Pager pager;
	public Boolean includeContents;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		JsonElement jsonIncludeContents = includeContents == null ? JsonNull.INSTANCE : new JsonPrimitive(includeContents);
		object.add("includeContents", jsonIncludeContents);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("pager")) {
			JsonElement jsonPager = jsonObject.get("pager");
			if (jsonPager != null) {
				pager = new Pager();
				pager.fromJson(jsonPager.getAsJsonObject());
			}
		}
		if (jsonObject.has("includeContents")) {
			JsonElement jsonIncludeContents = jsonObject.get("includeContents");
			if (jsonIncludeContents != null) {
				includeContents = Boolean.valueOf(jsonIncludeContents.getAsBoolean());
			}
		}
	}

	public GetPostsRequest pager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public GetPostsRequest includeContents(boolean includeContents) {
		this.includeContents = includeContents;
		return this;
	}
}