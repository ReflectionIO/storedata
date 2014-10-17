//
//  DataAccountIngestor.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdataingestors;

import io.reflection.app.datatypes.shared.DataAccountFetch;

/**
 * @author billy1380
 * 
 */
public interface DataAccountIngestor {

	void ingest(DataAccountFetch fetch);

}
