//  
//  GetTopItemsResponse.java
//  storedata
//
//  Created by William Shakour on 03 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.api.core.call;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.spacehopperstudios.storedata.api.datatypes.Pager;
import com.spacehopperstudios.storedata.datatypes.Item;
import com.spacehopperstudios.storedata.datatypes.Rank;
import com.willshex.gson.json.service.Response;

public class GetTopItemsResponse extends Response {
	public List<Rank> ranks;
	public List<Item> items;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonRanks = JsonNull.INSTANCE;
		if (ranks != null) {
			jsonRanks = new JsonArray();
			for (int i = 0; i < ranks.size(); i++) {
				JsonElement jsonRanksItem = ranks.get(i) == null ? JsonNull.INSTANCE : ranks.get(i).toJson();
				((JsonArray) jsonRanks).add(jsonRanksItem);
			}
		}
 		object.add("ranks", jsonRanks);
		JsonElement jsonItems = JsonNull.INSTANCE;
		if (items != null) {
			jsonItems = new JsonArray();
			for (int i = 0; i < items.size(); i++) {
				JsonElement jsonItemsItem = items.get(i) == null ? JsonNull.INSTANCE : items.get(i).toJson();
				((JsonArray) jsonItems).add(jsonItemsItem);
			}
		}
		object.add("items", jsonItems);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("ranks")) {
			JsonElement jsonRanks = jsonObject.get("ranks");
			if (jsonRanks != null) {
				ranks = new ArrayList<Rank>();
				Rank rank = null;
				for (int i = 0; i < jsonRanks.getAsJsonArray().size(); i++) {
					if (jsonRanks.getAsJsonArray().get(i) != null) {
						(rank = new Rank()).fromJson(jsonRanks.getAsJsonArray().get(i).getAsJsonObject());
						ranks.add(rank);
					}
				}
			}
		}

		if (jsonObject.has("items")) {
			JsonElement jsonItems = jsonObject.get("items");
			if (jsonItems != null) {
				items = new ArrayList<Item>();
				Item item = null;
				for (int i = 0; i < jsonItems.getAsJsonArray().size(); i++) {
					if (jsonItems.getAsJsonArray().get(i) != null) {
						(item = new Item()).fromJson(jsonItems.getAsJsonArray().get(i).getAsJsonObject());
						items.add(item);
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