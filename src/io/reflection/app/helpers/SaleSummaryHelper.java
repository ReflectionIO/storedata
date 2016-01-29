//
//  SaleSummaryHelper.java
//  storedata
//
//  Created by Mitul Amin on 30 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.datatypes.shared.SaleSummary;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.service.lookupitem.LookupItemService;

/**
 * @author mamin
 *
 */
public class SaleSummaryHelper {
	private static final Logger						LOG				= Logger.getLogger(SaleSummaryHelper.class.getName());

	public static final SaleSummaryHelper	INSTANCE	= new SaleSummaryHelper();

	public static enum SALE_SOURCE {
		DB, INGEST;
	}

	private SaleSummaryHelper() {

	}

	public boolean summariseSales(Long dataaccountid, List<Sale> sales, SALE_SOURCE saleSource, Connection saleConnection) throws DataAccessException {
		if (dataaccountid == null) {
			LOG.log(GaeLevel.DEBUG, String.format("Data account id is null. Returning without doing anything."));
			return false;
		}
		if (sales == null || sales.size() == 0) {
			LOG.log(GaeLevel.DEBUG, String.format("Sales is %s. Returning without doing anything.", sales == null ? "NULL" : "an empty array"));
			return false;
		}

		LOG.log(GaeLevel.DEBUG, String.format("Summarising data account id %s", dataaccountid));

		/*
		 * Over all logic for this process is:
		 * 	1. We load all known items for this dataaccount from the DB (lookup items). This gives us the itemids, parentids, sku / parent identifier (parentsku), country, currency, price, title
		 * 		1.1 Note that we also store the mapping of parentsku to the parent's id
		 *
		 * 	2. For each sale item, we find the main item for it (if the sale is an IAP we use the parent sku to find the parent else we use the item id).
		 * 		2.1 In case we find IAPs that we don't know the main item for, we put these away to deal with later as the main item may there later in the sale array
		 * 		2.2 Once we have found the main item that the current sale item relates to, we create / update the sale summary for this item/country combination
		 * 		2.3 While creating / update the sale summary we may also update the main lookup item to get the latest title, price, currency changes.
		 *
		 * 	3. Once all the sale items have been processed, process all the IAPs we could not find parent items in the first pass.
		 * 		3.1 If we still can't find the parent item for the IAP we log it and drop the item.
		 *
		 * 	4. Now that we have all the summaries and lookup item updates we start to persist these
		 * 		4.1 We update/insert (via mysql upsert) all the lookup items
		 * 		4.2 The sale summaries are then updated / inserted (via mysql upsert) as well
		 */

		ArrayList<LookupItem> updatedLookupItems = new ArrayList<LookupItem>();

		LookupItemService lookupService = LookupItemService.INSTANCE;

		List<LookupItem> lookupItemsForAccount = lookupService.getLookupItemsForAccount(dataaccountid);

		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = lookupService.mapItemsByCountry(lookupItemsForAccount);
		HashMap<String, Integer> skuByItemMap = lookupService.mapItemsBySku(lookupItemsForAccount);

		// we will store all items->countries->actual_sale_summary in this map of a map
		HashMap<Integer, HashMap<String, SaleSummary>> summaries = summariseSales(dataaccountid, sales, saleSource, updatedLookupItems, itemByCountryMap, skuByItemMap);

		// Lets update the DB with all the summaries and newly found items
		// 4.1 upsert all lookup items in
		upsertLookupItems(updatedLookupItems, dataaccountid, saleConnection);

		// 4.2 upsert all the sales summaries
		upsertSaleSummaries(summaries, dataaccountid, saleConnection);

		return true;
	}

