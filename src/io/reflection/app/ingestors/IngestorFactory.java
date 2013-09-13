//  
//  IngestorFactory.java
//  storedata
//
//  Created by William Shakour on 30 Aug 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.ingestors;

import java.util.ArrayList;
import java.util.List;

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
		} else if ("azn".equals(store.toLowerCase())) {} else if ("gpl".equals(store.toLowerCase())) {}

		return ingestor;
	}

	public static List<String> getIngestorCountries(String store) {
		List<String> countries = new ArrayList<>();
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
