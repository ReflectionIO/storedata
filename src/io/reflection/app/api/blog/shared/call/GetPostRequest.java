//  
//  GetPostRequest.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call;

import io.reflection.app.api.shared.datatypes.Request;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetPostRequest extends Request {
	public String code;
	public Long id;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonCode = code == null ? JsonNull.INSTANCE : new JsonPrimitive(code);
		object.add("code", jsonCode);
		JsonElement jsonId = id == null ? JsonNull.INSTANCE : new JsonPrimitive(id);
		object.add("id", jsonId);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("code")) {
			JsonElement jsonCode = jsonObject.get("code");
			if (jsonCode != null) {
				code = jsonCode.getAsString();
			}
		}
		if (jsonObject.has("id")) {
			JsonElement jsonId = jsonObject.get("id");
			if (jsonId != null) {
				id = Long.valueOf(jsonId.getAsLong());
			}
		}
	}

	public GetPostRequest code(String code) {
		this.code = code;
		return this;
	}

	public GetPostRequest id(Long id) {
		this.id = id;
		return this;
	}
}
