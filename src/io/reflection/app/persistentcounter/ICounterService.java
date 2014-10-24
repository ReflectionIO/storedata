//
//  ICounterService.java
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
public interface ICounterService {

	/**
	 * Retrieve the value of this sharded counter.
	 * 
	 * @param counterName
	 * @return
	 */
	Long getCount(String counterName);

	/**
	 * Increment the value of this sharded counter of 1 unit.
	 * 
	 * @param counterName
	 */
	void increment(String counterName);

	/**
	 * Increment the value of this sharded counter by the specified value.
	 * 
	 * @param counterName
	 * @param incrementValue
	 */
	void increment(String counterName, Integer incrementValue);
}
