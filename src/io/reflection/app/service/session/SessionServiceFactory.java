//  
//  SessionServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.session;

final class SessionServiceFactory {

	/**
	 * @return
	 */
	public static ISessionService createNewSessionService() {
		return new SessionService();
	}

}