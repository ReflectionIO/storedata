//  
//  EventTypeType.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.HashMap;
import java.util.Map;

public enum EventTypeType {
	EventTypeTypeSystem("system"),
	EventTypeTypeUser("user"),
	EventTypeTypeRank("rank"), ;
	private String value;
	private static Map<String, EventTypeType> valueLookup = null;

	public String toString() {
		return value;
	}

	private EventTypeType(String value) {
		this.value = value;
	}

	public static EventTypeType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, EventTypeType>();
			for (EventTypeType currentEventTypeType : EventTypeType.values()) {
				valueLookup.put(currentEventTypeType.value, currentEventTypeType);
			}
		}
		return valueLookup.get(value);
	}
}