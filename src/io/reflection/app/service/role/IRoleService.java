//  
//  IRoleService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.role;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.shared.datatypes.Role;

import com.spacehopperstudios.service.IService;

public interface IRoleService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Role getRole(Long id) throws DataAccessException;

	/**
	 * @param role
	 * @return
	 */
	public Role addRole(Role role) throws DataAccessException;

	/**
	 * @param role
	 * @return
	 */
	public Role updateRole(Role role) throws DataAccessException;

	/**
	 * @param role
	 */
	public void deleteRole(Role role) throws DataAccessException;

	/**
	 * @param name
	 * @return
	 */
	public Role getNamedRole(String name) throws DataAccessException;

}