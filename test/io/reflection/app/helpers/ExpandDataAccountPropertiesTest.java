//
//  ExpandDataAccountPropertiesTest.java
//  storedata
//
//  Created by mamin on 4 Feb 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.helpers.AppleReporterHelper.AppleReporterException;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccount.IDataAccountService;
import io.reflection.app.shared.util.PagerHelper;

/**
 * @author mamin
 *
 */
public class ExpandDataAccountPropertiesTest {

	private static final String DBPassword = "sooth28@duns";
	private static final String DBUsername = "rio_app_user";
	private static final String DB = "rio";

	// Live DB
	private static final String DBServer = "173.194.244.48";
	// DEV DB
	// private static final String DBServer = "173.194.226.90";

	@Before
	public void setup() {
		System.setProperty("connection.native", "true");
		System.setProperty("db.connection.server", DBServer);
		System.setProperty("db.connection.catalogue", DB);
		System.setProperty("db.connection.username", DBUsername);
		System.setProperty("db.connection.password", DBPassword);
	}

	@Test
	public void expandDataAccountPropertiesToColumns() throws DataAccessException {
		IDataAccountService dataAccountService = DataAccountServiceProvider.provide();

		List<DataAccount> dataAccounts = dataAccountService.getDataAccounts(PagerHelper.createInfinitePager());

		for (DataAccount dataAccount : dataAccounts) {
			List<SimpleEntry<String, String>> accountAndVendorIdsFromProperties = DataAccountPropertiesHelper
					.getAccountAndVendorIdsFromProperties(dataAccount.properties);
			if (accountAndVendorIdsFromProperties != null && accountAndVendorIdsFromProperties.size() > 0) {
				String accountId = accountAndVendorIdsFromProperties.get(0).getKey();
				String vendorId = accountAndVendorIdsFromProperties.get(0).getValue();

				dataAccount.accountId(accountId);
				dataAccount.vendorId(vendorId);
				dataAccountService.updateDataAccount(dataAccount, false);

				System.out.println(String.format("DataAccount id: %3d: username: %42s, vendorId: %-11s, accountId: %-11s", dataAccount.id, dataAccount.username,
						dataAccount.vendorId, dataAccount.accountId));
			}

			String vendorId = DataAccountPropertiesHelper.getPrimaryVendorId(dataAccount.properties);
			if (vendorId != null) {
				try {
					String accountIdForVendorId = AppleReporterHelper.getAccountIdForVendorId(dataAccount.username, dataAccount.password, vendorId);
					if (accountIdForVendorId != null && accountIdForVendorId.trim().length() > 0) {
						dataAccount.accountId(accountIdForVendorId);
					}
				} catch (AppleReporterException e) {}

				dataAccount.vendorId(vendorId);
				dataAccountService.updateDataAccount(dataAccount, false);
				System.out.println(String.format("DataAccount id: %3d: username: %42s, vendorId: %-11s, accountId: %-11s", dataAccount.id, dataAccount.username,
						dataAccount.vendorId, dataAccount.accountId));
			}
		}
	}
}
