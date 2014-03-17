//  
//  PostServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.post;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class PostServiceProvider {

	/**
	 * @return
	 */
	public static IPostService provide() {
		IPostService postService = null;

		if ((postService = (IPostService) ServiceDiscovery.getService(ServiceType.ServiceTypePost.toString())) == null) {
			postService = PostServiceFactory.createNewPostService();
			ServiceDiscovery.registerService(postService);
		}

		return postService;
	}

}