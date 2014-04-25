//  
//  IStoreService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Store;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IStoreService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Store getStore(Long id) throws DataAccessException;

	/**
	 * @param store
	 * @return
	 */
	public Store addStore(Store store) throws DataAccessException;

	/**
	 * @param store
	 * @return
	 */
	public Store updateStore(Store store) throws DataAccessException;

	/**
	 * @param store
	 */
	public void deleteStore(Store store) throws DataAccessException;

	/**
	 * @param country
	 * @param pager
	 * @return
	 */
	public List<Store> getCountryStores(Country country, Pager pager) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 */
	public List<Store> getStores(Pager pager) throws DataAccessException;

	/**
	 * @param query
	 * @param pager
	 * @return
	 */
	public List<Store> searchStores(String query, Pager pager) throws DataAccessException;

	/**
	 * @param a3Code
	 * @return
	 */
	public Store getA3CodeStore(String a3Code) throws DataAccessException;

	/**
	 * @param name
	 * @return
	 */
	public Store getNamedStore(String name) throws DataAccessException;

	/**
	 * 
	 * @return
	 * @throws DataAccessException
	 */
	public long getStoresCount() throws DataAccessException;
	
	/**
	 * 
	 * @param dataSource
	 * @return
	 * @throws DataAccessException
	 */
	public List<Store> getDataSourceStores(DataSource dataSource) throws DataAccessException;

}