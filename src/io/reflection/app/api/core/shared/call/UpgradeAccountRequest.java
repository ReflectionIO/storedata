//  
//  UpgradeAccountRequest.java
//  reflection.io
//
//  Created by William Shakour on October 6, 2015.
//  Copyrights © 2015 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2015 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Role;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class UpgradeAccountRequest extends Request {
	public Role role;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonRole = role == null ? JsonNull.INSTANCE : role.toJson();
		object.add("role", jsonRole);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("role")) {
			JsonElement jsonRole = jsonObject.get("role");
			if (jsonRole != null) {
				role = new Role();
				role.fromJson(jsonRole.getAsJsonObject());
			}
		}
	}

	public UpgradeAccountRequest role(Role role) {
		this.role = role;
		return this;
	}

}