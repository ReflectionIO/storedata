//  
//  GetDataAccountFetchesRequest.java
//  reflection.io
//
//  Created by William Shakour on October 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.DataAccount;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetDataAccountFetchesRequest extends Request {
	public DataAccount dataAccount;
	public Pager pager;
	public Date start;
	public Date end;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataAccount = dataAccount == null ? JsonNull.INSTANCE : dataAccount.toJson();
		object.add("dataAccount", jsonDataAccount);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		JsonElement jsonStart = start == null ? JsonNull.INSTANCE : new JsonPrimitive(start.getTime());
		object.add("start", jsonStart);
		JsonElement jsonEnd = end == null ? JsonNull.INSTANCE : new JsonPrimitive(end.getTime());
		object.add("end", jsonEnd);
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
		if (jsonObject.has("start")) {
			JsonElement jsonStart = jsonObject.get("start");
			if (jsonStart != null) {
				start = new Date(jsonStart.getAsLong());
			}
		}
		if (jsonObject.has("end")) {
			JsonElement jsonEnd = jsonObject.get("end");
			if (jsonEnd != null) {
				end = new Date(jsonEnd.getAsLong());
			}
		}
	}

	public GetDataAccountFetchesRequest dataAccount(DataAccount dataAccount) {
		this.dataAccount = dataAccount;
		return this;
	}

	public GetDataAccountFetchesRequest pager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public GetDataAccountFetchesRequest start(Date start) {
		this.start = start;
		return this;
	}

	public GetDataAccountFetchesRequest end(Date end) {
		this.end = end;
		return this;
	}
}