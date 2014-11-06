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

	public static final String PERMISSION_FULL_RANK_VIEW_CODE = "FRV";
	public static final String PERMISSION_MANAGE_FEED_FETCHES_CODE = "MFF";
	public static final String PERMISSION_MANAGE_USERS_CODE = "MUS";
	public static final String PERMISSION_MANAGE_ROLES_CODE = "MRL";
	public static final String PERMISSION_MANAGE_PERMISSIONS_CODE = "MPR";
	public static final String PERMISSION_MANAGE_EMAIL_TEMPLATES_CODE = "MET";
	public static final String PERMISSION_MANAGE_ITEMS_CODE = "MIT";
	public static final String PERMISSION_MANAGE_BLOG_POSTS_CODE = "MBL";
	public static final String PERMISSION_BLOG_POST_CODE = "BPT";
	public static final String PERMISSION_BLOG_PUBLISH_OWN_CODE = "BPO";
	public static final String PERMISSION_BLOG_EDIT_OWN_CODE = "BEO";
	public static final String PERMISSION_BLOG_DELETE_OWN_CODE = "BDO";
	public static final String PERMISSION_BLOG_LIST_OWN_CODE = "BLO";
	public static final String PERMISSION_BLOG_PUBLISH_ANY_CODE = "BPA";
	public static final String PERMISSION_BLOG_EDIT_ANY_CODE = "BEA";
	public static final String PERMISSION_BLOG_DELETE_ANY_CODE = "BDA";
	public static final String PERMISSION_BLOG_LIST_ANY_CODE = "BLA";
	public static final String PERMISSION_BLOG_EDIT_PUBLISHED_CODE = "BEP";
	public static final String PERMISSION_BLOG_DELETE_PUBLISHED_CODE = "BDP";
	public static final String PERMISSION_HAS_LINKED_ACCOUNT_CODE = "HLA";
	public static final String PERMISSION_MANAGE_CATEGORIES_CODE = "MCA";
	public static final String PERMISSION_MANAGE_DATA_ACCOUNTS_CODE = "MDA";
	public static final String PERMISSION_MANAGE_SIMPLE_MODEL_RUN_CODE = "MSM";

	public static final String ROLE_ADMIN_CODE = "ADM";
	public static final Long ROLE_ADMIN_ID = Long.valueOf(1);
	public static final String ROLE_DEVELOPER_CODE = "DEV";
	public static final String ROLE_PREMIUM_CODE = "PRE";
	public static final String ROLE_ALPHA_CODE = "ALF";
	public static final String ROLE_FIRST_CLOSED_BETA_CODE = "BT1";
	public static final String ROLE_TEST_CODE = "TST";

	public static final String STORE_IPHONE_A3_CODE = "iph";
	public static final String STORE_IPAD_A3_CODE = "ipa";

	private static Role adminRole;

	/**
	 * Creates an admin role
	 * 
	 * @param id
	 * @return
	 */
	public static Role adminRole() {
		if (adminRole == null) {
			adminRole = new Role();
			adminRole.id = ROLE_ADMIN_ID;
		}
		return adminRole;
	}

	/**
	 * Creates a role with a given code
	 * 
	 * @param code
	 * @return
	 */
	public static Role createRole(String code) {
		Role role = new Role();
		role.code = code;
		return role;
	}

	/**
	 * Creates a permission with a given code
	 * 
	 * @param id
	 * @return
	 */
	public static Permission createPermission(String code) {
		Permission permission = new Permission();
		permission.code = code;
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
