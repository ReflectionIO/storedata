//  
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.api.core.call.GetCountriesRequest;
import io.reflection.app.api.core.call.GetCountriesResponse;
import io.reflection.app.api.core.call.GetItemRanksRequest;
import io.reflection.app.api.core.call.GetItemRanksResponse;
import io.reflection.app.api.core.call.GetStoresRequest;
import io.reflection.app.api.core.call.GetStoresResponse;
import io.reflection.app.api.core.call.GetTopItemsRequest;
import io.reflection.app.api.core.call.GetTopItemsResponse;
import io.reflection.app.api.datatypes.SortDirectionType;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Item;
import io.reflection.app.datatypes.Rank;
import io.reflection.app.datatypes.Store;
import io.reflection.app.input.ValidationError;
import io.reflection.app.input.ValidationHelper;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.googlecode.objectify.cmd.Query;
import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.shared.StatusType;

public final class Core extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Core.class.getName());

	public GetCountriesResponse getCountries(GetCountriesRequest input) {
		LOG.finer("Entering getCountries");
		GetCountriesResponse output = new GetCountriesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ValidationError.GetCountriesNeedsStoreOrQuery.getCode(),
						ValidationError.GetCountriesNeedsStoreOrQuery.getMessage("GetCountriesRequest: input"));

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

			List<Country> countries = null;

			if (isStore) {
				countries = CountryServiceProvider.provide().getStoreCountries(input.store, input.pager);
			} else if (isQuery) {
				// we do not modify the query with a filter if the query is *
				if (input.query.equals("*")) {
					countries = CountryServiceProvider.provide().getCountries(input.pager);
				} else {
					countries = CountryServiceProvider.provide().searchCountries(input.query, input.pager);
				}
			} else
				throw new InputValidationException(ValidationError.GetCountriesNeedsStoreOrQuery.getCode(),
						ValidationError.GetCountriesNeedsStoreOrQuery.getMessage("input"));

			if (countries != null) {
				output.countries = countries;
				output.pager = input.pager;

				if (output.pager.totalCount == null && output.pager.count != null && output.countries.size() < output.pager.count.intValue()) {
					output.pager.totalCount = Long.valueOf(output.countries.size());
				}
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
				throw new InputValidationException(ValidationError.GetCountriesNeedsStoreOrQuery.getCode(),
						ValidationError.GetCountriesNeedsStoreOrQuery.getMessage("GetStoresRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			boolean isQuery = false;
			boolean isCountry = false;

			try {
				input.country = ValidationHelper.validateCountry(input.country, "input");
				isCountry = true;
			} catch (InputValidationException ex) {
			}

			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {
			}

			List<Store> stores = null;

			if (isCountry) {
				stores = StoreServiceProvider.provide().getCountryStores(input.country, input.pager);
			} else if (isQuery) {
				// we do not modify the query with a filter if the query is *
				if (input.query.equals("*")) {
					stores = StoreServiceProvider.provide().getStores(input.pager);
				} else {
					stores = StoreServiceProvider.provide().searchStores(input.query, input.pager);
				}
			} else
				throw new InputValidationException(ValidationError.GetStoresNeedsCountryOrQuery.getCode(),
						ValidationError.GetStoresNeedsCountryOrQuery.getMessage("input"));

			if (stores != null) {
				output.stores = stores;
				output.pager = input.pager;

				if (output.pager.totalCount == null && output.pager.count != null && output.stores.size() < output.pager.count.intValue()) {
					output.pager.totalCount = Long.valueOf(output.stores.size());
				}
			}

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("GetTopItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			input.country = ValidationHelper.validateCountry(input.country, "input");
			
			if (input.listType == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.on == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(), ValidationError.InvalidValueNull.getMessage("Date: input.on"));

			input.store = ValidationHelper.validateStore(input.store, "input");
			
			if (input.store == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(), ValidationError.InvalidValueNull.getMessage("Store: input.store"));
			
			input.listType = ValidationHelper.validateListType(input.listType, input.store);

			Calendar cal = Calendar.getInstance();
			cal.setTime(input.on);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			Date after = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, 1);
			Date before = cal.getTime();

			String order = "";

			if (input.pager.sortDirection != null && input.pager.sortDirection == SortDirectionType.SortDirectionTypeAscending) {
				order = "-";
			}
			
			if (input.pager.sortBy == null || input.pager.sortBy.length() == 0) {
				order += "position";
			} else {
				order += input.pager.sortBy;
			}
			
			// TODO validate the sort by - only sortable rank column names allowed

			Query<Rank> query = ofy().load().type(Rank.class).filter("date >=", after).filter("date <", before).filter("country", input.country.a2Code)
					.filter("type", input.listType).filter("source", input.store.a3Code).offset(input.pager.start.intValue())
					.limit(input.pager.count.intValue()).order("-date").order(order);

			List<Rank> ranks = query.list();

			if (ranks != null && ranks.size() != 0) {
				List<String> itemIds = new ArrayList<String>();
				final Map<String, Rank> lookup = new HashMap<String, Rank>();

				for (Rank rank : ranks) {
					if (!lookup.containsKey(rank.itemId)) {
						itemIds.add(rank.itemId);
						lookup.put(rank.itemId, rank);
					}
				}

				output.ranks = ranks;
				output.items = ofy().load().type(Item.class).filter("externalId in", itemIds).list();
				output.pager = input.pager;
			}

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("GetItemRanksRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			input.item = ValidationHelper.validateItem(input.item, "input");

			input.country = ValidationHelper.validateCountry(input.country, "input");

			if (input.type == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("String: input.type"));

			Calendar cal = Calendar.getInstance();

			if (input.before == null)
				input.before = cal.getTime();

			if (input.after == null) {
				cal.setTime(input.before);
				cal.add(Calendar.DAY_OF_YEAR, -30);
				input.after = cal.getTime();
			}

			long diff = input.before.getTime() - input.after.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ValidationError.DateRangeOutOfBounds.getCode(),
						ValidationError.DateRangeOutOfBounds.getMessage("0-60 days: input.before - input.after"));

			output.ranks = ofy().load().type(Rank.class).offset(input.pager.start.intValue()).limit(input.pager.count.intValue())
					.filter("itemId", input.item.externalId).filter("source", input.item.source).filter("date <=", input.before).filter("date >=", input.after)
					.filter("type", input.type).filter("country", input.country.a2Code).order("-date").list();

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getItemRanks");
		return output;
	}
}