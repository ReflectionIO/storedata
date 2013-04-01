/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

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

		return getData(key);
	}

	private String getData(String key) {
		String data = null;
		String endpoint = System.getProperty(key);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("endpoint is [%s]", endpoint));
		}

		try {
			URL url = new URL(endpoint);
			URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();

			HTTPRequest request = new HTTPRequest(url, HTTPMethod.POST);
			request.getFetchOptions().setDeadline(Double.valueOf(20));

			if (LOG.isTraceEnabled()) {
				LOG.trace("Fetching response");
			}
			HTTPResponse response = fetcher.fetch(request);

			int responseCode = response.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				byte[] content = response.getContent();

				if (content == null || content.length == 0) {
					LOG.error(String.format("Response for [%s] was empty", endpoint));
				} else {
					data = new String(content);

					if (LOG.isInfoEnabled()) {
						LOG.info(String.format("Recievend [%d] bytes for request", content.length));
					}

					if (LOG.isTraceEnabled()) {
						LOG.trace(data);
					}
				}
			} else {
				LOG.error(String.format("Http error occured for request to [%s] with code [%d]", endpoint, responseCode));
			}
		} catch (MalformedURLException e) {
			LOG.error(String.format("Error creating url [%s] from property [%s]", endpoint, key), e);
		} catch (IOException e) {
			LOG.error(String.format("Error fetching response for url [%s]", endpoint, key), e);
		}

		return data;
	}

}
