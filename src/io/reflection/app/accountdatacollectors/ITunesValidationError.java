//
//  ITunesValidationError.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

/**
 * @author billy1380
 * 
 */
public enum ITunesValidationError {

	NullVendorsArray(200001, "Could not find vendor array in additional properties while linking iTunes Connect account"),
	VendorsNotArray(200002, "Vendors parameter found but is not an array"),
	NullVendorId(200003, "Found vendor id in array but its value was null"),
	BadVendorIdFormat(200004, "Invalid vendor id, should be 8nnnnnnn");

	private int mCode;
	private String mMessage;

	ITunesValidationError(int code, String message) {
		this.mCode = code;
		this.mMessage = message;
	}

	public int getCode() {
		return mCode;
	}

	public String getMessage() {
		return mMessage;
	}

	public String getMessage(String parent) {
		return String.format(mMessage, parent == null ? "?" : parent);
	}

	public String getMessage(String parent, int minValue, int maxValue) {
		return String.format(mMessage, minValue, maxValue, parent == null ? "?" : parent);
	}
}
