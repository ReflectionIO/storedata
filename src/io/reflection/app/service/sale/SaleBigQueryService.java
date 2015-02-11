//
//  SaleBigQueryService.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.service.sale;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.bigquery.BigQueryHelper;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.ServiceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableRow;

/**
 * @author William Shakour (billy1380)
 *
 */
final class SaleBigQueryService implements ISaleService {

	public static final String NAME = ServiceType.ServiceTypeSale.toString() + ".bigquery";

	private static final Logger LOG = Logger.getLogger(SaleBigQueryService.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spacehopperstudios.service.IService#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getSale(java.lang.Long)
	 */
	@Override
	public Sale getSale(Long id) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#addSale(io.reflection.app.datatypes.shared.Sale)
	 */
	@Override
	public Sale addSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#updateSale(io.reflection.app.datatypes.shared.Sale)
	 */
	@Override
	public Sale updateSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#deleteSale(io.reflection.app.datatypes.shared.Sale)
	 */
	@Override
	public void deleteSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountItems(io.reflection.app.datatypes.shared.DataAccount, java.util.List,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Item> getDataAccountItems(DataAccount dataAccount, List<String> typeIdentifiers, Pager pager) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountItemsCount(io.reflection.app.datatypes.shared.DataAccount, java.util.List)
	 */
	@Override
	public Long getDataAccountItemsCount(DataAccount dataAccount, List<String> typeIdentifiers) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#addSalesBatch(java.util.Collection)
	 */
	@Override
	public Long addSalesBatch(Collection<Sale> sales) throws DataAccessException {
		TableRow row;
		TableDataInsertAllRequest.Rows rows;
		List<TableDataInsertAllRequest.Rows> rowList = new ArrayList<>();
		long i = 0;
		for (Sale sale : sales) {
			rows = new TableDataInsertAllRequest.Rows();

			row = new TableRow();

			row.set("dataaccountfetchid", sale.account.id);
			row.set("dataaccountid", sale.fetch.id);
			row.set("itemid", sale.item.internalId);
			row.set("country", sale.country);
			row.set("sku", sale.sku);
			row.set("developer", sale.developer);
			row.set("title", sale.title);
			row.set("version", sale.version);
			row.set("typeidentifier", sale.typeIdentifier);
			row.set("units", sale.units);
			row.set("proceeds", sale.proceeds);
			row.set("currency", sale.currency);
			row.set("begin", sale.begin.getTime() / 1000);
			row.set("end", sale.end.getTime() / 1000);
			row.set("customercurrency", sale.customerCurrency);
			row.set("customerprice", sale.customerPrice);
			row.set("promocode", sale.promoCode);
			row.set("parentidentifier", sale.parentIdentifier);
			row.set("subscription", sale.subscription);
			row.set("period", sale.period);
			row.set("category", sale.category);

			rows.setInsertId(sale.fetch.id.toString() + "-" + Long.toString(i++));
			rows.setJson(row);

			rowList.add(rows);
		}

		TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows(rowList);

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			try {
				LOG.log(GaeLevel.TRACE, content.toPrettyString());
			} catch (IOException ex) {}
		}

		TableDataInsertAllResponse response;
		try {
			response = BigQueryHelper.insertAll("sale", content);
		} catch (IOException ex) {
			i = 0;
			throw new DataAccessException(ex);
		}

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			try {
				LOG.log(GaeLevel.TRACE, response.toPrettyString());
			} catch (IOException ex) {}
		}

		// FIXME: should really check the response code and return 0 if failed to insert
		return Long.valueOf(i);
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
		String getSalesQuery = String.format("SELECT * FROM sale WHERE country='%s' AND (%d=%d OR category='%s') AND dataaccountid=%d AND %s", country.a2Code,
				24, category == null ? 24 : category.id.longValue(), category == null ? "" : category.name, linkedAccount.id.longValue(),
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

		try {
			QueryResponse response = BigQueryHelper.queryBigqueryQuick(getSalesQuery);

			List<TableRow> rows;
			if (response != null && (rows = response.getRows()) != null) {
				Sale sale;
				for (TableRow row : rows) {
					sale = toSale(row);

					if (sale != null) {
						sales.add(sale);
					}
				}
			}
		} catch (IOException e) {
			throw new DataAccessException(e);
		}

		return sales;
	}

	private Sale toSale(TableRow row) {
		Sale sale = new Sale();

		if (row.containsKey("dataaccountfetchid")) {
			sale.fetch = new DataAccountFetch();
			sale.fetch.id = (Long) row.get("dataaccountfetchid");
		}

		if (row.containsKey("dataaccountid")) {
			sale.account = new DataAccount();
			sale.account.id = (Long) row.get("dataaccountid");
		}

		if (row.containsKey("itemid")) {
			sale.item = new Item();
			sale.item.internalId = (String) row.get("itemid");
		}

		if (row.containsKey("country")) {
			sale.country = (String) row.get("country");
		}

		if (row.containsKey("sku")) {
			sale.sku = (String) row.get("sku");
		}

		if (row.containsKey("developer")) {
			sale.developer = (String) row.get("developer");
		}

		if (row.containsKey("title")) {
			sale.title = (String) row.get("title");
		}

		if (row.containsKey("version")) {
			sale.version = (String) row.get("version");
		}

		if (row.containsKey("typeidentifier")) {
			sale.typeIdentifier = (String) row.get("typeidentifier");
		}

		if (row.containsKey("units")) {
			sale.units = (Integer) row.get("units");
		}

		if (row.containsKey("proceeds")) {
			sale.proceeds = (Float) row.get("proceeds");
		}

		if (row.containsKey("currency")) {
			sale.currency = (String) row.get("currency");
		}

		if (row.containsKey("begin")) {
			sale.begin = BigQueryHelper.dateColumn(row.get("begin"));
		}

		if (row.containsKey("end")) {
			sale.end = BigQueryHelper.dateColumn(row.get("end"));
		}

		if (row.containsKey("customercurrency")) {
			sale.customerCurrency = (String) row.get("customercurrency");
		}

		if (row.containsKey("customerprice")) {
			sale.customerPrice = (Float) row.get("customerprice");
		}

		if (row.containsKey("promocode")) {
			sale.promoCode = (String) row.get("promocode");
		}

		if (row.containsKey("parentidentifier")) {
			sale.parentIdentifier = (String) row.get("parentidentifier");
		}

		if (row.containsKey("subscription")) {
			sale.subscription = (String) row.get("subscription");
		}

		if (row.containsKey("period")) {
			sale.period = (String) row.get("period");
		}

		if (row.containsKey("category")) {
			sale.category = (String) row.get("category");
		}

		return sale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getSalesCount(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.Category,
	 * io.reflection.app.datatypes.shared.DataAccount, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getSalesCount(Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getItemSalesCount(io.reflection.app.datatypes.shared.Item, io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.Category, io.reflection.app.datatypes.shared.DataAccount, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getItemSalesCount(Item item, Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getItem(java.lang.String)
	 */
	@Override
	public Item getItem(String itemId) throws DataAccessException {
		Item item = null;

		String getItemQuery = String.format("SELECT developer, title, itemid FROM dev.sale WHERE itemid ='%s' LIMIT 1", itemId);

		try {
			QueryResponse response = BigQueryHelper.queryBigqueryQuick(getItemQuery);

			List<TableRow> rows;
			if (response != null && (rows = response.getRows()) != null) {
				for (TableRow row : rows) {
					item = toMockItem(row);
				}
			}
		} catch (IOException e) {
			throw new DataAccessException(e);
		}

		return item;
	}

	private Item toMockItem(TableRow row) {
		Item item = new Item();

		if (row.containsKey("developer")) {
			item.creatorName = (String) row.get("developer");
		}

		if (row.containsKey("title")) {
			item.name = (String) row.get("title");
		}

		if (row.containsKey("itemid")) {
			item.internalId = (String) row.get("itemid");
		}

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccount(java.lang.String)
	 */
	@Override
	public DataAccount getItemIdDataAccount(String itemId) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getSaleIds(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.DataAccount,
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public List<Long> getSaleIds(Country country, DataAccount linkedAccount, Date start, Date end) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getAllSaleIds(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getAllSaleIds(Pager pager) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountFetchSales(io.reflection.app.datatypes.shared.DataAccountFetch,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Sale> getDataAccountFetchSales(DataAccountFetch dataAccountFetch, Pager pager) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountFetchSaleIds(io.reflection.app.datatypes.shared.DataAccountFetch,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getDataAccountFetchSaleIds(DataAccountFetch dataAccountFetch, Pager pager) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.sale.ISaleService#getSkuItemId(java.lang.String)
	 */
	@Override
	public String getSkuItemId(String sku) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}
