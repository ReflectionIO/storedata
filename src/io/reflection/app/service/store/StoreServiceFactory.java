//  
//  StoreServiceFactory.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

final class StoreServiceFactory {

	/**
	 * @return
	 */
	public static IStoreService createNewStoreService() {
		return new StoreService();
	}

}