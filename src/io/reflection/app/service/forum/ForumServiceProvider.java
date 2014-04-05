//  
//  ForumServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.forum;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class ForumServiceProvider {

	/**
	 * @return
	 */
	public static IForumService provide() {
		IForumService forumService = null;

		if ((forumService = (IForumService) ServiceDiscovery.getService(ServiceType.ServiceTypeForum.toString())) == null) {
			forumService = ForumServiceFactory.createNewForumService();
			ServiceDiscovery.registerService(forumService);
		}

		return forumService;
	}

}