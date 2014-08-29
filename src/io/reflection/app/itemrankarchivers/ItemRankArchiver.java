//
//  ItemRankArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

import java.util.List;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;

/**
 * @author billy1380
 * 
 */
public interface ItemRankArchiver {
	/**
	 * 
	 * @param id
	 */
	void enqueue(Long id);

	/**
	 * 
	 * @param id
	 * @throws DataAccessException
	 */
	void archiveRank(Long id) throws DataAccessException;

	/**
	 * 
	 * @param rank
	 * @throws DataAccessException
	 */
	void archive(Rank rank) throws DataAccessException;

	/**
	 * @param slice
	 * @param item
	 * @param store
	 * @param country
	 * @param category
	 * @return
	 */
	String createKey(Long slice, Item item, Store store, Country country, Category category);

	/**
	 * @param key
	 * @return
	 */
	List<Rank> getItemRanks(String key);

}
