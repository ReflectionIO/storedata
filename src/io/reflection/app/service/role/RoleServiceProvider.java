//  
//  RoleServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.role;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class RoleServiceProvider {

	/**
	 * @return
	 */
	public static IRoleService provide() {
		IRoleService roleService = null;

		if ((roleService = (IRoleService) ServiceDiscovery.getService(ServiceType.ServiceTypeRole.toString())) == null) {
			roleService = RoleServiceFactory.createNewRoleService();
			ServiceDiscovery.registerService(roleService);
		}

		return roleService;
	}

}