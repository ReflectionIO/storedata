//  
//  TriggerIngestRequest.java
//  reflection.io
//
//  Created by William Shakour on October 23, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TriggerIngestRequest extends Request {
	public Country country;
	public Store store;
	public Category category;
	public List<String> listTypes;
	public Long code;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonCountry = country == null ? JsonNull.INSTANCE : country.toJson();
		object.add("country", jsonCountry);
		JsonElement jsonStore = store == null ? JsonNull.INSTANCE : store.toJson();
		object.add("store", jsonStore);
		JsonElement jsonCategory = category == null ? JsonNull.INSTANCE : category.toJson();
		object.add("category", jsonCategory);
		JsonElement jsonListTypes = JsonNull.INSTANCE;
		if (listTypes != null) {
			jsonListTypes = new JsonArray();
			for (int i = 0; i < listTypes.size(); i++) {
				JsonElement jsonListTypesItem = listTypes.get(i) == null ? JsonNull.INSTANCE : new JsonPrimitive(listTypes.get(i));
				((JsonArray) jsonListTypes).add(jsonListTypesItem);
			}
		}
		object.add("listTypes", jsonListTypes);
		JsonElement jsonCode = code == null ? JsonNull.INSTANCE : new JsonPrimitive(code);
		object.add("code", jsonCode);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("country")) {
			JsonElement jsonCountry = jsonObject.get("country");
			if (jsonCountry != null) {
				country = new Country();
				country.fromJson(jsonCountry.getAsJsonObject());
			}
		}
		if (jsonObject.has("store")) {
			JsonElement jsonStore = jsonObject.get("store");
			if (jsonStore != null) {
				store = new Store();
				store.fromJson(jsonStore.getAsJsonObject());
			}
		}
		if (jsonObject.has("category")) {
			JsonElement jsonCategory = jsonObject.get("category");
			if (jsonCategory != null) {
				category = new Category();
				category.fromJson(jsonCategory.getAsJsonObject());
			}
		}
		if (jsonObject.has("listTypes")) {
			JsonElement jsonListTypes = jsonObject.get("listTypes");
			if (jsonListTypes != null) {
				listTypes = new ArrayList<String>();
				String item = null;
				for (int i = 0; i < jsonListTypes.getAsJsonArray().size(); i++) {
					if (jsonListTypes.getAsJsonArray().get(i) != null) {
						item = jsonListTypes.getAsJsonArray().get(i).getAsString();
						listTypes.add(item);
					}
				}
			}
		}

		if (jsonObject.has("code")) {
			JsonElement jsonCode = jsonObject.get("code");
			if (jsonCode != null) {
				code = Long.valueOf(jsonCode.getAsLong());
			}
		}
	}

	public TriggerIngestRequest country(Country country) {
		this.country = country;
		return this;
	}

	public TriggerIngestRequest store(Store store) {
		this.store = store;
		return this;
	}

	public TriggerIngestRequest category(Category category) {
		this.category = category;
		return this;
	}

	public TriggerIngestRequest listTypes(List<String> listTypes) {
		this.listTypes = listTypes;
		return this;
	}

	public TriggerIngestRequest code(Long code) {
		this.code = code;
		return this;
	}
}