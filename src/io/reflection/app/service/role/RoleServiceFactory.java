//  
//  RoleServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.role;

final class RoleServiceFactory {

	/**
	 * @return
	 */
	public static IRoleService createNewRoleService() {
		return new RoleService();
	}

}