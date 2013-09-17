//  
//  ApplicationServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.application;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class ApplicationServiceProvider {

	/**
	 * @return
	 */
	public static IApplicationService provide() {
		IApplicationService applicationService = null;

		if ((applicationService = (IApplicationService) ServiceDiscovery.getService(ServiceType.ServiceTypeApplication.toString())) == null) {
			applicationService = ApplicationServiceFactory.createNewApplicationService();
			ServiceDiscovery.registerService(applicationService);
		}

		return applicationService;
	}

}