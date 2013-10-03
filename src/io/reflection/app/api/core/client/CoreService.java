//  
//  core/CoreService.java
//  storedata
//
//  Created by William Shakour on October 2, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.client;

import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.GetTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsResponse;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;

public final class CoreService extends JsonService {
	public static final String CoreMethodGetCountries = "GetCountries";

	public void getCountries(GetCountriesRequest input, final AsyncCallback<GetCountriesResponse> output) {
		try {
			sendRequest(CoreMethodGetCountries, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetCountriesResponse outputParameter = new GetCountriesResponse();
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

	public static final String CoreMethodGetStores = "GetStores";

	public void getStores(GetStoresRequest input, final AsyncCallback<GetStoresResponse> output) {
		try {
			sendRequest(CoreMethodGetStores, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetStoresResponse outputParameter = new GetStoresResponse();
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

	public static final String CoreMethodGetTopItems = "GetTopItems";

	public void getTopItems(GetTopItemsRequest input, final AsyncCallback<GetTopItemsResponse> output) {
		try {
			sendRequest(CoreMethodGetTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetTopItemsResponse outputParameter = new GetTopItemsResponse();
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

	public static final String CoreMethodGetAllTopItems = "GetAllTopItems";

	public void getAllTopItems(GetAllTopItemsRequest input, final AsyncCallback<GetAllTopItemsResponse> output) {
		try {
			sendRequest(CoreMethodGetAllTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetAllTopItemsResponse outputParameter = new GetAllTopItemsResponse();
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

	public static final String CoreMethodGetItemRanks = "GetItemRanks";

	public void getItemRanks(GetItemRanksRequest input, final AsyncCallback<GetItemRanksResponse> output) {
		try {
			sendRequest(CoreMethodGetItemRanks, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetItemRanksResponse outputParameter = new GetItemRanksResponse();
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