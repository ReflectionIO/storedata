//  
//  Session.java
//  storedata
//
//  Created by William Shakour on 21 June 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.datatypes;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Session extends DataType {
	public Date expires;
	public String accessCode;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonExpires = expires == null ? JsonNull.INSTANCE : new JsonPrimitive(expires.getTime());
		object.add("expires", jsonExpires);
		JsonElement jsonAccessCode = accessCode == null ? JsonNull.INSTANCE : new JsonPrimitive(accessCode);
		object.add("accessCode", jsonAccessCode);
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
		if (jsonObject.has("accessCode")) {
			JsonElement jsonAccessCode = jsonObject.get("accessCode");
			if (jsonAccessCode != null) {
				accessCode = jsonAccessCode.getAsString();
			}
		}
	}
}