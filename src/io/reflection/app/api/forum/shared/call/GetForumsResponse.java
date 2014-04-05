//  
//  GetForumsResponse.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Forum;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.willshex.gson.json.service.shared.Response;

public class GetForumsResponse extends Response {
	public List<Forum> forums;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonForums = JsonNull.INSTANCE;
		if (forums != null) {
			jsonForums = new JsonArray();
			for (int i = 0; i < forums.size(); i++) {
				JsonElement jsonForumsItem = forums.get(i) == null ? JsonNull.INSTANCE : forums.get(i).toJson();
				((JsonArray) jsonForums).add(jsonForumsItem);
			}
		}
		object.add("forums", jsonForums);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("forums")) {
			JsonElement jsonForums = jsonObject.get("forums");
			if (jsonForums != null) {
				forums = new ArrayList<Forum>();
				Forum item = null;
				for (int i = 0; i < jsonForums.getAsJsonArray().size(); i++) {
					if (jsonForums.getAsJsonArray().get(i) != null) {
						(item = new Forum()).fromJson(jsonForums.getAsJsonArray().get(i).getAsJsonObject());
						forums.add(item);
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