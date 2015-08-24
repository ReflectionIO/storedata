//
//  SaleService.java
//  reflection.io
//
//  Created by William Shakour on December 30, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.sale;

import static com.spacehopperstudios.utility.StringUtils.*;

import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spacehopperstudios.utility.StringUtils;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;

final class SaleService implements ISaleService {
	@Override
	public String getName() {
		return ServiceType.ServiceTypeSale.toString();
	}

	@Override
	public Sale getSale(Long id) throws DataAccessException {
		Sale sale = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection saleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		String getSaleQuery = String.format("SELECT * FROM `sale` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSaleQuery);

			if (saleConnection.fetchNextRow()) {
				sale = toSale(saleConnection);
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
		return sale;
	}

	/**
	 * To sale
	 *
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private Sale toSale(Connection connection) throws DataAccessException {
		Sale sale = new Sale();
		sale.id = connection.getCurrentRowLong("id");
		sale.created = connection.getCurrentRowDateTime("created");
		sale.deleted = connection.getCurrentRowString("deleted");

		sale.account = new DataAccount();
		sale.account.id = connection.getCurrentRowLong("dataaccountid");

		sale.item = new Item();
		sale.item.internalId = connection.getCurrentRowString("itemid");

		sale.country = stripslashes(connection.getCurrentRowString("country"));
		sale.sku = stripslashes(connection.getCurrentRowString("sku"));
		sale.developer = stripslashes(connection.getCurrentRowString("developer"));
		sale.title = stripslashes(connection.getCurrentRowString("title"));
		sale.version = stripslashes(connection.getCurrentRowString("version"));
		sale.typeIdentifier = stripslashes(connection.getCurrentRowString("typeidentifier"));
		sale.units = connection.getCurrentRowInteger("units");

		Integer proceeds = connection.getCurrentRowInteger("proceeds");
		if (proceeds != null) {
			sale.proceeds = Float.valueOf(proceeds.floatValue() / 100.0f);
		}

		sale.currency = stripslashes(connection.getCurrentRowString("currency"));
		sale.begin = connection.getCurrentRowDateTime("begin");
		sale.end = connection.getCurrentRowDateTime("end");
		sale.customerCurrency = stripslashes(connection.getCurrentRowString("customercurrency"));

		Integer customerPrice = connection.getCurrentRowInteger("customerprice");
		if (customerPrice != null) {
			sale.customerPrice = Float.valueOf(customerPrice.floatValue() / 100.0f);
		}

		sale.promoCode = stripslashes(connection.getCurrentRowString("promocode"));
		sale.parentIdentifier = stripslashes(connection.getCurrentRowString("parentidentifier"));
		sale.subscription = stripslashes(connection.getCurrentRowString("subscription"));
		sale.period = stripslashes(connection.getCurrentRowString("period"));
		sale.category = stripslashes(connection.getCurrentRowString("category"));

		return sale;
	}

	@Override
	public Sale addSale(Sale sale) throws DataAccessException {
		Sale addedSale = null;

		// TODO: sort out nullable values

		final String addSaleQuery = String
				.format("INSERT INTO `sale` (`dataaccountid`,`itemid`,`country`,`sku`,`developer`,`title`,`version`,`typeidentifier`,`units`,`proceeds`,`currency`,`begin`,`end`,`customercurrency`,`customerprice`,`promocode`,`parentidentifier`,`subscription`,`period`,`category`) VALUES (%d,%d,'%s','%s','%s','%s','%s','%s',%d,%d,'%s',FROM_UNIXTIME(%d),FROM_UNIXTIME(%d),'%s',%d,'%s','%s','%s','%s','%s')",
						sale.account.id.longValue(), sale.item.id.longValue(), addslashes(sale.country), addslashes(sale.sku), addslashes(sale.developer),
						addslashes(sale.title), addslashes(sale.version), addslashes(sale.typeIdentifier), sale.units.intValue(),
						(int) (sale.proceeds.floatValue() * 100.0f), addslashes(sale.currency), sale.begin.getTime() / 1000, sale.end.getTime() / 1000,
						addslashes(sale.customerCurrency), (int) (sale.customerPrice.floatValue() * 100.0f), addslashes(sale.promoCode),
						addslashes(sale.parentIdentifier), addslashes(sale.subscription), addslashes(sale.period), addslashes(sale.category));

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(addSaleQuery);

			if (saleConnection.getAffectedRowCount() > 0) {
				addedSale = getSale(Long.valueOf(saleConnection.getInsertedId()));

				if (addedSale == null) {
					addedSale = sale;
					addedSale.id = Long.valueOf(saleConnection.getInsertedId());
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return addedSale;
	}

	@Override
	public Sale updateSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountItems(io.reflection.app.shared.datatypes.DataAccount,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Item> getDataAccountItems(DataAccount dataAccount, List<String> typeIdentifiers, Pager pager) throws DataAccessException {
		List<Item> items = new ArrayList<Item>();

		Map<String, Item> itemsFoundInSalesSummary = new HashMap<String, Item>();
		List<String> itemIdsToLookForInRanks = new ArrayList<String>();

		String getSaleQuery = String.format(
				"select itemid, title, developer_name from dataaccount d inner join sale_summary s on (d.id=s.dataaccountid) where d.id=%d group by itemid",
				dataAccount.id.longValue());

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection saleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSaleQuery);

			while (saleConnection.fetchNextRow()) {
				Item item = new Item();

				item.creatorName = saleConnection.getCurrentRowString("developer_name");
				item.name = saleConnection.getCurrentRowString("title");
				item.internalId = saleConnection.getCurrentRowString("itemid");

				itemsFoundInSalesSummary.put(item.internalId, item);

				itemIdsToLookForInRanks.add(saleConnection.getCurrentRowString("itemid"));
			}

			// return only Items which were found in the item table (these will have icons. we found during rank gathers)
			items.addAll(ItemServiceProvider.provide().getInternalIdItemBatch(itemIdsToLookForInRanks));

			// remove all the items found from ranks (via item table) and
			for (Item itemFromRanks : items) {
				String internalId = itemFromRanks.internalId;

				itemsFoundInSalesSummary.remove(internalId);
			}

			items.addAll(itemsFoundInSalesSummary.values());
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return items;
	}

	/**
	 * Generate dummy items from sale table for items out of the top 400
	 *
	 * @param itemId
	 * @return
	 * @throws DataAccessException
	 */
	private List<Item> generateDummyItems(Collection<String> itemId, Long dataAccountId) throws DataAccessException {
		List<Item> items = new ArrayList<Item>();

		String itemIdsQuerypart = null;
		if (itemId.size() == 1) {
			itemIdsQuerypart = String.format("`itemid`='%s'", itemId.iterator().next());
		} else {
			itemIdsQuerypart = "`itemid` IN ('" + StringUtils.join(itemId, "' ,'") + "' )";
		}

		String getDataAccountItemsQuery = String.format(
				"select distinct itemid, title, developer_name from dataaccount d inner join sale_summary s on (d.id=s.dataaccountid) where d.id=%d and %s",
				dataAccountId.longValue(), itemIdsQuerypart);

		Connection dataConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());
		try {
			dataConnection.connect();
			dataConnection.executeQuery(getDataAccountItemsQuery);

			while (dataConnection.fetchNextRow()) {
				Item item = new Item();

				item.creatorName = dataConnection.getCurrentRowString("developer_name");
				item.name = dataConnection.getCurrentRowString("title");
				item.internalId = dataConnection.getCurrentRowString("itemid");
				item.properties = "{\"usesIap\":true}"; // this is the default. the items we find in the ranks table will override this.

				items.add(item);
			}
		} finally {
			if (dataConnection != null) {
				dataConnection.disconnect();
			}
		}

		return items;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountItemsCount()
	 */
	@Override
	public Long getDataAccountItemsCount(DataAccount dataAccount, List<String> typeIdentifiers) throws DataAccessException {

		Long dataCount = Long.valueOf(0);
		String typeId = "";
		if (typeIdentifiers != null && typeIdentifiers.size() > 0) {
			typeId = "AND `typeidentifier` IN ('" + StringUtils.join(typeIdentifiers, "','") + "')";
		}
		String getDataAccountsCountQuery = String.format("SELECT COUNT(DISTINCT `itemid`) AS `datacount` FROM `sale_summary` WHERE `dataaccountid`=%d",
				dataAccount.id.longValue());

		Connection dataConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());
		try {
			dataConnection.connect();
			dataConnection.executeQuery(getDataAccountsCountQuery);

			if (dataConnection.fetchNextRow()) {
				dataCount = dataConnection.getCurrentRowLong("datacount");
			}
		} finally {
			if (dataConnection != null) {
				dataConnection.disconnect();
			}
		}

		return dataCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#addSalesBatch(java.util.Collection)
	 */
	@Override
	public Long addSalesBatch(Collection<Sale> sales) throws DataAccessException {
		Long addedSalesBatchCount = Long.valueOf(0);

		// TODO: sort out nullable values

		StringBuffer addSalesBatchQuery = new StringBuffer();

		addSalesBatchQuery
		.append("INSERT INTO `sale` (`dataaccountid`,`itemid`,`country`,`sku`,`developer`,`title`,`version`,`typeidentifier`,`units`,`proceeds`,`currency`,`begin`,`end`,`customercurrency`,`customerprice`,`promocode`,`parentidentifier`,`subscription`,`period`,`category`) VALUES");

		for (Sale sale : sales) {
			if (addSalesBatchQuery.charAt(addSalesBatchQuery.length() - 1) != 'S') {
				addSalesBatchQuery.append(",");
			}

			addSalesBatchQuery.append(String.format(
					"(%d,%s,'%s','%s','%s','%s','%s','%s',%d,%d,'%s',FROM_UNIXTIME(%d),FROM_UNIXTIME(%d),'%s',%d,'%s','%s','%s','%s','%s')",
					sale.account.id.longValue(), sale.item.internalId == null ? "NULL" : "'" + sale.item.internalId + "'", addslashes(sale.country),
							addslashes(sale.sku), addslashes(sale.developer), addslashes(sale.title), addslashes(sale.version), addslashes(sale.typeIdentifier),
							sale.units.intValue(), (int) (sale.proceeds.floatValue() * 100.0f), addslashes(sale.currency), sale.begin.getTime() / 1000,
							sale.end.getTime() / 1000, addslashes(sale.customerCurrency), (int) (sale.customerPrice.floatValue() * 100.0f), addslashes(sale.promoCode),
							addslashes(sale.parentIdentifier), addslashes(sale.subscription), addslashes(sale.period), addslashes(sale.category)));
		}

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(addSalesBatchQuery.toString());

			addedSalesBatchCount = Long.valueOf(saleConnection.getAffectedRowCount());
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return addedSalesBatchCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getSales(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.Category,
	 * io.reflection.app.datatypes.shared.DataAccount, java.util.Date, java.util.Date, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Sale> getSales(Country country, Category category, DataAccount linkedAccount, Date start, Date end, Pager pager) throws DataAccessException {
		List<Sale> sales = new ArrayList<Sale>();

		// FIXME: for now we use the all category for the iOS store... we should get the category passed in, or attempt to detect it based on the linked account
		// (category relates to store by a3code)
		// we are using end for date but we could equally use begin
		String getSalesQuery = String.format(
				"SELECT * FROM `sale` WHERE `country`='%s' AND (%d=%d OR `category`='%s') AND `dataaccountid`=%d AND %s AND `deleted`='n'", country.a2Code, 24,
				category == null ? 24 : category.id.longValue(), category == null ? "" : category.name, linkedAccount.id.longValue(),
						SqlQueryHelper.beforeAfterQuery(end, start, "end"));

		if (pager != null) {
			String sortByQuery = "id";

			// if (pager.sortBy != null && ("code".equals(pager.sortBy) || "name".equals(pager.sortBy))) {
			// sortByQuery = pager.sortBy;
			// }

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
					case SortDirectionTypeAscending:
						sortDirectionQuery = "ASC";
						break;
					default:
						break;
				}
			}

			getSalesQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getSalesQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getSalesQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSalesQuery);

			while (saleConnection.fetchNextRow()) {
				Sale sale = toSale(saleConnection);

				if (sale != null) {
					sales.add(sale);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return sales;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getSalesCount(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.Category,
	 * io.reflection.app.datatypes.shared.DataAccount, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getSalesCount(Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException {
		Long salesCount = Long.valueOf(0);

		String getSalesQuery = String
				.format("SELECT count(1) AS `salescount` FROM `sale` WHERE `country`='%s' AND (%d=%d OR `category`='%s') AND `dataaccountid`=%d AND %s AND `deleted`='n'",
						country.a2Code, 24, category == null ? 24 : category.id.longValue(), category == null ? "" : category.name,
								linkedAccount.id.longValue(), SqlQueryHelper.beforeAfterQuery(end, start, "end"));

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSalesQuery);

			if (saleConnection.fetchNextRow()) {
				salesCount = saleConnection.getCurrentRowLong("salescount");
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return salesCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getItemSales(io.reflection.app.datatypes.shared.Item, io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.Category, io.reflection.app.datatypes.shared.DataAccount, java.util.Date, java.util.Date,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Sale> getItemSales(Item item, Country country, Category category, DataAccount linkedAccount, Date start, Date end, Pager pager)
			throws DataAccessException {
		List<Sale> sales = new ArrayList<Sale>();

		// FIXME: for now we use the all category for the iOS store... we should get the category passed in, or attempt
		// to detect it based on the linked account
		// (category relates to store by a3code)
		// we are using end for date but we could equally use begin
		String getSalesQuery = String
				.format("SELECT * FROM `sale` WHERE `country`='%s' AND (%d=%d OR `category`='%s') AND `dataaccountid`=%d AND %s AND (`itemid`='%7$s' OR parentidentifier = (SELECT `sku` FROM `sale` WHERE `itemid`='%7$s' LIMIT 1)) AND `deleted`='n'",
						country.a2Code, 24, category == null ? 24 : category.id.longValue(), category == null ? "" : category.name,
								linkedAccount.id.longValue(), SqlQueryHelper.beforeAfterQuery(end, start, "end"), item.internalId);

		if (pager != null) {
			String sortByQuery = "id";

			// if (pager.sortBy != null && ("code".equals(pager.sortBy) || "name".equals(pager.sortBy))) {
			// sortByQuery = pager.sortBy;
			// }

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
					case SortDirectionTypeAscending:
						sortDirectionQuery = "ASC";
						break;
					default:
						break;
				}
			}

			getSalesQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getSalesQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getSalesQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSalesQuery);

			while (saleConnection.fetchNextRow()) {
				Sale sale = toSale(saleConnection);

				if (sale != null) {
					sales.add(sale);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return sales;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getItemSalesCount(io.reflection.app.datatypes.shared.Item, io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.Category, io.reflection.app.datatypes.shared.DataAccount, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getItemSalesCount(Item item, Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException {
		Long salesCount = Long.valueOf(0);

		String getSalesQuery = String
				.format("SELECT count(1) AS `salescount` FROM `sale` WHERE `country`='%s' AND (%d=%d OR `category`='%s') AND `dataaccountid`=%d AND %s AND `itemid`='%s' AND `deleted`='n'",
						country.a2Code, 24, category == null ? 24 : category.id.longValue(), category == null ? "" : category.name,
								linkedAccount.id.longValue(), SqlQueryHelper.beforeAfterQuery(end, start, "end"), item.internalId);

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSalesQuery);

			if (saleConnection.fetchNextRow()) {
				salesCount = saleConnection.getCurrentRowLong("salescount");
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return salesCount;
	}

	private Item toMockItem(Connection connection) throws DataAccessException {
		Item mockItem = new Item();

		mockItem.creatorName = connection.getCurrentRowString("developer");
		mockItem.name = connection.getCurrentRowString("title");
		mockItem.internalId = connection.getCurrentRowString("itemid");

		return mockItem;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getItem(java.lang.String)
	 */
	@Override
	public Item getItem(String itemId) throws DataAccessException {
		Item item = null;

		String getItemQuery = String.format("SELECT `developer`, `title`, `itemid` FROM `sale` WHERE `itemid`='%s' AND `deleted`='n' LIMIT 1", itemId);

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getItemQuery);

			if (saleConnection.fetchNextRow()) {
				item = toMockItem(saleConnection);
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
		return item;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccount(java.lang.String)
	 */
	@Override
	public DataAccount getDataAccount(String itemId) throws DataAccessException {
		DataAccount dataAccount = null;

		String getDataAccountIdQuery = String.format("SELECT `dataaccountid` FROM `sale` WHERE `itemid`='%s' AND `deleted`='n' LIMIT 1", itemId);

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getDataAccountIdQuery);

			if (saleConnection.fetchNextRow()) {
				dataAccount = DataAccountServiceProvider.provide().getDataAccount(saleConnection.getCurrentRowLong("dataaccountid"));
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return dataAccount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getSaleIds(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.DataAccount,
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public List<Long> getSaleIds(Country country, DataAccount linkedAccount, Date start, Date end) throws DataAccessException {
		List<Long> saleIds = new ArrayList<Long>();

		// FIXME: for now we use the all category for the iOS store... we should get the category passed in, or attempt to detect it based on the linked account
		// (category relates to store by a3code)
		// we are using end for date but we could equally use begin
		String getSaleIdsQuery = String.format("SELECT `id` FROM `sale` WHERE `country`='%s' AND `dataaccountid`=%d AND %s AND `deleted`='n'", country.a2Code,
				linkedAccount.id.longValue(), SqlQueryHelper.beforeAfterQuery(end, start, "end"));

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSaleIdsQuery);

			Long id;
			while (saleConnection.fetchNextRow()) {
				id = saleConnection.getCurrentRowLong("id");

				if (id != null) {
					saleIds.add(id);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return saleIds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getAllSaleIds(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getAllSaleIds(Pager pager) throws DataAccessException {
		List<Long> saleIds = new ArrayList<Long>();

		String getAllSalesIdsQuery = String.format("SELECT `id` FROM `sale` WHERE `deleted`='n' ORDER BY `%s` %s LIMIT %d, %d", pager.sortBy == null ? "id"
				: stripslashes(pager.sortBy), pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
						pager.start == null ? Pager.DEFAULT_START.longValue() : pager.start.longValue(), pager.count == null ? Pager.DEFAULT_COUNT.longValue()
								: pager.count.longValue());

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getAllSalesIdsQuery);

			Long saleId;
			while (saleConnection.fetchNextRow()) {
				saleId = saleConnection.getCurrentRowLong("id");

				if (saleId != null) {
					saleIds.add(saleId);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return saleIds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountFetchSales(io.reflection.app.datatypes.shared.DataAccountFetch,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Sale> getDataAccountFetchSales(DataAccountFetch dataAccountFetch, Pager pager) throws DataAccessException {
		List<Sale> sales = new ArrayList<Sale>();

		if (dataAccountFetch.date == null || dataAccountFetch.linkedAccount == null) {
			dataAccountFetch = DataAccountFetchServiceProvider.provide().getDataAccountFetch(dataAccountFetch.id);
		}

		String getDataAccountFetchSalesQuery = String.format(
				"SELECT * FROM `sale` WHERE `end`=FROM_UNIXTIME(%d) AND `dataaccountid`=%d AND `deleted`='n' ORDER BY `%s` %s LIMIT %d, %d",
				dataAccountFetch.date.getTime() / 1000, dataAccountFetch.linkedAccount.id.longValue(),
				pager.sortBy == null ? "id" : stripslashes(pager.sortBy), pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
						pager.start == null ? Pager.DEFAULT_START.longValue() : pager.start.longValue(), pager.count == null ? Pager.DEFAULT_COUNT.longValue()
								: pager.count.longValue());
		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getDataAccountFetchSalesQuery);

			Sale sale;
			while (saleConnection.fetchNextRow()) {
				sale = toSale(saleConnection);

				if (sale != null) {
					sales.add(sale);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return sales;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountFetchSaleIds(io.reflection.app.datatypes.shared.DataAccountFetch,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getDataAccountFetchSaleIds(DataAccountFetch dataAccountFetch, Pager pager) throws DataAccessException {
		List<Long> saleIds = new ArrayList<Long>();

		if (dataAccountFetch.date == null || dataAccountFetch.linkedAccount == null) {
			dataAccountFetch = DataAccountFetchServiceProvider.provide().getDataAccountFetch(dataAccountFetch.id);
		}

		String getDataAccountFetchSaleIdsQuery = String.format(
				"SELECT `id` FROM `sale` WHERE `end`=FROM_UNIXTIME(%d) AND `dataaccountid`=%d AND `deleted`='n' ORDER BY `%s` %s LIMIT %d, %d",
				dataAccountFetch.date.getTime() / 1000, dataAccountFetch.linkedAccount.id.longValue(),
				pager.sortBy == null ? "id" : stripslashes(pager.sortBy), pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
						pager.start == null ? Pager.DEFAULT_START.longValue() : pager.start.longValue(), pager.count == null ? Pager.DEFAULT_COUNT.longValue()
								: pager.count.longValue());
		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getDataAccountFetchSaleIdsQuery);

			Long saleId;
			while (saleConnection.fetchNextRow()) {
				saleId = saleConnection.getCurrentRowLong("id");

				if (saleId != null) {
					saleIds.add(saleId);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return saleIds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getSkuItemId(java.lang.String)
	 */
	@Override
	public String getSkuItemId(String sku) throws DataAccessException {
		String itemId = null;

		String getSkuItemIdQuery = String.format("SELECT `itemid` FROM `sale` WHERE `sku`='%s' LIMIT 1", addslashes(sku));

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSkuItemIdQuery);

			if (saleConnection.fetchNextRow()) {
				itemId = saleConnection.getCurrentRowString("itemid");
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return itemId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getItemPrices(java.lang.String[], java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Sale> getItemPrices(String[] itemIds, String country, Date start, Date end) throws DataAccessException {
		Map<String, Sale> result = new HashMap<String, Sale>();

		StringBuilder itemIdsAsSqlArray = new StringBuilder();

		for (String itemid : itemIds) {
			if (itemIdsAsSqlArray.length() > 0) {
				itemIdsAsSqlArray.append(',');
			}

			itemIdsAsSqlArray.append('\'').append(itemid).append('\'');
		}

		String getItemPricesQuery = String
				.format("select max(id), itemid, customerprice, currency from sale where country='%s' and itemid in(%s) and typeidentifier in ('1', '1T', '1F') and %s group by itemid;",
						country, itemIdsAsSqlArray.toString(), SqlQueryHelper.beforeAfterQuery(end, start, "begin"));

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getItemPricesQuery);

			while (saleConnection.fetchNextRow()) {
				String itemid = saleConnection.getCurrentRowString("itemid");
				Long price = saleConnection.getCurrentRowLong("customerprice");

				if (price != null) {
					Sale sale = new Sale();
					sale.customerPrice = (float) (price / 100f);
					sale.currency = saleConnection.getCurrentRowString("currency");

					result.put(itemid, sale);
				}
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#summariseSalesForDataAccountOnDate(java.lang.Long, java.util.Date)
	 */
	@Override
	public void summariseSalesForDataAccountOnDate(Long id, Date date) throws DataAccessException {
		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			saleConnection.connect();
			String query = String.format("CALL repopulate_sale_summary_for_dataaccount_on_date(%d, '%s')", id, sdf.format(date));
			saleConnection.executeQuery(query);
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getSoldItemIdsForAccountInDateRange(java.lang.Long, java.util.Date, java.util.Date)
	 */
	@Override
	public List<SimpleEntry<String, String>> getSoldItemIdsForAccountInDateRange(Long dataAccountId, Date gatherFrom, Date gatherTo)
			throws DataAccessException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String getItemsByCountryQuery = String.format("SELECT DISTINCT itemid, country FROM `sale_summary` WHERE dataaccountid=%d AND date BETWEEN '%s' AND '%s'",
				dataAccountId, sdf.format(gatherFrom), sdf.format(gatherTo));

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getItemsByCountryQuery);

			ArrayList<SimpleEntry<String, String>> list = new ArrayList<SimpleEntry<String, String>>();

			while (saleConnection.fetchNextRow()) {
				SimpleEntry<String, String> entry = new SimpleEntry<String, String>(saleConnection.getCurrentRowString("itemid"),
						saleConnection.getCurrentRowString("country"));
				list.add(entry);
			}

			return list;
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.sale.ISaleService#getIapItemIdsForParentItemOnDate(java.lang.Long, java.lang.String, java.util.Date)
	 */
	@Override
	public List<String> getIapItemIdsForParentItemOnDate(Long dataAccountId, String mainItemId, Date date) throws DataAccessException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String getItemsByCountryQuery = String.format("select distinct(itemid) as itemid from sale where "
				+ "dataaccountid=%s and `begin`='%s' and parentidentifier="
				+ "  (select sku from sale where dataaccountid=%s and itemid='%s' limit 1)",
				dataAccountId, sdf.format(date), dataAccountId, mainItemId);

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getItemsByCountryQuery);

			ArrayList<String> list = new ArrayList<String>();

			while (saleConnection.fetchNextRow()) {
				String entry = saleConnection.getCurrentRowString("itemid");
				list.add(entry);
			}

			return list;
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#getIapItemIdsForParentItemBetweenDates(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<String> getIapItemIdsForParentItemBetweenDates(Long dataAccountId, String mainItemId, Date from, Date to) throws DataAccessException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String getItemsByCountryQuery = String.format("select distinct(itemid) as itemid from sale where "
				+ "dataaccountid=%s and `begin` BETWEEN '%s' and '%s' and parentidentifier="
				+ "  (select sku from sale where dataaccountid=%d and itemid='%s' limit 1)",
				dataAccountId, sdf.format(from), sdf.format(to), dataAccountId, mainItemId);

		Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getItemsByCountryQuery);

			ArrayList<String> list = new ArrayList<String>();

			while (saleConnection.fetchNextRow()) {
				String entry = saleConnection.getCurrentRowString("itemid");
				list.add(entry);
			}

			return list;
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.sale.ISaleService#deleteSales(java.lang.Long, java.util.Date)
	 */
	@Override
	public void deleteSales(Long dataAccountId, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Connection saleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSale.toString());
			saleConnection.executeQuery(String.format("delete from sale where dataaccountid=%d and begin='%s'", dataAccountId, sdf.format(date)));
		} catch (Exception e) {
		}
	}
}