//  
//  GetSalesRequest.java
//  reflection.io
//
//  Created by William Shakour on April 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GetSalesRequest extends Request {

	public DataAccount linkedAccount;
	public Country country;
	public Category category;
	public Pager pager;
	public Date start;
	public Date end;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonLinkedAccount = linkedAccount == null ? JsonNull.INSTANCE : linkedAccount.toJson();
		object.add("linkedAccount", jsonLinkedAccount);
		JsonElement jsonCountry = country == null ? JsonNull.INSTANCE : country.toJson();
		object.add("country", jsonCountry);
		JsonElement jsonCategory = category == null ? JsonNull.INSTANCE : category.toJson();
		object.add("category", jsonCategory);
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
		if (jsonObject.has("linkedAccount")) {
			JsonElement jsonLinkedAccount = jsonObject.get("linkedAccount");
			if (jsonLinkedAccount != null) {
				linkedAccount = new DataAccount();
				linkedAccount.fromJson(jsonLinkedAccount.getAsJsonObject());
			}
		}
		if (jsonObject.has("country")) {
			JsonElement jsonCountry = jsonObject.get("country");
			if (jsonCountry != null) {
				country = new Country();
				country.fromJson(jsonCountry.getAsJsonObject());
			}
		}
		if (jsonObject.has("category")) {
			JsonElement jsonCategory = jsonObject.get("category");
			if (jsonCategory != null) {
				category = new Category();
				category.fromJson(jsonCategory.getAsJsonObject());
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

	public GetSalesRequest linkedAccount(DataAccount linkedAccount) {
		this.linkedAccount = linkedAccount;
		return this;
	}

	public GetSalesRequest country(Country country) {
		this.country = country;
		return this;
	}

	public GetSalesRequest category(Category category) {
		this.category = category;
		return this;
	}

	public GetSalesRequest pager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public GetSalesRequest start(Date start) {
		this.start = start;
		return this;
	}

	public GetSalesRequest end(Date end) {
		this.end = end;
		return this;
	}
}