package io.reflection.app.accountdatacollectors;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.willshex.gson.json.service.server.InputValidationException;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.LookupItem;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.datatypes.shared.SaleSummary;
import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.AppleReporterHelper.AppleReporterException;
import io.reflection.app.helpers.AppleReporterHelper.DateType;
import io.reflection.app.helpers.SaleSummaryHelper;
import io.reflection.app.helpers.SalesReportHelper;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.lookupitem.LookupItemService;
import io.reflection.app.service.sale.SaleServiceProvider;

public class ItunesReporterCollectorTest {
	// To enable the tests you need to fill in the username and password for an itunes user who has access to 2 accounts.
	// Tested originally with the Cube TV account.

	private static final String	USERID			= "";
	private static final String	PASSWORD		= "";
	private static final String	ACCOUNTID		= "308588";
	private static final String	VENDORID		= "85037116";

	// for the splits vs report test, you need access to a DB for the lookup items table to be able to summarise sales
	private static final String	DBPassword	= "sooth28@duns";
	private static final String	DBUsername	= "rio_app_user";
	private static final String	DB					= "rio";

	// Live DB
	// private static final String DBServer = "173.194.244.48";
	// DEV DB
	private static final String	DBServer		= "173.194.226.90";

	@Before
	public void setup() {
		System.setProperty("connection.native", "true");
		System.setProperty("db.connection.server", DBServer);
		System.setProperty("db.connection.catalogue", DB);
		System.setProperty("db.connection.username", DBUsername);
		System.setProperty("db.connection.password", DBPassword);
	}

	@Test
	@Ignore
	public void getReportTest() throws IOException, AppleReporterException {

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		byte[] report = null;
		report = AppleReporterHelper.getReport(USERID, PASSWORD, ACCOUNTID, VENDORID, AppleReporterHelper.DateType.DAILY, cal.getTime());

		assertNotNull(report);
	}

	@Test
	@Ignore
	public void getReportVendorExceptionTest() throws IOException {
		String accountId = "308588";
		String vendorId = "123";

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		try {
			AppleReporterHelper.getReport(USERID, PASSWORD, accountId, vendorId, AppleReporterHelper.DateType.DAILY, cal.getTime());
		} catch (AppleReporterException e) {
			assertEquals(200, e.getErrorCode()); // Apple Error code 200 = Invalid vendor number
		}
	}

	@Test
	@Ignore
	public void getReportAccountExceptionTest() throws IOException {
		String accountId = "123";
		String vendorId = "85037116";

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		try {
			AppleReporterHelper.getReport(USERID, PASSWORD, accountId, vendorId, AppleReporterHelper.DateType.DAILY, cal.getTime());
		} catch (AppleReporterException e) {
			assertEquals(216, e.getErrorCode()); // Apple Error code 216 = Invalid account number
		}
	}

	@Test
	@Ignore
	public void getVendorIdFromItunesWithoutAccountIdTest() throws InputValidationException {
		try {
			AppleReporterHelper.getVendors(USERID, PASSWORD);
			fail("This call should have crashed as there should be 2 accounts on itunes and you can't get vendors without an accountid");
		} catch (AppleReporterException e) {
			assertEquals(214, e.getErrorCode());
		}
	}

	@Test
	@Ignore
	public void getVendorIdFromItunesTest() throws InputValidationException, AppleReporterException {
		List<String> vendors = AppleReporterHelper.getVendors(USERID, PASSWORD, ACCOUNTID);
		assertNotNull(vendors);
		assertEquals(1, vendors.size());
	}

	@Test
	@Ignore
	public void getAccountsFromITunesWithInvalidLoginTest() {
		try {
			AppleReporterHelper.getAccounts("abcd", "1234");
		} catch (AppleReporterException e) {
			assertEquals(108, e.getErrorCode());
		}
	}

