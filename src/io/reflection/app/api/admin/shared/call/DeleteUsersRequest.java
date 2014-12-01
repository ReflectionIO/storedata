//  
//  DeleteUsersRequest.java
//  reflection.io
//
//  Created by William Shakour on October 30, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DeleteUsersRequest extends Request {
	public List<User> users;
	public Boolean allTestUsers;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonUsers = JsonNull.INSTANCE;
		if (users != null) {
			jsonUsers = new JsonArray();
			for (int i = 0; i < users.size(); i++) {
				JsonElement jsonUsersItem = users.get(i) == null ? JsonNull.INSTANCE : users.get(i).toJson();
				((JsonArray) jsonUsers).add(jsonUsersItem);
			}
		}
		object.add("users", jsonUsers);
		JsonElement jsonAllTestUsers = allTestUsers == null ? JsonNull.INSTANCE : new JsonPrimitive(allTestUsers);
		object.add("allTestUsers", jsonAllTestUsers);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("users")) {
			JsonElement jsonUsers = jsonObject.get("users");
			if (jsonUsers != null) {
				users = new ArrayList<User>();
				User item = null;
				for (int i = 0; i < jsonUsers.getAsJsonArray().size(); i++) {
					if (jsonUsers.getAsJsonArray().get(i) != null) {
						(item = new User()).fromJson(jsonUsers.getAsJsonArray().get(i).getAsJsonObject());
						users.add(item);
					}
				}
			}
		}

		if (jsonObject.has("allTestUsers")) {
			JsonElement jsonAllTestUsers = jsonObject.get("allTestUsers");
			if (jsonAllTestUsers != null) {
				allTestUsers = Boolean.valueOf(jsonAllTestUsers.getAsBoolean());
			}
		}
	}

	public DeleteUsersRequest users(List<User> users) {
		this.users = users;
		return this;
	}

	public DeleteUsersRequest allTestUsers(boolean allTestUsers) {
		this.allTestUsers = allTestUsers;
		return this;
	}
}