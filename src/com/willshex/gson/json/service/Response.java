//  
//  Response.java
//  storedata
//
//  Created by William Shakour on 21 June 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.willshex.gson.json.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.willshex.gson.json.Jsonable;

public class Response extends Jsonable {
	public Error error;
	public StatusType status;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonError = error == null ? JsonNull.INSTANCE : error.toJson();
		object.add("error", jsonError);
		JsonElement jsonStatus = status == null ? JsonNull.INSTANCE : new JsonPrimitive(status.toString());
		object.add("status", jsonStatus);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("error")) {
			JsonElement jsonError = jsonObject.get("error");
			if (jsonError != null) {
				error = new Error();
				error.fromJson(jsonError.getAsJsonObject());
			}
		}
		if (jsonObject.has("status")) {
			JsonElement jsonStatus = jsonObject.get("status");
			if (jsonStatus != null) {
				status = StatusType.fromString(jsonStatus.getAsString());
			}
		}
	}
}