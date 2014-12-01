//  
//  LookupApplicationResponse.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.lookup.shared.call;

import io.reflection.app.datatypes.shared.Application;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.reflection.app.api.shared.datatypes.Response;

public class LookupApplicationResponse extends Response {
	public List<Application> applications;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonApplications = JsonNull.INSTANCE;
		if (applications != null) {
			jsonApplications = new JsonArray();
			for (int i = 0; i < applications.size(); i++) {
				JsonElement jsonApplicationsItem = applications.get(i) == null ? JsonNull.INSTANCE : applications.get(i).toJson();
				((JsonArray) jsonApplications).add(jsonApplicationsItem);
			}
		}
		object.add("applications", jsonApplications);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("applications")) {
			JsonElement jsonApplications = jsonObject.get("applications");
			if (jsonApplications != null) {
				applications = new ArrayList<Application>();
				Application item = null;
				for (int i = 0; i < jsonApplications.getAsJsonArray().size(); i++) {
					if (jsonApplications.getAsJsonArray().get(i) != null) {
						(item = new Application()).fromJson(jsonApplications.getAsJsonArray().get(i).getAsJsonObject());
						applications.add(item);
					}
				}
			}
		}
	}

	public LookupApplicationResponse applications(List<Application> applications) {
		this.applications = applications;
		return this;
	}
}