//
//  ValidationError.java
//  storedata
//
//  Created by William Shakour on 5 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.input;

/**
 * @author billy1380
 * 
 */
public enum ValidationError {

	AccessCodeNull(100001, "Invalid value null for String: %s.accessCode"), 
	AccessCodeNoMatch(100002, "Invalid value does not match scheme for String: %s.accessCode"), 
	StoreNull(100003, "Invalid value null for Store: %s.store"), 
	StoreNotFound(100004, "Store not found Store: %s.value"), 
	StoreNoLookup(100005, "Invalid store lookup, need either id or a3Code or name for Store: %s.store"),
	SearchQueryNull(100006, "Invalid value null search query for String: %s.query"),
	PagerStartLargerThanTotal(100007, "Invalid value for start, should be less than totalCount for Pager: %.pager"),
	PagerStartNegative(100008, "Invalid negative value for Long: %s.pager.start"), 
	PagerCountTooSmall(100009, "Invalid 0 or negative value for Long: %s.pager.count"), 
	PagerCountTooLarge(100010, "Invalid value, maximum count should be <= 30 Long: %s.pager.count"),
	
	GetCountriesNeedsStoreOrQuery(100101, "GetCountries call should either have a query or a store. To get all countries use * for the query"), ;

	private int code;
	private String message;

	ValidationError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage(String parent) {
		return String.format(message, parent == null ? "" : parent);
	}

}
