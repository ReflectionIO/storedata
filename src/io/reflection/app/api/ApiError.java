//
//  ValidationError.java
//  storedata
//
//  Created by William Shakour on 5 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api;

/**
 * @author billy1380
 * 
 */
public enum ApiError {

	AccessCodeNull(100001, "Invalid value null for String: %s.accessCode"),

	TokenNoMatch(100002, "Invalid value does not match scheme for String: %s"),

	StoreNull(100003, "Invalid value null for Store: %s.store"),
	StoreNotFound(100004, "Store not found Store: %s.value"),
	StoreNoLookup(100005, "Invalid store lookup, need either id or a3Code or name for Store: %s.store"),

	SearchQueryNull(100006, "Invalid value null search query for String: %s.query"),

	PagerStartLargerThanTotal(100007, "Invalid value for start, should be less than totalCount for Pager: %s.pager"),
	PagerStartNegative(100008, "Invalid negative value for Long: %s.pager.start"),
	PagerCountTooSmall(100009, "Invalid 0 or negative value for Long: %s.pager.count"),
	PagerCountTooLarge(100010, "Invalid value, maximum count should be <= 30 Long: %s.pager.count"),

	CountryNull(100011, "Invalid value null for Country: %s.country"),
	CountryNotFound(100012, "Country not found Country: %s.country"),
	CountryNoLookup(100013, "Invalid country lookup, need either id or a2Code or name for Country: %s.country"),

	InvalidValueNull(100014, "Invalid value null %s"),

	ItemNull(100015, "Invalid value null for Item: %s.item"),
	ItemNotFound(100016, "Item not found Item: %s.item"),
	ItemNoLookup(100017, "Invalid item lookup, need either id or externalId or internalId for Item: %s.item"),

	DateRangeOutOfBounds(100018, "Invalid date range, should be between %s"),

	UserNull(100019, "Invalid value null for User: %s.user"),

	StringNull(100020, "Invalid value null or empty for String: %s"),
	BadEmailFormat(100021, "Invalid email address for Email: %s"),
	ListTypeNotFound(100022, "List type not found for String: %s"),

	UserNotFound(100023, "User not found User: %s"),
	UserNoLookup(100024, "Invalid user lookup, need either id or username for User: %s"),

	InvalidStringTooShort(100025, "Invalid value too short (%d-%d): %s"),
	InvalidStringTooLong(100026, "Invalid value too long (%d-%d): %s"),

	RoleNull(100027, "Invalid value null for Role: %s"),
	RoleNotFound(100028, "Role not found Role: %s"),
	RoleNoLookup(100029, "Invalid user role lookup, need either id or name for Role: %s"),

	PermissionNull(100030, "Invalid value null for Permission: %s"),
	PermissionNotFound(100031, "Permission not found Permission: %s"),
	PermissionNoLookup(100032, "Invalid permission lookup, need either id or name for Permission: %s"),

	SessionNull(100033, "Invalid value null for Session: %s"),
	SessionNotFound(100034, "Session not found Session: %s"),
	SessionNoLookup(100035, "Invalid session lookup, need either id or token for Session: %s"),

	IncorrectPasswordForChange(100037, "Incorrect current password for user: %s"),
	InvalidPasswordSameAsCurrent(100038, "Invalid password, current and new passwords are identical: %s"),

	DataSourceNull(100039, "Invalid value null for DataSource: %s"),
	DataSourceNotFound(100040, "Data source not found DataSource: %s"),
	DataSourceNoLookup(100041, "Invalid data source lookup, need either id, a3Code or name for DataSource: %s"),

	GetCountriesNeedsStoreOrQuery(100101, "GetCountries call should either have a store or a query. To get all countries use * for the query: %s"),

	GetStoresNeedsCountryOrQuery(100201, "GetStores call should either have a country or a query. To get all stores use * for the query: %s"),

	// lookup service
	LookupApplicationNeedsInternalOrExternalId(100301,
			"LookupApplication should have at least one internal or external application id, you can also add multiple ids of each type: %s"),

	InvalidCredentials(200000, "Invalid credentials, either the username (e-mail) or password are incorrect"),

	MissingPermissions(300000, "User does not have required permissions"),
	MissingRoles(300001, "User does not have required role"),
	MissingRolesAndOrPermissions(300002, "User does not have required roles and/or permissions"), ;

	private int code;
	private String message;

	ApiError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getMessage(String parent) {
		return String.format(message, parent == null ? "?" : parent);
	}

	public String getMessage(String parent, int minValue, int maxValue) {
		return String.format(message, parent == null ? "?" : parent, minValue, maxValue);
	}

}
