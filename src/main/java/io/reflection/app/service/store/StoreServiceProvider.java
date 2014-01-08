//  
//  StoreServiceProvider.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class StoreServiceProvider {

	/**
	 * @return
	 */
	public static IStoreService provide() {
		IStoreService storeService = null;

		if ((storeService = (IStoreService) ServiceDiscovery.getService(ServiceType.ServiceTypeStore.toString())) == null) {
			storeService = StoreServiceFactory.createNewStoreService();
			ServiceDiscovery.registerService(storeService);
		}

		return storeService;
	}

}