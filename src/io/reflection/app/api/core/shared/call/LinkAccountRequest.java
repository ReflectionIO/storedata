//  
//  LinkAccountRequest.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.shared.datatypes.DataSource;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LinkAccountRequest extends Request {
	public DataSource source;
	public List<String> keys;
	public List<String> values;
	public String username;
	public String password;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonSource = source == null ? JsonNull.INSTANCE : source.toJson();
		object.add("source", jsonSource);
		JsonElement jsonKeys = JsonNull.INSTANCE;
		if (keys != null) {
			jsonKeys = new JsonArray();
			for (int i = 0; i < keys.size(); i++) {
				JsonElement jsonKeysItem = keys.get(i) == null ? JsonNull.INSTANCE : new JsonPrimitive(keys.get(i));
				((JsonArray) jsonKeys).add(jsonKeysItem);
			}
		}
		object.add("keys", jsonKeys);
		JsonElement jsonValues = JsonNull.INSTANCE;
		if (values != null) {
			jsonValues = new JsonArray();
			for (int i = 0; i < values.size(); i++) {
				JsonElement jsonValuesItem = values.get(i) == null ? JsonNull.INSTANCE : new JsonPrimitive(values.get(i));
				((JsonArray) jsonValues).add(jsonValuesItem);
			}
		}
		object.add("values", jsonValues);
		JsonElement jsonUsername = username == null ? JsonNull.INSTANCE : new JsonPrimitive(username);
		object.add("username", jsonUsername);
		JsonElement jsonPassword = password == null ? JsonNull.INSTANCE : new JsonPrimitive(password);
		object.add("password", jsonPassword);
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
		if (jsonObject.has("keys")) {
			JsonElement jsonKeys = jsonObject.get("keys");
			if (jsonKeys != null) {
				keys = new ArrayList<String>();
				String item = null;
				for (int i = 0; i < jsonKeys.getAsJsonArray().size(); i++) {
					if (jsonKeys.getAsJsonArray().get(i) != null) {
						item = jsonKeys.getAsJsonArray().get(i).getAsString();
						keys.add(item);
					}
				}
			}
		}

		if (jsonObject.has("values")) {
			JsonElement jsonValues = jsonObject.get("values");
			if (jsonValues != null) {
				values = new ArrayList<String>();
				String item = null;
				for (int i = 0; i < jsonValues.getAsJsonArray().size(); i++) {
					if (jsonValues.getAsJsonArray().get(i) != null) {
						item = jsonValues.getAsJsonArray().get(i).getAsString();
						values.add(item);
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
	}
}