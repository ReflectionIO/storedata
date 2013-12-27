//  
//  IDataAccountService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccount;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.shared.datatypes.DataAccount;
import io.reflection.app.shared.datatypes.DataSource;

import com.spacehopperstudios.service.IService;

public interface IDataAccountService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDataAccount(Long id) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount addDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * 
	 * @param dataSource
	 * @param username
	 * @param password
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount addDataAccount(DataSource dataSource, String username, String password) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount updateDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void deleteDataAccount(DataAccount dataAccount) throws DataAccessException;

}