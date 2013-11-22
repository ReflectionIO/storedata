//  
//  IPermissionService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.permission;

import io.reflection.app.shared.datatypes.Permission;

import com.spacehopperstudios.service.IService;

public interface IPermissionService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Permission getPermission(Long id);

	/**
	 * @param permission
	 * @return
	 */
	public Permission addPermission(Permission permission);

	/**
	 * @param permission
	 * @return
	 */
	public Permission updatePermission(Permission permission);

	/**
	 * @param permission
	 */
	public void deletePermission(Permission permission);

}