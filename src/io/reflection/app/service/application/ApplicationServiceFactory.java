//  
//  ApplicationServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.application;

final class ApplicationServiceFactory {

	/**
	 * @return
	 */
	public static IApplicationService createNewApplicationService() {
		return new ApplicationService();
	}

}