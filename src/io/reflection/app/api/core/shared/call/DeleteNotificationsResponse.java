//  
//  DeleteNotificationsResponse.java
//  reflection.io
//
//  Created by William Shakour on December 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call;

import io.reflection.app.api.shared.datatypes.Response;

import com.google.gson.JsonObject;

public class DeleteNotificationsResponse extends Response {
	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
	}
}