	public HashMap<Integer, HashMap<String, SaleSummary>> summariseSales(Long dataaccountid, List<Sale> sales, SALE_SOURCE saleSource, ArrayList<LookupItem> updatedLookupItems,
			HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap, HashMap<String, Integer> skuByItemMap) {
		HashMap<Integer, HashMap<String, SaleSummary>> summaries = new HashMap<Integer, HashMap<String, SaleSummary>>();

		// this will hold all sale items that we don't know the main item for
		ArrayList<Sale> iapItemsMissingParents = new ArrayList<Sale>();

		LOG.log(GaeLevel.DEBUG, String.format("Processing %d sales", sales.size()));
		for (Sale sale : sales) {
			LookupItem mainLookupItemForSummary = findOrCreateMainLookupItem(dataaccountid, sale, itemByCountryMap, skuByItemMap, updatedLookupItems, saleSource);

			if (mainLookupItemForSummary == null) {
				// We have never seen a parent item with this sku before.
				// We may find its parent later in the sale array so lets park this item for processing later.
				// NOTE THIS IS THE ONLY CASE IN WHICH WE SKIP PROCESSING A SALE ITEM.
				iapItemsMissingParents.add(sale);

				continue;
			}

			// WE HAVE LOOKED UP THE MAIN ITEM FOR THIS SALE
			processSummaryForSale(dataaccountid, summaries, sale, mainLookupItemForSummary, updatedLookupItems, saleSource);
		}   // end of for each sale
		LOG.log(GaeLevel.DEBUG, String.format("Finished the first pass of processing sales"));

		// Having processed all the sales at least once, lets go over the IAP items that we didn't know the parents of
		for (Sale sale : iapItemsMissingParents) {
			LookupItem mainLookupItemForSummary = findOrCreateMainLookupItem(dataaccountid, sale, itemByCountryMap, skuByItemMap, updatedLookupItems, saleSource);

			if (mainLookupItemForSummary == null) {
				// WE STILL DON'T know the parent item of this IAP. We have to drop it.
				LOG.log(GaeLevel.DEBUG, String.format("Got an IAP with an unknown parent item. Dropping it. Dataaccountid: %s, itemid: %s, country: %s",
						dataaccountid, sale.item.internalId, sale.country));
				continue;
			}

			// WE HAVE LOOKED UP THE MAIN ITEM FOR THIS SALE
			processSummaryForSale(dataaccountid, summaries, sale, mainLookupItemForSummary, updatedLookupItems, saleSource);
		}
		LOG.log(GaeLevel.DEBUG, String.format("Finished the second pass of processing sales"));
		return summaries;
	}

