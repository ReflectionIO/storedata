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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.googlecode.objectify.cmd.Query;
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
				throw new InputValidationException(ValidationError.GetCountriesNeedsStoreOrQuery.getCode(),
						ValidationError.GetCountriesNeedsStoreOrQuery.getMessage("GetCountriesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			boolean isQuery = false;
			boolean isStore = false;

			try {
				input.store = ValidationHelper.validateStore(input.store, "input");
				isStore = true;
			} catch (InputValidationException ex) {}

			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {}

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
					query = query.filter("a2Code >=", input.query).filter("a2Code <", input.query + "\uFFFD");
					countries = query.list();

					if (countries.size() < input.pager.count.intValue()) {
						query = ofy().load().type(Country.class).offset(input.pager.start.intValue()).limit(input.pager.count.intValue() - countries.size())
								.filter("name >=", input.query).filter("name <", input.query + "\uFFFD");

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
			} catch (InputValidationException ex) {}

			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {}

			Query<Store> query = ofy().load().type(Store.class).offset(input.pager.start.intValue()).limit(input.pager.count.intValue());
			List<Store> stores = null;

			if (isCountry) {
				query = query.filter("a3Code in", input.country.stores);
				stores = query.list();
			} else if (isQuery) {
				// we do not modify the query with a filter if the query is *
				if (input.query.equals("*")) {
					stores = query.list();
				} else {
					query = query.filter("a3Code >=", input.query).filter("a3Code <", input.query + "\uFFFD");
					stores = query.list();

					if (stores.size() < input.pager.count.intValue()) {
						query = ofy().load().type(Store.class).offset(input.pager.start.intValue()).limit(input.pager.count.intValue() - stores.size())
								.filter("name >=", input.query).filter("name <", input.query + "\uFFFD");

						// We have no control over whether stores is a read-only list so we just copy it
						List<Store> merged = new ArrayList<Store>(query.list());
						merged.addAll(stores);
						stores = merged;
					}
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

			// TODO: validate the list type by store
			if (input.listType == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.on == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(), ValidationError.InvalidValueNull.getMessage("Date: input.on"));

			input.store = ValidationHelper.validateStore(input.store, "input");

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

			// TODO: validate the sort direction
			if (input.pager.sortBy == null || input.pager.sortBy.length() == 0) {
				order += "position";
			} else {
				order += input.pager.sortBy;
			}

			Query<Rank> query = ofy().load().type(Rank.class).filter("date >=", after).filter("date <", before).filter("country", input.country.a2Code)
					.filter("type", input.listType).filter("source", input.store.a3Code).offset(input.pager.start.intValue())
					.limit(input.pager.count.intValue()).order("date").order(order);

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
					.filter("type", input.type).filter("country", input.country.a2Code).list();

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getItemRanks");
		return output;
	}
}