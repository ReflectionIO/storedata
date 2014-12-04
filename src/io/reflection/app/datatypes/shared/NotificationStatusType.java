//  
//  NotificationStatusType.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.HashMap;
import java.util.Map;

public enum NotificationStatusType {
	NotificationStatusTypeSending("sending"),
	NotificationStatusTypeSent("sent"),
	NotificationStatusTypeRead("read"),
	NotificationStatusTypeFailed("failed"), ;
	private String value;
	private static Map<String, NotificationStatusType> valueLookup = null;

	public String toString() {
		return value;
	}

	private NotificationStatusType(String value) {
		this.value = value;
	}

	public static NotificationStatusType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, NotificationStatusType>();
			for (NotificationStatusType currentNotificationStatusType : NotificationStatusType.values()) {
				valueLookup.put(currentNotificationStatusType.value, currentNotificationStatusType);
			}
		}
		return valueLookup.get(value);
	}
}