	@Test
	@Ignore
	public void getAccountsFromITunesTest() throws AppleReporterException {
		Map<String, String> accounts = AppleReporterHelper.getAccounts(USERID, PASSWORD);
		assertNotNull(accounts);
		assertEquals(2, accounts.size());
	}

	@Test
	@Ignore
	public void testGetSummary() throws DataAccessException {
		DataAccount miniclip = new DataAccount();
		miniclip.id = 264L;
		miniclip.username = "finance@miniclip.com";
		miniclip.password = "";
		String miniclipVendorId = "85011246";

		String segaFootballManager2016Id = "997012040";

		DataAccount sega = new DataAccount();
		sega.id = 332L;
		sega.username = "reflections@sega.net";
		sega.password = "";
		String segaVendorId = "80046332";

		String EightballPoolItemId = "543186831";

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		SaleSummary eightBallDBSummary = SaleServiceProvider.provide().getSaleSummary(miniclip.id, EightballPoolItemId, "gb", cal.getTime());
		SaleSummary segaFootballManagerDBSummary = SaleServiceProvider.provide().getSaleSummary(sega.id, segaFootballManager2016Id, "gb", cal.getTime());

		System.out.println("Done: \n" + eightBallDBSummary + "\n\n" + segaFootballManagerDBSummary);
	}

	@Test
	@Ignore
	public void splitsVsReportComparison() throws AppleReporterException, IOException, DataAccessException {
		DataAccount miniclip = new DataAccount();
		miniclip.id = 264L;
		miniclip.username = "finance@miniclip.com";
		miniclip.password = "";
		String miniclipVendorId = "85011246";
		String EightballPoolItemId = "543186831";

		DataAccount sega = new DataAccount();
		sega.id = 332L;
		sega.username = "reflections@sega.net";
		sega.password = "";
		String segaVendorId = "80046332";
		String segaFootballManager2016Id = "997012040";

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		HashMap<Integer, HashMap<String, SaleSummary>> miniclipSummaries = getSaleSummariesForAccount(miniclip, miniclipVendorId, cal);
		HashMap<Integer, HashMap<String, SaleSummary>> segaSummaries = getSaleSummariesForAccount(sega, segaVendorId, cal);
		assertNotNull(miniclipSummaries);
		assertNotNull(segaSummaries);

		SaleSummary eightBallDBSummary = SaleServiceProvider.provide().getSaleSummary(miniclip.id, EightballPoolItemId, "gb", cal.getTime());
		SaleSummary segaFootballManagerDBSummary = SaleServiceProvider.provide().getSaleSummary(sega.id, segaFootballManager2016Id, "gb", cal.getTime());

		SaleSummary eightBallReportSummary = getSaleSummaryForItemInCountry(EightballPoolItemId, "gb", miniclipSummaries);
		SaleSummary segaFootballManagerReportSummary = getSaleSummaryForItemInCountry(segaFootballManager2016Id, "gb", segaSummaries);

		System.out.println("8 Ball Pool - DB summary:\n" + eightBallDBSummary + "\nReport summary:\n" + eightBallReportSummary + "\n\n");
		System.out.println("Football manager - DB summary:\n" + segaFootballManagerDBSummary + "\nReport summary:\n" + segaFootballManagerReportSummary + "\n\n");

		compareSummaries(eightBallDBSummary, eightBallReportSummary);
		compareSummaries(segaFootballManagerDBSummary, segaFootballManagerReportSummary);
	}

