//  
//  GetDataAccountsResponse.java
//  reflection.io
//
//  Created by William Shakour on October 7, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetDataAccountsResponse extends Response {
	public List<DataAccount> dataAccounts;
	public List<DataSource> dataSources;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataAccounts = JsonNull.INSTANCE;
		if (dataAccounts != null) {
			jsonDataAccounts = new JsonArray();
			for (int i = 0; i < dataAccounts.size(); i++) {
				JsonElement jsonDataAccountsItem = dataAccounts.get(i) == null ? JsonNull.INSTANCE : dataAccounts.get(i).toJson();
				((JsonArray) jsonDataAccounts).add(jsonDataAccountsItem);
			}
		}
		object.add("dataAccounts", jsonDataAccounts);
		JsonElement jsonDataSources = JsonNull.INSTANCE;
		if (dataSources != null) {
			jsonDataSources = new JsonArray();
			for (int i = 0; i < dataSources.size(); i++) {
				JsonElement jsonDataSourcesItem = dataSources.get(i) == null ? JsonNull.INSTANCE : dataSources.get(i).toJson();
				((JsonArray) jsonDataSources).add(jsonDataSourcesItem);
			}
		}
		object.add("dataSources", jsonDataSources);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("dataAccounts")) {
			JsonElement jsonDataAccounts = jsonObject.get("dataAccounts");
			if (jsonDataAccounts != null) {
				dataAccounts = new ArrayList<DataAccount>();
				DataAccount item = null;
				for (int i = 0; i < jsonDataAccounts.getAsJsonArray().size(); i++) {
					if (jsonDataAccounts.getAsJsonArray().get(i) != null) {
						(item = new DataAccount()).fromJson(jsonDataAccounts.getAsJsonArray().get(i).getAsJsonObject());
						dataAccounts.add(item);
					}
				}
			}
		}

		if (jsonObject.has("dataSources")) {
			JsonElement jsonDataSources = jsonObject.get("dataSources");
			if (jsonDataSources != null) {
				dataSources = new ArrayList<DataSource>();
				DataSource item = null;
				for (int i = 0; i < jsonDataSources.getAsJsonArray().size(); i++) {
					if (jsonDataSources.getAsJsonArray().get(i) != null) {
						(item = new DataSource()).fromJson(jsonDataSources.getAsJsonArray().get(i).getAsJsonObject());
						dataSources.add(item);
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