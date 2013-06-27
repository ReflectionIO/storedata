//  
//  StatusType.java
//  storedata
//
//  Created by William Shakour on 21 June 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.willshex.gson.json.service;

import java.util.HashMap;
import java.util.Map;

public enum StatusType {
	StatusTypeSuccess("success"), StatusTypeFailure("failure"), ;
	private String value;
	private static Map<String, StatusType> valueLookup = null;

	public String toString() {
		return value;
	}

	private StatusType(String value) {
		this.value = value;
	}

	public static StatusType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, StatusType>();
			for (StatusType currentStatusType : StatusType.values()) {
				valueLookup.put(currentStatusType.value, currentStatusType);
			}
		}
		return valueLookup.get(value);
	}
}