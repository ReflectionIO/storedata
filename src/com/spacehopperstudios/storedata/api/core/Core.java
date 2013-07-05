//  
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.api.core;

import static com.spacehopperstudios.storedata.objectify.PersistenceService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.cmd.Query;
import com.spacehopperstudios.storedata.api.core.call.GetCountriesRequest;
import com.spacehopperstudios.storedata.api.core.call.GetCountriesResponse;
import com.spacehopperstudios.storedata.api.core.call.GetItemRanksRequest;
import com.spacehopperstudios.storedata.api.core.call.GetItemRanksResponse;
import com.spacehopperstudios.storedata.api.core.call.GetStoresRequest;
import com.spacehopperstudios.storedata.api.core.call.GetStoresResponse;
import com.spacehopperstudios.storedata.api.core.call.GetTopItemsRequest;
import com.spacehopperstudios.storedata.api.core.call.GetTopItemsResponse;
import com.spacehopperstudios.storedata.datatypes.Country;
import com.spacehopperstudios.storedata.input.ValidationError;
import com.spacehopperstudios.storedata.input.ValidationHelper;
import com.willshex.gson.json.service.ActionHandler;
import com.willshex.gson.json.service.InputValidationException;
import com.willshex.gson.json.service.StatusType;

public final class Core extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Core.class.getName());

	public GetCountriesResponse getCountries(GetCountriesRequest input) {
		LOG.finer("Entering getCountries");
		GetCountriesResponse output = new GetCountriesResponse();
		try {
			if (input == null)
				throw new NullPointerException("Invalid argument null - GetCountriesRequest: input");

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			boolean isQuery = false;
			boolean isStore = false;

			try {
				input.store = ValidationHelper.validateStore(input.store, "input");
				isStore = true;
			} catch (InputValidationException ex) {
			}

			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {
			}

			Query<Country> query = ofy().load().type(Country.class).offset(input.pager.start.intValue()).limit(input.pager.count.intValue());
			List<Country> countries = null;

			if (isStore) {
				query = query.filter("a2Code in", input.store.countries);
				countries = query.list();
			} else if (isQuery) {
				// we do not modify the query with a filter if the query is *
				if (input.query.equals("*")) {
					countries = query.list();
				} else {
					query = query.filter("a2Code", input.query);
					countries = query.list();

					if (countries.size() < input.pager.count.intValue()) {
						query = ofy().load().type(Country.class).offset(input.pager.start.intValue()).limit(input.pager.count.intValue() - countries.size())
								.filter("name", input.query);

						// We have no control over whether countries is a read-only list so we just copy it
						List<Country> merged = new ArrayList<Country>(query.list());
						merged.addAll(countries);
						countries = merged;
					}
				}
			} else
				throw new InputValidationException(ValidationError.GetCountriesNeedsStoreOrQuery.getCode(),
						ValidationError.GetCountriesNeedsStoreOrQuery.getMessage("input"));

			if (countries != null) {
				output.countries = countries;
				output.pager = input.pager;
			}

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