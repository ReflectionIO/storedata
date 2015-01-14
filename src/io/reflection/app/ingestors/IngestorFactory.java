//  
//  IngestorFactory.java
//  storedata
//
//  Created by William Shakour on 30 Aug 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.ingestors;

import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

		if (DataTypeHelper.IOS_STORE_A3.equals(store.toLowerCase())) {
			ingestor = new IngestorIOS();
		} else if ("azn".equals(store.toLowerCase())) {
			// do amazon ingest here
		} else if ("gpl".equals(store.toLowerCase())) {
			// do google play ingest here
		}

		return ingestor;
	}

	public static Collection<String> getIngestorCountries(String store) {
		Set<String> countries = new HashSet<String>();
		String propertyValue = System.getProperty("ingest." + store + ".countries");

		if (propertyValue != null && propertyValue.length() > 0) {
			String[] split = propertyValue.split(",");

			if (split != null) {
				for (int i = 0; i < split.length; i++) {
					if (split[i] != null) {
						countries.add(split[i]);
					}
				}
			}
		}

		return countries;
	}
}
