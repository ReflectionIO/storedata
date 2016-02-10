//
//  DataAccount.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DataAccount extends DataType {
	public DataSource							source;
	public List<Item>							items;
	public User										user;
	public List<DataAccountFetch>	fetches;
	public String									username;
	public String									password;
	public String									properties;
	public String									developerName;
	public String									active;

	public String									accountId;
	public String									vendorId;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonSource = source == null ? JsonNull.INSTANCE : source.toJson();
		object.add("source", jsonSource);
		JsonElement jsonItems = JsonNull.INSTANCE;
		if (items != null) {
			jsonItems = new JsonArray();
			for (int i = 0; i < items.size(); i++) {
				JsonElement jsonItemsItem = items.get(i) == null ? JsonNull.INSTANCE : items.get(i).toJson();
				((JsonArray) jsonItems).add(jsonItemsItem);
			}
		}
		object.add("items", jsonItems);
		JsonElement jsonUser = user == null ? JsonNull.INSTANCE : user.toJson();
		object.add("user", jsonUser);
		JsonElement jsonDataAccountFetches = JsonNull.INSTANCE;
		if (fetches != null) {
			jsonDataAccountFetches = new JsonArray();
			for (int i = 0; i < fetches.size(); i++) {
				JsonElement jsonDataAccountFetchesItem = fetches.get(i) == null ? JsonNull.INSTANCE : fetches.get(i).toJson();
				((JsonArray) jsonDataAccountFetches).add(jsonDataAccountFetchesItem);
			}
		}
		object.add("dataAccountFetches", jsonDataAccountFetches);
		JsonElement jsonUsername = username == null ? JsonNull.INSTANCE : new JsonPrimitive(username);
		object.add("username", jsonUsername);
		JsonElement jsonPassword = password == null ? JsonNull.INSTANCE : new JsonPrimitive(password);
		object.add("password", jsonPassword);
		JsonElement jsonProperties = properties == null ? JsonNull.INSTANCE : new JsonPrimitive(properties);
		object.add("properties", jsonProperties);
		JsonElement jsonDeveloperName = developerName == null ? JsonNull.INSTANCE : new JsonPrimitive(developerName);
		object.add("developerName", jsonDeveloperName);
		JsonElement jsonActive = active == null ? JsonNull.INSTANCE : new JsonPrimitive(active);
		object.add("active", jsonActive);
		JsonElement jsonAccountId = accountId == null ? JsonNull.INSTANCE : new JsonPrimitive(accountId);
		object.add("accountId", jsonAccountId);
		JsonElement jsonVendorId = vendorId == null ? JsonNull.INSTANCE : new JsonPrimitive(vendorId);
		object.add("vendorId", jsonVendorId);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("source")) {
			JsonElement jsonSource = jsonObject.get("source");
			if (jsonSource != null) {
				source = new DataSource();
				source.fromJson(jsonSource.getAsJsonObject());
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
		if (jsonObject.has("user")) {
			JsonElement jsonUser = jsonObject.get("user");
			if (jsonUser != null) {
				user = new User();
				user.fromJson(jsonUser.getAsJsonObject());
			}
		}
		if (jsonObject.has("dataAccountFetches")) {
			JsonElement jsonDataAccountFetches = jsonObject.get("dataAccountFetches");
			if (jsonDataAccountFetches != null) {
				fetches = new ArrayList<DataAccountFetch>();
				DataAccountFetch dataAccountFetch = null;
				for (int i = 0; i < jsonDataAccountFetches.getAsJsonArray().size(); i++) {
					if (jsonDataAccountFetches.getAsJsonArray().get(i) != null) {
						(dataAccountFetch = new DataAccountFetch()).fromJson(jsonDataAccountFetches.getAsJsonArray().get(i).getAsJsonObject());
						fetches.add(dataAccountFetch);
					}
				}
			}
		}
		if (jsonObject.has("username")) {
			JsonElement jsonUsername = jsonObject.get("username");
			if (jsonUsername != null) {
				username = jsonUsername.getAsString();
			}
		}
		if (jsonObject.has("password")) {
			JsonElement jsonPassword = jsonObject.get("password");
			if (jsonPassword != null) {
				password = jsonPassword.getAsString();
			}
		}
		if (jsonObject.has("properties")) {
			JsonElement jsonProperties = jsonObject.get("properties");
			if (jsonProperties != null) {
				properties = jsonProperties.getAsString();
			}
		}
		if (jsonObject.has("developerName")) {
			JsonElement jsonDeveloperName = jsonObject.get("developerName");
			if (jsonDeveloperName != null) {
				developerName = jsonDeveloperName.getAsString();
			}
		}
		if (jsonObject.has("active")) {
			JsonElement jsonActive = jsonObject.get("active");
			if (jsonActive != null) {
				active = jsonActive.getAsString();
			}
		}
		if (jsonObject.has("accountId")) {
			JsonElement jsonAccountId = jsonObject.get("accountId");
			if (jsonAccountId != null) {
				accountId = jsonAccountId.getAsString();
			}
		}
		if (jsonObject.has("vendorId")) {
			JsonElement jsonVendorId = jsonObject.get("vendorId");
			if (jsonVendorId != null) {
				vendorId = jsonVendorId.getAsString();
			}
		}
	}

	public DataAccount source(DataSource source) {
		this.source = source;
		return this;
	}

	public DataAccount items(List<Item> items) {
		this.items = items;
		return this;
	}

	public DataAccount username(String username) {
		this.username = username;
		return this;
	}

	public DataAccount password(String password) {
		this.password = password;
		return this;
	}

	public DataAccount properties(String properties) {
		this.properties = properties;
		return this;
	}

	public DataAccount developerName(String developerName) {
		this.developerName = developerName;
		return this;
	}

	public DataAccount active(String active) {
		this.active = active;
		return this;
	}

	public DataAccount accountId(String accountId) {
		this.accountId = accountId;
		return this;
	}

	public DataAccount vendorId(String vendorId) {
		this.vendorId = vendorId;
		return this;
	}
}
