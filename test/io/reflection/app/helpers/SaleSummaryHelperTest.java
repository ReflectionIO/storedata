package io.reflection.app.helpers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.datatypes.shared.SaleSummary;
import io.reflection.app.helpers.SaleSummaryHelper.SALE_SOURCE;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.service.sale.SaleServiceProvider;

public class SaleSummaryHelperTest {
	private static final String	DBPassword	= "password";
	private static final String	DBUsername	= "root";
	private static final String	DB					= "rio";
	private static final String	DBServer		= "localhost";

	Connection dbCon = null;

	Long	dataAccountIdToTest				= 211L;
	int		expectedDataAccountItems	= 219;

	private SaleSummaryHelper summaryHelper;

	private Sale	testSubscriptionSaleEntry;
	private Date	dateToTest;

	@Before
	public void setup() throws DataAccessException {
		System.setProperty("connection.native", "true");
		System.setProperty("db.connection.server", DBServer);
		System.setProperty("db.connection.catalogue", DB);
		System.setProperty("db.connection.username", DBUsername);
		System.setProperty("db.connection.password", DBPassword);

		dbCon = new Connection(DBServer, DB, DBUsername, DBPassword);
		dbCon.connect();

		summaryHelper = SaleSummaryHelper.INSTANCE;

		Calendar cal = Calendar.getInstance();
		cal.set(2014, 5, 25); // June (0 based)
		dateToTest = cal.getTime();

		testSubscriptionSaleEntry = new Sale()
				.item(new Item().internalId("609683400"))
				.country("GB")
				.sku("apple.creator.subscription.pro.yearly")
				.title("Creator Subscription Pro (Yearly)")
				.typeIdentifier("IA9")
				.units(3)
				.currency("GBP")
				.begin(dateToTest)
				.end(dateToTest)
				.customerCurrency("GBP")
				.customerPrice(37.99f)
				.parentIdentifier("0001");
	}

	@After
	public void tearDown() throws DataAccessException {
		dbCon.realDisconnect();
	}

	@Test
	public void testLoadLookupItems() throws DataAccessException {
		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = new HashMap<Integer, HashMap<String, LookupItem>>();
		HashMap<String, Integer> parentskuByParentMap = new HashMap<String, Integer>();

		summaryHelper.loadLookupItems(dataAccountIdToTest, dbCon, itemByCountryMap, parentskuByParentMap);

		int totalItems = 0;

		for (Integer itemId : itemByCountryMap.keySet()) {
			HashMap<String, LookupItem> countries = itemByCountryMap.get(itemId);
			if (countries != null) {
				totalItems += countries.size();
			}
		}

		Assert.assertNotNull(itemByCountryMap);
		Assert.assertTrue(!itemByCountryMap.isEmpty());
		Assert.assertEquals("Number of items loaded from db for the account does not match our expectation", expectedDataAccountItems, totalItems);
	}

	@Test
	public void testFindOrCreateMainLookupItem() throws DataAccessException {
		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = new HashMap<Integer, HashMap<String, LookupItem>>();
		HashMap<String, Integer> parentskuByParentMap = new HashMap<String, Integer>();

		summaryHelper.loadLookupItems(dataAccountIdToTest, dbCon, itemByCountryMap, parentskuByParentMap);

		ArrayList<LookupItem> updatedLookupItems = new ArrayList<LookupItem>();

		LookupItem lookupItem = summaryHelper.findOrCreateMainLookupItem(dataAccountIdToTest, testSubscriptionSaleEntry, itemByCountryMap, parentskuByParentMap, updatedLookupItems, SALE_SOURCE.DB);

		Assert.assertNotNull(lookupItem);
		Assert.assertNull(lookupItem.parentid);
		Assert.assertNull(lookupItem.parentsku);
	}


	@Test
	public void testProcessSummaryForSale() throws DataAccessException {
		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = new HashMap<Integer, HashMap<String, LookupItem>>();
		HashMap<String, Integer> parentskuByParentMap = new HashMap<String, Integer>();

		summaryHelper.loadLookupItems(dataAccountIdToTest, dbCon, itemByCountryMap, parentskuByParentMap);

		ArrayList<LookupItem> updatedLookupItems = new ArrayList<LookupItem>();
		LookupItem mainLookupItem = summaryHelper.findOrCreateMainLookupItem(dataAccountIdToTest, testSubscriptionSaleEntry, itemByCountryMap, parentskuByParentMap, updatedLookupItems, SALE_SOURCE.DB);

		HashMap<Integer, HashMap<String, SaleSummary>> summaries = new HashMap<Integer, HashMap<String, SaleSummary>>();
		summaryHelper.processSummaryForSale(dataAccountIdToTest, summaries, testSubscriptionSaleEntry, mainLookupItem, updatedLookupItems, SALE_SOURCE.DB);

		assertTrue(summaries.size() == 1);
		HashMap<String, SaleSummary> gbSummary = summaries.values().iterator().next();
		assertNotNull(gbSummary);
		assertTrue(gbSummary.size() == 1);
		SaleSummary saleSummary = gbSummary.values().iterator().next();
		assertEquals(new Integer(11397), saleSummary.subs_revenue);
	}

	@Test
	public void testUpdateLookupItemWithSaleDetails() {
		/*
			lookupItem.currency = sale.currency;
			lookupItem.title = sale.title;
			lookupItem.parentsku = sale.parentIdentifier == null ? null : sale.parentIdentifier.trim();
			lookupItem.sku = sale.sku == null ? null : sale.sku.trim();
			lookupItem.price = getSaleItemPrice(sale, saleSource);
		 */

		LookupItem item = new LookupItem();
		summaryHelper.updateLookupItemWithSaleDetails(testSubscriptionSaleEntry, SALE_SOURCE.DB, item);

		assertEquals(testSubscriptionSaleEntry.currency, item.currency);
		assertEquals(testSubscriptionSaleEntry.title, item.title);
		assertEquals(testSubscriptionSaleEntry.parentIdentifier.trim(), item.parentsku);
		assertEquals(testSubscriptionSaleEntry.sku, item.sku);
		assertEquals(new Integer(3799), item.price);
	}

	@Test
	public void testSummariseSales() throws DataAccessException {
		ArrayList<Sale> sales = SaleServiceProvider.provide().getSalesForDataAccountOnDate(dataAccountIdToTest, dateToTest);

		summaryHelper.summariseSales(dataAccountIdToTest, sales, SALE_SOURCE.DB, dbCon);
	}
}
