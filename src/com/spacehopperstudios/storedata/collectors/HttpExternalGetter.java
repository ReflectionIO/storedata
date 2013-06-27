/**
 * 
 */
package com.spacehopperstudios.storedata.collectors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.spacehopperstudios.storedata.logging.GaeLevel;

/**
 * @author billy1380
 * 
 */
public class HttpExternalGetter {

	private static final Logger LOG = Logger.getLogger(HttpExternalGetter.class.getName());

	/**
	 * Gets data from a url using HTTP POST (although it looks like it could easily have been a GET)
	 * 
	 * If the url is not stored or requires modifying then this method can be called directly
	 * 
	 * @param key
	 * @return
	 */
	public static String getData(URL url, HTTPMethod method) {
		String data = null;
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();

		try {
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.POST);
			request.getFetchOptions().setDeadline(Double.valueOf(20));

			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Fetching response");
			}
			HTTPResponse response = fetcher.fetch(request);

			int responseCode = response.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				byte[] content = response.getContent();

				if (content == null || content.length == 0) {
					LOG.log(Level.SEVERE, String.format("Response for [%s] was empty", url.toString()));
				} else {
					data = new String(content);

					if (LOG.isLoggable(Level.INFO)) {
						LOG.info(String.format("Recievend [%d] bytes for request", content.length));
					}

					if (LOG.isLoggable(GaeLevel.TRACE)) {
						LOG.log(GaeLevel.TRACE, data);
					}
				}
			} else {
				LOG.log(Level.SEVERE, String.format("Http error occured for request to [%s] with code [%d]", url.toString(), responseCode));
			}
		} catch (IOException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, String.format("Error fetching response for url [%s]", url.toString()), e);
			}
		}

		return data;
	}

	/**
	 * If the url is stored in a system property, this method will fetch the property using the key and get the data based on the stored url
	 * 
	 * @param key
	 * @return
	 */
	public static String getData(String endpoint, HTTPMethod method) {
		String data = null;

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("endpoint is [%s]", endpoint));
		}

		try {
			URL url = new URL(endpoint);
			data = getData(url, method);
		} catch (MalformedURLException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, String.format("Error creating url from endpoint [%s]", endpoint), e);
			}
		}

		return data;
	}
	
	public static String getData(String endpoint) {
		return getData(endpoint, HTTPMethod.POST);
	}
}
