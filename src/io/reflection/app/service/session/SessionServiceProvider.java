//  
//  SessionServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.session;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class SessionServiceProvider {

	/**
	 * @return
	 */
	public static ISessionService provide() {
		ISessionService sessionService = null;

		if ((sessionService = (ISessionService) ServiceDiscovery.getService(ServiceType.ServiceTypeSession.toString())) == null) {
			sessionService = SessionServiceFactory.createNewSessionService();
			ServiceDiscovery.registerService(sessionService);
		}

		return sessionService;
	}

}