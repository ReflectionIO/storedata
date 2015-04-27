//  
//  ListPropertyType.java
//  reflection.io
//
//  Created by William Shakour on March 7, 2015.
//  Copyrights © 2015 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2015 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.HashMap;
import java.util.Map;

public enum ListPropertyType {
	ListPropertyTypeRevenue("revenue"),
	ListPropertyTypeDownloads("downloads"), ;
	private String value;
	private static Map<String, ListPropertyType> valueLookup = null;

	public String toString() {
		return value;
	}

	private ListPropertyType(String value) {
		this.value = value;
	}

	public static ListPropertyType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, ListPropertyType>();
			for (ListPropertyType currentListPropertyType : ListPropertyType.values()) {
				valueLookup.put(currentListPropertyType.value, currentListPropertyType);
			}
		}
		return valueLookup.get(value);
	}
}