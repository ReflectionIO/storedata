//  
//  GetDataAccountFetchesResponse.java
//  reflection.io
//
//  Created by William Shakour on October 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.DataAccountFetch;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetDataAccountFetchesResponse extends Response {
	public List<DataAccountFetch> dataAccountFetches;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataAccountFetches = JsonNull.INSTANCE;
		if (dataAccountFetches != null) {
			jsonDataAccountFetches = new JsonArray();
			for (int i = 0; i < dataAccountFetches.size(); i++) {
				JsonElement jsonDataAccountFetchesItem = dataAccountFetches.get(i) == null ? JsonNull.INSTANCE : dataAccountFetches.get(i).toJson();
				((JsonArray) jsonDataAccountFetches).add(jsonDataAccountFetchesItem);
			}
		}
		object.add("dataAccountFetches", jsonDataAccountFetches);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("dataAccountFetches")) {
			JsonElement jsonDataAccountFetches = jsonObject.get("dataAccountFetches");
			if (jsonDataAccountFetches != null) {
				dataAccountFetches = new ArrayList<DataAccountFetch>();
				DataAccountFetch item = null;
				for (int i = 0; i < jsonDataAccountFetches.getAsJsonArray().size(); i++) {
					if (jsonDataAccountFetches.getAsJsonArray().get(i) != null) {
						(item = new DataAccountFetch()).fromJson(jsonDataAccountFetches.getAsJsonArray().get(i).getAsJsonObject());
						dataAccountFetches.add(item);
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

	public GetDataAccountFetchesResponse dataAccountFetches(List<DataAccountFetch> dataAccountFetches) {
		this.dataAccountFetches = dataAccountFetches;
		return this;
	}

	public GetDataAccountFetchesResponse pager(Pager pager) {
		this.pager = pager;
		return this;
	}
}