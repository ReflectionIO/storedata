//  
//  ResourceServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.resource;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class ResourceServiceProvider {

	/**
	 * @return
	 */
	public static IResourceService provide() {
		IResourceService resourceService = null;

		if ((resourceService = (IResourceService) ServiceDiscovery.getService(ServiceType.ServiceTypeResource.toString())) == null) {
			resourceService = ResourceServiceFactory.createNewResourceService();
			ServiceDiscovery.registerService(resourceService);
		}

		return resourceService;
	}

}