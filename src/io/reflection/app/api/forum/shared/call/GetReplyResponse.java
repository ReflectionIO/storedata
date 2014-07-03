//  
//  GetReplyResponse.java
//  reflection.io
//
//  Created by William Shakour on July 3, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call;

import io.reflection.app.api.shared.datatypes.Response;
import io.reflection.app.datatypes.shared.Reply;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class GetReplyResponse extends Response {
	public Reply reply;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonReply = reply == null ? JsonNull.INSTANCE : reply.toJson();
		object.add("reply", jsonReply);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("reply")) {
			JsonElement jsonReply = jsonObject.get("reply");
			if (jsonReply != null) {
				reply = new Reply();
				reply.fromJson(jsonReply.getAsJsonObject());
			}
		}
	}
}