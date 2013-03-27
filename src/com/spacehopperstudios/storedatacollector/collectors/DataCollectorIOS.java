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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * @author billy1380
 * 
 */
public class DataCollectorIOS implements DataCollector {

	private static final Logger LOG = Logger.getLogger(DataCollectorIOS.class);

	private static final String COUNTRIES_KEY = "gather.iOS.countries";

	private static final String KEY_FORMAT = "gather.iOS.%s.%s";

	private static final String TOP_FREE_APPS = "topfreeapplications";
	private static final String TOP_PAID_APPS = "toppaidapplications";
	private static final String TOP_GROSSING_APPS = "topgrossingapplications";
	private static final String TOP_FREE_IPAD_APPS = "topfreeipadapplications";
	private static final String TOP_PAID_IPAD_APPS = "toppaidipadapplications";
	private static final String TOP_GROSSING_IPAD_APPS = "topgrossingipadapplications";
	private static final String NEW_APPS = "newapplications";
	private static final String NEW_FREE_APPS = "newfreeapplications";
	private static final String NEW_PAID_APPS = "newpaidapplications";

	private static final int MAX_DATA_CHUNK_LENGTH = 500000;

	private static final String DATASTORE_ENITY_NAME = "FeedFetch";

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

	private List<String> splitEqually(String text, int size) {
		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}

		return ret;
	}

	private void store(String data, String countryCode, String store, String type, Date date) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving Data to data store");
		}

		List<String> splitData = splitEqually(data, MAX_DATA_CHUNK_LENGTH);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Data split into [%d] chuncks", splitData.size()));
		}

		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

		for (int i = 0; i < splitData.size(); i++) {
			Entity entity = new Entity(DATASTORE_ENITY_NAME);

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Data chunck [%d of %d] is [%d] characters", i, splitData.size(), splitData.get(i).length()));
			}

			Text dataAsTextField = new Text(splitData.get(i));
			entity.setProperty("data", dataAsTextField);
			entity.setProperty("country", countryCode);
			entity.setProperty("store", store);
			entity.setProperty("type", type);
			entity.setProperty("date", date);
			entity.setProperty("part", Integer.valueOf(i + 1));
			entity.setProperty("totalparts", Integer.valueOf(splitData.size()));

			if (LOG.isTraceEnabled()) {
				LOG.trace(String.format("Saving entity [%s]", entity.toString()));
			}

			datastoreService.put(entity);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Storing entity complete [%s] [%s] [%s] at [%s]", countryCode, store, type, date.toString()));
		}
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
