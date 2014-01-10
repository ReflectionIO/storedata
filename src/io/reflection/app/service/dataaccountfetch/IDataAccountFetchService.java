//  
//  IDataAccountFetchService.java
//  reflection.io
//
//  Created by William Shakour on January 9, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccountfetch;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.shared.datatypes.DataAccountFetch;

import com.spacehopperstudios.service.IService;

public interface IDataAccountFetchService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public DataAccountFetch getDataAccountFetch(Long id) throws DataAccessException;

	/**
	 * @param dataAccountFetch
	 * @return
	 */
	public DataAccountFetch addDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException;

	/**
	 * @param dataAccountFetch
	 * @return
	 */
	public DataAccountFetch updateDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException;

	/**
	 * @param dataAccountFetch
	 */
	public void deleteDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException;

}