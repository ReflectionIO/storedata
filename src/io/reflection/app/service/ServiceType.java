//  
//  ServiceType.java
//  storedata
//
//  Created by William Shakour on 4 Sep 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author William Shakour
 * 
 */
public enum ServiceType {
	ServiceTypeDatabase("database"),
	ServiceTypeItem("item"),
	ServiceTypeRank("rank"),
	ServiceTypeCountry("country"),
	ServiceTypeStore("store");

	private String value;
	private static Map<String, ServiceType> valueLookup = null;

	private ServiceType(String value) {
		this.value = value;
	}

	public String toString() {
		return "service." + value;
	}

	public static ServiceType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, ServiceType>();
			for (ServiceType currentServiceType : ServiceType.values()) {
				valueLookup.put(currentServiceType.value, currentServiceType);
			}
		}
		return valueLookup.get(value);
	}
}
