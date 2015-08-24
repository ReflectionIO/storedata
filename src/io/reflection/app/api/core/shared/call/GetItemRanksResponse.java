//  
//  GetItemRanksResponse.java
//  storedata
//
//  Created by William Shakour on September 10, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetItemRanksResponse extends Response {
	public List<Rank> ranks;
	public Pager pager;
	public Item item;
	public List<Date> outOfLeaderboardDates;

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
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		JsonElement jsonItem = item == null ? JsonNull.INSTANCE : item.toJson();
		object.add("item", jsonItem);
		JsonElement jsonOutOfLeaderboardDates = JsonNull.INSTANCE;
		if (outOfLeaderboardDates != null) {
			jsonOutOfLeaderboardDates = new JsonArray();
			for (int i = 0; i < outOfLeaderboardDates.size(); i++) {
				JsonElement jsonOutOfTop200DatesItem = outOfLeaderboardDates.get(i) == null ? JsonNull.INSTANCE : new JsonPrimitive(outOfLeaderboardDates
						.get(i).getTime());
				((JsonArray) jsonOutOfLeaderboardDates).add(jsonOutOfTop200DatesItem);
			}
		}
		object.add("outOfLeaderboardDates", jsonOutOfLeaderboardDates);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("ranks")) {
			JsonElement jsonRanks = jsonObject.get("ranks");
			if (jsonRanks != null) {
				ranks = new ArrayList<Rank>();
				Rank item = null;
				for (int i = 0; i < jsonRanks.getAsJsonArray().size(); i++) {
					if (jsonRanks.getAsJsonArray().get(i) != null) {
						(item = new Rank()).fromJson(jsonRanks.getAsJsonArray().get(i).getAsJsonObject());
						ranks.add(item);
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
		if (jsonObject.has("item")) {
			JsonElement jsonItem = jsonObject.get("item");
			if (jsonItem != null) {
				item = new Item();
				item.fromJson(jsonItem.getAsJsonObject());
			}
		}

		if (jsonObject.has("outOfLeaderboardDates")) {
			JsonElement jsonOutOfLeaderboardDates = jsonObject.get("outOfLeaderboardDates");
			if (jsonOutOfLeaderboardDates != null) {
				outOfLeaderboardDates = new ArrayList<Date>();
				Date item = null;
				for (int i = 0; i < jsonOutOfLeaderboardDates.getAsJsonArray().size(); i++) {
					if (jsonOutOfLeaderboardDates.getAsJsonArray().get(i) != null) {
						item = new Date(jsonOutOfLeaderboardDates.getAsJsonArray().get(i).getAsLong());
						outOfLeaderboardDates.add(item);
					}
				}
			}
		}
	}

	public GetItemRanksResponse ranks(List<Rank> ranks) {
		this.ranks = ranks;
		return this;
	}

	public GetItemRanksResponse pager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public GetItemRanksResponse item(Item item) {
		this.item = item;
		return this;
	}
}