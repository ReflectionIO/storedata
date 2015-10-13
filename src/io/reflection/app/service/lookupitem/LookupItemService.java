//
//  LookupItemService.java
//  storedata
//
//  Created by mamin on 7 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.service.lookupitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;

/**
 * @author mamin
 *
 */
public class LookupItemService {
	private transient static final Logger LOG = Logger.getLogger(LookupItemService.class.getName());
	public static LookupItemService INSTANCE = new LookupItemService();

	private LookupItemService() {
		// private constructor just to make sure this class is used as a singleton
	}

	public List<LookupItem> getLookupItemsForAccount(Long dataAccountId) throws DataAccessException {

		String getLookupItemsForDataAccountQuery = String.format("select * from lkp_items where dataaccountid=%s", dataAccountId);

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeLookupItem.toString());

		try {
			saleConnection.connect();
			LOG.log(GaeLevel.DEBUG, String.format("Loading lookup items for data account id %s", dataAccountId));
			saleConnection.executeQuery(getLookupItemsForDataAccountQuery);

			ArrayList<LookupItem> list = new ArrayList<LookupItem>();
			while (saleConnection.fetchNextRow()) {
				// create the item from the resultset
				LookupItem item = new LookupItem()
						.dataaccountid(saleConnection.getCurrentRowInteger("dataaccountid"))
						.itemid(saleConnection.getCurrentRowInteger("itemid"))
						.parentid(saleConnection.getCurrentRowInteger("parentid"))
						.title(saleConnection.getCurrentRowString("title"))
						.country(saleConnection.getCurrentRowString("country"))
						.currency(saleConnection.getCurrentRowString("currency"))
						.price(saleConnection.getCurrentRowInteger("price"))
						.sku(saleConnection.getCurrentRowString("sku"))
						.parentsku(saleConnection.getCurrentRowString("parentsku"));

				list.add(item);
			}

			LOG.log(GaeLevel.DEBUG, String.format("Loaded %d entries", list.size()));

			return list;
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
	}


	/**
	 * @param lookupItemsForAccount
	 * @return
	 */
	public HashMap<Integer, HashMap<String, LookupItem>> mapItemsByCountry(List<LookupItem> lookupItemsForAccount) {
		if (lookupItemsForAccount == null) return null;

		LOG.log(GaeLevel.DEBUG, String.format("Mapping lookup items by itemid and country"));

		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = new HashMap<Integer, HashMap<String, LookupItem>>(lookupItemsForAccount.size());

		for (LookupItem item : lookupItemsForAccount) {
			// look up the item from the map
			HashMap<String, LookupItem> itemCountriesMap = itemByCountryMap.get(item.itemid);
			if (itemCountriesMap == null) {
				// if this is the first time it is encountered, create the countries hashmap
				itemCountriesMap = new HashMap<String, LookupItem>();
				itemByCountryMap.put(item.itemid, itemCountriesMap);
			}

			// add this item as the lookup item for this country
			itemCountriesMap.put(item.country, item);
		}

		LOG.log(GaeLevel.DEBUG, String.format("Returning %d items", itemByCountryMap.size()));

		return itemByCountryMap;
	}

	/**
	 * @param lookupItemsForAccount
	 * @return
	 */
	public HashMap<String, Integer> mapItemsBySku(List<LookupItem> lookupItemsForAccount) {
		if (lookupItemsForAccount == null) return null;

		LOG.log(GaeLevel.DEBUG, String.format("Mapping items by their skus"));
		HashMap<String, Integer> skuByItemMap = new HashMap<String, Integer>(lookupItemsForAccount.size());

		for (LookupItem item : lookupItemsForAccount) {
			if (!skuByItemMap.containsKey(item.sku)) {
				skuByItemMap.put(item.sku, item.itemid);
			}
		}

		LOG.log(GaeLevel.DEBUG, String.format("Returning %d items mapped by their sku", skuByItemMap.size()));

		return skuByItemMap;
	}

	/**
	 * @param lookupItemsForAccount
	 * @return
	 */
	public HashMap<String, String> mapItemsByParentId(List<LookupItem> lookupItemsForAccount) {
		if (lookupItemsForAccount == null) return null;
		HashMap<String, String> parentIdByItemIds = new HashMap<String, String>();

		LOG.log(GaeLevel.DEBUG, String.format("Mapping iap item ids by parent id"));

		for (LookupItem currentItem : lookupItemsForAccount) {
			if (currentItem.parentsku != null && currentItem.parentsku.length() >= 0) {
				continue; // we ignore IAPs for this. Only main item ids are mapped
			}

			if (parentIdByItemIds.containsKey(currentItem.itemid)) {
				continue;
			}

			ArrayList<Integer> iapIds = new ArrayList<Integer>();

			for (LookupItem checkIapItem : lookupItemsForAccount) {
				if (currentItem.itemid.equals(checkIapItem.parentid)) {
					if (!iapIds.contains(checkIapItem.itemid)) {
						iapIds.add(checkIapItem.itemid);
					}
				}
			}

			StringBuilder ids = new StringBuilder();
			for (Integer iapItemId : iapIds) {
				if (ids.length() > 0) {
					ids.append(',');
				}

				ids.append(iapItemId);
			}

			parentIdByItemIds.put(currentItem.itemid.toString(), ids.toString());
		}

		LOG.log(GaeLevel.DEBUG, String.format("Returning %d main items and their iap ids", parentIdByItemIds.size()));

		return parentIdByItemIds;
	}
}
