//
//  DataAccountPropertiesHelper.java
//  storedata
//
//  Created by mamin on 18 Jan 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.willshex.gson.json.shared.Convert;

/**
 * @author mamin
 *
 */
public class DataAccountPropertiesHelper {
	private static final String	VENDORS									= "vendors";
	private static final String	ACCOUNTS_AND_VENDOR_IDS	= "accountsAndVendorIds";
	private static final String	VENDOR_ID								= "vendorId";
	private static final String	ACCOUNT_ID							= "accountId";

	/**
	 * @param accountId
	 * @param vendorId
	 * @param jsonProperties
	 */
	public static String addAccountIdAndVendorIdToProperties(String accountId, String vendorId, String jsonProperties) {
		JsonObject jsonObject = addAccountIdAndVendorIdToProperties(accountId, vendorId, Convert.toJsonObject(jsonProperties));
		return jsonObject.toString();
	}

	/**
	 * @param accountId
	 * @param vendorId
	 * @param jsonProperties
	 */
	@SuppressWarnings("unchecked")
	private static JsonObject addAccountIdAndVendorIdToProperties(String accountId, String vendorId, JsonObject jsonProperties) {
		if (jsonProperties == null) {
			jsonProperties = new JsonObject();
		}

		JsonArray accountAndVendorIds = jsonProperties.getAsJsonArray(ACCOUNTS_AND_VENDOR_IDS);
		if (accountAndVendorIds == null) {
			accountAndVendorIds = new JsonArray();
			jsonProperties.add(ACCOUNTS_AND_VENDOR_IDS, accountAndVendorIds);
		}

		boolean combinationAlreadyExists = false;
		for (int i = 0; i < accountAndVendorIds.size(); i++) {
			JsonObject obj = accountAndVendorIds.get(i).getAsJsonObject();
			String objAccountId = obj.getAsJsonPrimitive(ACCOUNT_ID).getAsString();
			String objVendorId = obj.getAsJsonPrimitive(VENDOR_ID).getAsString();

			if (accountId.equals(objAccountId) && vendorId.equals(objVendorId)) {
				combinationAlreadyExists = true;
				break;
			}
		}

		if (!combinationAlreadyExists) {
			JsonObject account = createObjectWithProperties(
					new SimpleEntry<String, String>(ACCOUNT_ID, accountId),
					new SimpleEntry<String, String>(VENDOR_ID, vendorId));

			accountAndVendorIds.add(account);
		}

		return jsonProperties;
	}

	/**
	 * @param jsonProperties
	 * @return
	 */
	public static List<String> getVendorIdsFromProperties(String jsonProperties) {
		if (jsonProperties == null || jsonProperties.trim().length() == 0) return new ArrayList<>(0);

		return getVendorIdsFromProperties(Convert.toJsonObject(jsonProperties));
	}

	/**
	 * @param jsonProperties
	 * @return
	 */
	private static List<String> getVendorIdsFromProperties(JsonObject jsonProperties) {
		if (jsonProperties == null) return new ArrayList<>(0);

		JsonElement vendorsElement = jsonProperties.get(VENDORS);
		if (!vendorsElement.isJsonArray()) return new ArrayList<>(0);

		JsonArray vendors = vendorsElement.getAsJsonArray();
		ArrayList<String> vendorIdList = new ArrayList<>(vendors.size());
		for (int i = 0; i < vendors.size(); i++) {
			vendorIdList.add(vendors.get(i).getAsString());
		}

		return vendorIdList;
	}

	@SuppressWarnings("unchecked")
	private static JsonObject createObjectWithProperties(SimpleEntry<String, String>... properties) {
		JsonObject obj = new JsonObject();

		for (SimpleEntry<String, String> entry : properties) {
			obj.addProperty(entry.getKey(), entry.getValue());
		}

		return obj;
	}

	/**
	 * @param jsonProperties
	 * @return
	 */
	public static List<SimpleEntry<String, String>> getAccountAndVendorIdsFromProperties(String jsonProperties) {
		if (jsonProperties == null || jsonProperties.trim().length() == 0) return new ArrayList<>(0);

		return getAccountAndVendorIdsFromProperties(Convert.toJsonObject(jsonProperties));
	}

	/**
	 * @param jsonProperties
	 * @return
	 */
	private static List<SimpleEntry<String, String>> getAccountAndVendorIdsFromProperties(JsonObject jsonProperties) {
		if (jsonProperties == null) return new ArrayList<SimpleEntry<String, String>>(0);

		JsonElement accountsAndVendorIdsElement = jsonProperties.get(ACCOUNTS_AND_VENDOR_IDS);
		if (!accountsAndVendorIdsElement.isJsonArray()) return new ArrayList<>(0);

		JsonArray accountsAndVendorIds = accountsAndVendorIdsElement.getAsJsonArray();
		ArrayList<SimpleEntry<String, String>> vendorIdList = new ArrayList<>(accountsAndVendorIds.size());
		for (int i = 0; i < accountsAndVendorIds.size(); i++) {
			JsonObject pair = accountsAndVendorIds.get(i).getAsJsonObject();
			String accountId = pair.get(ACCOUNT_ID).getAsString();
			String vendorId = pair.get(VENDOR_ID).getAsString();

			vendorIdList.add(new SimpleEntry<>(accountId, vendorId));
		}

		return vendorIdList;
	}
}
