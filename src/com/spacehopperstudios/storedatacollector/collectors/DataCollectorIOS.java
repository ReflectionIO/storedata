/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author billy1380
 * 
 */
public class DataCollectorIOS extends DataStoreDataCollector implements DataCollector {

	private static final Logger LOG = Logger.getLogger(DataCollectorIOS.class);

	private static final String COUNTRIES_KEY = "gather.iOS.countries";

	private static final String KEY_FORMAT = "gather.iOS.%s.%s";

	public static final String TOP_FREE_APPS = "topfreeapplications";
	public static final String TOP_PAID_APPS = "toppaidapplications";
	public static final String TOP_GROSSING_APPS = "topgrossingapplications";
	public static final String TOP_FREE_IPAD_APPS = "topfreeipadapplications";
	public static final String TOP_PAID_IPAD_APPS = "toppaidipadapplications";
	public static final String TOP_GROSSING_IPAD_APPS = "topgrossingipadapplications";
	public static final String NEW_APPS = "newapplications";
	public static final String NEW_FREE_APPS = "newfreeapplications";
	public static final String NEW_PAID_APPS = "newpaidapplications";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spacehopperstudios.storedatacollector.collectors.DataCollector#collect()
	 */
	@Override
	public boolean collect() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Entering...");
		}

		boolean success = false;
		try {
			String countries = System.getProperty(COUNTRIES_KEY);
			if (LOG.isInfoEnabled()) {
				LOG.info(String.format("Found countries [%s].", countries));
			}

			List<String> splitCountries = new ArrayList<String>();
			Collections.addAll(splitCountries, countries.split(","));

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("[%d] countries to fetch data for", splitCountries.size()));
			}

			String data;
			for (String countryCode : splitCountries) {
				data = get(countryCode, TOP_FREE_APPS);
				store(data, countryCode, "ios", TOP_FREE_APPS, new Date());

				data = get(countryCode, TOP_PAID_APPS);
				store(data, countryCode, "ios", TOP_PAID_APPS, new Date());

				data = get(countryCode, TOP_GROSSING_APPS);
				store(data, countryCode, "ios", TOP_GROSSING_APPS, new Date());

				data = get(countryCode, TOP_FREE_IPAD_APPS);
				store(data, countryCode, "ios", TOP_FREE_IPAD_APPS, new Date());

				data = get(countryCode, TOP_PAID_IPAD_APPS);
				store(data, countryCode, "ios", TOP_PAID_IPAD_APPS, new Date());

				data = get(countryCode, TOP_GROSSING_IPAD_APPS);
				store(data, countryCode, "ios", TOP_GROSSING_IPAD_APPS, new Date());

				data = get(countryCode, NEW_APPS);
				store(data, countryCode, "ios", NEW_APPS, new Date());

				data = get(countryCode, NEW_FREE_APPS);
				store(data, countryCode, "ios", NEW_FREE_APPS, new Date());

				data = get(countryCode, NEW_PAID_APPS);
				store(data, countryCode, "ios", NEW_PAID_APPS, new Date());
			}

			success = true;
		} finally {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Exiting...");
			}
		}

		return success;
	}

	private String get(String countryCode, String type) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Getting data for [%s] and type [%s]", countryCode, type));
		}

		String key = String.format(KEY_FORMAT, countryCode, type);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Key for endpoint is [%s]", key));
		}

		return HttpDataGetter.getData(key);
	}

	

}
