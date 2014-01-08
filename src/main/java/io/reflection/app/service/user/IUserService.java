//  
//  IUserService.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.user;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IUserService extends IService {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(Long id) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @return
	 */
	public User addUser(User user) throws DataAccessException;

	/**
	 * 
	 * @param mask
	 * @param pager
	 * @return
	 */
	public List<User> searchUsers(String mask, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param mask
	 * @return
	 */
	public Long searchUsersCount(String mask) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param newPassword
	 */
	public void updateUserPassword(User user, String newPassword) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @return
	 */
	public User updateUser(User user) throws DataAccessException;

	/**
	 * 
	 * @param user
	 */
	public void deleteUser(User user) throws DataAccessException;

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public User getLoginUser(String username, String password) throws DataAccessException;

	/**
	 * 
	 * @param pager
	 * @return
	 */
	public List<User> getUsers(Pager pager) throws DataAccessException;

	/**
	 * 
	 * @return
	 */
	public Long getUsersCount() throws DataAccessException;

	/**
	 * 
	 * @param user
	 */
	public void updateLoginTime(User user) throws DataAccessException;

	/**
	 * @param username
	 * @return
	 */
	public User getUsernameUser(String username) throws DataAccessException;

	/**
	 * @param user
	 * @param role
	 */
	public void assignRole(User user, Role role) throws DataAccessException;

	/**
	 * @param user
	 * @param role
	 * @return
	 */
	public Boolean hasRole(User user, Role role) throws DataAccessException;

	/**
	 * @param user
	 * @param permission
	 */
	public void assignPermission(User user, Permission permission) throws DataAccessException;

	/**
	 * @param user
	 * @param permission
	 * @return
	 */
	public Boolean hasPermission(User user, Permission permission) throws DataAccessException;

	/**
	 * @param user
	 * @return
	 */
	public List<Role> getRoles(User user) throws DataAccessException;

	/**
	 * @param user
	 * @return
	 */
	public List<Permission> getPermissions(User user) throws DataAccessException;

}