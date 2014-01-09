//  
//  PermissionServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.permission;

final class PermissionServiceFactory {

	/**
	 * @return
	 */
	public static IPermissionService createNewPermissionService() {
		return new PermissionService();
	}

}