//  
//  GetDataAccountFetchesRequest.java
//  reflection.io
//
//  Created by William Shakour on October 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.DataAccount;

public class GetDataAccountFetchesRequest extends Request {
	public DataAccount dataAccount;
	public Pager pager;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataAccount = dataAccount == null ? JsonNull.INSTANCE : dataAccount.toJson();
		object.add("dataAccount", jsonDataAccount);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
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
		if (jsonObject.has("pager")) {
			JsonElement jsonPager = jsonObject.get("pager");
			if (jsonPager != null) {
				pager = new Pager();
				pager.fromJson(jsonPager.getAsJsonObject());
			}
		}
	}
}