/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
public class HttpDataGetter {

	private static final Logger LOG = Logger.getLogger(HttpDataGetter.class);

	/**
	 * Gets data from a url using HTTP POST (although it looks like it could easily have been a GET)
	 * 
	 * If the url is not stored or requires modifying then this method can be called directly
	 * 
	 * @param key
	 * @return
	 */
	public static String getData(URL url) {
		String data = null;
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();

		try {
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
					LOG.error(String.format("Response for [%s] was empty", url.toString()));
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
				LOG.error(String.format("Http error occured for request to [%s] with code [%d]", url.toString(), responseCode));
			}
		} catch (IOException e) {
			LOG.error(String.format("Error fetching response for url [%s]", url.toString()), e);
		}

		return data;
	}

	/**
	 * If the url is stored in a system property, this method will fetch the property using the key and get the data based on the stored url
	 * 
	 * @param key
	 * @return
	 */
	public static String getData(String key) {
		String data = null;
		String endpoint = System.getProperty(key);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("endpoint is [%s]", endpoint));
		}

		try {
			URL url = new URL(endpoint);
			data = getData(url);
		} catch (MalformedURLException e) {
			LOG.error(String.format("Error creating url [%s] from property [%s]", endpoint, key), e);
		}

		return data;
	}
}
