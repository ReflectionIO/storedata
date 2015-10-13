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

import java.util.Collection;
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
	 * @param role
	 * @return
	 * @throws DataAccessException
	 */
	public List<User> getRoleUsers(Role role) throws DataAccessException;

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
	 * @param userIds
	 */
	public void deleteUsers(Collection<User> users) throws DataAccessException;

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
	 */
	public void assignExpiringRole(User user, Role role, int expiringDays) throws DataAccessException;

	/**
	 * @param user
	 * @param role
	 * @return
	 */
	public Boolean hasRole(User user, Role role) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param role
	 * @return
	 * @throws DataAccessException
	 */
	public Boolean hasRole(User user, Role role, boolean includeExpired) throws DataAccessException;

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
	 * @param permission
	 * @return
	 */
	public Boolean hasPermission(User user, Permission permission, Boolean deleted) throws DataAccessException;

	/**
	 * @param user
	 * @return
	 */
	public List<Role> getUserRoles(User user) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param includeExpired
	 * @return
	 * @throws DataAccessException
	 */
	public List<Role> getUserRoles(User user, boolean includeExpired) throws DataAccessException;

	/**
	 * @param user
	 * @return
	 */
	public List<Permission> getPermissions(User user) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param permission
	 * @throws DataAccessException
	 */
	public void revokePermission(User user, Permission permission) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void revokeAllPermissions(User user) throws DataAccessException;

	/**
	 * 
	 * @param userIds
	 * @throws DataAccessException
	 */
	public void revokeUsersAllPermissions(Collection<User> users) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void revokeRole(User user, Role role) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void setUserRoleAsExpired(User user, Role role) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void revokeAllRoles(User user) throws DataAccessException;

	/**
	 * 
	 * @param users
	 * @throws DataAccessException
	 */
	public void revokeUsersAllRoles(Collection<User> users) throws DataAccessException;

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
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getDataAccounts(User user, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getDataAccountsIds(User user, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param users
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getUsersDataAccounts(Collection<User> users, Pager pager) throws DataAccessException;

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
	 * @return
	 */
	public List<User> getDataAccountOwnerBatch(Collection<Long> dataAccountIds) throws DataAccessException;

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
	 * @return
	 * @throws DataAccessException
	 */
	public Boolean hasDataAccounts(User user) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void deleteDataAccount(User user, DataAccount dataAccount) throws DataAccessException;

	/**
	 * 
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void restoreUserDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 */
	public void deleteAllUsersDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void deleteAllDataAccounts(User user) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @throws DataAccessException
	 */
	public void deleteUsersAllDataAccounts(Collection<User> users) throws DataAccessException;

	/**
	 * Adds a user to a data account or restores a deleted row if one exist
	 * 
	 * @param user
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void addOrRestoreUserDataAccount(User user, DataAccount dataAccount) throws DataAccessException;

}