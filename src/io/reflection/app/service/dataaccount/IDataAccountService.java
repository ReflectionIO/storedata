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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.DataAccount;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IDataAccountService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDataAccount(Long id) throws DataAccessException;

	/**
	 *
	 * @param id
	 * @param deleted
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDataAccount(Long id, Boolean deleted) throws DataAccessException;

	/**
	 * 
	 * @param username
	 * @param vendor
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDataAccount(String username, String vendor) throws DataAccessException;

	/**
	 *
	 * @param username
	 * @param sourceid
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDataAccount(String username, Long sourceid) throws DataAccessException;

	/**
	 *
	 * @param username
	 * @param sourceid
	 * @param deleted
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getDataAccount(String username, Long sourceid, Boolean deleted) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount addDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount updateDataAccount(DataAccount dataAccount, boolean collect) throws DataAccessException;

	/**
	 *
	 * @param dataAccountId
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount restoreDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void deleteDataAccount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getDataAccounts(Pager pager) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getActiveDataAccounts(Pager pager) throws DataAccessException;

	/**
	 *
	 * @return
	 * @throws DataAccessException
	 */
	public Long getDataAccountsCount() throws DataAccessException;

	/**
	 *
	 * @return
	 * @throws DataAccessException
	 */
	public Long getActiveDataAccountsCount() throws DataAccessException;

	/**
	 *
	 * @param pager
	 * @param ids
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getIdsDataAccounts(Collection<Long> ids, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param vendorId
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getVendorDataAccounts(String vendorId, Boolean includeInactive) throws DataAccessException;

	/**
	 *
	 * @param dataAccount
	 * @throws DataAccessException
	 */
	public void triggerDataAccountFetch(DataAccount dataAccount) throws DataAccessException;

	/**
	 * Trigger single date data account fetch
	 *
	 * @param dataAccount
	 * @param date
	 * @throws DataAccessException
	 */
	public void triggerSingleDateDataAccountFetch(DataAccount dataAccount, Date date) throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param date
	 * @throws DataAccessException
	 */
	void triggerSingleDateDataAccountFetch(Long dataAccountId, Date date) throws DataAccessException;

	/**
	 * Trigger Multiple Date Data Account Fetch - dates are sequential and separated by a number of days
	 *
	 * @param dataAccount
	 * @param date
	 * @param days
	 * @throws DataAccessException
	 */
	public void triggerMultipleDateDataAccountFetch(DataAccount dataAccount, Date date, Integer days) throws DataAccessException;

	/**
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getActiveDataAccountIDs() throws DataAccessException;

	/**
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getAllDataAccountIDs() throws DataAccessException;

	/**
	 * @param userId
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataAccount> getDataAccountForUser(Long userId) throws DataAccessException;

}
