//  
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.api.core;

import java.util.logging.Logger;

import com.spacehopperstudios.storedata.api.core.call.GetCountriesRequest;
import com.spacehopperstudios.storedata.api.core.call.GetCountriesResponse;
import com.spacehopperstudios.storedata.api.core.call.GetItemRanksRequest;
import com.spacehopperstudios.storedata.api.core.call.GetItemRanksResponse;
import com.spacehopperstudios.storedata.api.core.call.GetStoresRequest;
import com.spacehopperstudios.storedata.api.core.call.GetStoresResponse;
import com.spacehopperstudios.storedata.api.core.call.GetTopItemsRequest;
import com.spacehopperstudios.storedata.api.core.call.GetTopItemsResponse;
import com.willshex.gson.json.service.ActionHandler;
import com.willshex.gson.json.service.StatusType;

public final class Core extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Core.class.getName());

	public GetCountriesResponse getCountries(GetCountriesRequest input) {
		LOG.finer("Entering getCountries");
		GetCountriesResponse output = new GetCountriesResponse();
		try {
			if (input == null)
				throw new NullPointerException("Invalid argument null - GetCountriesRequest: input");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getCountries");
		return output;
	}

	public GetStoresResponse getStores(GetStoresRequest input) {
		LOG.finer("Entering getStores");
		GetStoresResponse output = new GetStoresResponse();
		try {
			if (input == null)
				throw new NullPointerException("Invalid argument null - GetStoresRequest: input");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getStores");
		return output;
	}

	public GetTopItemsResponse getTopItems(GetTopItemsRequest input) {
		LOG.finer("Entering getTopItems");
		GetTopItemsResponse output = new GetTopItemsResponse();
		try {
			if (input == null)
				throw new NullPointerException("Invalid argument null - GetTopItemsRequest: input");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getTopItems");
		return output;
	}

	public GetItemRanksResponse getItemRanks(GetItemRanksRequest input) {
		LOG.finer("Entering getItemRanks");
		GetItemRanksResponse output = new GetItemRanksResponse();
		try {
			if (input == null)
				throw new NullPointerException("Invalid argument null - GetItemRanksRequest: input");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getItemRanks");
		return output;
	}
}