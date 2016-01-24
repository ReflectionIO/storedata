//
//  DataAccountPropertiesHelperTest.java
//  storedata
//
//  Created by mamin on 22 Jan 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import static org.junit.Assert.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import org.junit.Test;

/**
 * @author mamin
 *
 */
public class DataAccountPropertiesHelperTest {
	@Test
	public void getAccountAndVendorIdsFromProperties() {
		List<SimpleEntry<String, String>> accountAndVendorIdPairs = DataAccountPropertiesHelper
				.getAccountAndVendorIdsFromProperties("{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}");
		assertNotNull(accountAndVendorIdPairs);
		assertTrue(accountAndVendorIdPairs.size() == 1);
		assertEquals("308588", accountAndVendorIdPairs.get(0).getKey());
		assertEquals("85037116", accountAndVendorIdPairs.get(0).getValue());

		accountAndVendorIdPairs = DataAccountPropertiesHelper.getAccountAndVendorIdsFromProperties(
				"{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"},{\"accountId\":\"12345\",\"vendorId\":\"ABCDEF\"}]}");
		assertNotNull(accountAndVendorIdPairs);
		assertTrue(accountAndVendorIdPairs.size() == 2);
		assertEquals("308588", accountAndVendorIdPairs.get(0).getKey());
		assertEquals("85037116", accountAndVendorIdPairs.get(0).getValue());
		assertEquals("12345", accountAndVendorIdPairs.get(1).getKey());
		assertEquals("ABCDEF", accountAndVendorIdPairs.get(1).getValue());

		accountAndVendorIdPairs = DataAccountPropertiesHelper.getAccountAndVendorIdsFromProperties("{\"vendors\":[\"85037116\"]}");
		assertNotNull(accountAndVendorIdPairs);
		assertEquals(0, accountAndVendorIdPairs.size());
	}

	@Test
	public void getVendorIdsFromProperties() {
		String testProperties = "{\"vendors\":[\"85037116\"]}";
		List<String> vendorIds = DataAccountPropertiesHelper.getVendorIdsFromProperties(testProperties);
		assertNotNull(vendorIds);
		assertEquals(1, vendorIds.size());
		assertEquals("85037116", vendorIds.get(0));
	}

	@Test
	public void addAccountIdAndVendorIdToProperties() {
		String testProperties = "{\"vendors\":[\"85037116\"]}";
		String accountId = "308588";
		String vendorId = "85037116";
		String accountId2 = "12345";
		String vendorId2 = "ABCDEF";

		String result = DataAccountPropertiesHelper.addAccountIdAndVendorIdToProperties(accountId, vendorId, testProperties);
		assertNotNull(result);
		assertEquals("{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}", result);

		result = DataAccountPropertiesHelper.addAccountIdAndVendorIdToProperties(accountId, vendorId, "");
		assertNotNull(result);
		assertEquals("{\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}", result);

		// make sure we don't add duplicates
		result = DataAccountPropertiesHelper.addAccountIdAndVendorIdToProperties(accountId, vendorId,
				"{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}");
		assertNotNull(result);
		assertEquals("{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}", result);

		result = DataAccountPropertiesHelper.addAccountIdAndVendorIdToProperties(accountId2, vendorId2,
				"{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}");
		assertNotNull(result);
		assertEquals("{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"},{\"accountId\":\"12345\",\"vendorId\":\"ABCDEF\"}]}", result);
	}

	@Test
	public void clearAccountAndVendorIdsTest() {
		String result = DataAccountPropertiesHelper.clearAccountAndVendorIds("{\"vendors\":[\"85037116\"],\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}");
		assertNotNull(result);
		assertEquals("{\"vendors\":[\"85037116\"]}", result);

		result = DataAccountPropertiesHelper.clearAccountAndVendorIds("{\"accountsAndVendorIds\":[{\"accountId\":\"308588\",\"vendorId\":\"85037116\"}]}");
		assertNotNull(result);
		assertEquals("{}", result);

		result = DataAccountPropertiesHelper.clearAccountAndVendorIds(null);
		assertNotNull(result);
		assertEquals("{}", result);

		result = DataAccountPropertiesHelper.clearAccountAndVendorIds("{\"vendors\":[\"85037116\"]}");
		assertNotNull(result);
		assertEquals("{\"vendors\":[\"85037116\"]}", result);
	}
}
