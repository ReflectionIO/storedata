//
//  ItemRankArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public interface ItemRankArchiver {
	/**
	 * 
	 * @param id
	 */
	void enqueueIdRank(Long id);

	/**
	 * Add a set of rank ids to be queued
	 * 
	 * @param pager
	 *            the page of rank ids to be queued
	 * @param more
	 *            indicates to the archiver to queue the next set
	 */
	void enqueuePagerRanks(Pager pager, Boolean next);

	/**
	 * 
	 * @param id
	 */
	void enqueueIdFeedFetch(Long id);
	
	/**
	 * 
	 * @param id
	 */
	void archiveIdFeedFetchRanks(Long id);

	/**
	 * 
	 * @param id
	 * @throws DataAccessException
	 */
	void archiveIdRank(Long id) throws DataAccessException;

	/**
	 * 
	 * @param rank
	 * @throws DataAccessException
	 */
	void archiveRank(Rank rank) throws DataAccessException;

	/**
	 * 
	 * @param slice
	 * @param item
	 * @param form
	 * @param store
	 * @param country
	 * @param category
	 * @return
	 */
	String createKey(Long slice, Item item, FormType form, Store store, Country country, Category category);

	/**
	 * @param key
	 * @return
	 */
	List<Rank> getItemRanks(String key);

}
