//  
//  CoreJsonServlet.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsRequest;

import com.google.gson.JsonObject;
import com.willshex.gson.json.service.server.JsonServlet;

@SuppressWarnings("serial")
public final class CoreJsonServlet extends JsonServlet {
	@Override
	protected String processAction(String action, JsonObject request) {
		String output = "null";
		Core service = new Core();

		if ("GetCountries".equals(action)) {
			GetCountriesRequest input = new GetCountriesRequest();
			input.fromJson(request);
			output = service.getCountries(input).toString();
		} else if ("GetStores".equals(action)) {
			GetStoresRequest input = new GetStoresRequest();
			input.fromJson(request);
			output = service.getStores(input).toString();
		} else if ("GetTopItems".equals(action)) {
			GetTopItemsRequest input = new GetTopItemsRequest();
			input.fromJson(request);
			output = service.getTopItems(input).toString();
		} else if ("GetAllTopItems".equals(action)) {
			GetAllTopItemsRequest input = new GetAllTopItemsRequest();
			input.fromJson(request);
			output = service.getAllTopItems(input).toString();
		} else if ("GetItemRanks".equals(action)) {
			GetItemRanksRequest input = new GetItemRanksRequest();
			input.fromJson(request);
			output = service.getItemRanks(input).toString();
		}

		return output;
	}
}