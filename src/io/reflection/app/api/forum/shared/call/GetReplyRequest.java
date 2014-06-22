//
//  GetReplyRequest.java
//  storedata
//
//  Created by William Shakour (donsasikumar) on 21 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.api.shared.datatypes.Request;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author donsasikumar
 *
 */
public class GetReplyRequest extends Request {
	public Long id;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonId = id == null ? JsonNull.INSTANCE : new JsonPrimitive(id);
		object.add("id", jsonId);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("id")) {
			JsonElement jsonId = jsonObject.get("id");
			if (jsonId != null) {
				id = Long.valueOf(jsonId.getAsLong());
			}
		}
	}
}
