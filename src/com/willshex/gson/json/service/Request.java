//  
//  Request.java
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

public class Request extends Jsonable {
	public String accessCode;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonAccessCode = accessCode == null ? JsonNull.INSTANCE : new JsonPrimitive(accessCode);
		object.add("accessCode", jsonAccessCode);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("accessCode")) {
			JsonElement jsonAccessCode = jsonObject.get("accessCode");
			if (jsonAccessCode != null) {
				accessCode = jsonAccessCode.getAsString();
			}
		}
	}
}