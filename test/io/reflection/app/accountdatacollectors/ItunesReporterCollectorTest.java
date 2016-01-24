package io.reflection.app.accountdatacollectors;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.willshex.gson.json.service.server.InputValidationException;

import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.AppleReporterHelper.AppleReporterException;

public class ItunesReporterCollectorTest {
	// To enable the tests you need to fill in the username and password for an itunes user who has access to 2 accounts.
	// Tested originally with the Cube TV account.

	private static final String	USERID		= "";
	private static final String	PASSWORD	= "";
	private static final String	ACCOUNTID	= "308588";
	private static final String	VENDORID	= "85037116";

	@Test
	public void getReportTest() throws IOException, AppleReporterException {

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2016, 0, 1);

		byte[] report = null;
		report = AppleReporterHelper.getReport(USERID, PASSWORD, ACCOUNTID, VENDORID, AppleReporterHelper.DateType.DAILY, cal.getTime());

		assertNotNull(report);
	}

	@Test
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
	public void getVendorIdFromItunesWithoutAccountIdTest() throws InputValidationException {
		try {
			AppleReporterHelper.getVendors(USERID, PASSWORD);
			fail("This call should have crashed as there should be 2 accounts on itunes and you can't get vendors without an accountid");
		} catch (AppleReporterException e) {
			assertEquals(214, e.getErrorCode());
		}
	}

	@Test
	public void getVendorIdFromItunesTest() throws InputValidationException, AppleReporterException {
		List<String> vendors = AppleReporterHelper.getVendors(USERID, PASSWORD, ACCOUNTID);
		assertNotNull(vendors);
		assertEquals(1, vendors.size());
	}

	@Test
	public void getAccountsFromITunesWithInvalidLoginTest() {
		try {
			AppleReporterHelper.getAccounts("abcd", "1234");
		} catch (AppleReporterException e) {
			assertEquals(108, e.getErrorCode());
		}
	}

	@Test
	public void getAccountsFromITunesTest() throws AppleReporterException {
		Map<String, String> accounts = AppleReporterHelper.getAccounts(USERID, PASSWORD);
		assertNotNull(accounts);
		assertEquals(2, accounts.size());
	}
}
