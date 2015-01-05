//  
//  NotificationTypeType.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.HashMap;
import java.util.Map;

public enum NotificationTypeType {
	NotificationTypeTypeEmail("email"),
	NotificationTypeTypeText("text"),
	NotificationTypeTypeInternal("internal"), ;
	private String value;
	private static Map<String, NotificationTypeType> valueLookup = null;

	public String toString() {
		return value;
	}

	private NotificationTypeType(String value) {
		this.value = value;
	}

	public static NotificationTypeType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, NotificationTypeType>();
			for (NotificationTypeType currentNotificationTypeType : NotificationTypeType.values()) {
				valueLookup.put(currentNotificationTypeType.value, currentNotificationTypeType);
			}
		}
		return valueLookup.get(value);
	}
}