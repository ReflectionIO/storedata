//  
//  SaleService.java
//  reflection.io
//
//  Created by William Shakour on December 30, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.sale;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.item.ItemServiceProvider;

import java.util.ArrayList;
import java.util.List;

import com.spacehopperstudios.utility.StringUtils;

final class SaleService implements ISaleService {
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
		sale.item.id = connection.getCurrentRowLong("itemid");

		sale.country = stripslashes(connection.getCurrentRowString("country"));
		sale.sku = stripslashes(connection.getCurrentRowString("sku"));
		sale.developer = stripslashes(connection.getCurrentRowString("developer"));
		sale.title = stripslashes(connection.getCurrentRowString("title"));
		sale.version = stripslashes(connection.getCurrentRowString("version"));
		sale.typeIdentifier = stripslashes(connection.getCurrentRowString("typeidentifier"));
		sale.units = connection.getCurrentRowInteger("units");
		sale.proceeds = connection.getCurrentRowInteger("proceeds");
		sale.currency = stripslashes(connection.getCurrentRowString("currency"));
		sale.begin = connection.getCurrentRowDateTime("begin");
		sale.end = connection.getCurrentRowDateTime("end");
		sale.customerCurrency = stripslashes(connection.getCurrentRowString("customercurrency"));
		sale.customerPrice = connection.getCurrentRowInteger("customerprice");
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
						addslashes(sale.title), addslashes(sale.version), addslashes(sale.typeIdentifier), sale.units.intValue(), sale.proceeds.intValue(),
						addslashes(sale.currency), sale.begin.getTime() / 1000, sale.end.getTime() / 1000, addslashes(sale.customerCurrency),
						sale.customerPrice.intValue(), addslashes(sale.promoCode), addslashes(sale.parentIdentifier), addslashes(sale.subscription),
						addslashes(sale.period), addslashes(sale.category));

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
	public List<Item> getDataAccountItems(DataAccount dataAccount, Pager pager) throws DataAccessException {

		List<String> itemIds = new ArrayList<String>();
		List<String> itemIdsTop400 = new ArrayList<String>();
		List<Item> items = new ArrayList<Item>();

		String getSaleQuery = String.format("SELECT DISTINCT `itemid` FROM `sale` WHERE `dataaccountid`=%d AND `deleted`='n' ORDER BY `%s` %s LIMIT %d, %d",
				dataAccount.id.longValue(), pager.sortBy == null ? "id" : pager.sortBy,
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start == null ? Pager.DEFAULT_START.longValue()
						: pager.start.longValue(), pager.count == null ? Pager.DEFAULT_COUNT.longValue() : pager.count.longValue());

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection saleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSaleQuery);

			while (saleConnection.fetchNextRow()) {
				itemIds.add(saleConnection.getCurrentRowString("itemid"));
			}
			items.addAll(ItemServiceProvider.provide().getInternalIdItemBatch(itemIds)); // add Items from items table (the ones in the top 400)
			for (Item i : items) {
				itemIdsTop400.add(i.internalId);
			}

		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}

		itemIds.removeAll(itemIdsTop400); // items out of the top 400

		items.addAll(generateDummyItems(itemIds)); // get dummy items from sale table

		return items;
	}

	private List<Item> generateDummyItems(List<String> itemId) throws DataAccessException {
		List<Item> items = new ArrayList<Item>();

		String typesQueryPart = null;
		if (itemId.size() == 1) {
			typesQueryPart = String.format("CAST(`itemid` AS BINARY)=CAST('%s' AS BINARY)", itemId.get(0));
		} else {
			typesQueryPart = "CAST(`itemid` AS BINARY) IN (CAST('" + StringUtils.join(itemId, "' AS BINARY),CAST('") + "' AS BINARY))";
		}

		String getSaleItemQuery = String.format("SELECT DISTINCT `title`,`developer`,`itemid`  FROM `sale` WHERE %s AND `deleted`='n'", typesQueryPart);
		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection saleItemConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSale.toString());
		try {
			saleItemConnection.connect();
			saleItemConnection.executeQuery(getSaleItemQuery);
			while (saleItemConnection.fetchNextRow()) {
				Item fake = new Item();
				fake.creatorName = saleItemConnection.getCurrentRowString("developer");
				fake.name = saleItemConnection.getCurrentRowString("title");
				fake.externalId = saleItemConnection.getCurrentRowString("itemid");
				items.add(fake);
			}

		} finally {
			if (saleItemConnection != null) {
				saleItemConnection.disconnect();
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
	public Long getDataAccountItemsCount(DataAccount dataAccount) throws DataAccessException {

		Long dataCount = Long.valueOf(0);
		String getDataAccountsCountQuery = String.format(
				"SELECT COUNT(DISTINCT `itemid`) AS `datacount` FROM `sale` WHERE `deleted`='n' AND `dataaccountid`=%d ", dataAccount.id.longValue());

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
	 * @see io.reflection.app.service.sale.ISaleService#addSalesBatch(java.util.List)
	 */
	@Override
	public Long addSalesBatch(List<Sale> sales) throws DataAccessException {
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
					sale.units.intValue(), sale.proceeds.intValue(), addslashes(sale.currency), sale.begin.getTime() / 1000, sale.end.getTime() / 1000,
					addslashes(sale.customerCurrency), sale.customerPrice.intValue(), addslashes(sale.promoCode), addslashes(sale.parentIdentifier),
					addslashes(sale.subscription), addslashes(sale.period), addslashes(sale.category)));
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
}