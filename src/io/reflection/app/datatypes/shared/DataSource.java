//  
//  DataSource.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DataSource extends DataType {
	public String a3Code;
	public String name;
	public String url;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonA3Code = a3Code == null ? JsonNull.INSTANCE : new JsonPrimitive(a3Code);
		object.add("a3Code", jsonA3Code);
		JsonElement jsonName = name == null ? JsonNull.INSTANCE : new JsonPrimitive(name);
		object.add("name", jsonName);
		JsonElement jsonUrl = url == null ? JsonNull.INSTANCE : new JsonPrimitive(url);
		object.add("url", jsonUrl);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("a3Code")) {
			JsonElement jsonA3Code = jsonObject.get("a3Code");
			if (jsonA3Code != null) {
				a3Code = jsonA3Code.getAsString();
			}
		}
		if (jsonObject.has("name")) {
			JsonElement jsonName = jsonObject.get("name");
			if (jsonName != null) {
				name = jsonName.getAsString();
			}
		}
		if (jsonObject.has("url")) {
			JsonElement jsonUrl = jsonObject.get("url");
			if (jsonUrl != null) {
				url = jsonUrl.getAsString();
			}
		}
	}
}