	/**
	 * @param dbSummary
	 * @param reportSummary
	 */
	private void compareSummaries(SaleSummary dbSummary, SaleSummary reportSummary) {
		System.out.println("\nItem: " + dbSummary.title);
		System.out.println(String.format("%25s | %9s | %9s", "Field", "Database", "Reporter"));
		System.out.println(String.format("%25s | %9d | %9d", "iap_revenue", dbSummary.iap_revenue, reportSummary.iap_revenue));
		System.out.println(String.format("%25s | %9d | %9d", "iphone_app_revenue", dbSummary.iphone_app_revenue, reportSummary.iphone_app_revenue));
		System.out.println(String.format("%25s | %9d | %9d", "iphone_iap_revenue", dbSummary.iphone_iap_revenue, reportSummary.iphone_iap_revenue));
		System.out.println(String.format("%25s | %9d | %9d", "ipad_app_revenue", dbSummary.ipad_app_revenue, reportSummary.ipad_app_revenue));
		System.out.println(String.format("%25s | %9d | %9d", "ipad_iap_revenue", dbSummary.ipad_iap_revenue, reportSummary.ipad_iap_revenue));
		System.out.println(String.format("%25s | %9d | %9d", "iphone_downloads", dbSummary.iphone_downloads, reportSummary.iphone_downloads));
		System.out.println(String.format("%25s | %9d | %9d", "ipad_downloads", dbSummary.ipad_downloads, reportSummary.ipad_downloads));
		System.out.println(String.format("%25s | %9d | %9d", "universal_downloads", dbSummary.universal_downloads, reportSummary.universal_downloads));
		System.out.println(String.format("%25s | %9d | %9d", "universal_app_revenue", dbSummary.universal_app_revenue, reportSummary.universal_app_revenue));
		System.out.println();
	}

	/**
	 * @param itemId
	 * @param countryCode
	 * @param summaries
	 * @return
	 */
	private SaleSummary getSaleSummaryForItemInCountry(String itemId, String countryCode, HashMap<Integer, HashMap<String, SaleSummary>> summaries) {
		int key = Integer.parseInt(itemId);

		if (!summaries.containsKey(key)) return null;

		HashMap<String, SaleSummary> itemSummaries = summaries.get(key);
		if (!itemSummaries.containsKey(countryCode)) return null;

		return itemSummaries.get(countryCode);
	}

	private HashMap<Integer, HashMap<String, SaleSummary>> getSaleSummariesForAccount(DataAccount dataAccount, String vendorId, Calendar cal) throws AppleReporterException, DataAccessException {

		LookupItemService lookupService = LookupItemService.INSTANCE;

		String accountId = AppleReporterHelper.getAccountIdForVendorId(dataAccount.username, dataAccount.password, vendorId);

		byte[] reportBinary = AppleReporterHelper.getReport(dataAccount.username, dataAccount.password, accountId, vendorId, DateType.DAILY, cal.getTime());

		List<Sale> sales = SalesReportHelper.convertReportToSales(reportBinary, null);
		assertNotNull(sales);

		ArrayList<LookupItem> updatedLookupItems = new ArrayList<LookupItem>();
		List<LookupItem> lookupItemsForAccount = lookupService.getLookupItemsForAccount(dataAccount.id);

		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = lookupService.mapItemsByCountry(lookupItemsForAccount);
		HashMap<String, Integer> skuByItemMap = lookupService.mapItemsBySku(lookupItemsForAccount);

		HashMap<Integer, HashMap<String, SaleSummary>> summaries = SaleSummaryHelper.INSTANCE.summariseSales(
				dataAccount.id,
				sales,
				SaleSummaryHelper.SALE_SOURCE.INGEST,
				updatedLookupItems,
				itemByCountryMap,
				skuByItemMap);
		return summaries;
	}

