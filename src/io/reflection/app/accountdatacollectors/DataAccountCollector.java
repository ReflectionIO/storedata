//
//  DataAccountCollector.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;

import java.util.Date;

import com.willshex.gson.json.service.server.ServiceException;

/**
 * @author billy1380
 * 
 */
public interface DataAccountCollector {

	public static final String ACCOUNT_DATA_BUCKET_KEY = "account.data.bucket";

	/**
	 * @param dataAccount
	 * @param date
	 * @return
	 */
	boolean collect(DataAccount dataAccount, Date date) throws DataAccessException, ServiceException;

	/**
	 * 
	 * @param properties
	 * @throws ServiceException
	 */
	void validateProperties(String properties) throws ServiceException;

}
