//
//  IDataAccountFetchService.java
//  reflection.io
//
//  Created by William Shakour on January 9, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccountfetch;

import java.util.Date;
import java.util.List;

import com.spacehopperstudios.service.IService;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;

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
	 *
	 * @param dataAccountFetch
	 * @throws DataAccessException
	 */
	public void deleteDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException;

	/**
	 * Checks whether there have been any errors in the last few (?) fetches of an account
	 *
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public Boolean isFetchable(DataAccount dataAccount) throws DataAccessException;

	/**
	 *
	 * @param dataAccount
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccountFetch> getFailedDataAccountFetches(DataAccount dataAccount, Pager pager) throws DataAccessException;

	/**
	 *
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public Long getFailedDataAccountFetchesCount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @param date
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccountFetch getDateDataAccountFetch(DataAccount dataAccount, Date date) throws DataAccessException;

	/**
	 *
	 * @param dataAccount
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccountFetch> getDataAccountFetches(DataAccount dataAccount, Date start, Date end, Pager pager) throws DataAccessException;

	/**
	 *
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccountFetch> getDataAccountFetches(Date start, Date end, Pager pager) throws DataAccessException;

	/**
	 *
	 * @param dataAccount
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public Long getDataAccountFetchesCount(DataAccount dataAccount, Date start, Date end) throws DataAccessException;

	/**
	 *
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public Long getDataAccountFetchesCount(Date start, Date end) throws DataAccessException;

	/**
	 *
	 * @param fetch
	 * @throws DataAccessException
	 */
	public void triggerDataAccountFetchIngest(DataAccountFetch fetch) throws DataAccessException;

	/**
	 * @param fetchForDate
	 * @param dataaccountfetchstatustypegathered
	 * @return
	 * @throws DataAccessException
	 */
	public Long getDataAccountFetchesWithStatusCount(Date fetchForDate, DataAccountFetchStatusType statusType)
			throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param date
	 * @return
	 * @throws DataAccessException 
	 */
	public DataAccountFetch getDataAccountFetch(Long dataAccountId, Date date) throws DataAccessException;

}