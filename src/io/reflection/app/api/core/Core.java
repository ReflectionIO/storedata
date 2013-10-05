//  
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.input.ValidationError;
import io.reflection.app.input.ValidationHelper;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Rank;
import io.reflection.app.shared.datatypes.Store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "a3code";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

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
			} else throw new InputValidationException(ValidationError.GetCountriesNeedsStoreOrQuery.getCode(),
					ValidationError.GetCountriesNeedsStoreOrQuery.getMessage("input"));

			if (countries != null) {
				output.countries = countries;
				output.pager = input.pager;
				updatePager(output.pager, output.countries);
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

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "a3code";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

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
			} else throw new InputValidationException(ValidationError.GetStoresNeedsCountryOrQuery.getCode(),
					ValidationError.GetStoresNeedsCountryOrQuery.getMessage("input"));

			if (stores != null) {
				output.stores = stores;
				output.pager = input.pager;
				updatePager(output.pager, output.stores);
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

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			if (input.listType == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.on == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(), ValidationError.InvalidValueNull.getMessage("Date: input.on"));

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.store == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("Store: input.store"));

			input.listType = ValidationHelper.validateListType(input.listType, input.store);

			Calendar cal = Calendar.getInstance();
			cal.setTime(input.on);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			Date before = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Date after = cal.getTime();

			List<Rank> ranks = RankServiceProvider.provide().getRanks(input.country, input.store, input.listType, after, before, input.pager);

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
				output.items = ItemServiceProvider.provide().getExternalIdItemBatch(itemIds);

				output.pager = input.pager;
				updatePager(output.pager, output.ranks,
						input.pager.totalCount == null ? RankServiceProvider.provide().getRanksCount(input.country, input.store, input.listType, after, before)
								: null);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getTopItems");
		return output;
	}

	public GetAllTopItemsResponse getAllTopItems(GetAllTopItemsRequest input) {
		LOG.finer("Entering getAllTopItems");
		GetAllTopItemsResponse output = new GetAllTopItemsResponse();

		try {
			if (input == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("GetAllTopItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			if (input.listType == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.on == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(), ValidationError.InvalidValueNull.getMessage("Date: input.on"));

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.store == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("Store: input.store"));

			Calendar cal = Calendar.getInstance();
			cal.setTime(input.on);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			Date before = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Date after = cal.getTime();

			List<String> itemIds = new ArrayList<String>();
			final Map<String, Rank> lookup = new HashMap<String, Rank>();

			String freeListType;
			String code = null;

			List<Rank> ranks = RankServiceProvider.provide().getRanks(input.country, input.store, freeListType = getFreeListName(input.store, input.listType),
					after, before, input.pager);

			if (ranks != null && ranks.size() != 0) {
				for (Rank rank : ranks) {
					if (!lookup.containsKey(rank.itemId)) {
						itemIds.add(rank.itemId);
						lookup.put(rank.itemId, rank);
					}
				}

				if (code == null) {
					code = ranks.get(0).code;
				}

				output.freeRanks = ranks;
			}

			if (code == null) {
				ranks = RankServiceProvider.provide().getRanks(input.country, input.store, getPaidListName(input.store, input.listType), after, before,
						input.pager);
			} else {
				ranks = RankServiceProvider.provide().getGatherCodeRanks(input.country, input.store, getPaidListName(input.store, input.listType), code,
						input.pager);
			}

			if (ranks != null && ranks.size() != 0) {
				for (Rank rank : ranks) {
					if (!lookup.containsKey(rank.itemId)) {
						itemIds.add(rank.itemId);
						lookup.put(rank.itemId, rank);
					}
				}

				if (code == null) {
					code = ranks.get(0).code;
				}

				output.paidRanks = ranks;
			}

			if (code == null) {
				ranks = RankServiceProvider.provide().getRanks(input.country, input.store, getGrossingListName(input.store, input.listType), after, before,
						input.pager);
			} else {
				ranks = RankServiceProvider.provide().getGatherCodeRanks(input.country, input.store, getGrossingListName(input.store, input.listType), code,
						input.pager);
			}

			if (ranks != null && ranks.size() != 0) {
				for (Rank rank : ranks) {
					if (!lookup.containsKey(rank.itemId)) {
						itemIds.add(rank.itemId);
						lookup.put(rank.itemId, rank);
					}
				}

				if (code == null) {
					code = ranks.get(0).code;
				}

				output.grossingRanks = ranks;
			}

			output.items = ItemServiceProvider.provide().getExternalIdItemBatch(itemIds);

			output.pager = input.pager;
			if (input.pager.totalCount == null) {
				if (code == null) {
					input.pager.totalCount = RankServiceProvider.provide().getRanksCount(input.country, input.store, freeListType, after, before);
				} else {
					input.pager.totalCount = RankServiceProvider.provide().getGatherCodeRanksCount(input.country, input.store, freeListType, code);
				}
			}

			updatePager(output.pager, output.freeRanks, input.pager.totalCount);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getAllTopItems");
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

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.item = ValidationHelper.validateItem(input.item, "input");

			input.country = ValidationHelper.validateCountry(input.country, "input");

			if (input.listType == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("String: input.listType"));

			Calendar cal = Calendar.getInstance();

			if (input.before == null) input.before = cal.getTime();

			if (input.after == null) {
				cal.setTime(input.before);
				cal.add(Calendar.DAY_OF_YEAR, -30);
				input.after = cal.getTime();
			}

			Store store = StoreServiceProvider.provide().getA3CodeStore(input.item.source);
			input.listType = ValidationHelper.validateListType(input.listType, store);

			long diff = input.before.getTime() - input.after.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ValidationError.DateRangeOutOfBounds.getCode(),
						ValidationError.DateRangeOutOfBounds.getMessage("0-60 days: input.before - input.after"));

			output.ranks = RankServiceProvider.provide().getItemRanks(input.country, store, input.listType, input.item, input.after, input.before, input.pager);

			if (input.pager.start.intValue() == 0) {
				output.item = input.item;
			}

			output.pager = input.pager;
			updatePager(output.pager, output.ranks);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getItemRanks");
		return output;
	}

	/**
	 * @param pager
	 * @param ranks
	 */
	private void updatePager(Pager pager, List<?> list) {
		updatePager(pager, list, null);

	}

	private void updatePager(Pager pager, List<?> list, Long total) {
		pager.start = Long.valueOf(pager.start.longValue() + list.size());

		if (total != null) {
			pager.totalCount = total;
		}
	}

	private String getFreeListName(Store store, String type) {
		String listName = null;

		if ("ios".equalsIgnoreCase(store.a3Code)) {
			if ("ipad".equalsIgnoreCase(type)) {
				listName = CollectorIOS.TOP_FREE_IPAD_APPS;
			} else {
				listName = CollectorIOS.TOP_FREE_APPS;
			}
		}

		return listName;
	}

	private String getPaidListName(Store store, String type) {
		String listName = null;

		if ("ios".equalsIgnoreCase(store.a3Code)) {
			if ("ipad".equalsIgnoreCase(type)) {
				listName = CollectorIOS.TOP_PAID_IPAD_APPS;
			} else {
				listName = CollectorIOS.TOP_PAID_APPS;
			}
		}

		return listName;

	}

	private String getGrossingListName(Store store, String type) {
		String listName = null;

		if ("ios".equalsIgnoreCase(store.a3Code)) {
			if ("ipad".equalsIgnoreCase(type)) {
				listName = CollectorIOS.TOP_GROSSING_IPAD_APPS;
			} else {
				listName = CollectorIOS.TOP_GROSSING_APPS;
			}
		}

		return listName;
	}
}