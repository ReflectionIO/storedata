//  
//  GetSimpleModelRunsResponse.java
//  reflection.io
//
//  Created by William Shakour on October 20, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.SimpleModelRun;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetSimpleModelRunsResponse extends Response {
	public List<SimpleModelRun> simpleModelRuns;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonSimpleModelRuns = JsonNull.INSTANCE;
		if (simpleModelRuns != null) {
			jsonSimpleModelRuns = new JsonArray();
			for (int i = 0; i < simpleModelRuns.size(); i++) {
				JsonElement jsonSimpleModelRunsItem = simpleModelRuns.get(i) == null ? JsonNull.INSTANCE : simpleModelRuns.get(i).toJson();
				((JsonArray) jsonSimpleModelRuns).add(jsonSimpleModelRunsItem);
			}
		}
		object.add("simpleModelRuns", jsonSimpleModelRuns);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("simpleModelRuns")) {
			JsonElement jsonSimpleModelRuns = jsonObject.get("simpleModelRuns");
			if (jsonSimpleModelRuns != null) {
				simpleModelRuns = new ArrayList<SimpleModelRun>();
				SimpleModelRun item = null;
				for (int i = 0; i < jsonSimpleModelRuns.getAsJsonArray().size(); i++) {
					if (jsonSimpleModelRuns.getAsJsonArray().get(i) != null) {
						(item = new SimpleModelRun()).fromJson(jsonSimpleModelRuns.getAsJsonArray().get(i).getAsJsonObject());
						simpleModelRuns.add(item);
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