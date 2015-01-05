//  
//  GetNotificationsRequest.java
//  reflection.io
//
//  Created by William Shakour on December 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Request;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetNotificationsRequest extends Request {
	public Pager pager;
	public String query;
	public Boolean includeDeleted;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		JsonElement jsonQuery = query == null ? JsonNull.INSTANCE : new JsonPrimitive(query);
		object.add("query", jsonQuery);
		JsonElement jsonIncludeDeleted = includeDeleted == null ? JsonNull.INSTANCE : new JsonPrimitive(includeDeleted);
		object.add("includeDeleted", jsonIncludeDeleted);
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
		if (jsonObject.has("query")) {
			JsonElement jsonQuery = jsonObject.get("query");
			if (jsonQuery != null) {
				query = jsonQuery.getAsString();
			}
		}
		if (jsonObject.has("includeDeleted")) {
			JsonElement jsonIncludeDeleted = jsonObject.get("includeDeleted");
			if (jsonIncludeDeleted != null) {
				includeDeleted = Boolean.valueOf(jsonIncludeDeleted.getAsBoolean());
			}
		}
	}

	public GetNotificationsRequest pager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public GetNotificationsRequest query(String query) {
		this.query = query;
		return this;
	}

	public GetNotificationsRequest includeDeleted(Boolean includeDeleted) {
		this.includeDeleted = includeDeleted;
		return this;
	}
}