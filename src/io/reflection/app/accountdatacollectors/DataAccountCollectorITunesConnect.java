//
//  DataAccountCollectorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import io.reflection.app.shared.datatypes.DataAccount;

import java.util.Date;

/**
 * @author billy1380
 * 
 */
public class DataAccountCollectorITunesConnect implements DataAccountCollector {

	private static final String USERNAME_KEY = "USERNAME";
	private static final String PASSWORD_KEY = "PASSWORD";
	private static final String VENDOR_NUMBER_KEY = "VNDNUMBER";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#collect(io.reflection.app.shared.datatypes.DataAccount, java.util.Date, java.util.Date)
	 */
	@Override
	public void collect(DataAccount dataAccount, Date begin, Date end) {

	}

}
