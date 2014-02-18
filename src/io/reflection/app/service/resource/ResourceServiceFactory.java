//  
//  ResourceServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.resource;

final class ResourceServiceFactory {

	/**
	 * @return
	 */
	public static IResourceService createNewResourceService() {
		return new ResourceService();
	}

}