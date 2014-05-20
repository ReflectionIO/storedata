//
//  DataTypeHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;

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
}
