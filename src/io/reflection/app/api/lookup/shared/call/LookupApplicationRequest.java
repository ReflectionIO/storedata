//  
//  LookupApplicationRequest.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.lookup.shared.call;

import io.reflection.app.api.lookup.shared.datatypes.LookupDetailType;
import io.reflection.app.api.shared.datatypes.Request;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LookupApplicationRequest extends Request {
	public List<String> internalIds;
	public List<String> externalIds;
	public LookupDetailType detail;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonInternalIds = JsonNull.INSTANCE;
		if (internalIds != null) {
			jsonInternalIds = new JsonArray();
			for (int i = 0; i < internalIds.size(); i++) {
				JsonElement jsonInternalIdsItem = internalIds.get(i) == null ? JsonNull.INSTANCE : new JsonPrimitive(internalIds.get(i));
				((JsonArray) jsonInternalIds).add(jsonInternalIdsItem);
			}
		}
		object.add("internalIds", jsonInternalIds);
		JsonElement jsonExternalIds = JsonNull.INSTANCE;
		if (externalIds != null) {
			jsonExternalIds = new JsonArray();
			for (int i = 0; i < externalIds.size(); i++) {
				JsonElement jsonExternalIdsItem = externalIds.get(i) == null ? JsonNull.INSTANCE : new JsonPrimitive(externalIds.get(i));
				((JsonArray) jsonExternalIds).add(jsonExternalIdsItem);
			}
		}
		object.add("externalIds", jsonExternalIds);
		JsonElement jsonDetail = detail == null ? JsonNull.INSTANCE : new JsonPrimitive(detail.toString());
		object.add("detail", jsonDetail);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("internalIds")) {
			JsonElement jsonInternalIds = jsonObject.get("internalIds");
			if (jsonInternalIds != null) {
				internalIds = new ArrayList<String>();
				for (int i = 0; i < jsonInternalIds.getAsJsonArray().size(); i++) {
					if (jsonInternalIds.getAsJsonArray().get(i) != null) {
						internalIds.add(jsonInternalIds.getAsJsonArray().get(i).getAsString());
					}
				}
			}
		}

		if (jsonObject.has("externalIds")) {
			JsonElement jsonExternalIds = jsonObject.get("externalIds");
			if (jsonExternalIds != null) {
				externalIds = new ArrayList<String>();
				for (int i = 0; i < jsonExternalIds.getAsJsonArray().size(); i++) {
					if (jsonExternalIds.getAsJsonArray().get(i) != null) {
						externalIds.add(jsonExternalIds.getAsJsonArray().get(i).getAsString());
					}
				}
			}
		}

		if (jsonObject.has("detail")) {
			JsonElement jsonDetail = jsonObject.get("detail");
			if (jsonDetail != null) {
				detail = LookupDetailType.fromString(jsonDetail.getAsString());
			}
		}
	}

	public LookupApplicationRequest internalIds(List<String> internalIds) {
		this.internalIds = internalIds;
		return this;
	}

	public LookupApplicationRequest externalIds(List<String> externalIds) {
		this.externalIds = externalIds;
		return this;
	}

	public LookupApplicationRequest detail(LookupDetailType detail) {
		this.detail = detail;
		return this;
	}
}