//
//  JsonUtils.java
//  Scores
//
//  Created by William Shakour on August 16, 2011.
//  Copyright © 2011 WillShex Ltd. All rights reserved.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class JsonUtils {
	/**
	 * Clean Json method will remove null properties, empty collections, empty dictionaries/types
	 * 
	 * @param json
	 * @return
	 */
	public static String cleanJson(String json) {
		String cleaned = json;

		cleaned = cleaned.replaceAll("\"[a-zA-z]+[a-zA-Z0-9]*\":null", "");
		cleaned = cleaned.replaceAll(",,", ",");
		cleaned = cleaned.replaceAll(", ", ",");
		cleaned = cleaned.replaceAll("\\{,", "{");
		cleaned = cleaned.replaceAll(",\\}", "}");
		cleaned = cleaned.replaceAll("\\[,", "[");
		cleaned = cleaned.replaceAll(",\\]", "]");
		cleaned = cleaned.replaceAll(":\\{\\}", ":null");
		cleaned = cleaned.replaceAll(":\\{ \\}", ":null");
		cleaned = cleaned.replaceAll(":\\[\\]", ":[]");
		cleaned = cleaned.replaceAll(":\\[ \\]", ":{}");
		cleaned = cleaned.replaceAll("\\{\\}", "");
		cleaned = cleaned.replaceAll("\\[\\]", "");
		cleaned = cleaned.replaceAll(":,", ":null,");
		cleaned = cleaned.replaceAll(":\\}", ":null}");

		if (!json.equals(cleaned)) {
			cleaned = JsonUtils.cleanJson(cleaned);
		}

		if ("".equals(cleaned) || "{}".equals(cleaned) || "[]".equals(cleaned)) {
			cleaned = "null";
		}

		return cleaned;
	}

	public static JsonObject toJsonObject(String json) {
		JsonObject object = null;

		if (json != null) {
			JsonElement element = (new JsonParser()).parse(json);

			if (element != null) {
				object = element.getAsJsonObject();
			}
		}
		return object;
	}

	public static String fromJsonObject(JsonObject object) {
		String json = null;
		if (object != null) {
			json = object.toString();
		}
		return json;
	}
}