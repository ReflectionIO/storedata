//
//  DataTypeHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.datatypes.shared.User;

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

	public static final float SMALL_MONEY = 0.0000001f;

	public static final String IOS_STORE_A3 = "ios";

	public static final String ITC_SOURCE_A3 = "itc";

	private static final Store IOS_STORE = new Store();

	public static final String ACTIVE_VALUE = "y";
	public static final String INACTIVE_VALUE = "n";

	public static final String PERMISSION_FULL_RANK_VIEW_CODE = "FRV";
	public static final String PERMISSION_MANAGE_FEED_FETCHES_CODE = "MFF";
	public static final String PERMISSION_MANAGE_USERS_CODE = "MUS";
	public static final String PERMISSION_MANAGE_ROLES_CODE = "MRL";
	public static final String PERMISSION_MANAGE_PERMISSIONS_CODE = "MPR";
	public static final String PERMISSION_MANAGE_EVENTS_CODE = "MET";
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
	public static final String PERMISSION_MANAGE_EVENT_SUBSCRIPTIONS_CODE = "MES";
	public static final String PERMISSION_SEND_NOTIFICATIONS_CODE = "SNO";

	public static final String CHANGE_PASSWORD_EVENT_CODE = "PAS";
	public static final String RESET_PASSWORD_EVENT_CODE = "RPS";
	public static final String NEW_USER_EVENT_CODE = "WEL";
	public static final String SELECTED_USER_EVENT_CODE = "SEL";
	public static final String THANK_YOU_EVENT_CODE = "TRQ";
	public static final String SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE = "SCE";
	public static final String SALES_GATHER_GENERIC_ERROR_EVENT_CODE = "SGE";

	public static final String ROLE_ADMIN_CODE = "ADM";
	public static final Long ROLE_ADMIN_ID = Long.valueOf(1);
	public static final String ROLE_DEVELOPER_CODE = "DEV";
	public static final String ROLE_PREMIUM_CODE = "PRE";
	public static final String ROLE_ALPHA_CODE = "ALF";
	public static final String ROLE_FIRST_CLOSED_BETA_CODE = "BT1";
	public static final String ROLE_TEST_CODE = "TST";

	public static final String STORE_IPHONE_A3_CODE = "iph";
	public static final String STORE_IPAD_A3_CODE = "ipa";
	
	public static final String STORE_IPHONE_NAME = "iPhone Store";
	public static final String STORE_IPAD_NAME = "iPad Store";

	private static final Role ADMIN_ROLE = new Role();

	/**
	 * Gets instance of admin role
	 * 
	 * @param id
	 * @return
	 */
	public static Role adminRole() {
		if (ADMIN_ROLE.id == null || ROLE_ADMIN_ID.longValue() != ADMIN_ROLE.id.longValue()) {
			ADMIN_ROLE.id = ROLE_ADMIN_ID;
		}

		return ADMIN_ROLE;
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
			IOS_STORE.id = null;
		}

		return IOS_STORE;
	}

	/**
	 * Creates an instance of Store with an a3Code
	 * 
	 * @param a3Code
	 * @return
	 */
	public static Store createStore(String a3Code) {
		Store s = new Store();
		s.a3Code = a3Code.toLowerCase();
		return s;
	}

	/**
	 * Creates an instance of a Country with an a2Code
	 * 
	 * @param a2Code
	 * @return
	 */
	public static Country createCountry(String a2Code) {
		Country c = new Country();
		c.a2Code = a2Code.toLowerCase();

		return c;
	}

	public static boolean isZero(float value) {
		return value < SMALL_MONEY;
	}

	public static Category createCategory(Long id) {
		Category c = new Category();
		c.id = id;
		return c;
	}

	/**
	 * @param id
	 * @return
	 */
	public static FeedFetch createFeedFetch(Long id) {
		FeedFetch f = new FeedFetch();
		f.id = id;
		return f;
	}

	public static DataAccount createDataAccount(Long id) {
		DataAccount da = new DataAccount();
		da.id = id;
		return da;
	}

	/**
	 * @param id
	 * @return
	 */
	public static DataAccountFetch createDataAccountFetch(Long id) {
		DataAccountFetch daf = new DataAccountFetch();
		daf.id = id;
		return daf;
	}

}