	/**
	 * @param dataaccountid
	 * @param sale
	 * @param itemByCountryMap
	 * @param skuByItemMap
	 * @param updatedLookupItems
	 * @param saleSource
	 * @return
	 */
	public LookupItem findOrCreateMainLookupItem(Long dataaccountid, Sale sale, HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap,
			HashMap<String, Integer> skuByItemMap, ArrayList<LookupItem> updatedLookupItems, SALE_SOURCE saleSource) {

		Integer itemId = Integer.parseInt(sale.item.internalId);
		LookupItem mainLookupItemForSummary = null;

		if (sale.typeIdentifier.startsWith("1") || sale.typeIdentifier.startsWith("7")) {
			// this is a main item download / purchase / update

			HashMap<String, LookupItem> lookupItemCountriesMap = itemByCountryMap.get(itemId);

			if (lookupItemCountriesMap == null) {
				// LOG.log(GaeLevel.DEBUG, String.format("Could not find this main item %s in the lkp_items table ", itemId));
				// This is the first time we have come across this item. We need to add it to the lookup items table
				lookupItemCountriesMap = new HashMap<String, LookupItem>();
				itemByCountryMap.put(itemId, lookupItemCountriesMap);

				mainLookupItemForSummary = new LookupItem();
				mainLookupItemForSummary.dataaccountid = dataaccountid.intValue();
				mainLookupItemForSummary.itemid = itemId;
				mainLookupItemForSummary.country = sale.country;

				// update price, currency, title, etc.
				updateLookupItemWithSaleDetails(sale, saleSource, mainLookupItemForSummary);

				lookupItemCountriesMap.put(sale.country, mainLookupItemForSummary);

				lookupItemUpdated(updatedLookupItems, mainLookupItemForSummary);
			} else {
				mainLookupItemForSummary = lookupItemCountriesMap.get(sale.country);
				if (mainLookupItemForSummary == null) {
					// LOG.log(GaeLevel.DEBUG, String.format("Could not find this main item %s in %s in the lkp_items table ", itemId, sale.country));
					// we have seen this item before but never for this country. Need to add it to the DB.

					mainLookupItemForSummary = new LookupItem();
					mainLookupItemForSummary.dataaccountid = dataaccountid.intValue();
					mainLookupItemForSummary.itemid = itemId;
					mainLookupItemForSummary.country = sale.country;

					// update price, currency, title, etc.
					updateLookupItemWithSaleDetails(sale, saleSource, mainLookupItemForSummary);

					lookupItemCountriesMap.put(sale.country, mainLookupItemForSummary);

					lookupItemUpdated(updatedLookupItems, mainLookupItemForSummary);
				}
			}

			// END IF LOOKUP FOR MAIN ITEM
		} else {
			// this is an IAP

			HashMap<String, LookupItem> iapLookupItemCountriesMap = itemByCountryMap.get(itemId);
			LookupItem iapLookupItem = null;

			if (iapLookupItemCountriesMap == null) {
				// LOG.log(GaeLevel.DEBUG, String.format("Could not find this IAP item %s in the lkp_items table ", itemId));

				// This is the first time we have come across this item. We need to add it to the lookup items table
				iapLookupItemCountriesMap = new HashMap<String, LookupItem>();
				itemByCountryMap.put(itemId, iapLookupItemCountriesMap);

				iapLookupItem = new LookupItem();
				iapLookupItem.dataaccountid = dataaccountid.intValue();
				iapLookupItem.itemid = itemId;
				iapLookupItem.country = sale.country;

				// update price, currency, title, etc.
				updateLookupItemWithSaleDetails(sale, saleSource, iapLookupItem);

				iapLookupItemCountriesMap.put(sale.country, iapLookupItem);

				lookupItemUpdated(updatedLookupItems, iapLookupItem);
			} else {
				iapLookupItem = iapLookupItemCountriesMap.get(sale.country);

				if (iapLookupItem == null) {
					// LOG.log(GaeLevel.DEBUG, String.format("Could not find this IAP item %s in %s in the lkp_items table but it does exist in at least 1 other country", itemId, sale.country));
					// we have seen this item before but never for this country. Need to add it to the DB.

					iapLookupItem = new LookupItem();
					iapLookupItem.dataaccountid = dataaccountid.intValue();
					iapLookupItem.itemid = itemId;
					iapLookupItem.country = sale.country;

					updateLookupItemWithSaleDetails(sale, saleSource, iapLookupItem);

					iapLookupItemCountriesMap.put(sale.country, iapLookupItem);

					lookupItemUpdated(updatedLookupItems, iapLookupItem);
				}
			}

			Integer parentId = iapLookupItem.parentid;
			if (parentId == null) {
				// TODO
				// LOG.log(GaeLevel.DEBUG, String.format("This sale is an IAP but the lookup item does not have a parent id. Trying to look it up via the sku"));

				parentId = skuByItemMap.get((sale.parentIdentifier == null ? "" : sale.parentIdentifier).trim());
				if (parentId == null)    // We have never seen a parent item with this sku before.
					return null;

				iapLookupItem.parentid = parentId;
				// LOG.log(GaeLevel.DEBUG, String.format("Found a parent child relationship. itemid:%d is a child of %d", itemId, parentId));

				lookupItemUpdated(updatedLookupItems, iapLookupItem);
			}

			HashMap<String, LookupItem> lookupItemCountriesMap = itemByCountryMap.get(parentId);
			if (lookupItemCountriesMap == null) {
				// TODO if lookupItemCountriesMap may be null (exceptions), so we need to create it and then also the mainlookupitemforsummary

				LOG.log(GaeLevel.DEBUG, String.format("Could not find an entry in the lookup items for %d which is supposed to be the parent of %d", parentId, itemId));
			}

			mainLookupItemForSummary = lookupItemCountriesMap == null ? null : lookupItemCountriesMap.get(sale.country);
			if (mainLookupItemForSummary == null) {
				// we have seen this parent item before but never for this country. Need to add it to the DB.

				mainLookupItemForSummary = new LookupItem();
				mainLookupItemForSummary.dataaccountid = dataaccountid.intValue();
				mainLookupItemForSummary.itemid = parentId;
				mainLookupItemForSummary.country = sale.country;

				// we can't tell the main item's title or price but we know it has to be the same currency.
				mainLookupItemForSummary.currency = sale.currency;

				lookupItemCountriesMap.put(sale.country, mainLookupItemForSummary);

				lookupItemUpdated(updatedLookupItems, mainLookupItemForSummary);
			}
			// END OF LOOKUP FOR IAP
		}

		return mainLookupItemForSummary;
	}

	private void lookupItemUpdated(ArrayList<LookupItem> updatedLookupItems, LookupItem lookupItem) {
		if (!updatedLookupItems.contains(lookupItem)) {
			// LOG.log(GaeLevel.DEBUG, String.format("Lookup item updated %s", lookupItem));
			updatedLookupItems.add(lookupItem);
		}
	}

