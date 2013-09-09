//  
//  IStoreService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

import java.util.List;

import io.reflection.app.api.datatypes.Pager;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Store;

import com.spacehopperstudios.service.IService;

public interface IStoreService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Store getStore(Long id);

	/**
	 * @param store
	 * @return
	 */
	public Store addStore(Store store);

	/**
	 * @param store
	 * @return
	 */
	public Store updateStore(Store store);

	/**
	 * @param store
	 */
	public void deleteStore(Store store);

	/**
	 * @param country
	 * @param pager 
	 * @return
	 */
	public List<Store> getCountryStores(Country country, Pager pager);

	/**
	 * @param pager
	 * @return
	 */
	public List<Store> getStores(Pager pager);

	/**
	 * @param query
	 * @param pager
	 * @return
	 */
	public List<Store> searchStores(String query, Pager pager);

	/**
	 * @param a3Code
	 * @return
	 */
	public Store getA3CodeStore(String a3Code);

	/**
	 * @param name
	 * @return
	 */
	public Store getNamedStore(String name);

	/**
	 * @return
	 */
	public long getStoresCount();

}