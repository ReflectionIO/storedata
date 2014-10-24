//
//  CounterServiceFactory.java
//  storedata
//
//  Created by Stefano Capuzzi on 23 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.persistentcounter;

/**
 * @author Stefano Capuzzi
 *
 */
public class CounterServiceFactory {

	private static AppEngineDataStoreCounterService counterService = null;

	public static ICounterService getCounterService() {
		if (counterService == null) {
			counterService = new AppEngineDataStoreCounterService();
		}
		return counterService;
	}
}
