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

import com.spacehopperstudios.service.IService;

public interface IDataAccountService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public DataAccount getDataAccount(Long id) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @return
	 */
	public DataAccount addDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @return
	 */
	public DataAccount updateDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 */
	public void deleteDataAccount(DataAccount dataAccount) throws DataAccessException;

}