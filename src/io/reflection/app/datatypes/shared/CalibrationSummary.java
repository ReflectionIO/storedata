//  
//  CalibrationSummary.java
//  reflection.io
//
//  Created by William Shakour on March 7, 2015.
//  Copyrights © 2015 reflection.io. All rights reserved.
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

public class CalibrationSummary extends DataType {
	public FeedFetch feedFetch;
	public List<Rank> hits;
	public List<Rank> misses;
	public SimpleModelRun simpleModelRun;
	public Date salesSummaryDate;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonFeedFetch = feedFetch == null ? JsonNull.INSTANCE : feedFetch.toJson();
		object.add("feedFetch", jsonFeedFetch);
		JsonElement jsonHits = JsonNull.INSTANCE;
		if (hits != null) {
			jsonHits = new JsonArray();
			for (int i = 0; i < hits.size(); i++) {
				JsonElement jsonHitsItem = hits.get(i) == null ? JsonNull.INSTANCE : hits.get(i).toJson();
				((JsonArray) jsonHits).add(jsonHitsItem);
			}
		}
		object.add("hits", jsonHits);
		JsonElement jsonMisses = JsonNull.INSTANCE;
		if (misses != null) {
			jsonMisses = new JsonArray();
			for (int i = 0; i < misses.size(); i++) {
				JsonElement jsonMissesItem = misses.get(i) == null ? JsonNull.INSTANCE : misses.get(i).toJson();
				((JsonArray) jsonMisses).add(jsonMissesItem);
			}
		}
		object.add("misses", jsonMisses);
		JsonElement jsonSimpleModelRun = simpleModelRun == null ? JsonNull.INSTANCE : simpleModelRun.toJson();
		object.add("simpleModelRun", jsonSimpleModelRun);
		JsonElement jsonSalesSummaryDate = salesSummaryDate == null ? JsonNull.INSTANCE : new JsonPrimitive(salesSummaryDate.getTime());
		object.add("salesSummaryDate", jsonSalesSummaryDate);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("feedFetch")) {
			JsonElement jsonFeedFetch = jsonObject.get("feedFetch");
			if (jsonFeedFetch != null) {
				feedFetch = new FeedFetch();
				feedFetch.fromJson(jsonFeedFetch.getAsJsonObject());
			}
		}
		if (jsonObject.has("hits")) {
			JsonElement jsonHits = jsonObject.get("hits");
			if (jsonHits != null) {
				hits = new ArrayList<Rank>();
				Rank item = null;
				for (int i = 0; i < jsonHits.getAsJsonArray().size(); i++) {
					if (jsonHits.getAsJsonArray().get(i) != null) {
						(item = new Rank()).fromJson(jsonHits.getAsJsonArray().get(i).getAsJsonObject());
						hits.add(item);
					}
				}
			}
		}

		if (jsonObject.has("misses")) {
			JsonElement jsonMisses = jsonObject.get("misses");
			if (jsonMisses != null) {
				misses = new ArrayList<Rank>();
				Rank item = null;
				for (int i = 0; i < jsonMisses.getAsJsonArray().size(); i++) {
					if (jsonMisses.getAsJsonArray().get(i) != null) {
						(item = new Rank()).fromJson(jsonMisses.getAsJsonArray().get(i).getAsJsonObject());
						misses.add(item);
					}
				}
			}
		}

		if (jsonObject.has("simpleModelRun")) {
			JsonElement jsonSimpleModelRun = jsonObject.get("simpleModelRun");
			if (jsonSimpleModelRun != null) {
				simpleModelRun = new SimpleModelRun();
				simpleModelRun.fromJson(jsonSimpleModelRun.getAsJsonObject());
			}
		}
		if (jsonObject.has("salesSummaryDate")) {
			JsonElement jsonSalesSummaryDate = jsonObject.get("salesSummaryDate");
			if (jsonSalesSummaryDate != null) {
				salesSummaryDate = new Date(jsonSalesSummaryDate.getAsLong());
			}
		}
	}

	public CalibrationSummary feedFetch(FeedFetch feedFetch) {
		this.feedFetch = feedFetch;
		return this;
	}

	public CalibrationSummary hits(List<Rank> hits) {
		this.hits = hits;
		return this;
	}

	public CalibrationSummary misses(List<Rank> misses) {
		this.misses = misses;
		return this;
	}

	public CalibrationSummary simpleModelRun(SimpleModelRun simpleModelRun) {
		this.simpleModelRun = simpleModelRun;
		return this;
	}

	public CalibrationSummary salesSummaryDate(Date salesSummaryDate) {
		this.salesSummaryDate = salesSummaryDate;
		return this;
	}
}