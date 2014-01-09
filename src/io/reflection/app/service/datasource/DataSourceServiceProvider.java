//  
//  DataSourceServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.datasource;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class DataSourceServiceProvider {

	/**
	 * @return
	 */
	public static IDataSourceService provide() {
		IDataSourceService dataSourceService = null;

		if ((dataSourceService = (IDataSourceService) ServiceDiscovery.getService(ServiceType.ServiceTypeDataSource.toString())) == null) {
			dataSourceService = DataSourceServiceFactory.createNewDataSourceService();
			ServiceDiscovery.registerService(dataSourceService);
		}

		return dataSourceService;
	}

}