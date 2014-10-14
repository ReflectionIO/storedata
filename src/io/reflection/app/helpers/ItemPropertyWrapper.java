//
//  ItemPropertyWrapper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static com.willshex.gson.json.shared.Convert.toJsonObject;

/**
 * @author billy1380
 * 
 */
public class ItemPropertyWrapper {

	JsonObject mObject = null;

	/**
	 * @param properties
	 */
	public ItemPropertyWrapper(String properties) {
		if (properties != null) {
			mObject = toJsonObject(properties);
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean getBoolean(String name) {
		return getBoolean(name, Boolean.FALSE).booleanValue();
	}

	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public Boolean getBoolean(String name, Boolean defaultValue) {
		Boolean value = defaultValue;

		if (mObject != null) {
			JsonElement e = mObject.get(name);

			if (e != null) {
				value = e.getAsBoolean();
			}
		}

		return value;
	}

}
