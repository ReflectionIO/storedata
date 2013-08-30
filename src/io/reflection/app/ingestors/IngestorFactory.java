//  
//  IngestorFactory.java
//  storedata
//
//  Created by William Shakour on 30 Aug 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.ingestors;


/**
 * @author billy1380
 * 
 */
public class IngestorFactory {

	/**
	 * @param store
	 * @return
	 */
	public static Ingestor getIngestorForStore(String store) {
		Ingestor ingestor = null;

		if ("ios".equals(store.toLowerCase())) {
			ingestor = new IngestorIOS();
		} else if ("amazon".equals(store.toLowerCase())) {
		} else if ("play".equals(store.toLowerCase())) {
		}

		return ingestor;
	}

}
