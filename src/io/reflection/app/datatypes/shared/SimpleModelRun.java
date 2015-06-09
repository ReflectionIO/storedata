//  
//  SimpleModelRun.java
//  reflection.io
//
//  Created by William Shakour on September 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SimpleModelRun extends DataType {
	public FeedFetch feedFetch;
	public Double a;
	public Double b;
	public Double aStandardError;
	public Double bStandardError;
	public Double adjustedRSquared;
	public Double regressionSumSquares;
	public Date summaryDate;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonFeedFetch = feedFetch == null ? JsonNull.INSTANCE : feedFetch.toJson();
		object.add("feedFetch", jsonFeedFetch);
		JsonElement jsonA = a == null ? JsonNull.INSTANCE : new JsonPrimitive(a);
		object.add("a", jsonA);
		JsonElement jsonB = b == null ? JsonNull.INSTANCE : new JsonPrimitive(b);
		object.add("b", jsonB);
		JsonElement jsonAStandardError = aStandardError == null ? JsonNull.INSTANCE : new JsonPrimitive(aStandardError);
		object.add("aStandardError", jsonAStandardError);
		JsonElement jsonBStandardError = bStandardError == null ? JsonNull.INSTANCE : new JsonPrimitive(bStandardError);
		object.add("bStandardError", jsonBStandardError);
		JsonElement jsonAdjustedRSquared = adjustedRSquared == null ? JsonNull.INSTANCE : new JsonPrimitive(adjustedRSquared);
		object.add("adjustedRSquared", jsonAdjustedRSquared);
		JsonElement jsonRegressionSumSquares = regressionSumSquares == null ? JsonNull.INSTANCE : new JsonPrimitive(regressionSumSquares);
		object.add("regressionSumSquares", jsonRegressionSumSquares);
		JsonElement jsonSummaryDate = summaryDate == null ? JsonNull.INSTANCE : new JsonPrimitive(summaryDate.getTime());
		object.add("summaryDate", jsonSummaryDate);
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
		if (jsonObject.has("a")) {
			JsonElement jsonA = jsonObject.get("a");
			if (jsonA != null) {
				a = Double.valueOf(jsonA.getAsDouble());
			}
		}
		if (jsonObject.has("b")) {
			JsonElement jsonB = jsonObject.get("b");
			if (jsonB != null) {
				b = Double.valueOf(jsonB.getAsDouble());
			}
		}
		if (jsonObject.has("aStandardError")) {
			JsonElement jsonAStandardError = jsonObject.get("aStandardError");
			if (jsonAStandardError != null) {
				aStandardError = Double.valueOf(jsonAStandardError.getAsDouble());
			}
		}
		if (jsonObject.has("bStandardError")) {
			JsonElement jsonBStandardError = jsonObject.get("bStandardError");
			if (jsonBStandardError != null) {
				bStandardError = Double.valueOf(jsonBStandardError.getAsDouble());
			}
		}
		if (jsonObject.has("adjustedRSquared")) {
			JsonElement jsonAdjustedRSquared = jsonObject.get("adjustedRSquared");
			if (jsonAdjustedRSquared != null) {
				adjustedRSquared = Double.valueOf(jsonAdjustedRSquared.getAsDouble());
			}
		}
		if (jsonObject.has("regressionSumSquares")) {
			JsonElement jsonRegressionSumSquares = jsonObject.get("regressionSumSquares");
			if (jsonRegressionSumSquares != null) {
				regressionSumSquares = Double.valueOf(jsonRegressionSumSquares.getAsDouble());
			}
		}
		if (jsonObject.has("summaryDate")) {
			JsonElement jsonSummaryDate = jsonObject.get("summaryDate");
			if (jsonSummaryDate != null) {
				summaryDate = new Date(jsonSummaryDate.getAsLong());
			}
		}
	}

	public SimpleModelRun feedFetch(FeedFetch feedFetch) {
		this.feedFetch = feedFetch;
		return this;
	}

	public SimpleModelRun a(Double a) {
		this.a = a;
		return this;
	}

	public SimpleModelRun b(Double b) {
		this.b = b;
		return this;
	}

	public SimpleModelRun aStandardError(Double aStandardError) {
		this.aStandardError = aStandardError;
		return this;
	}

	public SimpleModelRun bStandardError(Double bStandardError) {
		this.bStandardError = bStandardError;
		return this;
	}

	public SimpleModelRun adjustedRSquared(Double adjustedRSquared) {
		this.adjustedRSquared = adjustedRSquared;
		return this;
	}

	public SimpleModelRun regressionSumSquares(Double regressionSumSquares) {
		this.regressionSumSquares = regressionSumSquares;
		return this;
	}
		
	public SimpleModelRun summaryDate(Date summaryDate) {
		this.summaryDate = summaryDate;
		return this;
	}
}
