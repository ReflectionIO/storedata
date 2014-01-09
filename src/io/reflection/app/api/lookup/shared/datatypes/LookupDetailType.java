//  
//  LookupDetailType.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.lookup.shared.datatypes;

import java.util.HashMap;
import java.util.Map;

public enum LookupDetailType {
	LookupDetailTypeShort("short"),
	LookupDetailTypeMedium("medium"),
	LookupDetailTypeDetailed("detailed"), ;
	private String value;
	private static Map<String, LookupDetailType> valueLookup = null;

	public String toString() {
		return value;
	}

	private LookupDetailType(String value) {
		this.value = value;
	}

	public static LookupDetailType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, LookupDetailType>();
			for (LookupDetailType currentLookupDetailType : LookupDetailType.values()) {
				valueLookup.put(currentLookupDetailType.value, currentLookupDetailType);
			}
		}
		return valueLookup.get(value);
	}
}