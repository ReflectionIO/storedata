//  
//  UserServiceProvider.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.user;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class UserServiceProvider {

	/**
	 * @return
	 */
	public static IUserService provide() {
		IUserService userService = null;

		if ((userService = (IUserService) ServiceDiscovery.getService(ServiceType.ServiceTypeUser.toString())) == null) {
			userService = UserServiceFactory.createNewUserService();
			ServiceDiscovery.registerService(userService);
		}

		return userService;
	}

}