/**
 * 
 */
package com.spacehopperstudios.storedata.datatypes;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@Entity
@Cache
public class Item extends DataType {

	@Index
	public String externalId;
	@Index
	public String internalId;

	public String name;
	public Float price;

	@Index
	public String source;

	@Index
	public String type;

	public Date added;
	public String currency;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonExternalId = externalId == null ? JsonNull.INSTANCE : new JsonPrimitive(externalId);
		object.add("externalId", jsonExternalId);
		JsonElement jsonInternalId = internalId == null ? JsonNull.INSTANCE : new JsonPrimitive(internalId);
		object.add("internalId", jsonInternalId);
		JsonElement jsonName = name == null ? JsonNull.INSTANCE : new JsonPrimitive(name);
		object.add("name", jsonName);
		JsonElement jsonPrice = price == null ? JsonNull.INSTANCE : new JsonPrimitive(price);
		object.add("price", jsonPrice);
		JsonElement jsonSource = source == null ? JsonNull.INSTANCE : new JsonPrimitive(source);
		object.add("source", jsonSource);
		JsonElement jsonType = type == null ? JsonNull.INSTANCE : new JsonPrimitive(type);
		object.add("type", jsonType);
		JsonElement jsonAdded = added == null ? JsonNull.INSTANCE : new JsonPrimitive(added.getTime());
		object.add("added", jsonAdded);
		JsonElement jsonCurrency = currency == null ? JsonNull.INSTANCE : new JsonPrimitive(currency);
		object.add("currency", jsonCurrency);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("externalId")) {
			JsonElement jsonExternalId = jsonObject.get("externalId");
			if (jsonExternalId != null) {
				externalId = jsonExternalId.getAsString();
			}
		}
		if (jsonObject.has("internalId")) {
			JsonElement jsonInternalId = jsonObject.get("internalId");
			if (jsonInternalId != null) {
				internalId = jsonInternalId.getAsString();
			}
		}
		if (jsonObject.has("name")) {
			JsonElement jsonName = jsonObject.get("name");
			if (jsonName != null) {
				name = jsonName.getAsString();
			}
		}
		if (jsonObject.has("price")) {
			JsonElement jsonPrice = jsonObject.get("price");
			if (jsonPrice != null) {
				price = Float.valueOf(jsonPrice.getAsFloat());
			}
		}
		if (jsonObject.has("source")) {
			JsonElement jsonSource = jsonObject.get("source");
			if (jsonSource != null) {
				source = jsonSource.getAsString();
			}
		}
		if (jsonObject.has("type")) {
			JsonElement jsonType = jsonObject.get("type");
			if (jsonType != null) {
				type = jsonType.getAsString();
			}
		}
		if (jsonObject.has("added")) {
			JsonElement jsonAdded = jsonObject.get("added");
			if (jsonAdded != null) {
				added = new Date(jsonAdded.getAsLong());
			}
		}
		if (jsonObject.has("currency")) {
			JsonElement jsonCurrency = jsonObject.get("currency");
			if (jsonCurrency != null) {
				currency = jsonCurrency.getAsString();
			}
		}
	}

}
