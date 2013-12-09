//  
//  IPermissionService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.permission;

import java.util.List;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.Permission;

import com.spacehopperstudios.service.IService;

public interface IPermissionService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Permission getPermission(Long id) throws DataAccessException;

	/**
	 * @param permission
	 * @return
	 */
	public Permission addPermission(Permission permission) throws DataAccessException;

	/**
	 * @param permission
	 * @return
	 */
	public Permission updatePermission(Permission permission) throws DataAccessException;

	/**
	 * @param permission
	 */
	public void deletePermission(Permission permission) throws DataAccessException;

	/**
	 * @param name
	 * @return
	 */
	public Permission getNamedPermission(String name) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 */
	public List<Permission> getPermissions(Pager pager) throws DataAccessException;

	/**
	 * @return
	 */
	public Long getPermissionsCount() throws DataAccessException;

	/**
	 * @param permissions
	 * @return
	 */
	public List<Permission> getIdPermissionsBatch(List<Long> permissionIds) throws DataAccessException;

	/**
	 * 
	 * @param permissions
	 * @throws DataAccessException
	 */
	public void inflatePermissions(List<Permission> permissions) throws DataAccessException;

}