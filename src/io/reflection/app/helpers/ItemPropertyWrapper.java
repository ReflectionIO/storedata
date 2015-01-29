//
//  ItemPropertyWrapper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.willshex.gson.json.shared.Convert.toJsonObject;

/**
 * @author billy1380
 * 
 */
public class ItemPropertyWrapper {

	public static final int IAP_DAY_OFFSET = 10;
	
	public static final String PROPERTY_IAP = "usesIap";
	public static final String PROPERTY_IAP_ON = "usesIap.on";
	
	JsonObject mObject = null;

	/**
	 * @param properties
	 */
	public ItemPropertyWrapper(String properties) {
		if (properties != null && !properties.equals("null") && properties.length() > 0) {
			mObject = toJsonObject(properties);
		} else {
			mObject = new JsonObject();
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

	public String getString(String name) {
		return getString(name, null);
	}

	public String getString(String name, String defaultValue) {
		String value = defaultValue;

		if (mObject != null) {
			JsonElement e = mObject.get(name);

			if (e != null) {
				value = e.getAsString();
			}
		}

		return value;
	}

	public Date getDate(String name) {
		return getDate(name, null);
	}

	public Date getDate(String name, Date defaultValue) {
		Date value = defaultValue;

		if (mObject != null) {
			JsonElement e = mObject.get(name);

			if (e != null) {
				value = new Date(e.getAsLong());
			}
		}

		return value;
	}

	public void setBoolean(String name, Boolean value) {
		if (value == null) {
			mObject.add(name, JsonNull.INSTANCE);
		} else {
			mObject.add(name, new JsonPrimitive(value));
		}
	}

	public void setString(String name, String value) {
		if (value == null) {
			mObject.add(name, JsonNull.INSTANCE);
		} else {
			mObject.add(name, new JsonPrimitive(value));
		}
	}

	public void setDate(String name, Date value) {
		if (value == null) {
			mObject.add(name, JsonNull.INSTANCE);
		} else {
			mObject.add(name, new JsonPrimitive(Long.valueOf(value.getTime())));
		}
	}

	public String toString() {
		return mObject.toString();
	}

}
