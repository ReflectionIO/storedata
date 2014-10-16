//  
//  TriggerDataAccountFetchIngestRequest.java
//  reflection.io
//
//  Created by William Shakour on October 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.DataAccountFetch;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class TriggerDataAccountFetchIngestRequest extends Request {
	public DataAccountFetch fetch;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonFetch = fetch == null ? JsonNull.INSTANCE : fetch.toJson();
		object.add("fetch", jsonFetch);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("fetch")) {
			JsonElement jsonFetch = jsonObject.get("fetch");
			if (jsonFetch != null) {
				fetch = new DataAccountFetch();
				fetch.fromJson(jsonFetch.getAsJsonObject());
			}
		}
	}
}