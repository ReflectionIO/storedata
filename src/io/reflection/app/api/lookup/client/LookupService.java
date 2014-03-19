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
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.HttpException;
import com.willshex.gson.json.service.client.JsonService;

public final class LookupService extends JsonService {
	public static final String LookupMethodLookupApplication = "LookupApplication";

	public Request lookupApplication(final LookupApplicationRequest input, final AsyncCallback<LookupApplicationResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(LookupMethodLookupApplication, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						LookupApplicationResponse outputParameter = new LookupApplicationResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(LookupService.this, LookupMethodLookupApplication, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(LookupService.this, LookupMethodLookupApplication, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(LookupService.this, LookupMethodLookupApplication, input, exception);
				}
			});
			onCallStart(LookupService.this, LookupMethodLookupApplication, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(LookupService.this, LookupMethodLookupApplication, input, exception);
		}
		return handle;
	}
}