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
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
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
	 * @param newPassword
	 * @param notify
	 * @throws DataAccessException
	 */
	public void updateUserPassword(User user, String newPassword, Boolean notify) throws DataAccessException;

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

	/**
	 * @param user
	 * @param source
	 * @param username
	 * @param password
	 * @param properties
	 * @return
	 */
	public DataAccount addDataAccount(User user, DataSource datasource, String username, String password, String properties) throws DataAccessException;

	/**
	 * 
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount restoreDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getDataAccounts(User user, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @return
	 * @throws DataAccessException
	 */
	public Long getDataAccountsCount(User user) throws DataAccessException;

	/**
	 * @return
	 */
	public User getDataAccountOwner(DataAccount dataAccount) throws DataAccessException;

	/**
	 * Marks the user with a reset code and sends an email notification to the user with the assigned action code
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void markForReset(User user) throws DataAccessException;

	/**
	 * Gets the user for a given action code. Current usage include reset code and add to private beta code
	 * 
	 * @param code
	 * @return
	 * @throws DataAccessException
	 */
	public User getActionCodeUser(String code) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public Boolean hasDataAccount(User user, DataAccount dataAccount) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param username
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDeletedDataAccount(User user, String username) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void deleteDataAccount(User user, DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 */
	public void deleteAllUsersDataAccount(DataAccount dataAccount) throws DataAccessException;
}