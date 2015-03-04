//
//  Queue.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

/**
 * @author William Shakour (billy1380)
 *
 */
public interface Queue {

	public static final String DEFERRED = "deferred";
	public static final String GATHER = "gather";
	public static final String INGEST = "ingest";
	public static final String MODEL = "model";
	public static final String PREDICT = "predict";
	public static final String PERSIST = "persist";
	public static final String ITEM_PROPERTY_LOOKUP = "itempropertylookup";
	public static final String DATA_ACCOUNT_GATHER = "dataaccountgather";
	public static final String DATA_ACCOUNT_INGEST = "dataaccountingest";
	public static final String CALL_SERVICE_METHOD = "callservicemethod";
	public static final String REFRESH_ITEM_PROPERTIES = "refreshitemproperties";
	public static final String ARCHIVE = "archive";

}
