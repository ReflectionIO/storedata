//  
//  IUserService.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.user;

import java.util.List;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.DataType;
import io.reflection.app.shared.datatypes.User;

import com.spacehopperstudios.service.IService;

public interface IUserService extends IService {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(Long id);

	/**
	 * 
	 * @param user
	 * @return
	 */
	public User addUser(User user);

	/**
	 * 
	 * @param mask
	 * @param pager
	 * @return
	 */
	public List<User> searchUsers(String mask, Pager pager);

	/**
	 * 
	 * @param mask
	 * @return
	 */
	public Long searchUsersCount(String mask);

	/**
	 * 
	 * @param user
	 * @param newPassword
	 */
	public void updateUserPassword(User user, String newPassword);

	/**
	 * 
	 * @param user
	 * @return
	 */
	public User updateUser(User user);

	/**
	 * 
	 * @param user
	 */
	public void deleteUser(User user);

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public User getLoginUser(String username, String password);

	/**
	 * 
	 * @param pager
	 * @return
	 */
	public List<User> getUsers(Pager pager);

	/**
	 * 
	 * @return
	 */
	public Long getUsersCount();

	/**
	 * 
	 * @param user
	 */
	public void updateLoginTime(User user);

	/**
	 * Gets the User with the session id
	 * 
	 * @param session
	 * @return
	 */
	public User getSessionUser(DataType session);

}