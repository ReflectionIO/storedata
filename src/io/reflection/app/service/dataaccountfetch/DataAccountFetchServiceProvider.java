//  
//  DataAccountFetchServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on January 9, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccountfetch;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class DataAccountFetchServiceProvider {

	/**
	 * @return
	 */
	public static IDataAccountFetchService provide() {
		IDataAccountFetchService dataAccountFetchService = null;

		if ((dataAccountFetchService = (IDataAccountFetchService) ServiceDiscovery.getService(ServiceType.ServiceTypeDataAccountFetch.toString())) == null) {
			dataAccountFetchService = DataAccountFetchServiceFactory.createNewDataAccountFetchService();
			ServiceDiscovery.registerService(dataAccountFetchService);
		}

		return dataAccountFetchService;
	}

}