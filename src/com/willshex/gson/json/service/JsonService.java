//
//  JsonService.java
//  spacecloud
//
//  Created by William Shakour on February 12, 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.willshex.gson.json.service;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public abstract class JsonService {

	protected String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void sendRequest(String action, Request input, Response output) {
		String requestData = "action=" + action + "&request=" + input.toString();
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
		try {
			URL endpoint = new URL(url);
			HTTPRequest request = new HTTPRequest(endpoint, HTTPMethod.POST);
			request.setPayload(requestData.getBytes());
			request.getFetchOptions().setDeadline(Double.valueOf(20));
			HTTPResponse response = fetcher.fetch(request);
			int responseCode = response.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String responseText = new String(response.getContent());
				if (responseText != null && !"".equals(responseText)) {
					output.fromJson(responseText);
				} else {
					output.status = StatusType.StatusTypeFailure;
					output.error = new Error();
					output.error.code = -1;
					output.error.message = "Response was empty";
				}
			} else {
				output.status = StatusType.StatusTypeFailure;
				output.error = new Error();
				output.error.code = -1;
				output.error.message = "Http error occured with code [" + Integer.toString(responseCode) + "]";
			}
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = new Error();
			output.error.code = -1;
			output.error.message = e.toString();
		}
	}

}