	/**
	 * @param sale
	 * @param saleSource
	 * @param lookupItem
	 */
	public void updateLookupItemWithSaleDetails(Sale sale, SALE_SOURCE saleSource, LookupItem lookupItem) {
		lookupItem.currency = sale.currency;
		lookupItem.title = sale.title;
		lookupItem.parentsku = sale.parentIdentifier == null ? null : sale.parentIdentifier.trim();
		lookupItem.sku = sale.sku == null ? null : sale.sku.trim();
		lookupItem.price = getSaleItemPrice(sale, saleSource);
	}

	/**
	 * @param dataaccountid
	 * @param summary
	 * @param sale
	 * @param mainLookupItemForSummary
	 * @param updatedLookupItems
	 * @param saleSource
	 */
	public void processSummaryForSale(Long dataaccountid, HashMap<Integer, HashMap<String, SaleSummary>> summary, Sale sale, LookupItem mainLookupItemForSummary,
			ArrayList<LookupItem> updatedLookupItems, SALE_SOURCE saleSource) {
		// these two properties we use whether this is the main item or an iap
		int units = sale.units == null ? 0 : sale.units;
		int absUnits = Math.abs(units);
		// we always deal in pennies in this code. The DB code however returns pounds.
		int saleCustomerPrice = getSaleItemPrice(sale, saleSource);

		HashMap<String, SaleSummary> currentItem = summary.get(mainLookupItemForSummary.itemid);
		if (currentItem == null) {
			currentItem = new HashMap<String, SaleSummary>();
			summary.put(mainLookupItemForSummary.itemid, currentItem);
		}

		SaleSummary currentSummary = currentItem.get(sale.country);
		if (currentSummary == null) {
			currentSummary = new SaleSummary();
			currentSummary.dataaccountid = dataaccountid.intValue();
			currentSummary.itemid = String.valueOf(mainLookupItemForSummary.itemid);
			currentSummary.country = sale.country;
			currentSummary.date = sale.begin;

			// these may be null as we may have just created this lookup item. When saving to the DB we need to make sure we fill these details in via lookups.
			currentSummary.currency = mainLookupItemForSummary.currency;
			currentSummary.price = mainLookupItemForSummary.price;
			currentSummary.title = mainLookupItemForSummary.title;

			currentItem.put(sale.country, currentSummary);
		}

		int saleRevenue = absUnits * saleCustomerPrice;
		if (sale.typeIdentifier.startsWith("1") || sale.typeIdentifier.startsWith("7")) {
			// this is a main item download / purchase / update

			if (sale.typeIdentifier.startsWith("1")) {
				// this is a download of a paid app
				if (sale.device != null) {
					switch (sale.device) {
						case "iPod touch":
						case "iPhone":
							currentSummary.iphone_app_revenue += saleRevenue;
							currentSummary.iphone_downloads += units;

							// NOTE for 1, 1T, 1F (purchases / downloads), we update the titles, currency and prices in case they have been updated.
							updateCurrentSummaryTitlePriceAndCurrency(currentSummary, mainLookupItemForSummary, sale, saleCustomerPrice, updatedLookupItems);
							break;
						case "iPad":
							currentSummary.ipad_app_revenue += saleRevenue;
							currentSummary.ipad_downloads += units;

							// NOTE for 1, 1T, 1F (purchases / downloads), we update the titles, currency and prices in case they have been updated.
							updateCurrentSummaryTitlePriceAndCurrency(currentSummary, mainLookupItemForSummary, sale, saleCustomerPrice, updatedLookupItems);
							break;
						default:
							currentSummary.universal_app_revenue += saleRevenue;
							currentSummary.universal_downloads += units;

							// NOTE for 1, 1T, 1F (purchases / downloads), we update the titles, currency and prices in case they have been updated.
							updateCurrentSummaryTitlePriceAndCurrency(currentSummary, mainLookupItemForSummary, sale, saleCustomerPrice, updatedLookupItems);
							break;
					}
				} else {
					switch (sale.typeIdentifier) {
						case "1":
							currentSummary.iphone_app_revenue += saleRevenue;
							currentSummary.iphone_downloads += units;

							// NOTE for 1, 1T, 1F (purchases / downloads), we update the titles, currency and prices in case they have been updated.
							updateCurrentSummaryTitlePriceAndCurrency(currentSummary, mainLookupItemForSummary, sale, saleCustomerPrice, updatedLookupItems);

							break;
						case "1T":
							currentSummary.ipad_app_revenue += saleRevenue;
							currentSummary.ipad_downloads += units;

							// NOTE for 1, 1T, 1F (purchases / downloads), we update the titles, currency and prices in case they have been updated.
							updateCurrentSummaryTitlePriceAndCurrency(currentSummary, mainLookupItemForSummary, sale, saleCustomerPrice, updatedLookupItems);
							break;
						case "1F":
							currentSummary.universal_app_revenue += saleRevenue;
							currentSummary.universal_downloads += units;

							// NOTE for 1, 1T, 1F (purchases / downloads), we update the titles, currency and prices in case they have been updated.
							updateCurrentSummaryTitlePriceAndCurrency(currentSummary, mainLookupItemForSummary, sale, saleCustomerPrice, updatedLookupItems);
							break;
					}
				}

			} else if (sale.typeIdentifier.startsWith("7")) {
				// this is the download of an update of an app
				if (sale.device != null) {
					switch (sale.device) {
						case "iPod touch":
						case "iPhone":
							currentSummary.iphone_updates += units;
							break;
						case "iPad":
							currentSummary.ipad_updates += units;
							break;
						default:
							currentSummary.universal_updates += units;
							break;
					}
				} else {
					switch (sale.typeIdentifier) {
						case "7":
							currentSummary.iphone_updates += units;
							break;
						case "7T":
							currentSummary.ipad_updates += units;
							break;
						case "7F":
							currentSummary.universal_updates += units;
							break;
					}
				}
			}

			boolean lookupItemUpdated = false;

			if (currentSummary.currency != null && !currentSummary.currency.equals(mainLookupItemForSummary.currency)) {
				mainLookupItemForSummary.currency = currentSummary.currency;
				lookupItemUpdated = true;
			}
			if (currentSummary.title != null && !currentSummary.title.equals(mainLookupItemForSummary.title)) {
				mainLookupItemForSummary.title = currentSummary.title;
				lookupItemUpdated = true;
			}
			if (currentSummary.price != null && !currentSummary.price.equals(mainLookupItemForSummary.title)) {
				mainLookupItemForSummary.title = currentSummary.title;
				lookupItemUpdated = true;
			}

			if (lookupItemUpdated) {
				lookupItemUpdated(updatedLookupItems, mainLookupItemForSummary);
			}

			// END IF MAIN ITEM
		} else {
			// this is an IAP
			switch (sale.typeIdentifier) {
				case "IA1":
					currentSummary.iap_revenue += saleRevenue;

					if (sale.device != null) {
						if ("iphone".equalsIgnoreCase(sale.device)) {
							currentSummary.iphone_iap_revenue += saleRevenue;
						} else if ("ipod touch".equalsIgnoreCase(sale.device)) {
							currentSummary.iphone_iap_revenue += saleRevenue;
						} else if ("ipad".equalsIgnoreCase(sale.device)) {
							currentSummary.ipad_iap_revenue += saleRevenue;
						}
					}

					break;
				case "IA9":
				case "IAY":
					currentSummary.subs_revenue += saleRevenue;
					currentSummary.paid_subs_count += units;
					break;
				case "IAC":
					currentSummary.free_subs_count += units;
					break;
			}
			// END OF IAP
		}
	}

