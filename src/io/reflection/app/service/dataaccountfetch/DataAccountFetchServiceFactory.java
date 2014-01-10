//  
//  DataAccountFetchServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on January 9, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccountfetch;

final class DataAccountFetchServiceFactory {

	/**
	 * @return
	 */
	public static IDataAccountFetchService createNewDataAccountFetchService() {
		return new DataAccountFetchService();
	}

}