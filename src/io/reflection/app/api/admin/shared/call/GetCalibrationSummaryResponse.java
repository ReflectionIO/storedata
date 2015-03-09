//  
//  GetCalibrationSummaryResponse.java
//  reflection.io
//
//  Created by William Shakour on March 8, 2015.
//  Copyrights © 2015 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2015 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.CalibrationSummary;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.ListPropertyType;
import io.reflection.app.datatypes.shared.ListTypeType;
import io.reflection.app.datatypes.shared.Store;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetCalibrationSummaryResponse extends Response {
	public ListTypeType listType;
	public Store store;
	public List<Item> items;
	public ListPropertyType listProperty;
	public FormType form;
	public Category category;
	public CalibrationSummary calibrationSummary;
	public Country country;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonListType = listType == null ? JsonNull.INSTANCE : new JsonPrimitive(listType.toString());
		object.add("listType", jsonListType);
		JsonElement jsonStore = store == null ? JsonNull.INSTANCE : store.toJson();
		object.add("store", jsonStore);
		JsonElement jsonItems = JsonNull.INSTANCE;
		if (items != null) {
			jsonItems = new JsonArray();
			for (int i = 0; i < items.size(); i++) {
				JsonElement jsonItemsItem = items.get(i) == null ? JsonNull.INSTANCE : items.get(i).toJson();
				((JsonArray) jsonItems).add(jsonItemsItem);
			}
		}
		object.add("items", jsonItems);
		JsonElement jsonListProperty = listProperty == null ? JsonNull.INSTANCE : new JsonPrimitive(listProperty.toString());
		object.add("listProperty", jsonListProperty);
		JsonElement jsonForm = form == null ? JsonNull.INSTANCE : new JsonPrimitive(form.toString());
		object.add("form", jsonForm);
		JsonElement jsonCategory = category == null ? JsonNull.INSTANCE : category.toJson();
		object.add("category", jsonCategory);
		JsonElement jsonCalibrationSummary = calibrationSummary == null ? JsonNull.INSTANCE : calibrationSummary.toJson();
		object.add("calibrationSummary", jsonCalibrationSummary);
		JsonElement jsonCountry = country == null ? JsonNull.INSTANCE : country.toJson();
		object.add("country", jsonCountry);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("listType")) {
			JsonElement jsonListType = jsonObject.get("listType");
			if (jsonListType != null) {
				listType = ListTypeType.fromString(jsonListType.getAsString());
			}
		}
		if (jsonObject.has("store")) {
			JsonElement jsonStore = jsonObject.get("store");
			if (jsonStore != null) {
				store = new Store();
				store.fromJson(jsonStore.getAsJsonObject());
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

		if (jsonObject.has("listProperty")) {
			JsonElement jsonListProperty = jsonObject.get("listProperty");
			if (jsonListProperty != null) {
				listProperty = ListPropertyType.fromString(jsonListProperty.getAsString());
			}
		}
		if (jsonObject.has("form")) {
			JsonElement jsonForm = jsonObject.get("form");
			if (jsonForm != null) {
				form = FormType.fromString(jsonForm.getAsString());
			}
		}
		if (jsonObject.has("category")) {
			JsonElement jsonCategory = jsonObject.get("category");
			if (jsonCategory != null) {
				category = new Category();
				category.fromJson(jsonCategory.getAsJsonObject());
			}
		}
		if (jsonObject.has("calibrationSummary")) {
			JsonElement jsonCalibrationSummary = jsonObject.get("calibrationSummary");
			if (jsonCalibrationSummary != null) {
				calibrationSummary = new CalibrationSummary();
				calibrationSummary.fromJson(jsonCalibrationSummary.getAsJsonObject());
			}
		}
		if (jsonObject.has("country")) {
			JsonElement jsonCountry = jsonObject.get("country");
			if (jsonCountry != null) {
				country = new Country();
				country.fromJson(jsonCountry.getAsJsonObject());
			}
		}
	}

	public GetCalibrationSummaryResponse listType(ListTypeType listType) {
		this.listType = listType;
		return this;
	}

	public GetCalibrationSummaryResponse store(Store store) {
		this.store = store;
		return this;
	}

	public GetCalibrationSummaryResponse items(List<Item> items) {
		this.items = items;
		return this;
	}

	public GetCalibrationSummaryResponse listProperty(ListPropertyType listProperty) {
		this.listProperty = listProperty;
		return this;
	}

	public GetCalibrationSummaryResponse form(FormType form) {
		this.form = form;
		return this;
	}

	public GetCalibrationSummaryResponse category(Category category) {
		this.category = category;
		return this;
	}

	public GetCalibrationSummaryResponse calibrationSummary(CalibrationSummary calibrationSummary) {
		this.calibrationSummary = calibrationSummary;
		return this;
	}

	public GetCalibrationSummaryResponse country(Country country) {
		this.country = country;
		return this;
	}
}