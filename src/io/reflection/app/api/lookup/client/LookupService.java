//  
//  lookup/LookupService.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.lookup.client;

import io.reflection.app.api.lookup.shared.call.LookupApplicationRequest;
import io.reflection.app.api.lookup.shared.call.LookupApplicationResponse;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;

public final class LookupService extends JsonService {
	public static final String LookupMethodLookupApplication = "LookupApplication";

	public void lookupApplication(LookupApplicationRequest input, final AsyncCallback<LookupApplicationResponse> output) {
		try {
			sendRequest(LookupMethodLookupApplication, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LookupApplicationResponse outputParameter = new LookupApplicationResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}
}