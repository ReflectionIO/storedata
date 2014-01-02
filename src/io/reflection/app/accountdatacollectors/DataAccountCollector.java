//
//  DataAccountCollector.java
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
public interface DataAccountCollector {

	/**
	 * @param dataAccount
	 * @param begin
	 * @param end
	 */
	void collect(DataAccount dataAccount, Date begin, Date end);

}
