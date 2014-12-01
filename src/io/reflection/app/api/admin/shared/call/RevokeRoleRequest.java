//  
//  RevokeRoleRequest.java
//  reflection.io
//
//  Created by William Shakour on September 25, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class RevokeRoleRequest extends Request {
	public User user;
	public Role role;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonUser = user == null ? JsonNull.INSTANCE : user.toJson();
		object.add("user", jsonUser);
		JsonElement jsonRole = role == null ? JsonNull.INSTANCE : role.toJson();
		object.add("role", jsonRole);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("user")) {
			JsonElement jsonUser = jsonObject.get("user");
			if (jsonUser != null) {
				user = new User();
				user.fromJson(jsonUser.getAsJsonObject());
			}
		}
		if (jsonObject.has("role")) {
			JsonElement jsonRole = jsonObject.get("role");
			if (jsonRole != null) {
				role = new Role();
				role.fromJson(jsonRole.getAsJsonObject());
			}
		}
	}

	public RevokeRoleRequest user(User user) {
		this.user = user;
		return this;
	}

	public RevokeRoleRequest role(Role role) {
		this.role = role;
		return this;
	}
}