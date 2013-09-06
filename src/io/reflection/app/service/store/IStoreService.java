//  
//  IStoreService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

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

}