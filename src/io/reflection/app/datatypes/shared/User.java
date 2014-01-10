//  
//  User.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class User extends DataType {
	public List<Permission> permissions;
	public List<Role> roles;
	public String forename;
	public String surname;
	public String username;
	public String avatar;
	public String company;
	public String password;
	public Date lastLoggedIn;
	public String verified;
	public Date expires;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonPermissions = JsonNull.INSTANCE;
		if (permissions != null) {
			jsonPermissions = new JsonArray();
			for (int i = 0; i < permissions.size(); i++) {
				JsonElement jsonPermissionsItem = permissions.get(i) == null ? JsonNull.INSTANCE : permissions.get(i).toJson();
				((JsonArray) jsonPermissions).add(jsonPermissionsItem);
			}
		}
		object.add("permissions", jsonPermissions);
		JsonElement jsonRoles = JsonNull.INSTANCE;
		if (roles != null) {
			jsonRoles = new JsonArray();
			for (int i = 0; i < roles.size(); i++) {
				JsonElement jsonRolesItem = roles.get(i) == null ? JsonNull.INSTANCE : roles.get(i).toJson();
				((JsonArray) jsonRoles).add(jsonRolesItem);
			}
		}
		object.add("roles", jsonRoles);
		JsonElement jsonForename = forename == null ? JsonNull.INSTANCE : new JsonPrimitive(forename);
		object.add("forename", jsonForename);
		JsonElement jsonSurname = surname == null ? JsonNull.INSTANCE : new JsonPrimitive(surname);
		object.add("surname", jsonSurname);
		JsonElement jsonUsername = username == null ? JsonNull.INSTANCE : new JsonPrimitive(username);
		object.add("username", jsonUsername);
		JsonElement jsonAvatar = avatar == null ? JsonNull.INSTANCE : new JsonPrimitive(avatar);
		object.add("avatar", jsonAvatar);
		JsonElement jsonCompany = company == null ? JsonNull.INSTANCE : new JsonPrimitive(company);
		object.add("company", jsonCompany);
		JsonElement jsonPassword = password == null ? JsonNull.INSTANCE : new JsonPrimitive(password);
		object.add("password", jsonPassword);
		JsonElement jsonLastLoggedIn = lastLoggedIn == null ? JsonNull.INSTANCE : new JsonPrimitive(lastLoggedIn.getTime());
		object.add("lastLoggedIn", jsonLastLoggedIn);
		JsonElement jsonVerified = verified == null ? JsonNull.INSTANCE : new JsonPrimitive(verified);
		object.add("verified", jsonVerified);
		JsonElement jsonExpires = expires == null ? JsonNull.INSTANCE : new JsonPrimitive(expires.getTime());
		object.add("expires", jsonExpires);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("permissions")) {
			JsonElement jsonPermissions = jsonObject.get("permissions");
			if (jsonPermissions != null) {
				permissions = new ArrayList<Permission>();
				Permission item = null;
				for (int i = 0; i < jsonPermissions.getAsJsonArray().size(); i++) {
					if (jsonPermissions.getAsJsonArray().get(i) != null) {
						(item = new Permission()).fromJson(jsonPermissions.getAsJsonArray().get(i).getAsJsonObject());
						permissions.add(item);
					}
				}
			}
		}

		if (jsonObject.has("roles")) {
			JsonElement jsonRoles = jsonObject.get("roles");
			if (jsonRoles != null) {
				roles = new ArrayList<Role>();
				Role item = null;
				for (int i = 0; i < jsonRoles.getAsJsonArray().size(); i++) {
					if (jsonRoles.getAsJsonArray().get(i) != null) {
						(item = new Role()).fromJson(jsonRoles.getAsJsonArray().get(i).getAsJsonObject());
						roles.add(item);
					}
				}
			}
		}

		if (jsonObject.has("forename")) {
			JsonElement jsonForename = jsonObject.get("forename");
			if (jsonForename != null) {
				forename = jsonForename.getAsString();
			}
		}
		if (jsonObject.has("surname")) {
			JsonElement jsonSurname = jsonObject.get("surname");
			if (jsonSurname != null) {
				surname = jsonSurname.getAsString();
			}
		}
		if (jsonObject.has("username")) {
			JsonElement jsonUsername = jsonObject.get("username");
			if (jsonUsername != null) {
				username = jsonUsername.getAsString();
			}
		}
		if (jsonObject.has("avatar")) {
			JsonElement jsonAvatar = jsonObject.get("avatar");
			if (jsonAvatar != null) {
				avatar = jsonAvatar.getAsString();
			}
		}
		if (jsonObject.has("company")) {
			JsonElement jsonCompany = jsonObject.get("company");
			if (jsonCompany != null) {
				company = jsonCompany.getAsString();
			}
		}
		if (jsonObject.has("password")) {
			JsonElement jsonPassword = jsonObject.get("password");
			if (jsonPassword != null) {
				password = jsonPassword.getAsString();
			}
		}
		if (jsonObject.has("lastLoggedIn")) {
			JsonElement jsonLastLoggedIn = jsonObject.get("lastLoggedIn");
			if (jsonLastLoggedIn != null) {
				lastLoggedIn = new Date(jsonLastLoggedIn.getAsLong());
			}
		}
		if (jsonObject.has("verified")) {
			JsonElement jsonVerified = jsonObject.get("verified");
			if (jsonVerified != null) {
				verified = jsonVerified.getAsString();
			}
		}
		if (jsonObject.has("expires")) {
			JsonElement jsonExpires = jsonObject.get("expires");
			if (jsonExpires != null) {
				expires = new Date(jsonExpires.getAsLong());
			}
		}
	}
}