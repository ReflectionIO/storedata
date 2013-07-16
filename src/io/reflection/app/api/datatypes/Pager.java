//  
//  Pager.java
//  storedata
//
//  Created by William Shakour on 03 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.datatypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.willshex.gson.json.Jsonable;

public class Pager extends Jsonable {
	public Long start;
	public Long count;
	public String sortBy;
	public SortDirectionType sortDirection;
	public Long totalCount;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonStart = start == null ? JsonNull.INSTANCE : new JsonPrimitive(start);
		object.add("start", jsonStart);
		JsonElement jsonCount = count == null ? JsonNull.INSTANCE : new JsonPrimitive(count);
		object.add("count", jsonCount);
		JsonElement jsonSortBy = sortBy == null ? JsonNull.INSTANCE : new JsonPrimitive(sortBy);
		object.add("sortBy", jsonSortBy);
		JsonElement jsonSortDirection = sortDirection == null ? JsonNull.INSTANCE : new JsonPrimitive(sortDirection.toString());
		object.add("sortDirection", jsonSortDirection);
		JsonElement jsonTotalCount = totalCount == null ? JsonNull.INSTANCE : new JsonPrimitive(totalCount);
		object.add("totalCount", jsonTotalCount);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("start")) {
			JsonElement jsonStart = jsonObject.get("start");
			if (jsonStart != null) {
				start = Long.valueOf(jsonStart.getAsLong());
			}
		}
		if (jsonObject.has("count")) {
			JsonElement jsonCount = jsonObject.get("count");
			if (jsonCount != null) {
				count = Long.valueOf(jsonCount.getAsLong());
			}
		}
		if (jsonObject.has("sortBy")) {
			JsonElement jsonSortBy = jsonObject.get("sortBy");
			if (jsonSortBy != null) {
				sortBy = jsonSortBy.getAsString();
			}
		}
		if (jsonObject.has("sortDirection")) {
			JsonElement jsonSortDirection = jsonObject.get("sortDirection");
			if (jsonSortDirection != null) {
				sortDirection = SortDirectionType.fromString(jsonSortDirection.getAsString());
			}
		}
		if (jsonObject.has("totalCount")) {
			JsonElement jsonTotalCount = jsonObject.get("totalCount");
			if (jsonTotalCount != null) {
				totalCount = Long.valueOf(jsonTotalCount.getAsLong());
			}
		}
	}
}