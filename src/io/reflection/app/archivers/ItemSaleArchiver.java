//
//  ItemSalesArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Sale;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public interface ItemSaleArchiver {
	/**
	 * 
	 * @param id
	 */
	void enqueueIdSale(Long id);

	/**
	 * Add a set of sale ids to be queued
	 * 
	 * @param pager
	 *            the page of sale ids to be queued
	 * @param more
	 *            indicates to the archiver to queue the next set
	 */
	void enqueuePagerSales(Pager pager, Boolean next);

	/**
	 * 
	 * @param id
	 */
	void enqueueIdDataAccountFetch(Long id);

	/**
	 * 
	 * @param id
	 */
	void archiveIdDataAccountFetchSales(Long id);

	/**
	 * 
	 * @param id
	 * @throws DataAccessException
	 */
	void archiveIdSale(Long id) throws DataAccessException;

	/**
	 * 
	 * @param sale
	 * @throws DataAccessException
	 */
	void archiveSale(Sale sale) throws DataAccessException;

	/**
	 * 
	 * @return
	 */
	String createItemRanksKey(Long slice, Item item, Country country, FormType form);

	/**
	 * 
	 * @param slice
	 * @param dataAccount
	 * @param country
	 * @param form
	 * @return
	 */
	String createRanksKey(Long slice, DataAccount dataAccount, Country country, FormType form);

	/**
	 * @param key
	 * @return
	 */
	List<Rank> getRanks(String key);

	/**
	 * Creates a key to store and retrieve linked account times
	 * 
	 * @param dataAccount
	 * @param form
	 * @return
	 */
	String createItemsKey(DataAccount dataAccount, FormType form);

	/**
	 * @param key
	 * @return
	 */
	List<String> getItemsIds(String key);

}
