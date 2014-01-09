//  
//  PermissionServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.permission;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class PermissionServiceProvider {

	/**
	 * @return
	 */
	public static IPermissionService provide() {
		IPermissionService permissionService = null;

		if ((permissionService = (IPermissionService) ServiceDiscovery.getService(ServiceType.ServiceTypePermission.toString())) == null) {
			permissionService = PermissionServiceFactory.createNewPermissionService();
			ServiceDiscovery.registerService(permissionService);
		}

		return permissionService;
	}

}