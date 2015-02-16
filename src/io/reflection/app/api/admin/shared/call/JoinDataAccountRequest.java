//  
//  JoinDataAccountRequest.java
//  reflection.io
//
//  Created by William Shakour on October 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class JoinDataAccountRequest extends Request {
	public DataAccount dataAccount;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataAccount = dataAccount == null ? JsonNull.INSTANCE : dataAccount.toJson();
		object.add("dataAccount", jsonDataAccount);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("dataAccount")) {
			JsonElement jsonDataAccount = jsonObject.get("dataAccount");
			if (jsonDataAccount != null) {
				dataAccount = new DataAccount();
				dataAccount.fromJson(jsonDataAccount.getAsJsonObject());
			}
		}
	}

	public JoinDataAccountRequest dataAccount(DataAccount dataAccount) {
		this.dataAccount = dataAccount;
		return this;
	}
}