	@Test
	@Ignore
	public void compareDbWithReporter() throws FileNotFoundException, DataAccessException, AppleReporterException, IOException {
		DataAccount dataAccount1 = DataAccountServiceProvider.provide().getDataAccount(264L);
		String vendorId1 = "85011246";
		String itemId1 = "543186831";
		String reportFileName1 = "/tmp/miniclip_reporter_20160101.csv";

		DataAccount dataAccount2 = DataAccountServiceProvider.provide().getDataAccount(332L);
		String vendorId2 = "80046332";
		String itemId2 = "997012040";
		String reportFileName2 = "/tmp/sega_reporter_20160101.csv";

		DataAccount dataAccount3 = DataAccountServiceProvider.provide().getDataAccount(391L);

		// 391 on live only
		// for dev use the details below
		// DataAccount dataAccount3 = new DataAccount();
		// dataAccount3.id = 391L;
		// dataAccount3.username = "reflection@ninjakiwi.com";
		// dataAccount3.password = "";

		String vendorId3 = "85107048";
		String itemId3 = "563718995";
		String reportFileName3 = "/tmp/ninjakiwi_reporter_20160101.csv";

		testReporterIAPNumbers(dataAccount1, vendorId1, itemId1, reportFileName1);
		testReporterIAPNumbers(dataAccount2, vendorId2, itemId2, reportFileName2);
		testReporterIAPNumbers(dataAccount3, vendorId3, itemId3, reportFileName3);
	}

	private void testReporterIAPNumbers(DataAccount dataAccount, String vendorId, String itemId, String reportFileName)
			throws AppleReporterException, FileNotFoundException, IOException, DataAccessException {
		LookupItemService lookupService = LookupItemService.INSTANCE;

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		File reportFile = new File(reportFileName);
		if (!reportFile.exists() && !reportFile.isFile()) {
			writeReportToFile(dataAccount, vendorId, cal.getTime(), reportFile);
		}

		List<String> reportLines = IOUtils.readLines(new GZIPInputStream(new FileInputStream(reportFile)));
		reportLines.remove(0);
		List<Sale> sales = SalesReportHelper.convertReportToSales(reportLines, dataAccount);

		ArrayList<LookupItem> updatedLookupItems = new ArrayList<LookupItem>();
		List<LookupItem> lookupItemsForAccount = lookupService.getLookupItemsForAccount(dataAccount.id);

		HashMap<Integer, HashMap<String, LookupItem>> itemByCountryMap = lookupService.mapItemsByCountry(lookupItemsForAccount);
		HashMap<String, Integer> skuByItemMap = lookupService.mapItemsBySku(lookupItemsForAccount);

		HashMap<Integer, HashMap<String, SaleSummary>> summaries = SaleSummaryHelper.INSTANCE.summariseSales(
				dataAccount.id,
				sales,
				SaleSummaryHelper.SALE_SOURCE.DB, // as we don't convert to pennies
				updatedLookupItems,
				itemByCountryMap,
				skuByItemMap);

		SaleSummary poolReportSummary = getSaleSummaryForItemInCountry(itemId, "gb", summaries);
		SaleSummary poolDBSummary = SaleServiceProvider.provide().getSaleSummary(dataAccount.id, itemId, "gb", cal.getTime());

		compareSummaries(poolDBSummary, poolReportSummary);
	}

	private void writeReportToFile(DataAccount dataAccount, String vendorId, Date date, File outputFile) throws FileNotFoundException, IOException, AppleReporterException {
		String accountId = AppleReporterHelper.getAccountIdForVendorId(dataAccount.username, dataAccount.password, vendorId);

		byte[] reportBinary = AppleReporterHelper.getReport(dataAccount.username, dataAccount.password, accountId, vendorId, DateType.DAILY, date);

		IOUtils.write(reportBinary, new FileOutputStream(outputFile));
		System.out.println("Report written to: " + outputFile.getAbsolutePath());
	}

	@Test
	public void test() throws DataAccessException, AppleReporterException, InputValidationException {
		DataAccount spaceHopper = DataAccountServiceProvider.provide().getDataAccount(388L);

		Map<String, String> accounts = AppleReporterHelper.getAccounts(spaceHopper.username, spaceHopper.password);
		for (String accountName : accounts.keySet()) {
			String accountId = accounts.get(accountName);
			List<String> vendors = AppleReporterHelper.getVendors(spaceHopper.username, spaceHopper.password, accountId);

			for (String vendor : vendors) {
				System.out.println(String.format("Account: %s, accountId: %s, vendorId: %s", accountName, accountId, vendor));
			}
		}
	}
}
