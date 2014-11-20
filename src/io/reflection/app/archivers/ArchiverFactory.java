//
//  ItemRankArchiverFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivers;

/**
 * @author billy1380
 * 
 */
public class ArchiverFactory {

	private static ItemRankArchiver rank = null;
	private static ItemSaleArchiver sale = null;

	/**
	 * @param store
	 * @return
	 */
	public static ItemRankArchiver getItemRankArchiver() {
		if (rank == null) {
			rank = new DefaultItemRankArchiver();
		}

		return rank;
	}

	public static ItemSaleArchiver getItemSaleArchiver() {
		if (sale == null) {
			sale = new DefaultItemSaleArchiver();
		}

		return sale;
	}

}
