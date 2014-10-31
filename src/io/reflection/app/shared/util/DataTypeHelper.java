//
//  DataTypeHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.datatypes.shared.Store;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author billy1380
 * 
 */
public class DataTypeHelper {

	public static final String IOS_STORE_A3 = "ios";

	private static final Store IOS_STORE = new Store();

	public static final String ACTIVE_VALUE = "y";
	public static final String INACTIVE_VALUE = "n";

	public static final Long PERMISSION_FULL_RANK_VIEW_ID = Long.valueOf(1);
	public static final Long PERMISSION_BLOG_POST_ID = Long.valueOf(9);
	public static final Long PERMISSION_BLOG_LIST_ANY_ID = Long.valueOf(17);
	public static final Long PERMISSION_HAS_LINKED_ACCOUNT_ID = Long.valueOf(20);
	public static final Long PERMISSION_MANAGE_CATEGORIES_ID = Long.valueOf(21);
	public static final String PERMISSION_HAS_LINKED_ACCOUNT_CODE = "HLA";

	public static final Long ROLE_ADMIN_ID = Long.valueOf(1);
	public static final Long ROLE_DEVELOPER_ID = Long.valueOf(2);
	public static final Long ROLE_PREMIUM_ID = Long.valueOf(3);
	public static final Long ROLE_ALPHA_ID = Long.valueOf(4);
	public static final Long ROLE_BETA_ID = Long.valueOf(5);
	public static final Long ROLE_TEST_ID = Long.valueOf(6);

	public static final String STORE_IPHONE_A3_CODE = "iph";
	public static final String STORE_IPAD_A3_CODE = "ipa";

	/**
	 * Creates a role with a given id
	 * 
	 * @param id
	 * @return
	 */
	public static Role createRole(Long id) {
		Role role = new Role();
		role.id = id;
		return role;
	}

	/**
	 * Creates a permission with a given id
	 * 
	 * @param id
	 * @return
	 */
	public static Permission createPermission(Long id) {
		Permission permission = new Permission();
		permission.id = id;
		return permission;
	}

	/**
	 * Creates a User with a given id
	 * 
	 * @param id
	 * @return
	 */
	public static User createUser(Long id) {
		User user = new User();
		user.id = id;
		return user;
	}

	public static String itemIapState(Item item, String yes, String no, String unknown) {
		String usesIap = null;

		if (item != null) {
			usesIap = jsonPropertiesIapState(item.properties, yes, no, unknown);
		} else {
			usesIap = unknown;
		}

		return usesIap;
	}

	public static String jsonPropertiesIapState(String jsonProperties, String yes, String no, String unknown) {
		String usesIap = null;

		if (jsonProperties != null) {
			JsonElement propertiesJsonElement = (new JsonParser()).parse(jsonProperties);

			if (propertiesJsonElement.isJsonObject()) {
				JsonObject propertiesJsonObject = propertiesJsonElement.getAsJsonObject();
				JsonElement usesIapJsonElement = propertiesJsonObject.get("usesIap");

				if (usesIapJsonElement != null && usesIapJsonElement.isJsonPrimitive()) {
					if (usesIapJsonElement.getAsBoolean()) {
						usesIap = yes;
					} else {
						usesIap = no;
					}
				}
			}
		}

		if (usesIap == null) {
			usesIap = unknown;
		}

		return usesIap;
	}

	public static void sortRanksByDate(List<Rank> ranks) {
		if (ranks != null) {
			Collections.sort(ranks, new Comparator<Rank>() {

				@Override
				public int compare(Rank r1, Rank r2) {
					return r1.date.getTime() > r2.date.getTime() ? 1 : (r2.date.getTime() > r1.date.getTime() ? -1 : 0);
				}

			});
		}
	}

	public static void sortItemsByDate(List<Item> items) {
		if (items != null) {
			Collections.sort(items, new Comparator<Item>() {

				@Override
				public int compare(Item i1, Item i2) {
					return i1.added.getTime() > i2.added.getTime() ? 1 : (i2.added.getTime() > i1.added.getTime() ? -1 : 0);
				}

			});
		}
	}

	public static Store getIosStore() {
		if (!IOS_STORE_A3.equals(IOS_STORE.a3Code)) {
			IOS_STORE.a3Code = IOS_STORE_A3;
		}

		return IOS_STORE;
	}
}
