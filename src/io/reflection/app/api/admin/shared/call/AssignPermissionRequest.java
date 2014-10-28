//  
//  AssignPermissionRequest.java
//  reflection.io
//
//  Created by William Shakour on September 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class AssignPermissionRequest extends Request {
	public User user;
	public Permission permission;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonUser = user == null ? JsonNull.INSTANCE : user.toJson();
		object.add("user", jsonUser);
		JsonElement jsonPermission = permission == null ? JsonNull.INSTANCE : permission.toJson();
		object.add("permission", jsonPermission);
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
		if (jsonObject.has("permission")) {
			JsonElement jsonPermission = jsonObject.get("permission");
			if (jsonPermission != null) {
				permission = new Permission();
				permission.fromJson(jsonPermission.getAsJsonObject());
			}
		}
	}
}