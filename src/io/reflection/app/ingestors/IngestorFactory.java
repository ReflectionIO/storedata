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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author billy1380
 * 
 */
public class IngestorFactory {

	private static final Logger LOG = Logger.getLogger(IngestorFactory.class.getName());

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

	public static boolean shouldIngestCategories(String store) {
		return (System.getProperty("ingest." + store + ".categories") == null ? Boolean.TRUE.booleanValue() : Boolean.valueOf(System.getProperty("ingest."
				+ store + ".categories")));
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

	public static boolean shouldIngestFeedFetch(String store, String country) {
		boolean ingest = false;

		Collection<String> countries = getIngestorCountries(store);

		if (countries.contains(country)) {
			ingest = true;
		} else {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Country [" + country + "] not in list of countries to ingest.");
			}
		}

		return ingest;
	}

	/**
	 * @param store
	 * @return
	 */
	public static Ingestor getBigQueryIngestorForStore(String store) {
		Ingestor ingestor = null;

		if (DataTypeHelper.IOS_STORE_A3.equals(store.toLowerCase())) {
			ingestor = new BigQueryRankIngestorIos();
		}

		return ingestor;
	}
}
