package io.reflection.app.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.logging.client.ConsoleLogHandler;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.helpers.SaleSummaryHelper.SALE_SOURCE;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.service.sale.SaleServiceProvider;

/**
 * This test class is set to be ignored. It is used just for manual performance testing of the summarisation. It is not meant to be run as part of a normal test suite.
 */
public class SaleSummaryHelperUtil {
	private static final String	DBServer		= "localhost";
	private static final String	DB					= "rio";
	private static final String	DBUsername	= "root";
	private static final String	DBPassword	= "password";

	Connection dbCon = null;

	private SaleSummaryHelper summaryHelper;


	@Before
	public void setup() throws DataAccessException {
		Logger globalLog = java.util.logging.Logger.getGlobal();

		globalLog.setLevel(Level.FINER);
		globalLog.addHandler(new ConsoleLogHandler());

		System.setProperty("connection.native", "true");
		System.setProperty("db.connection.server", DBServer);
		System.setProperty("db.connection.catalogue", DB);
		System.setProperty("db.connection.username", DBUsername);
		System.setProperty("db.connection.password", DBPassword);

		dbCon = new Connection(DBServer, DB, DBUsername, DBPassword);
		dbCon.connect();

		summaryHelper = SaleSummaryHelper.INSTANCE;
	}

	@After
	public void tearDown() throws DataAccessException {
		dbCon.realDisconnect();
	}

	@Test
	public void testSummariseSales() throws DataAccessException {
		Calendar dateFrom = Calendar.getInstance();
		Calendar dateTo = Calendar.getInstance();

		// (0 based month)
		dateFrom.set(2014, 6, 25);
		dateTo.set(2015, 5, 30);

		long totalTime = 0;
		long maxTime = 0;
		long minTime = Long.MAX_VALUE;
		int count = 0;

		long lookupTime = System.currentTimeMillis();
		List<Long> dataAccountList = SaleServiceProvider.provide().getDataAccountsWithSalesBetweenDates(dateFrom.getTime(), dateTo.getTime());
		lookupTime = System.currentTimeMillis() - lookupTime;
		System.out.println(String.format("Lookup time: %ds. Accounts found: %d", (lookupTime == 0 ? 0 : lookupTime / 1000), dataAccountList.size()));

		while (dateFrom.before(dateTo) || dateFrom.equals(dateTo)) {
			System.out.println("Starting lap for: " + dateFrom.getTime());

			long lapStartTime = System.currentTimeMillis();

			for (Long dataAccountId : dataAccountList) {
				long accountTime = System.currentTimeMillis();
				ArrayList<Sale> sales = SaleServiceProvider.provide().getSalesForDataAccountOnDate(dataAccountId, dateFrom.getTime());
				summaryHelper.summariseSales(dataAccountId, sales, SALE_SOURCE.DB, dbCon);
				accountTime = System.currentTimeMillis() - accountTime;
				if (accountTime > 1000) {
					System.out.println(String.format("Account: %d took %dms", dataAccountId, accountTime));
				}
			}

			long lapTime = System.currentTimeMillis() - lapStartTime;
			totalTime += lapTime;

			if (lapTime < minTime) {
				minTime = lapTime;
			}
			if (lapTime > maxTime) {
				maxTime = lapTime;
			}

			dateFrom.add(Calendar.DATE, 1);
			System.out.println("Lap: " + count++);
		}

		System.out.println(String.format("Total time taken : %ds, average lap: %d, min lap: %dms, max lap: %dms", (totalTime == 0 ? 0 : totalTime / 1000),
				(totalTime == 0 || count == 0 ? 0 : totalTime / count / 1000), minTime, maxTime));
	}
}
