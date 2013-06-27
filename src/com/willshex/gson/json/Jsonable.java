//
//  Jsonable.java
//  Scores
//
//  Created by William Shakour on August 16, 2011.
//  Copyright © 2011 WillShex Ltd. All rights reserved.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.willshex.gson.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spacehopperstudios.utility.JsonUtils;

public abstract class Jsonable {
	public JsonObject toJson() {
		return new JsonObject();
	}

	public void fromJson(JsonObject jsonObject) {
		return;
	}

	public void fromJson(String json) {
		fromJson((JsonObject) (new JsonParser()).parse(json));
		return;
	}
	
	@Override
	public String toString() {
		return JsonUtils.cleanJson(toJson().toString());
	}
}