	/**
	 * @param currentSummary
	 * @param mainLookupItemForSummary
	 * @param sale
	 * @param saleCustomerPrice
	 * @param updatedLookupItems
	 */
	private void updateCurrentSummaryTitlePriceAndCurrency(SaleSummary currentSummary, LookupItem mainLookupItemForSummary, Sale sale, int saleCustomerPrice, ArrayList<LookupItem> updatedLookupItems) {
		currentSummary.currency = sale.currency;
		currentSummary.price = Math.abs((currentSummary.price == null || currentSummary.price < saleCustomerPrice) ? saleCustomerPrice : currentSummary.price);
		currentSummary.title = sale.title;

		if (!currentSummary.price.equals(mainLookupItemForSummary.price)) {
			mainLookupItemForSummary.price = currentSummary.price;
			lookupItemUpdated(updatedLookupItems, mainLookupItemForSummary);
		}
	}

	/**
	 * This method returns the sale entry's customer price in pennies depending if the source of the sale entry. When loaded from the DB the sales are in pounds
	 * and when created from the auto ingest summaries they are in pennies.
	 *
	 * @param sale
	 * @param saleSource
	 * @return
	 */
	public int getSaleItemPrice(Sale sale, SALE_SOURCE saleSource) {
		return sale.customerPrice == null ? 0 : (saleSource == SALE_SOURCE.DB ? (int) (sale.customerPrice * 100f) : sale.customerPrice.intValue());
	}

