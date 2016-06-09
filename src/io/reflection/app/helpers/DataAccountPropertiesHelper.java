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
import java.util.Collections;
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
	public static JsonObject addAccountIdAndVendorIdToProperties(String accountId, String vendorId, JsonObject jsonProperties) {
		if (jsonProperties == null) {
			jsonProperties = new JsonObject();
		}

		JsonArray accountAndVendorIds = null;
		try {
			accountAndVendorIds = jsonProperties.getAsJsonArray(ACCOUNTS_AND_VENDOR_IDS);
		} catch (Exception e) {
		}

		if (accountAndVendorIds == null) {
			accountAndVendorIds = new JsonArray();
			jsonProperties.add(ACCOUNTS_AND_VENDOR_IDS, accountAndVendorIds);
		}

		boolean combinationAlreadyExists = false;
		for (int i = 0; i < accountAndVendorIds.size(); i++) {
			JsonObject obj = null;
			try {
				obj = accountAndVendorIds.get(i).getAsJsonObject();
			} catch (Exception e) {
			}

			if (obj == null) {
				continue;
			}

			String objAccountId = null;
			String objVendorId = null;

			try {
				objAccountId = obj.getAsJsonPrimitive(ACCOUNT_ID).getAsString();
				objVendorId = obj.getAsJsonPrimitive(VENDOR_ID).getAsString();
			} catch (Exception e) {
			}

			if (objAccountId == null || objVendorId == null) {
				continue;
			}

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
	public static List<String> getVendorIdsFromProperties(JsonObject jsonProperties) {
		if (jsonProperties == null) return new ArrayList<>(0);

		JsonElement vendorsElement = jsonProperties.get(VENDORS);
		if (!vendorsElement.isJsonArray()) return new ArrayList<>(0);

		JsonArray vendors = null;
		try {
			vendors = vendorsElement.getAsJsonArray();
		} catch (Exception e) {
			return Collections.emptyList();
		}

		ArrayList<String> vendorIdList = new ArrayList<>(vendors.size());
		for (int i = 0; i < vendors.size(); i++) {
			try {
				vendorIdList.add(vendors.get(i).getAsString());
			} catch (Exception e) {
			}
		}

		return vendorIdList;
	}

	@SuppressWarnings("unchecked")
	public static JsonObject createObjectWithProperties(SimpleEntry<String, String>... properties) {
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
	public static List<SimpleEntry<String, String>> getAccountAndVendorIdsFromProperties(JsonObject jsonProperties) {
		if (jsonProperties == null) return Collections.emptyList();

		JsonElement accountsAndVendorIdsElement = jsonProperties.get(ACCOUNTS_AND_VENDOR_IDS);

		if (accountsAndVendorIdsElement == null) return new ArrayList<>(0);

		if (!accountsAndVendorIdsElement.isJsonArray()) return new ArrayList<>(0);

		JsonArray accountsAndVendorIds = null;
		try {
			accountsAndVendorIds = accountsAndVendorIdsElement.getAsJsonArray();
		} catch (Exception e) {
			return Collections.emptyList();
		}

		ArrayList<SimpleEntry<String, String>> vendorIdList = new ArrayList<>(accountsAndVendorIds.size());
		for (int i = 0; i < accountsAndVendorIds.size(); i++) {
			try {
				JsonObject pair = accountsAndVendorIds.get(i).getAsJsonObject();

				String accountId = pair.get(ACCOUNT_ID).getAsString();
				String vendorId = pair.get(VENDOR_ID).getAsString();

				vendorIdList.add(new SimpleEntry<>(accountId, vendorId));
			} catch (Exception e) {
				// sometimes these values are not null but are stored as JSONNull and getting them as strings causes an exception to be thrown.
			}
		}

		return vendorIdList;
	}

	/**
	 * @return
	 */
	public static String clearAccountAndVendorIds(String jsonProperties) {
		JsonObject jsonObject = clearAccountAndVendorIds(Convert.toJsonObject(jsonProperties));
		return jsonObject.toString();
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public static JsonObject clearAccountAndVendorIds(JsonObject jsonProperties) {
		if (jsonProperties == null) return new JsonObject();

		JsonElement accountsAndVendorIdsElement = jsonProperties.get(ACCOUNTS_AND_VENDOR_IDS);
		if (accountsAndVendorIdsElement != null) {
			jsonProperties.remove(ACCOUNTS_AND_VENDOR_IDS);
		}
		return jsonProperties;
	}

	/**
	 * @param properties
	 * @return
	 */
	public static String getPrimaryVendorId(String jsonProperties) {
		List<String> vendorIdsFromProperties = getVendorIdsFromProperties(jsonProperties);

		if (vendorIdsFromProperties == null || vendorIdsFromProperties.size() == 0) return null;

		return vendorIdsFromProperties.get(0);
	}
}
