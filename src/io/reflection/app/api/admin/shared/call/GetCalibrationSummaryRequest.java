//  
//  GetCalibrationSummaryRequest.java
//  reflection.io
//
//  Created by William Shakour on March 8, 2015.
//  Copyrights © 2015 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2015 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.FeedFetch;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetCalibrationSummaryRequest extends Request {
	public FeedFetch feedFetch;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonFeedFetch = feedFetch == null ? JsonNull.INSTANCE : feedFetch.toJson();
		object.add("feedFetch", jsonFeedFetch);
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
	}

	public GetCalibrationSummaryRequest feedFetch(FeedFetch feedFetch) {
		this.feedFetch = feedFetch;
		return this;
	}
}