	/**
	 * @param updatedLookupItems
	 * @param dataaccountid
	 * @param saleConnection
	 */
	public void upsertLookupItems(ArrayList<LookupItem> updatedLookupItems, Long dataaccountid, Connection saleConnection) {
		String updateQuery = "INSERT INTO lkp_items (dataaccountid, itemid, country, title, sku, parentsku, currency, price, parentid) "
				+ "VALUES(?, ?, ?,   ?, ?, ?,   ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, sku=?, parentsku=?, currency=?, price=?, parentid=?";

		LOG.log(GaeLevel.DEBUG, String.format("Upserting %d lookup items", updatedLookupItems.size()));

		try {
			PreparedStatement pstat = saleConnection.getRealConnection().prepareStatement(updateQuery);

			for (LookupItem item : updatedLookupItems) {
				int paramCount = 1;

				// for insert
				pstat.setObject(paramCount++, dataaccountid);
				pstat.setObject(paramCount++, item.itemid);
				pstat.setObject(paramCount++, item.country);
				pstat.setObject(paramCount++, item.title);
				pstat.setObject(paramCount++, item.sku);
				pstat.setObject(paramCount++, item.parentsku);
				pstat.setObject(paramCount++, item.currency);
				pstat.setObject(paramCount++, item.price);
				pstat.setObject(paramCount++, item.parentid);

				// for update
				pstat.setObject(paramCount++, item.title);
				pstat.setObject(paramCount++, item.sku);
				pstat.setObject(paramCount++, item.parentsku);
				pstat.setObject(paramCount++, item.currency);
				pstat.setObject(paramCount++, item.price);
				pstat.setObject(paramCount++, item.parentid);

				pstat.addBatch();
			}

			LOG.log(GaeLevel.DEBUG, String.format("Executing batch update of lookup items"));
			pstat.executeBatch();
			LOG.log(GaeLevel.DEBUG, String.format("Lookup item upsert complete"));
			try {
				pstat.close();
			} catch (Exception ex) {
			}

		} catch (SQLException e) {
			LOG.log(Level.WARNING, "SQL Exception on updating lookup items table for dataaccount: " + dataaccountid, e);
		}
	}

