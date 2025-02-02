//
//  ISaleService.java
//  reflection.io
//
//  Created by William Shakour on December 30, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.sale;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spacehopperstudios.service.IService;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.helpers.SaleSummaryHelper.SALE_SOURCE;

public interface ISaleService extends IService {

	// iPhone
	public static final String	FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS			= "1";
	public static final String	UPDATE_IPHONE_AND_IPOD_TOUCH_IOS								= "7";

	// Universal
	public static final String	FREE_OR_PAID_APP_UNIVERSAL_IOS									= "1F";
	public static final String	UPDATE_UNIVERSAL_IOS														= "7F";

	// iPad
	public static final String	FREE_OR_PAID_APP_IPAD_IOS												= "1T";
	public static final String	UPDATE_IPAD_IOS																	= "7T";

	// IAP
	public static final String	INAPP_PURCHASE_PURCHASE_IOS											= "IA1";
	// Subscription
	public static final String	INAPP_PURCHASE_SUBSCRIPTION_IOS									= "IA9";
	// Auto-renewable subscription
	public static final String	INAPP_PURCHASE_AUTO_RENEWABLE_SUBSCRIPTION_IOS	= "IAY";
	// Free subscription
	public static final String	INAPP_PURCHASE_FREE_SUBSCRIPTION_IOS						= "IAC";

	// Mac App - TO IGNORE
	public static final String	FREE_OR_PAID_APP_MAC_APP												= "F1";
	public static final String	UPDATE_MAC_APP																	= "F7";
	public static final String	INAPP_PURCHASE_MAC_APP													= "FI1";

	// Custom
	public static final String	PAID_APP_CUSTOM_IPHONE_AND_IPOD_TOUCH_IOS				= "1E";
	public static final String	PAID_APP_CUSTOM_IPAD_IOS												= "1EP";
	public static final String	PAID_APP_CUSTOM_UNIVERSAL_IOS										= "1EU";

	/**
	 * @param id
	 * @return
	 */
	public Sale getSale(Long id) throws DataAccessException;

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
	 *          The item internal id
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
	public DataAccount getDataAccount(String itemId) throws DataAccessException;

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
	 * @param itemIds
	 * @param start
	 * @param end
	 * @return
	 * @throws DataAccessException
	 */
	public Map<String, Sale> getItemPrices(String[] itemIds, String country, Date start, Date end) throws DataAccessException;

	/**
	 * @param id
	 * @param date
	 * @return
	 * @throws DataAccessException
	 */
	public boolean summariseSalesForDataAccountOnDate(Long id, Date date) throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param gatherFrom
	 * @param gatherTo
	 * @return
	 * @throws DataAccessException
	 */
	public List<SimpleEntry<String, String>> getSoldItemIdsForAccountInDateRange(Long dataAccountId, Date gatherFrom, Date gatherTo) throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param mainItemId
	 * @param date
	 * @return
	 * @throws DataAccessException
	 */
	public List<String> getIapItemIdsForParentItemOnDate(Long dataAccountId, String mainItemId, Date date) throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param itemId
	 * @param parse
	 * @param parse2
	 * @return
	 * @throws DataAccessException
	 */
	public List<String> getIapItemIdsForParentItemBetweenDates(Long dataAccountId, String itemId, Date parse, Date parse2) throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param date
	 */
	public void deleteSales(Long dataAccountId, Date date);

	/**
	 * @param dataaccountid
	 * @param sales
	 * @param sale_source
	 * @return
	 * @throws DataAccessException
	 */
	public boolean summariseSales(Long dataaccountid, List<Sale> sales, SALE_SOURCE saleSource) throws DataAccessException;

	/**
	 * @param dataAccountId
	 * @param date
	 * @return
	 * @throws DataAccessException
	 */
	ArrayList<Sale> getSalesForDataAccountOnDate(Long dataAccountId, Date date) throws DataAccessException;

	/**
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getDataAccountsWithSalesBetweenDates(Date dateFrom, Date dateTo) throws DataAccessException;

	/**
	 * @param from
	 * @param to
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getDataAccountIdsWithSaleSummariesBetweenDates(Date from, Date to) throws DataAccessException;

	/**
	 * @param date
	 * @param country
	 * @return
	 * @throws DataAccessException
	 */
	public HashMap<Long, ArrayList<Long>> getItemSalesInTop200(Date date, String country) throws DataAccessException;

}
