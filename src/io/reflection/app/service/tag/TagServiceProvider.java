//  
//  TagServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.tag;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class TagServiceProvider {

	/**
	 * @return
	 */
	public static ITagService provide() {
		ITagService tagService = null;

		if ((tagService = (ITagService) ServiceDiscovery.getService(ServiceType.ServiceTypeTag.toString())) == null) {
			tagService = TagServiceFactory.createNewTagService();
			ServiceDiscovery.registerService(tagService);
		}

		return tagService;
	}

}