	/**
	 * @param summaries
	 * @param dataaccountid
	 * @param saleConnection
	 */
	public void upsertSaleSummaries(HashMap<Integer, HashMap<String, SaleSummary>> summaries, Long dataaccountid, Connection saleConnection) {
		String updateQuery = "INSERT INTO sale_summary "
				+ " (dataaccountid, date, itemid, title, country, currency, price, "
				+ " iphone_app_revenue, ipad_app_revenue, universal_app_revenue, total_app_revenue, "
				+ " iap_revenue, total_revenue, "
				+ " iphone_downloads, ipad_downloads, universal_downloads, total_downloads, "
				+ " iphone_updates, ipad_updates, universal_updates, total_updates, "
				+ " total_download_and_updates, "
				+ "free_subs_count, paid_subs_count, total_subs_count, subs_revenue) "
				+ "VALUES("
				+ "  ?, ?, ?, ?, ?, ?, ?"
				+ ", ?, ?, ?, ?"
				+ ", ?, ?"
				+ ", ?, ?, ?, ?"
				+ ", ?, ?, ?, ?"
				+ ", ?"
				+ ", ?, ?, ?, ? "
				+ ") ON DUPLICATE KEY UPDATE currency=?, price=?, "
				+ " iphone_app_revenue=?, ipad_app_revenue=?, universal_app_revenue=?, total_app_revenue=?, "
				+ " iap_revenue=?, total_revenue=?, "
				+ " iphone_downloads=?, ipad_downloads=?, universal_downloads=?, total_downloads=?, "
				+ " iphone_updates=?, ipad_updates=?, universal_updates=?, total_updates=?, "
				+ " total_download_and_updates=?, "
				+ "free_subs_count=?, paid_subs_count=?, total_subs_count=?, subs_revenue=?";

		LOG.log(GaeLevel.DEBUG, String.format("Upserting sale summaries"));
		try {
			PreparedStatement pstat = saleConnection.getRealConnection().prepareStatement(updateQuery);

			int totalUpsertCount = 0;

			for (HashMap<String, SaleSummary> itemCountryMap : summaries.values()) {
				for (SaleSummary summary : itemCountryMap.values()) {
					int paramCount = 1;

					int totalAppRevenue = summary.iphone_app_revenue + summary.ipad_app_revenue + summary.universal_app_revenue;
					int totalRevenue = totalAppRevenue + summary.iap_revenue;

					int totalDownloads = summary.iphone_downloads + summary.ipad_downloads + summary.universal_downloads;
					int totalUpdates = summary.iphone_updates + summary.ipad_updates + summary.universal_updates;

					int totalDownloadsAndUpdates = totalDownloads + totalUpdates;

					int totalSubsCount = summary.free_subs_count + summary.paid_subs_count;

					// for insert
					pstat.setObject(paramCount++, summary.dataaccountid);
					pstat.setObject(paramCount++, summary.date);
					pstat.setObject(paramCount++, summary.itemid);
					pstat.setObject(paramCount++, summary.title);
					pstat.setObject(paramCount++, summary.country);
					pstat.setObject(paramCount++, summary.currency);
					pstat.setObject(paramCount++, summary.price);
					pstat.setObject(paramCount++, summary.iphone_app_revenue);
					pstat.setObject(paramCount++, summary.ipad_app_revenue);
					pstat.setObject(paramCount++, summary.universal_app_revenue);
					pstat.setObject(paramCount++, totalAppRevenue);
					pstat.setObject(paramCount++, summary.iap_revenue);
					pstat.setObject(paramCount++, totalRevenue);
					pstat.setObject(paramCount++, summary.iphone_downloads);
					pstat.setObject(paramCount++, summary.ipad_downloads);
					pstat.setObject(paramCount++, summary.universal_downloads);
					pstat.setObject(paramCount++, totalDownloads);
					pstat.setObject(paramCount++, summary.iphone_updates);
					pstat.setObject(paramCount++, summary.ipad_updates);
					pstat.setObject(paramCount++, summary.universal_updates);
					pstat.setObject(paramCount++, totalUpdates);
					pstat.setObject(paramCount++, totalDownloadsAndUpdates);
					pstat.setObject(paramCount++, summary.free_subs_count);
					pstat.setObject(paramCount++, summary.paid_subs_count);
					pstat.setObject(paramCount++, totalSubsCount);
					pstat.setObject(paramCount++, summary.subs_revenue);

					// for update
					pstat.setObject(paramCount++, summary.currency);
					pstat.setObject(paramCount++, summary.price);
					pstat.setObject(paramCount++, summary.iphone_app_revenue);
					pstat.setObject(paramCount++, summary.ipad_app_revenue);
					pstat.setObject(paramCount++, summary.universal_app_revenue);
					pstat.setObject(paramCount++, totalAppRevenue);
					pstat.setObject(paramCount++, summary.iap_revenue);
					pstat.setObject(paramCount++, totalRevenue);
					pstat.setObject(paramCount++, summary.iphone_downloads);
					pstat.setObject(paramCount++, summary.ipad_downloads);
					pstat.setObject(paramCount++, summary.universal_downloads);
					pstat.setObject(paramCount++, totalDownloads);
					pstat.setObject(paramCount++, summary.iphone_updates);
					pstat.setObject(paramCount++, summary.ipad_updates);
					pstat.setObject(paramCount++, summary.universal_updates);
					pstat.setObject(paramCount++, totalUpdates);
					pstat.setObject(paramCount++, totalDownloadsAndUpdates);
					pstat.setObject(paramCount++, summary.free_subs_count);
					pstat.setObject(paramCount++, summary.paid_subs_count);
					pstat.setObject(paramCount++, totalSubsCount);
					pstat.setObject(paramCount++, summary.subs_revenue);

					pstat.addBatch();
					totalUpsertCount++;
				}
			}

			LOG.log(GaeLevel.DEBUG, String.format("Executing batch update for %d sales upserts", totalUpsertCount));
			pstat.executeBatch();
			LOG.log(GaeLevel.DEBUG, String.format("Upsert complete"));

			try {
				pstat.close();
			} catch (Exception ex) {
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "SQL Exception on updating lookup items table for dataaccount: " + dataaccountid, e);
		}
	}
}
