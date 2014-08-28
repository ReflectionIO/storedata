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

	/**
	 * @param store
	 * @return
	 */
	public static ItemRankArchiver getItemRankArchiverForStore(String store) {
		ItemRankArchiver predictor = null;

		if ("ios".equals(store.toLowerCase())) {
			// ios store
			predictor = new DefaultItemRankArchiver();
		} else if ("azn".equals(store.toLowerCase())) {
			// amazon store
		} else if ("gpl".equals(store.toLowerCase())) {
			// google play store
		}

		return predictor;
	}

}
