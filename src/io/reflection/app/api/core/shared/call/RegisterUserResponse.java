//  
//  RegisterUserResponse.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class RegisterUserResponse extends Response {
	public User registeredUser;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonRegisteredUser = registeredUser == null ? JsonNull.INSTANCE : registeredUser.toJson();
		object.add("registeredUser", jsonRegisteredUser);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("registeredUser")) {
			JsonElement jsonRegisteredUser = jsonObject.get("registeredUser");
			if (jsonRegisteredUser != null) {
				registeredUser = new User();
				registeredUser.fromJson(jsonRegisteredUser.getAsJsonObject());
			}
		}
	}

	public RegisterUserResponse user(User user) {
		this.registeredUser = user;
		return this;
	}
}