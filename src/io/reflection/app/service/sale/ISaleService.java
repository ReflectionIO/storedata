//  
//  ISaleService.java
//  reflection.io
//
//  Created by William Shakour on December 30, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.sale;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.service.ServiceType;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.spacehopperstudios.service.IService;

public interface ISaleService extends IService {

	public static final String DEFAULT_NAME = ServiceType.ServiceTypeSale.toString();
	
	/**
	 * @param id
	 * @return
	 */
	public Sale getSale(Long id) throws DataAccessException;

	/**
	 * @param sale
	 * @return
	 */
	public Sale addSale(Sale sale) throws DataAccessException;

	/**
	 * @param sale
	 * @return
	 */
	public Sale updateSale(Sale sale) throws DataAccessException;

	/**
	 * @param sale
	 */
	public void deleteSale(Sale sale) throws DataAccessException;

	/**
	 * @param linkedAccount
	 * @param pager
	 * @return
	 */
	public List<Item> getDataAccountItems(DataAccount dataAccount, List<String> typeIdentifiers, Pager pager) throws DataAccessException;

	/**
	 * @return
	 */
	public Long getDataAccountItemsCount(DataAccount dataAccount, List<String> typeIdentifiers) throws DataAccessException;

	/**
	 * @param sales
	 * @return
	 * @throws DataAccessException
	 */
	public Long addSalesBatch(Collection<Sale> sales) throws DataAccessException;

	/**
	 * @param country
	 * @param category
	 * @param linkedAccount
	 * @param start
	 * @param end
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Sale> getSales(Country country, Category category, DataAccount linkedAccount, Date start, Date end, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param country
	 * @param category
	 * @param linkedAccount
	 * @param start
	 * @param end
	 * @return
	 * @throws DataAccessException
	 */
	public Long getSalesCount(Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException;

	/**
	 * 
	 * @param item
	 * @param country
	 * @param category
	 * @param linkedAccount
	 * @param start
	 * @param end
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Sale> getItemSales(Item item, Country country, Category category, DataAccount linkedAccount, Date start, Date end, Pager pager)
			throws DataAccessException;

	/**
	 * 
	 * @param item
	 * @param country
	 * @param category
	 * @param linkedAccount
	 * @param start
	 * @param end
	 * @return
	 * @throws DataAccessException
	 */
	public Long getItemSalesCount(Item item, Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException;

	/**
	 * Gets a "mock" item id based on the sales data
	 * 
	 * @param itemId
	 *            The item internal id
	 * @return
	 * @throws DataAccessException
	 */
	public Item getItem(String itemId) throws DataAccessException;

	/**
	 * Gets the data account for an item
	 * 
	 * @param internalId
	 * @return
	 * @throws DataAccessException
	 */
	public DataAccount getItemIdDataAccount(String itemId) throws DataAccessException;

	/**
	 * 
	 * @param country
	 * @param linkedAccount
	 * @param start
	 * @param end
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getSaleIds(Country country, DataAccount linkedAccount, Date start, Date end) throws DataAccessException;

	/**
	 * Get all sale ids
	 * 
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getAllSaleIds(Pager pager) throws DataAccessException;

	/**
	 * Get data account fetch sales
	 * 
	 * @param dataAccountFetch
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Sale> getDataAccountFetchSales(DataAccountFetch dataAccountFetch, Pager pager) throws DataAccessException;

	/**
	 * Get data account fetch sale ids
	 * 
	 * @param dataAccountFetch
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getDataAccountFetchSaleIds(DataAccountFetch dataAccountFetch, Pager pager) throws DataAccessException;

	/**
	 * @param parentIdentifier
	 * @return
	 * @throws DataAccessException
	 */
	public String getSkuItemId(String sku) throws DataAccessException;

	/**
	 * @param saleIds
	 * @return
	 * @throws DataAccessException
	 */
	public List<Sale> getSalesBatch(Collection<Long> saleIds) throws DataAccessException;

}
