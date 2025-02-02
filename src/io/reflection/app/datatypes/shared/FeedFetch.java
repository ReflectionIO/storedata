/**
 * 
 */
package io.reflection.app.datatypes.shared;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@Entity
public class FeedFetch extends DataType {

	public String country;

	public String data;

	@Index public Date date;

	@Index public String store;

	public Integer part = Integer.valueOf(0);

	@AlsoLoad("totalparts") public Integer totalParts = Integer.valueOf(0);

	@Index public String type;

	public Long code;

	public FeedFetchStatusType status;
	public Category category;

	public Boolean ingested;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonCategory = category == null ? JsonNull.INSTANCE : category.toJson();
		object.add("category", jsonCategory);
		JsonElement jsonCountry = country == null ? JsonNull.INSTANCE : new JsonPrimitive(country);
		object.add("country", jsonCountry);
		JsonElement jsonData = data == null ? JsonNull.INSTANCE : new JsonPrimitive(data);
		object.add("data", jsonData);
		JsonElement jsonDate = date == null ? JsonNull.INSTANCE : new JsonPrimitive(date.getTime());
		object.add("date", jsonDate);
		JsonElement jsonIngested = ingested == null ? JsonNull.INSTANCE : new JsonPrimitive(ingested);
		object.add("ingested", jsonIngested);
		JsonElement jsonStore = store == null ? JsonNull.INSTANCE : new JsonPrimitive(store);
		object.add("store", jsonStore);
		JsonElement jsonPart = part == null ? JsonNull.INSTANCE : new JsonPrimitive(part);
		object.add("part", jsonPart);
		JsonElement jsonTotalParts = totalParts == null ? JsonNull.INSTANCE : new JsonPrimitive(totalParts);
		object.add("totalParts", jsonTotalParts);
		JsonElement jsonType = type == null ? JsonNull.INSTANCE : new JsonPrimitive(type);
		object.add("type", jsonType);
		JsonElement jsonCode = code == null ? JsonNull.INSTANCE : new JsonPrimitive(code);
		object.add("code", jsonCode);
		JsonElement jsonStatus = status == null ? JsonNull.INSTANCE : new JsonPrimitive(status.toString());
		object.add("status", jsonStatus);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("category")) {
			JsonElement jsonCategory = jsonObject.get("category");
			if (jsonCategory != null) {
				category = new Category();
				category.fromJson(jsonCategory.getAsJsonObject());
			}
		}
		if (jsonObject.has("country")) {
			JsonElement jsonCountry = jsonObject.get("country");
			if (jsonCountry != null) {
				country = jsonCountry.getAsString();
			}
		}
		if (jsonObject.has("data")) {
			JsonElement jsonData = jsonObject.get("data");
			if (jsonData != null) {
				data = jsonData.getAsString();
			}
		}
		if (jsonObject.has("date")) {
			JsonElement jsonDate = jsonObject.get("date");
			if (jsonDate != null) {
				date = new Date(jsonDate.getAsLong());
			}
		}
		if (jsonObject.has("ingested")) {
			JsonElement jsonIngested = jsonObject.get("ingested");
			if (jsonIngested != null) {
				ingested = Boolean.valueOf(jsonIngested.getAsBoolean());
			}
		}
		if (jsonObject.has("store")) {
			JsonElement jsonStore = jsonObject.get("store");
			if (jsonStore != null) {
				store = jsonStore.getAsString();
			}
		}
		if (jsonObject.has("part")) {
			JsonElement jsonPart = jsonObject.get("part");
			if (jsonPart != null) {
				part = Integer.valueOf(jsonPart.getAsInt());
			}
		}
		if (jsonObject.has("totalParts")) {
			JsonElement jsonTotalParts = jsonObject.get("totalParts");
			if (jsonTotalParts != null) {
				totalParts = Integer.valueOf(jsonTotalParts.getAsInt());
			}
		}
		if (jsonObject.has("type")) {
			JsonElement jsonType = jsonObject.get("type");
			if (jsonType != null) {
				type = jsonType.getAsString();
			}
		}
		if (jsonObject.has("code")) {
			JsonElement jsonCode = jsonObject.get("code");
			if (jsonCode != null) {
				code = Long.valueOf(jsonCode.getAsLong());
			}
		}
		if (jsonObject.has("status")) {
			JsonElement jsonStatus = jsonObject.get("status");
			if (jsonStatus != null) {
				status = FeedFetchStatusType.fromString(jsonStatus.getAsString());
			}
		}
	}

	public FeedFetch category(Category category) {
		this.category = category;
		return this;
	}

	public FeedFetch country(String country) {
		this.country = country;
		return this;
	}

	public FeedFetch data(String data) {
		this.data = data;
		return this;
	}

	public FeedFetch date(Date date) {
		this.date = date;
		return this;
	}

	public FeedFetch ingested(Boolean ingested) {
		this.ingested = ingested;
		return this;
	}

	public FeedFetch store(String store) {
		this.store = store;
		return this;
	}

	public FeedFetch part(Integer part) {
		this.part = part;
		return this;
	}

	public FeedFetch totalParts(Integer totalParts) {
		this.totalParts = totalParts;
		return this;
	}

	public FeedFetch type(String type) {
		this.type = type;
		return this;
	}

	public FeedFetch code(Long code) {
		this.code = code;
		return this;
	}

	public FeedFetch status(FeedFetchStatusType status) {
		this.status = status;
		return this;
	}
}