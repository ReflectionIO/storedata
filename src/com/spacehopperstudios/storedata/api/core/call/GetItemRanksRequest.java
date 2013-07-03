//  
//  GetItemRanksRequest.java
//  storedata
//
//  Created by William Shakour on 03 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.api.core.call;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.spacehopperstudios.storedata.api.datatypes.Pager;
import com.spacehopperstudios.storedata.datatypes.Item;
import com.willshex.gson.json.service.Request;

public class GetItemRanksRequest extends Request {
	public Item item;
	public Pager pager;
	public Date after;
	public Date before;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonItem = item == null ? JsonNull.INSTANCE : item.toJson();
		object.add("item", jsonItem);
		JsonElement jsonPager = pager == null ? JsonNull.INSTANCE : pager.toJson();
		object.add("pager", jsonPager);
		JsonElement jsonAfter = after == null ? JsonNull.INSTANCE : new JsonPrimitive(after.getTime());
		object.add("after", jsonAfter);
		JsonElement jsonBefore = before == null ? JsonNull.INSTANCE : new JsonPrimitive(before.getTime());
		object.add("before", jsonBefore);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("item")) {
			JsonElement jsonItem = jsonObject.get("item");
			if (jsonItem != null) {
				item = new Item();
				item.fromJson(jsonItem.getAsJsonObject());
			}
		}
		if (jsonObject.has("pager")) {
			JsonElement jsonPager = jsonObject.get("pager");
			if (jsonPager != null) {
				pager = new Pager();
				pager.fromJson(jsonPager.getAsJsonObject());
			}
		}
		if (jsonObject.has("after")) {
			JsonElement jsonAfter = jsonObject.get("after");
			if (jsonAfter != null) {
				after = new Date(jsonAfter.getAsLong());
			}
		}
		if (jsonObject.has("before")) {
			JsonElement jsonBefore = jsonObject.get("before");
			if (jsonBefore != null) {
				before = new Date(jsonBefore.getAsLong());
			}
		}
	}
}