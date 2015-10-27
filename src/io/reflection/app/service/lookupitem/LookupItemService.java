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
import java.util.Map;
import java.util.logging.Logger;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.service.item.ItemServiceProvider;

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

		Connection lkpConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeLookupItem.toString());

		try {
			lkpConnection.connect();
			LOG.log(GaeLevel.DEBUG, String.format("Loading lookup items for data account id %s", dataAccountId));
			lkpConnection.executeQuery(getLookupItemsForDataAccountQuery);

			ArrayList<LookupItem> list = new ArrayList<LookupItem>();
			while (lkpConnection.fetchNextRow()) {
				// create the item from the resultset
				LookupItem item = new LookupItem()
						.dataaccountid(lkpConnection.getCurrentRowInteger("dataaccountid"))
						.itemid(lkpConnection.getCurrentRowInteger("itemid"))
						.parentid(lkpConnection.getCurrentRowInteger("parentid"))
						.title(lkpConnection.getCurrentRowString("title"))
						.country(lkpConnection.getCurrentRowString("country"))
						.currency(lkpConnection.getCurrentRowString("currency"))
						.price(lkpConnection.getCurrentRowInteger("price"))
						.sku(lkpConnection.getCurrentRowString("sku"))
						.parentsku(lkpConnection.getCurrentRowString("parentsku"));

				list.add(item);
			}

			LOG.log(GaeLevel.DEBUG, String.format("Loaded %d entries", list.size()));

			return list;
		} finally {
			if (lkpConnection != null) {
				lkpConnection.disconnect();
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

	/**
	 * @param dataAccountId
	 * @return
	 * @throws DataAccessException
	 */
	public List<Item> getDataAccountItems(DataAccount dataAccount) throws DataAccessException {
		Long dataAccountId = dataAccount.id;

		ArrayList<Item> items = new ArrayList<Item>();
		Map<String, Item> itemsFoundInSalesSummary = new HashMap<String, Item>();
		List<String> itemIdsToLookForInRanks = new ArrayList<String>();

		String getLookupItemsForDataAccountQuery = String.format("select itemid, title from lkp_items where dataaccountid=%s and parentsku is null group by itemid", dataAccountId);

		Connection lkpConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeLookupItem.toString());

		try {
			lkpConnection.connect();
			LOG.log(GaeLevel.DEBUG, String.format("Loading parent items for data account id %s", dataAccountId));
			lkpConnection.executeQuery(getLookupItemsForDataAccountQuery);

			while (lkpConnection.fetchNextRow()) {

				Item item = new Item();
				item.internalId = lkpConnection.getCurrentRowString("itemid");
				item.name = lkpConnection.getCurrentRowString("title");
				item.creatorName = dataAccount.developerName;

				itemsFoundInSalesSummary.put(item.internalId, item);

				itemIdsToLookForInRanks.add(item.internalId);
			}

			// return only Items which were found in the item table (these will have icons. we found during rank gathers)
			items.addAll(ItemServiceProvider.provide().getInternalIdItemBatch(itemIdsToLookForInRanks));
			// remove all the items found from ranks (via item table) and
			for (Item itemFromRanks : items) {
				String internalId = itemFromRanks.internalId;

				itemsFoundInSalesSummary.remove(internalId);
			}

			items.addAll(itemsFoundInSalesSummary.values());

			return items;
		} finally {
			if (lkpConnection != null) {
				lkpConnection.disconnect();
			}
		}
	}
}
