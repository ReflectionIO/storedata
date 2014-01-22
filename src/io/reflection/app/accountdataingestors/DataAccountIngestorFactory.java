//
//  DataAccountIngestorFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdataingestors;

/**
 * @author billy1380
 * 
 */
public class DataAccountIngestorFactory {
	/**
	 * @param source
	 * @return
	 */
	public static DataAccountIngestor getIngestorForSource(String source) {
		DataAccountIngestor ingestor = null;

		if ("itc".equalsIgnoreCase(source.toLowerCase())) {
			// itunes connect
			ingestor = new DataAccountIngestorITunesConnect();
		} else if ("azn".equals(source.toLowerCase())) {
			// amazon store
		} else if ("gpl".equals(source.toLowerCase())) {
			// google play store
		}

		return ingestor;
	}
}
