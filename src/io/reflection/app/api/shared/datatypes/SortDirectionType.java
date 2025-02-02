//  
//  SortDirectionType.java
//  storedata
//
//  Created by William Shakour on 03 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.shared.datatypes;

import java.util.HashMap;
import java.util.Map;

public enum SortDirectionType {
	SortDirectionTypeAscending("ascending"), SortDirectionTypeDescending("descending"), ;
	private String value;
	private static Map<String, SortDirectionType> valueLookup = null;

	public String toString() {
		return value;
	}

	private SortDirectionType(String value) {
		this.value = value;
	}

	public static SortDirectionType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, SortDirectionType>();
			for (SortDirectionType currentSortDirectionType : SortDirectionType.values()) {
				valueLookup.put(currentSortDirectionType.value, currentSortDirectionType);
			}
		}
		return valueLookup.get(value);
	}
}