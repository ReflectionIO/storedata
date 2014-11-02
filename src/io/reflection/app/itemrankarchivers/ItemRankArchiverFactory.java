//
//  ItemRankArchiverFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

/**
 * @author billy1380
 * 
 */
public class ItemRankArchiverFactory {

	private static ItemRankArchiver one = null;

	/**
	 * @param store
	 * @return
	 */
	public static ItemRankArchiver get() {
		if (one == null) {
			one = new DefaultItemRankArchiver();
		}

		return one;
	}

}
