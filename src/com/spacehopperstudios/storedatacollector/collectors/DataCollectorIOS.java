/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author billy1380
 * 
 */
public class DataCollectorIOS extends DataStoreDataCollector implements DataCollector {

	private static final Logger LOG = Logger.getLogger(DataCollectorIOS.class.getName());

	private static final String COUNTRIES_KEY = "gather.iOS.countries";

	private static final String KEY_FORMAT = "gather.iOS.%s";

	public static final String TOP_FREE_APPS = "topfreeapplications";
	public static final String TOP_PAID_APPS = "toppaidapplications";
	public static final String TOP_GROSSING_APPS = "topgrossingapplications";
	public static final String TOP_FREE_IPAD_APPS = "topfreeipadapplications";
	public static final String TOP_PAID_IPAD_APPS = "toppaidipadapplications";
	public static final String TOP_GROSSING_IPAD_APPS = "topgrossingipadapplications";
	public static final String NEW_APPS = "newapplications";
	public static final String NEW_FREE_APPS = "newfreeapplications";
	public static final String NEW_PAID_APPS = "newpaidapplications";

	@Override
	public void enqueueCountriesAndTypes() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spacehopperstudios.storedatacollector.collectors.DataCollector#collect()
	 */
	@Override
	public void collect(String country, String type) {
		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Entering...");
		}

		try {
			String countries = System.getProperty(COUNTRIES_KEY);
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Found countries [%s].", countries));
			}

			List<String> splitCountries = new ArrayList<String>();
			Collections.addAll(splitCountries, countries.split(","));

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("[%d] countries to fetch data for", splitCountries.size()));
			}

			String data;
			String code = UUID.randomUUID().toString();
			for (String countryCode : splitCountries) {
				data = get(countryCode, TOP_FREE_APPS);
				store(data, countryCode, "ios", TOP_FREE_APPS, new Date(), code);

				data = get(countryCode, TOP_PAID_APPS);
				store(data, countryCode, "ios", TOP_PAID_APPS, new Date(), code);

				data = get(countryCode, TOP_GROSSING_APPS);
				store(data, countryCode, "ios", TOP_GROSSING_APPS, new Date(), code);

				data = get(countryCode, TOP_FREE_IPAD_APPS);
				store(data, countryCode, "ios", TOP_FREE_IPAD_APPS, new Date(), code);

				data = get(countryCode, TOP_PAID_IPAD_APPS);
				store(data, countryCode, "ios", TOP_PAID_IPAD_APPS, new Date(), code);

				data = get(countryCode, TOP_GROSSING_IPAD_APPS);
				store(data, countryCode, "ios", TOP_GROSSING_IPAD_APPS, new Date(), code);

				data = get(countryCode, NEW_APPS);
				store(data, countryCode, "ios", NEW_APPS, new Date(), code);

				data = get(countryCode, NEW_FREE_APPS);
				store(data, countryCode, "ios", NEW_FREE_APPS, new Date(), code);

				data = get(countryCode, NEW_PAID_APPS);
				store(data, countryCode, "ios", NEW_PAID_APPS, new Date(), code);
			}

		} finally {
			if (LOG.isLoggable(Level.FINER)) {
				LOG.finer("Exiting...");
			}
		}
	}

	private String get(String countryCode, String type) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Getting data for [%s] and type [%s]", countryCode, type));
		}

		String key = String.format(KEY_FORMAT, type);
		
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("key is [%s]", key));
		}
		
		String endpoint = String.format(System.getProperty(key), countryCode);

		return HttpDataGetter.getData(endpoint);
	}

	

}
