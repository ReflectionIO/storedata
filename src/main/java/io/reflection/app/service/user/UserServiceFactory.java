//  
//  UserServiceFactory.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.user;

final class UserServiceFactory {

	/**
	 * @return
	 */
	public static IUserService createNewUserService() {
		return new UserService();
	}

}