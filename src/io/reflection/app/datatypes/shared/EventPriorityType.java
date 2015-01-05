//  
//  EventPriorityType.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.HashMap;
import java.util.Map;

public enum EventPriorityType {
	EventPriorityTypeCritical("critical"),
	EventPriorityTypeHigh("high"),
	EventPriorityTypeNormal("normal"),
	EventPriorityTypeLow("low"),
	EventPriorityTypeDebug("debug"), ;
	private String value;
	private static Map<String, EventPriorityType> valueLookup = null;

	public String toString() {
		return value;
	}

	private EventPriorityType(String value) {
		this.value = value;
	}

	public static EventPriorityType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, EventPriorityType>();
			for (EventPriorityType currentEventPriorityType : EventPriorityType.values()) {
				valueLookup.put(currentEventPriorityType.value, currentEventPriorityType);
			}
		}
		return valueLookup.get(value);
	}
}