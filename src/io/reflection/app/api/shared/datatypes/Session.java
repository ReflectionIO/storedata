//  
//  Session.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.shared.datatypes;

import io.reflection.app.shared.datatypes.DataType;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Session extends DataType {
	public Date expires;
	public String token;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonExpires = expires == null ? JsonNull.INSTANCE : new JsonPrimitive(expires.getTime());
		object.add("expires", jsonExpires);
		JsonElement jsonToken = token == null ? JsonNull.INSTANCE : new JsonPrimitive(token);
		object.add("token", jsonToken);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("expires")) {
			JsonElement jsonExpires = jsonObject.get("expires");
			if (jsonExpires != null) {
				expires = new Date(jsonExpires.getAsLong());
			}
		}
		if (jsonObject.has("token")) {
			JsonElement jsonToken = jsonObject.get("token");
			if (jsonToken != null) {
				token = jsonToken.getAsString();
			}
		}
	}
}