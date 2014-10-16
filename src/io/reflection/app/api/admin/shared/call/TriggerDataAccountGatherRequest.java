//  
//  TriggerDataAccountGatherRequest.java
//  reflection.io
//
//  Created by William Shakour on October 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.DataAccount;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TriggerDataAccountGatherRequest extends Request {
	public DataAccount dataAccount;
	public Integer days;
	public Date from;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataAccount = dataAccount == null ? JsonNull.INSTANCE : dataAccount.toJson();
		object.add("dataAccount", jsonDataAccount);
		JsonElement jsonDays = days == null ? JsonNull.INSTANCE : new JsonPrimitive(days);
		object.add("days", jsonDays);
		JsonElement jsonFrom = from == null ? JsonNull.INSTANCE : new JsonPrimitive(from.getTime());
		object.add("from", jsonFrom);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("dataAccount")) {
			JsonElement jsonDataAccount = jsonObject.get("dataAccount");
			if (jsonDataAccount != null) {
				dataAccount = new DataAccount();
				dataAccount.fromJson(jsonDataAccount.getAsJsonObject());
			}
		}
		if (jsonObject.has("days")) {
			JsonElement jsonDays = jsonObject.get("days");
			if (jsonDays != null) {
				days = Integer.valueOf(jsonDays.getAsInt());
			}
		}
		if (jsonObject.has("from")) {
			JsonElement jsonFrom = jsonObject.get("from");
			if (jsonFrom != null) {
				from = new Date(jsonFrom.getAsLong());
			}
		}
	}
}