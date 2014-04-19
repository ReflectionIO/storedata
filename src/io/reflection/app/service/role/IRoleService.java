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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;

import java.util.Collection;
import java.util.List;

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
	
	/**
	 * Gets the role using a lookup code
	 * @param code the code name for the role
	 * @return
	 */
	public Role getCodeRole(String code) throws DataAccessException; 

	/**
	 * @param pager
	 * @return
	 */
	public List<Role> getRoles(Pager pager) throws DataAccessException;

	/**
	 * @return
	 */
	public Long getRolesCount() throws DataAccessException;

	/**
	 * @param roles
	 * @return
	 */
	public List<Role> getIdRoles(Collection<Long> roleIds) throws DataAccessException;

	/**
	 * @param roles
	 * @throws DataAccessException
	 */
	public void inflateRoles(Collection<Role> roles) throws DataAccessException;

	public List<Permission> getPermissions(Role role) throws DataAccessException;

}