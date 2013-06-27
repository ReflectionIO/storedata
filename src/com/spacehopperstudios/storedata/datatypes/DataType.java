//  
//  DataType.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.datatypes;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.willshex.gson.json.Jsonable;

/**
 * @author billy1380
 * 
 */
@Entity
public class DataType extends Jsonable {

	/**
	 * DataType Id
	 */
	@Id
	public Long id;

	/**
	 * created date
	 */
	public Date created;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonId = id == null ? JsonNull.INSTANCE : new JsonPrimitive(id.doubleValue());
		object.add("id", jsonId);
		JsonElement jsonCreated = created == null ? JsonNull.INSTANCE : new JsonPrimitive(created.getTime());
		object.add("created", jsonCreated);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("id")) {
			JsonElement jsonId = jsonObject.get("id");
			if (jsonId != null) {
				id = Long.valueOf(jsonId.getAsLong());
			}
		}
		if (jsonObject.has("created")) {
			JsonElement jsonCreated = jsonObject.get("created");
			if (jsonCreated != null) {
				created = new Date(jsonCreated.getAsLong());
			}
		}
	}
}
