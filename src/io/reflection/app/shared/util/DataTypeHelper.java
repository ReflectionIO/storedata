//
//  DataTypeHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author billy1380
 * 
 */
public class DataTypeHelper {

	/**
	 * Creates a role with a given id
	 * 
	 * @param id
	 * @return
	 */
	public static Role createRole(Long id) {
		Role role = new Role();
		role.id = id;
		return role;
	}

	/**
	 * Creates a permission with a given id
	 * 
	 * @param id
	 * @return
	 */
	public static Permission createPermission(Long id) {
		Permission permission = new Permission();
		permission.id = id;
		return permission;
	}

	public static String itemIapState(Item item, String yes, String no, String unknown) {
		String usesIap = null;
		
		if (item != null) {
			usesIap = jsonPropertiesIapState(item.properties, yes, no, unknown);
		}

		return usesIap;
	}
	
	public static String jsonPropertiesIapState(String jsonProperties, String yes, String no, String unknown) {
		String usesIap = null;
		
		if (jsonProperties != null) {
			JsonElement propertiesJsonElement = (new JsonParser()).parse(jsonProperties);

			if (propertiesJsonElement.isJsonObject()) {
				JsonObject propertiesJsonObject = propertiesJsonElement.getAsJsonObject();
				JsonElement usesIapJsonElement = propertiesJsonObject.get("usesIap");

				if (usesIapJsonElement.isJsonPrimitive()) {
					if (usesIapJsonElement.getAsBoolean()) {
						usesIap = yes;
					} else {
						usesIap = no;
					}
				}
			}
		}
		
		if (usesIap == null) {
			usesIap = unknown;
		}
		
		return usesIap;
	}
}
