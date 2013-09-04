//  
//  DatabaseType.java
//  repackagables
//
//  Created by William Shakour on June 15, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.repackaged.scphopr.service.database;

import java.util.HashMap;
import java.util.Map;

public enum DatabaseType {
	DatabaseTypeSession("session"), @Deprecated
	DatabaseTypeProperty("property"), DatabaseTypePropertyvalue("propertyvalue"), DatabaseTypeMetaproperty("metaproperty"), DatabaseTypeApplication(
			"application"), DatabaseTypePublisher("publisher"), DatabaseTypeUser("user"), DatabaseTypeDevice("device"), DatabaseTypePropertymodifier(
			"propertymodifier");
	private String value;
	private static Map<String, DatabaseType> valueLookup = null;

	public String toString() {
		return value;
	}

	private DatabaseType(String value) {
		this.value = value;
	}

	public static DatabaseType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, DatabaseType>();
			for (DatabaseType currentDatabaseType : DatabaseType.values()) {
				valueLookup.put(currentDatabaseType.value, currentDatabaseType);
			}
		}
		return valueLookup.get(value);
	}
}