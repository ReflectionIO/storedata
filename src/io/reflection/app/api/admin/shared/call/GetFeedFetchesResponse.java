//  
//  GetFeedFetchesResponse.java
//  reflection.io
//
//  Created by William Shakour on October 18, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.FeedFetch;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.willshex.gson.json.service.shared.Response;

public class GetFeedFetchesResponse extends Response {
	public List<FeedFetch> ingested;
	public List<FeedFetch> uningested;
	public List<FeedFetch> mixed;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonIngested = JsonNull.INSTANCE;
		if (ingested != null) {
			jsonIngested = new JsonArray();
			for (int i = 0; i < ingested.size(); i++) {
				JsonElement jsonIngestedItem = ingested.get(i) == null ? JsonNull.INSTANCE : ingested.get(i).toJson();
				((JsonArray) jsonIngested).add(jsonIngestedItem);
			}
		}
		object.add("ingested", jsonIngested);
		JsonElement jsonUningested = JsonNull.INSTANCE;
		if (uningested != null) {
			jsonUningested = new JsonArray();
			for (int i = 0; i < uningested.size(); i++) {
				JsonElement jsonUningestedItem = uningested.get(i) == null ? JsonNull.INSTANCE : uningested.get(i).toJson();
				((JsonArray) jsonUningested).add(jsonUningestedItem);
			}
		}
		object.add("uningested", jsonUningested);
		JsonElement jsonMixed = JsonNull.INSTANCE;
		if (mixed != null) {
			jsonMixed = new JsonArray();
			for (int i = 0; i < mixed.size(); i++) {
				JsonElement jsonMixedItem = mixed.get(i) == null ? JsonNull.INSTANCE : mixed.get(i).toJson();
				((JsonArray) jsonMixed).add(jsonMixedItem);
			}
		}
		object.add("mixed", jsonMixed);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("ingested")) {
			JsonElement jsonIngested = jsonObject.get("ingested");
			if (jsonIngested != null) {
				ingested = new ArrayList<FeedFetch>();
				FeedFetch item = null;
				for (int i = 0; i < jsonIngested.getAsJsonArray().size(); i++) {
					if (jsonIngested.getAsJsonArray().get(i) != null) {
						(item = new FeedFetch()).fromJson(jsonIngested.getAsJsonArray().get(i).getAsJsonObject());
						ingested.add(item);
					}
				}
			}
		}

		if (jsonObject.has("uningested")) {
			JsonElement jsonUningested = jsonObject.get("uningested");
			if (jsonUningested != null) {
				uningested = new ArrayList<FeedFetch>();
				FeedFetch item = null;
				for (int i = 0; i < jsonUningested.getAsJsonArray().size(); i++) {
					if (jsonUningested.getAsJsonArray().get(i) != null) {
						(item = new FeedFetch()).fromJson(jsonUningested.getAsJsonArray().get(i).getAsJsonObject());
						uningested.add(item);
					}
				}
			}
		}

		if (jsonObject.has("mixed")) {
			JsonElement jsonMixed = jsonObject.get("mixed");
			if (jsonMixed != null) {
				mixed = new ArrayList<FeedFetch>();
				FeedFetch item = null;
				for (int i = 0; i < jsonMixed.getAsJsonArray().size(); i++) {
					if (jsonMixed.getAsJsonArray().get(i) != null) {
						(item = new FeedFetch()).fromJson(jsonMixed.getAsJsonArray().get(i).getAsJsonObject());
						mixed.add(item);
					}
				}
			}
		}

		if (jsonObject.has("pager")) {
			JsonElement jsonPager = jsonObject.get("pager");
			if (jsonPager != null) {
				pager = new Pager();
				pager.fromJson(jsonPager.getAsJsonObject());
			}
		}
	}
}