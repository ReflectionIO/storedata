//
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

import static io.reflection.app.service.sale.ISaleService.*;
import static io.reflection.app.shared.util.PagerHelper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.gson.json.service.shared.StatusType;

import io.reflection.app.accountdatacollectors.ITunesConnectDownloadHelper;
import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.CheckUsernameRequest;
import io.reflection.app.api.core.shared.call.CheckUsernameResponse;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.DeleteNotificationsRequest;
import io.reflection.app.api.core.shared.call.DeleteNotificationsResponse;
import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetCategoriesRequest;
import io.reflection.app.api.core.shared.call.GetCategoriesResponse;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse;
import io.reflection.app.api.core.shared.call.GetItemSalesRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.GetNotificationsRequest;
import io.reflection.app.api.core.shared.call.GetNotificationsResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.GetSalesRequest;
import io.reflection.app.api.core.shared.call.GetSalesResponse;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.GetTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;
import io.reflection.app.api.core.shared.call.IsAuthorisedRequest;
import io.reflection.app.api.core.shared.call.IsAuthorisedResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.LogoutRequest;
import io.reflection.app.api.core.shared.call.LogoutResponse;
import io.reflection.app.api.core.shared.call.RegisterInterestBusinessRequest;
import io.reflection.app.api.core.shared.call.RegisterInterestBusinessResponse;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.UpdateNotificationsRequest;
import io.reflection.app.api.core.shared.call.UpdateNotificationsResponse;
import io.reflection.app.api.core.shared.call.UpgradeAccountRequest;
import io.reflection.app.api.core.shared.call.UpgradeAccountResponse;
import io.reflection.app.api.exception.AuthenticationException;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.lookupitem.LookupItemService;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.service.session.ISessionService;
import io.reflection.app.service.session.SessionServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.service.user.IUserService;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

public final class Core extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Core.class.getName());

	public GetCountriesResponse getCountries(GetCountriesRequest input) {
		LOG.finer("Entering getCountries");
		GetCountriesResponse output = new GetCountriesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetCountriesRequest: input"));

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
				throw new InputValidationException(ApiError.GetCountriesNeedsStoreOrQuery.getCode(),
						ApiError.GetCountriesNeedsStoreOrQuery.getMessage("input"));

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
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetStoresRequest: input"));

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
				throw new InputValidationException(ApiError.GetStoresNeedsCountryOrQuery.getCode(),
						ApiError.GetStoresNeedsCountryOrQuery.getMessage("input"));

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

	// Nothing on the client app call this.
	public GetTopItemsResponse getTopItems(GetTopItemsRequest input) {
		LOG.finer("Entering getTopItems");
		GetTopItemsResponse output = new GetTopItemsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetTopItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.on == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Date: input.on"));

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.category == null) {
				input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
			} else {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");

				if (!input.store.a3Code.equals(input.category.store))
					throw new InputValidationException(ApiError.CategoryStoreMismatch.getCode(), ApiError.CategoryStoreMismatch.getMessage("input.category"));
			}

			input.listType = ValidationHelper.validateListType(input.listType, input.store);

			DateTime date = new DateTime(input.on.getTime(), DateTimeZone.UTC);
			int secondOfMinute = date.getSecondOfMinute();
			int millisOfSeconds = date.getMillisOfSecond();

			// Date end;
			Date start;

			boolean isPastDate = (secondOfMinute == 0) && (millisOfSeconds == 0);

			if (isPastDate) { // a date in the past
				// end = date.plusHours(24).toDate();
				start = date.toDate();
			} else { // today
				// end = date.minusHours(12).toDate();
				start = date.minusHours(24).toDate();
			}

			List<Rank> ranks = RankServiceProvider.provide().getRanks(input.country, input.category, input.listType, input.on);

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
				output.items = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);

				output.pager = input.pager;
				updatePager(output.pager, output.ranks,
						input.pager.totalCount == null ? RankServiceProvider.provide().getRanksCount(input.country, input.category, input.listType, start)
								: input.pager.totalCount);
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
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetAllTopItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.on == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Date: input.on"));

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.store == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Store: input.store"));

			if (input.category == null) {
				input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
			} else {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");

				if (!input.store.a3Code.equals(input.category.store))
					throw new InputValidationException(ApiError.CategoryStoreMismatch.getCode(), ApiError.CategoryStoreMismatch.getMessage("input.category"));
			}

			input.pager = ValidationHelper.validatePager(input.pager, "input");
			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

			boolean isAdmin = false;
			boolean isPremium = false;
			boolean isStandardDeveloper = false;
			boolean canSeePredictions = (DateTimeComparator.getDateOnlyInstance().compare(input.on, new DateTime().minusDays(FilterHelper.DEFAULT_LEADERBOARD_LAG_DAYS)) == 0);
			boolean isLoggedIn = false;

			if (input.session != null) {
				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

				isLoggedIn = true;

				List<Role> roles = UserServiceProvider.provide().getUserRoles(input.session.user);
				RoleServiceProvider.provide().inflateRoles(roles);
				List<Permission> permissions;
				for (Role role : roles) {
					if (DataTypeHelper.ROLE_ADMIN_CODE.equals(role.code)) {
						isAdmin = true;
						canSeePredictions = true;
						break;
					} else if (DataTypeHelper.ROLE_PREMIUM_CODE.equals(role.code)) {
						isPremium = true;
					} else if (DataTypeHelper.ROLE_DEVELOPER_CODE.equals(role.code)) {
						isStandardDeveloper = true;
					}

					permissions = RoleServiceProvider.provide().getPermissions(role); // Role permissions
					permissions.addAll(UserServiceProvider.provide().getPermissions(input.session.user)); // Add permissions of the user
					PermissionServiceProvider.provide().inflatePermissions(permissions);
					for (Permission permission : permissions) {
						if (isPremium && DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE.equals(permission.code)) {
							canSeePredictions = true;
							break;
						}
					}

				}
			}

			// if (!isLoggedIn) { // Force date to 2 days ago if is public call
			// input.on = new DateTime(DateTimeZone.UTC).minusDays(3).toDate();
			// }

			if (!isAdmin) {
				if (!(Arrays.asList("fr", "de", "gb", "it").contains(input.country.a2Code))) {
					input.country = new Country();
					input.country.a2Code = "gb";
				}
				input.store = new Store();
				input.store.a3Code = DataTypeHelper.IOS_STORE_A3;
				input.category = new Category();
				input.category.id = 15L;
			}
			input.country = ValidationHelper.validateCountry(input.country, "input");

			Collector collector = CollectorFactory.getCollectorForStore(input.store.a3Code);

			Set<String> itemIds = new HashSet<String>();
			List<Rank> ranks = new ArrayList<Rank>();

			if (input.listType.contains("all")) {
				List<String> listTypes = ApiHelper.getAllListTypes(input.store, input.listType);
				for (String listType : listTypes) {
					// get all the ranks for the list type (we are using an infinite pager with no sorting to allow us to generate a deletion key during
					// prediction)
					ranks = RankServiceProvider.provide().getRanks(input.country, input.category, listType, input.on);

					for (Rank rank : ranks) {
						itemIds.add(rank.itemId);
						int ranking = (collector.isGrossing(listType) ? rank.grossingPosition.intValue() : rank.position.intValue());
						if (!canSeePredictions || (!isLoggedIn && ranking > 10)) {
							rank.downloads = null;
							rank.revenue = null;
						}
					}

					if (collector.isFree(listType)) {
						output.freeRanks = ranks;
					} else if (collector.isPaid(listType)) {
						output.paidRanks = ranks;
						if (!isAdmin && !input.country.a2Code.equals("gb")) {// Remove paid ranks if not UK
							for (Rank paidRank : output.paidRanks) {
								paidRank.downloads = null;
								paidRank.revenue = null;
							}
						}
					} else if (collector.isGrossing(listType)) {
						output.grossingRanks = ranks;
					}
				}
			} else if (collector.isFree(input.listType)) {
				ranks = RankServiceProvider.provide().getRanks(input.country, input.category, input.listType, input.on);
				for (Rank rank : ranks) {
					itemIds.add(rank.itemId);
					if (!canSeePredictions || (!isLoggedIn && rank.position.intValue() > 10)) {
						rank.downloads = null;
						rank.revenue = null;
					}
				}
				output.freeRanks = ranks;

			} else if (collector.isPaid(input.listType)) {
				ranks = RankServiceProvider.provide().getRanks(input.country, input.category, input.listType, input.on);
				for (Rank rank : ranks) {
					itemIds.add(rank.itemId);
					if (!canSeePredictions || (!isLoggedIn && rank.position.intValue() > 10)) {
						rank.downloads = null;
						rank.revenue = null;
					}
					if (!isAdmin && !input.country.a2Code.equals("gb")) {// Remove paid ranks if not UK
						rank.downloads = null;
						rank.revenue = null;
					}
				}
				output.paidRanks = ranks;

			} else if (collector.isGrossing(input.listType)) {
				ranks = RankServiceProvider.provide().getRanks(input.country, input.category, input.listType, input.on);
				for (Rank rank : ranks) {
					itemIds.add(rank.itemId);
					if (!canSeePredictions || (!isLoggedIn && rank.grossingPosition.intValue() > 10)) {
						rank.downloads = null;
						rank.revenue = null;
					}
				}
				output.grossingRanks = ranks;
			}

			output.items = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);

			output.pager = input.pager;

			// if (input.pager.totalCount == null && input.pager.boundless != Boolean.TRUE) {
			input.pager.totalCount = 200L;
			// }

			// updatePager(output.pager, output.freeRanks, input.pager.totalCount);
			// }

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
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetItemRanksRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			// boolean isLoggedIn = false;
			boolean isAdmin = false;

			if (input.session != null) {
				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");
				// isLoggedIn = true;
				isAdmin = UserServiceProvider.provide().hasRole(input.session.user, DataTypeHelper.adminRole());
			}

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
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			Store store = StoreServiceProvider.provide().getA3CodeStore(input.item.source);

			// right now category
			if (input.category == null) {
				input.category = CategoryServiceProvider.provide().getAllCategory(store);
			} else {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");

				if (!input.item.source.equals(input.category.store))
					throw new InputValidationException(ApiError.CategoryStoreMismatch.getCode(), ApiError.CategoryStoreMismatch.getMessage("input.category"));
			}

			if (input.end == null) {
				input.end = DateTime.now(DateTimeZone.UTC).toDate();
			} else {
				input.end = (new DateTime(input.end.getTime(), DateTimeZone.UTC)).toDate();
			}

			if (input.start == null) {
				input.start = (new DateTime(input.end.getTime(), DateTimeZone.UTC)).minusDays(30).toDate();
			} else {
				input.start = (new DateTime(input.start.getTime(), DateTimeZone.UTC)).toDate();
			}

			input.listType = ValidationHelper.validateListType(input.listType, store);

			// FormType form = ModellerFactory.getModellerForStore(store.a3Code).getForm(input.listType);
			//
			// ItemRankArchiver archiver = ArchiverFactory.getItemRankArchiver();
			// long[] slices = SliceHelper.offsets(input.start, input.end);
			//
			// String key;
			// List<Rank> ranks;
			// for (long slice : slices) {
			// key = archiver.createKey(slice, input.item, form, store, input.country, input.category);
			//
			// ranks = archiver.getRanks(key);
			//
			// if (ranks != null) {
			// if (output.ranks == null) {
			// output.ranks = new ArrayList<Rank>();
			// }
			//
			// output.ranks.addAll(ranks);
			// }
			// }

			if (input.pager.start.intValue() == 0) {
				output.item = input.item;
			}

			output.ranks = RankServiceProvider.provide().getItemRanks(input.country, input.category, input.listType, input.item, input.start, input.end,
					input.pager);

			if (!isAdmin) {
				for (Rank r : output.ranks) {
					r.downloads = null;
					r.revenue = null;
				}
			}

			// // Identify out of leaderboard dates
			// List<Date> itemRanksDates = new ArrayList<Date>(); // Dates retrieved from the query
			// for (Rank r : output.ranks) {
			// itemRanksDates.add(r.date);
			// }
			// Calendar cal = Calendar.getInstance();
			// cal.setTime(input.start);
			// cal.set(Calendar.HOUR_OF_DAY, 0);
			// cal.set(Calendar.MINUTE, 0);
			// cal.set(Calendar.SECOND, 0);
			// cal.set(Calendar.MILLISECOND, 0);
			// List<Date> missingDates = new ArrayList<Date>(); // Dates missed from the query
			// while (DateTimeComparator.getDateOnlyInstance().compare(cal.getTime(), input.end) <= 0) {
			// if (!itemRanksDates.contains(cal.getTime())) { // No data retrieved in this date
			// missingDates.add(cal.getTime());
			// }
			// cal.add(Calendar.DAY_OF_MONTH, 1);
			// }
			// if (missingDates.size() > 0) { // Identify which dates are missing because the App was out of the rank that day
			// output.outOfLeaderboardDates = RankServiceProvider.provide().getOutOfLeaderboardDates(missingDates, input.country, input.category,
			// input.listType);
			// }

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

	public RegisterUserResponse registerUser(RegisterUserRequest input) {
		LOG.finer("Entering registerUser");
		RegisterUserResponse output = new RegisterUserResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("RegisterUserRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			boolean isAction = input.actionCode != null;

			if (input.accessCode.equalsIgnoreCase("b72b4e32-1062-4cc7-bc6b-52498ee10f09")) { // Alpha user
				input.user = ValidationHelper.validateUserWithoutPassword(input.user, "input.user");
			} else {
				if (isAction) { // Update password
					input.user.password = ValidationHelper.validatePassword(input.user.password, "input.user.password");
				} else { // Registration
					input.user = ValidationHelper.validateRegisteringUser(input.user, "input.user");
				}
			}

			User addedUser = null;

			if (isAction) {
				addedUser = UserServiceProvider.provide().getActionCodeUser(input.actionCode);

				if (addedUser == null)
					throw new InputValidationException(ApiError.InvalidActionCode.getCode(), ApiError.InvalidActionCode.getMessage("input.actionCode"));

				UserServiceProvider.provide().updateUserPassword(addedUser, input.user.password, Boolean.FALSE);

				LOG.info(String.format("Completed user registeration for user [%s %s] and email [%s] and action code [%s]", addedUser.forename,
						addedUser.surname, addedUser.username, input.actionCode));
			} else {
				// User that has registered the business interest OR requested to be part of the private beta but never completed the sign up process
				boolean notRegisteredUser = false;
				User u = UserServiceProvider.provide().getUsernameUser(input.user.username);
				if (u != null && u.lastLoggedIn == null) {
					notRegisteredUser = true;
					input.user.id = u.id;
					// Update previously inserted user
					addedUser = UserServiceProvider.provide().updateUser(input.user);
					UserServiceProvider.provide().updateUserPassword(addedUser, input.user.password, Boolean.FALSE);
				}

				if (!notRegisteredUser) {
					addedUser = UserServiceProvider.provide().addUser(input.user);
					LOG.info(String.format("Added user with name [%s %s] and email [%s],", addedUser.forename, addedUser.surname, addedUser.username));
				}

				if (addedUser != null) {
					output.registeredUser = addedUser;
					// Add standard developer role
					Role devRole = ValidationHelper.validateRole(DataTypeHelper.createRole(DataTypeHelper.ROLE_DEVELOPER_CODE), "input.role");
					UserServiceProvider.provide().assignRole(input.user, devRole);
					// Notify the registered user
					Map<String, Object> values = new HashMap<String, Object>();
					values.put("user", addedUser);
					Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.REGISTERED_NOW_LINK_EVENT_CODE);
					if (event != null) {
						String body = NotificationHelper.inflate(values, event.longBody);
						Notification notification = (new Notification()).from("hello@reflection.io").user(addedUser).event(event).body(body)
								.subject(event.subject);
						Notification added = NotificationServiceProvider.provide().addNotification(notification);
						if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
							notification.type = NotificationTypeType.NotificationTypeTypeInternal;
							NotificationServiceProvider.provide().addNotification(notification);
						}
					}
				}

			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting registerUser");
		return output;
	}

	public LoginResponse login(LoginRequest input) {
		LOG.finer("Entering login");
		LoginResponse output = new LoginResponse();

		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("LoginRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			boolean foundToken = false;

			if (input.session != null && input.session.token != null && !input.session.token.equals("")) {
				foundToken = true;
			}

			if (!foundToken) {
				if (input.username == null)
					throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.username"));

				if (input.password == null)
					throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.password"));

				IUserService userService = UserServiceProvider.provide();

				User user = userService.getLoginUser(input.username, input.password);

				if (user == null) throw new AuthenticationException(input.username);

				ISessionService sessionService = SessionServiceProvider.provide();

				if (LOG.isLoggable(Level.FINER)) {
					LOG.finer("Getting user session");
				}

				output.session = sessionService.getUserSession(user);

				if (output.session == null) {
					if (LOG.isLoggable(Level.FINER)) {
						LOG.finer("Existing session not found, creating new session");
					}

					output.session = sessionService.createUserSession(user, input.longTerm);

					if (output.session != null) {
						output.session.user = user;
					} else
						throw new Exception("Unexpected blank session after creating user session.");
				} else {
					output.session = SessionServiceProvider.provide().extendSession(output.session, ISessionService.SESSION_SHORT_DURATION);
					output.session.user = user;
				}
			} else {
				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");
				output.session.user = UserServiceProvider.provide().getUser(input.session.user.id);

			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting login");
		return output;
	}

	public LogoutResponse logout(LogoutRequest input) {
		LOG.finer("Entering logout");
		LogoutResponse output = new LogoutResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("LogoutRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			SessionServiceProvider.provide().deleteSession(input.session);

			output.session = null;

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting logout");
		return output;
	}

	public ChangePasswordResponse changePassword(ChangePasswordRequest input) {
		LOG.finer("Entering changePassword");
		ChangePasswordResponse output = new ChangePasswordResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ChangePasswordRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			boolean isReset = (input.resetCode != null);

			if (!isReset) {
				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");
				input.password = ValidationHelper.validatePassword(input.password, "input.password");
			}

			User user = null;

			if (isReset) {
				user = UserServiceProvider.provide().getActionCodeUser(input.resetCode);

				if (user == null)
					throw new InputValidationException(ApiError.InvalidActionCode.getCode(), ApiError.InvalidActionCode.getMessage("input.resetCode"));
			} else {
				User sessionUser = UserServiceProvider.provide().getUser(input.session.user.id);
				user = UserServiceProvider.provide().getLoginUser(sessionUser.username, input.password);

				if (user == null)
					throw new InputValidationException(ApiError.IncorrectPasswordForChange.getCode(),
							ApiError.IncorrectPasswordForChange.getMessage("input.password"));
			}

			input.newPassword = ValidationHelper.validatePassword(input.newPassword, "input.newPassword");

			if (!isReset && input.newPassword.equals(input.password))
				throw new InputValidationException(ApiError.InvalidPasswordSameAsCurrent.getCode(),
						ApiError.InvalidPasswordSameAsCurrent.getMessage("input.newPassword"));

			UserServiceProvider.provide().updateUserPassword(user, input.newPassword);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting changePassword");
		return output;
	}

	public ChangeUserDetailsResponse changeUserDetails(ChangeUserDetailsRequest input) {
		LOG.finer("Entering changeUserDetails");
		ChangeUserDetailsResponse output = new ChangeUserDetailsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ChangeUserDetailsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (!UserServiceProvider.provide().hasRole(input.session.user, DataTypeHelper.adminRole())) {
				if (input.user.id.longValue() != input.session.user.id.longValue()) throw new ServiceException(-1, "User and session user do not match");
			}

			ValidationHelper.validateExistingUser(input.user, "input.user");

			User user = UserServiceProvider.provide().updateUser(input.user);

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.fine(String.format("User with user id [%d] details updated", user.id));
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting changeUserDetails");
		return output;
	}

	public UpdateLinkedAccountResponse updateLinkedAccount(UpdateLinkedAccountRequest input) {
		LOG.finer("Entering updateLinkedAccount");
		UpdateLinkedAccountResponse output = new UpdateLinkedAccountResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("UpdateLinkedAccountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			String tempPassword = ValidationHelper.validateStringLength(input.linkedAccount.password, "input.linkedAccount.password", 2, 1000);

			if (tempPassword == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.linkedAccount.password"));

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			// DataAccountCollectorFactory.getCollectorForSource(input.linkedAccount.source.a3Code).validateProperties(input.linkedAccount.properties);

			input.linkedAccount.password = tempPassword;

			input.linkedAccount.source = new DataSource(); // Add iTunes Connect data source
			input.linkedAccount.source.a3Code = "itc";

			// Verify linked account with Apple
			try {
				AppleReporterHelper.getVendors(input.linkedAccount.username, input.linkedAccount.password);
			} catch (InputValidationException e) {

				if (e.getCode() != 214 && e.getCode() != 215 && e.getCode() != 216) throw new InputValidationException(ApiError.InvalidDataAccountCredentials.getCode(),
						ApiError.InvalidDataAccountCredentials.getMessage(input.linkedAccount.username));
			}

			DataAccount linkedAccount = DataAccountServiceProvider.provide().updateDataAccount(input.linkedAccount, true);

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.fine(String.format("Linked account with id [%d] details updated", linkedAccount.id.longValue()));
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting updateLinkedAccount");
		return output;
	}

	public DeleteLinkedAccountResponse deleteLinkedAccount(DeleteLinkedAccountRequest input) {
		LOG.finer("Entering deleteLinkedAccount");
		DeleteLinkedAccountResponse output = new DeleteLinkedAccountResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("DeleteLinkedAccountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			boolean hasDataAccount = UserServiceProvider.provide().hasDataAccount(input.session.user, input.linkedAccount).booleanValue();

			boolean isAdmin = UserServiceProvider.provide().hasRole(input.session.user, DataTypeHelper.adminRole());

			if (hasDataAccount || isAdmin) {
				User linkedAccountOwner = UserServiceProvider.provide().getDataAccountOwner(input.linkedAccount);
				// If the owner, remove all other users from this linked account (except test linked account)
				if (linkedAccountOwner != null && linkedAccountOwner.id.longValue() == input.session.user.id.longValue()
						&& input.linkedAccount.id.longValue() != 357) {
					UserServiceProvider.provide().deleteAllUsersDataAccount(input.linkedAccount);

					// Set linked account as inactive
					input.linkedAccount.active = DataTypeHelper.INACTIVE_VALUE;
					DataAccountServiceProvider.provide().updateDataAccount(input.linkedAccount, false);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.finer(String.format("Linked account with id [%d] deleted by owner [%d]", input.linkedAccount.id.longValue(),
								input.session.user.id.longValue()));
					}
				} else {
					UserServiceProvider.provide().deleteUserDataAccount(input.session.user, input.linkedAccount);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.finer(String.format("Linked account with id [%d] removed from user account [%d]", input.linkedAccount.id.longValue(),
								input.session.user.id.longValue()));
					}
				}
				// Revoke HLA permission
				if (!UserServiceProvider.provide().hasDataAccounts(input.session.user) && !isAdmin) {
					Permission hlaPermission = PermissionServiceProvider.provide().getCodePermission(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE);
					UserServiceProvider.provide().revokePermission(input.session.user, hlaPermission);
				}

			} else
				throw new InputValidationException(ApiError.DataAccountUserMissmatch.getCode(), ApiError.DataAccountUserMissmatch.getMessage());

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteLinkedAccount");
		return output;
	}

	public CheckUsernameResponse checkUsername(CheckUsernameRequest input) {
		LOG.finer("Entering checkUsername");
		CheckUsernameResponse output = new CheckUsernameResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("CheckUsernameRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			IUserService userService = UserServiceProvider.provide();

			Long userCount = userService.searchUsersCount(input.username);

			if (userCount.longValue() > 0) {
				Pager p = new Pager();
				p.start = Long.valueOf(0);
				p.count = userCount;

				List<User> users = userService.searchUsers(input.username, p);

				for (User user : users) {
					if (user.username.equalsIgnoreCase(input.username)) {
						output.usernameInUse = Boolean.TRUE;
						break;
					}
				}
			}

			if (output.usernameInUse != Boolean.TRUE) {
				output.usernameInUse = Boolean.FALSE;
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting checkUsername");
		return output;
	}

	public GetRolesAndPermissionsResponse getRolesAndPermissions(GetRolesAndPermissionsRequest input) {
		LOG.finer("Entering getRolesAndPermissions");
		GetRolesAndPermissionsResponse output = new GetRolesAndPermissionsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("GetRolesAndPermissionsResponse: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (input.permissionsOnly != Boolean.TRUE) {
				Date userRoleCreated = null;
				output.roles = UserServiceProvider.provide().getUserRoles(input.session.user);

				if (output.roles == null || output.roles.isEmpty()) {
					List<Role> expiredRoles = UserServiceProvider.provide().getUserRoles(input.session.user, true);
					// Premium role just expired
					if (expiredRoles != null && !expiredRoles.isEmpty() && expiredRoles.get(0).id == 3) { // TODO use code instead
						UserServiceProvider.provide().assignRole(input.session.user,
								RoleServiceProvider.provide().getCodeRole(DataTypeHelper.ROLE_DEVELOPER_CODE));
						// TODO Renew?? Check if already used the trial? (might be based on a date, e.g. if dev userrole created < date, already used the trial)
					} else {
						UserServiceProvider.provide().assignRole(input.session.user,
								RoleServiceProvider.provide().getCodeRole(DataTypeHelper.ROLE_DEVELOPER_CODE));
					}
					output.roles = UserServiceProvider.provide().getUserRoles(input.session.user);
				}
				userRoleCreated = output.roles.get(0).created;

				RoleServiceProvider.provide().inflateRoles(output.roles); // Convert from UserRole to Role
				output.roles.get(0).created = userRoleCreated;

				for (Role role : output.roles) {
					role.permissions = RoleServiceProvider.provide().getPermissions(role);

					if (role.permissions != null) {
						PermissionServiceProvider.provide().inflatePermissions(role.permissions);
					}
				}

				// Notify the frontend if the last role expired was premium
				// TODO if renewed throw new ServiceException(ApiError.PremiumRoleExpired.getCode(), ApiError.PremiumRoleExpired.getMessage());
				if (userRoleCreated != null) {
					output.daysSinceRoleAssigned = Days.daysBetween(new DateTime(userRoleCreated, DateTimeZone.UTC), DateTime.now(DateTimeZone.UTC)).getDays();
				}
			}

			if (input.rolesOnly != Boolean.TRUE) {
				output.permissions = UserServiceProvider.provide().getPermissions(input.session.user);

				if (output.permissions != null) {
					PermissionServiceProvider.provide().inflatePermissions(output.permissions);
				}
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getRolesAndPermissions");
		return output;
	}

	public GetLinkedAccountsResponse getLinkedAccounts(GetLinkedAccountsRequest input) {
		LOG.finer("Entering getLinkedAccounts");
		GetLinkedAccountsResponse output = new GetLinkedAccountsResponse();

		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetLinkedAccountsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "created";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			output.linkedAccounts = UserServiceProvider.provide().getDataAccounts(input.session.user, input.pager);

			// Keep only active linked accounts
			List<DataAccount> inactiveLinkedAccounts = new ArrayList<DataAccount>();
			for (DataAccount da : output.linkedAccounts) {
				if (DataTypeHelper.INACTIVE_VALUE.equals(da.active)) {
					inactiveLinkedAccounts.add(da);
				}
			}
			output.linkedAccounts.removeAll(inactiveLinkedAccounts);

			Map<Long, DataSource> dataSources = new HashMap<Long, DataSource>();
			for (DataAccount d : output.linkedAccounts) {
				if (d.source.id != null) {
					dataSources.put(d.source.id, null);
				}
			}

			if (dataSources != null && dataSources.size() > 0) {
				List<DataSource> lookupDataSources = DataSourceServiceProvider.provide().getDataSourceBatch(dataSources.keySet());
				output.dataSources = lookupDataSources;
			}

			output.pager = input.pager;
			updatePager(output.pager, output.linkedAccounts, input.pager.totalCount == null ? (long) output.linkedAccounts.size() : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getLinkedAccounts");
		return output;
	}

	public GetLinkedAccountItemsResponse getLinkedAccountItems(GetLinkedAccountItemsRequest input) {
		LOG.finer("Entering getLinkedAccountItems");
		GetLinkedAccountItemsResponse output = new GetLinkedAccountItemsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("GetLinkedAccountItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "created";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.store == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Store: input.store"));

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			Modeller modeller = ModellerFactory.getModellerForStore(input.store.a3Code);
			FormType form = modeller.getForm(input.listType);

			output.pager = input.pager;

			List<String> freeOrPaidApps = new ArrayList<String>();

			freeOrPaidApps.add(FREE_OR_PAID_APP_UNIVERSAL_IOS);
			if (form == FormType.FormTypeOther) {
				freeOrPaidApps.add(FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS);
			} else if (form == FormType.FormTypeTablet) {
				freeOrPaidApps.add(FREE_OR_PAID_APP_IPAD_IOS);
			}

			output.items = LookupItemService.INSTANCE.getDataAccountItems(input.linkedAccount);

			updatePager(output.pager, output.items,
					input.pager.totalCount == null ? SaleServiceProvider.provide().getDataAccountItemsCount(input.linkedAccount, freeOrPaidApps) : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getLinkedAccountItems");
		return output;
	}

	public LinkAccountResponse linkAccount(LinkAccountRequest input) {
		LOG.finer("Entering linkAccount");
		LinkAccountResponse output = new LinkAccountResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("LinkAccountResponse: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			input.source = ValidationHelper.validateDataSource(input.source, "input.source");

			if (input.username == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.username"));

			input.username = ValidationHelper.validateStringLength(input.username, "input.username", 2, 255);

			if (input.password == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.password"));

			input.password = ValidationHelper.validateStringLength(input.password, "input.password", 2, 1000);

			boolean isAdmin = UserServiceProvider.provide().hasRole(input.session.user, DataTypeHelper.adminRole());
			Permission hlaPermission = PermissionServiceProvider.provide().getCodePermission(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE);
			boolean hasHlaPermission = UserServiceProvider.provide().hasPermission(input.session.user, hlaPermission);

			output.linkedAccounts = new ArrayList<DataAccount>();
			List<String> vendors = new ArrayList<String>();

			if ("THETESTACCOUNT".equals(input.username) && "thegrange".equals(input.password)) { // TODO Redesign custom exceptions

				DataAccount testAccount = DataAccountServiceProvider.provide().getDataAccount(357L);
				output.linkedAccounts.add(testAccount);
				UserServiceProvider.provide().addOrRestoreUserDataAccount(input.session.user, testAccount);

			} else { // If not the test linked account, check if is a valid Apple linked account and retrieve the vendors

				boolean accountNumberRequired = false;
				try {
					vendors = AppleReporterHelper.getVendors(input.username, input.password);
				} catch (InputValidationException e) {

					if (e.getCode() == 214 || e.getCode() == 215 || e.getCode() == 216) { // Required/wrong account number (but valid credentials)
						accountNumberRequired = true;
					} else if (e.getCode() == 108) throw new InputValidationException(ApiError.InvalidDataAccountCredentials.getCode(),
							ApiError.InvalidDataAccountCredentials.getMessage(input.username));
					else
						throw e;
				}

				if (accountNumberRequired) {
					Map<String, String> accounts = AppleReporterHelper.getAccounts(input.username, input.password);
					for (String accountNumber : accounts.values()) { // For every account number, get the vendors
						vendors.addAll(AppleReporterHelper.getVendors(input.username, input.password, accountNumber));
					}
				}

				if (vendors.isEmpty()) throw new Exception("Vendor list is empty");

				// Add a data account for every vendor
				for (String v : vendors) {
					DataAccount addedAccount = UserServiceProvider.provide().addDataAccount(input.session.user, input.source, input.username, input.password,
							ITunesConnectDownloadHelper.createProperties(v));
					addedAccount.source = input.source;
					output.linkedAccounts.add(addedAccount);
				}

			}

			if (!output.linkedAccounts.isEmpty()) {
				if (input.session.user == null || input.session.user.forename == null) {
					input.session.user = UserServiceProvider.provide().getUser(input.session.user.id);
				}

				if (!hasHlaPermission && !isAdmin) {
					UserServiceProvider.provide().assignPermission(input.session.user, hlaPermission);
				}

				// If the first linked account, send an email to the user
				if (!hasHlaPermission) {
					Map<String, Object> values = new HashMap<String, Object>();
					values.put("user", input.session.user);
					values.put("dataaccount", output.linkedAccounts.get(0));

					Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.CONFIRMATION_ACCOUNT_LINKED_EVENT_CODE);
					if (event != null) {
						String body = NotificationHelper.inflate(values, event.longBody);
						Notification notification = (new Notification()).from("hello@reflection.io").user(input.session.user).event(event).body(body)
								.subject(event.subject);
						Notification added = NotificationServiceProvider.provide().addNotification(notification);
						if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
							notification.type = NotificationTypeType.NotificationTypeTypeInternal;
							NotificationServiceProvider.provide().addNotification(notification);
						}
					}
				}

				// Inform the admin of the linked account
				User listeningUser = UserServiceProvider.provide().getUsernameUser("chi@reflection.io");

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("listener", listeningUser);
				parameters.put("user", input.session.user);
				parameters.put("account", output.linkedAccounts.get(0));
				parameters.put("source", input.source);

				String body = NotificationHelper
						.inflate(
								parameters,
								"Hi ${listener.forename},\n\nThis is to let you know that the user [${user.id} - ${user.forename} ${user.surname}] has added the data account [${account.id}] for the data source [${source.name}] and the username ${account.username}.\n\nReflection");

				Notification notification = (new Notification()).from("hello@reflection.io").user(listeningUser).body(body)
						.priority(EventPriorityType.EventPriorityTypeHigh).subject("A user's has linked thier account account");
				Notification added = NotificationServiceProvider.provide().addNotification(notification);

				if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					NotificationServiceProvider.provide().addNotification(notification);
				}
			} else
				throw new Exception();

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting linkAccount");
		return output;
	}

	public IsAuthorisedResponse isAuthorised(IsAuthorisedRequest input) {
		LOG.finer("Entering isAuthorised");
		IsAuthorisedResponse output = new IsAuthorisedResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("LinkAccountResponse: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			// input.roles = ValidationHelper.validateRoles(input.roles, "input.roles");

			// input.permissions = ValidationHelper.validatePermissions(input.permissions, "input.permissions");

			List<Role> roles = UserServiceProvider.provide().getUserRoles(input.session.user);

			// check the roles
			if (input.roles != null) {
				for (Role inputRole : input.roles) {
					boolean foundRole = false;

					for (Role userRole : roles) {
						if (userRole.id.longValue() == inputRole.id.longValue()) {
							foundRole = true;
							break;
						}
					}

					if (!foundRole) {
						output.authorised = Boolean.FALSE;
						break;
					}
				}
			}

			if (input.permissions != null && output.authorised != Boolean.FALSE) {
				List<Permission> permissions = new ArrayList<Permission>();

				for (Role role : roles) {
					permissions.addAll(RoleServiceProvider.provide().getPermissions(role));
				}

				permissions.addAll(UserServiceProvider.provide().getPermissions(input.session.user));

				for (Permission inputPermission : input.permissions) {
					boolean foundPermission = false;

					for (Permission userPermission : permissions) {
						if (userPermission.id.longValue() == inputPermission.id.longValue()) {
							foundPermission = true;
							break;
						}
					}

					if (!foundPermission) {
						output.authorised = Boolean.FALSE;
						break;
					}
				}
			}

			if (output.authorised != Boolean.FALSE) {
				output.authorised = Boolean.TRUE;
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting isAuthorised");
		return output;
	}

	public SearchForItemResponse searchForItem(SearchForItemRequest input) {
		LOG.finer("Entering searchForItem");
		SearchForItemResponse output = new SearchForItemResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("SearchForItemRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			input.query = ValidationHelper.validateQuery(input.query, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.items = ItemServiceProvider.provide().searchItems(input.query, input.pager);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting searchForItem");
		return output;
	}

	public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest input) {
		LOG.finer("Entering forgotPassword");
		ForgotPasswordResponse output = new ForgotPasswordResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ForgotPasswordRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			input.username = ValidationHelper.validateEmail(input.username, true, "input.username");

			User user = UserServiceProvider.provide().getUsernameUser(input.username);

			if (user == null) throw new InputValidationException(ApiError.UserNotFound.getCode(), ApiError.UserNotFound.getMessage("input.username"));

			UserServiceProvider.provide().markForReset(user);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting forgotPassword");
		return output;
	}

	public GetUserDetailsResponse getUserDetails(GetUserDetailsRequest input) {
		LOG.finer("Entering getUserDetails");
		GetUserDetailsResponse output = new GetUserDetailsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetUserDetailsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			boolean isAction = input.actionCode != null;

			if (!isAction) {
				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

				if (input.userId == null)
					throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Long: input.userId"));
			}

			if (isAction) {
				output.user = UserServiceProvider.provide().getActionCodeUser(input.actionCode);

				if (output.user == null)
					throw new InputValidationException(ApiError.InvalidActionCode.getCode(), ApiError.InvalidActionCode.getMessage("input.actionCode"));
			} else {
				User sessionUser = UserServiceProvider.provide().getUser(input.session.user.id);

				if (input.userId != sessionUser.id) {
					ValidationHelper.validateAuthorised(input.session.user, RoleServiceProvider.provide().getRole(DataTypeHelper.ROLE_ADMIN_ID));
				}

				output.user = UserServiceProvider.provide().getUser(input.userId);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getUserDetails");
		return output;
	}

	public GetCategoriesResponse getCategories(GetCategoriesRequest input) {
		LOG.finer("Entering getCategories");
		GetCategoriesResponse output = new GetCategoriesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetCategoriesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.store == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Store: input.store"));

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.categories = CategoryServiceProvider.provide().getStoreCategories(input.store, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.categories, input.pager.totalCount == null ? CategoryServiceProvider.provide()
					.getStoreCategoriesCount(input.store) : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getCategories");
		return output;
	}

	public GetSalesResponse getSales(GetSalesRequest input) {
		LOG.finer("Entering getSales");
		GetSalesResponse output = new GetSalesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetSalesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			// if we only have a partial data source get look it up - because it is required for getting the stores
			if (input.linkedAccount.source.stores == null) {
				input.linkedAccount.source = DataSourceServiceProvider.provide().getDataSource(input.linkedAccount.source.id);
			}

			List<Store> stores = StoreServiceProvider.provide().getDataSourceStores(input.linkedAccount.source);

			// right now category
			if (input.category == null) {
				// TODO: input.category = CategoryServiceProvider.provide().getAllCategory(stores);
			} else {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");

				boolean foundStore = false;

				for (Store store : stores) {
					if (store.a3Code.equals(input.category.store)) {
						foundStore = true;
						break;
					}
				}

				if (!foundStore)
					throw new InputValidationException(ApiError.CategoryStoreMismatch.getCode(), ApiError.CategoryStoreMismatch.getMessage("input.category"));
			}

			if (input.end == null) {
				input.end = DateTime.now(DateTimeZone.UTC).toDate();
			}

			if (input.start == null) {
				input.start = (new DateTime(input.end.getTime(), DateTimeZone.UTC)).minusDays(30).toDate();
			}

			output.sales = SaleServiceProvider.provide().getSales(input.country, input.category, input.linkedAccount, input.start, input.end, input.pager);

			if (input.pager.start.intValue() == 0) {
				Map<String, Sale> internalIds = null;

				if (output.sales != null && output.sales.size() > 0) {

					internalIds = new HashMap<String, Sale>();

					for (Sale sale : output.sales) {
						if (!internalIds.containsKey(sale.item.internalId)) {
							internalIds.put(sale.item.internalId, sale);
						}
					}

					output.items = ItemServiceProvider.provide().getInternalIdItemBatch(internalIds.keySet());
				}
			}

			output.pager = input.pager;
			updatePager(output.pager, output.sales);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getSales");
		return output;
	}

	public GetItemSalesResponse getItemSales(GetItemSalesRequest input) {
		LOG.finer("Entering getItemSales");
		GetItemSalesResponse output = new GetItemSalesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetSalesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getItemSales");
		return output;
	}

	public GetSalesRanksResponse getSalesRanks(GetSalesRanksRequest input) {
		LOG.finer("Entering getSalesRanks");
		GetSalesRanksResponse output = new GetSalesRanksResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetSalesRanksRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			// if we only have a partial data source get look it up - because it is required for getting the stores
			if (input.linkedAccount.source.stores == null) {
				input.linkedAccount.source = DataSourceServiceProvider.provide().getDataSource(input.linkedAccount.source.id);
			}

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.end == null) {
				input.end = DateTime.now(DateTimeZone.UTC).toDate();
			}

			if (input.start == null) {
				input.start = (new DateTime(input.end.getTime(), DateTimeZone.UTC)).minusDays(30).toDate();
			}

			FormType form = null;
			Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);
			form = modeller.getForm(input.listType);

			output.ranks = RankServiceProvider.provide().getSaleSummaryAndRankForDataAccountAndFormType(input.linkedAccount.id, input.country, form,
					input.start, input.end, input.pager);

			if (output.ranks != null && output.ranks.size() > 0) {
				String[] itemIds = new String[output.ranks.size()];

				for (int i = 0; i < output.ranks.size(); i++) {
					itemIds[i] = output.ranks.get(i).itemId;
				}
			}

			output.pager = input.pager;
			updatePager(output.pager, output.ranks);
			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getSalesRanks");
		return output;
	}

	public GetItemSalesRanksResponse getItemSalesRanks(GetItemSalesRanksRequest input) {
		LOG.finer("Entering getItemSalesRanks");
		GetItemSalesRanksResponse output = new GetItemSalesRanksResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetSalesRanksRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "s.date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.item = ValidationHelper.validateItem(input.item, "input.item");

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			if (input.end == null) {
				input.end = DateTime.now(DateTimeZone.UTC).toDate();
			} else {
				input.end = (new DateTime(input.end.getTime(), DateTimeZone.UTC)).toDate();
			}

			if (input.start == null) {
				input.start = (new DateTime(input.start.getTime(), DateTimeZone.UTC)).minusDays(30).toDate();
			} else {
				input.start = (new DateTime(input.start.getTime(), DateTimeZone.UTC)).toDate();
			}

			FormType form = null;
			Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);
			form = modeller.getForm(input.listType);

			output.item = input.item;

			output.ranks = RankServiceProvider.provide().getSaleSummaryAndRankForItemAndFormType(input.item.internalId, input.country, input.category.id, form,
					input.start, input.end, input.pager);

			// TODO temp solution: show zero downloads and revenues if the dataaccountfetch that day isn't with status ingested
			Calendar cal = Calendar.getInstance();
			cal.setTime(input.start);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			DateTime progressiveDate = new DateTime(cal, DateTimeZone.UTC);
			List<Rank> emptyRange = new ArrayList<Rank>();
			while (DateTimeComparator.getDateOnlyInstance().compare(progressiveDate, input.end) <= 0) {
				Rank emptyRank = new Rank();
				emptyRank.date = new Date(progressiveDate.getMillis());
				emptyRank.downloads = new Integer(0);
				emptyRank.revenue = new Float(0);
				emptyRange.add(emptyRank);
				progressiveDate = progressiveDate.plusDays(1);
			}
			if (!output.ranks.isEmpty()) {
				for (Rank rank : output.ranks) {
					for (int i = 0; i < emptyRange.size(); i++) {
						if (DateTimeComparator.getDateOnlyInstance().compare(rank.date, emptyRange.get(i).date) == 0) {
							emptyRange.set(i, rank);
							break;
						}
					}
				}
			}
			output.ranks = new ArrayList<Rank>(emptyRange);

			output.pager = input.pager;
			updatePager(output.pager, output.ranks);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getItemSalesRanks");
		return output;
	}

	public GetLinkedAccountItemResponse getLinkedAccountItem(GetLinkedAccountItemRequest input) {
		LOG.finer("Entering getLinkedAccountItem");
		GetLinkedAccountItemResponse output = new GetLinkedAccountItemResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("GetLinkedAccountItemRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			if (input.session != null) {

				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

				input.item = ValidationHelper.validateItem(input.item, "input.item");

				DataAccount linkedAccount = SaleServiceProvider.provide().getDataAccount(input.item.internalId);

				if (linkedAccount != null) {
					boolean hasLinkedAccount = UserServiceProvider.provide().hasDataAccount(input.session.user, linkedAccount);

					if (hasLinkedAccount) {
						output.item = input.item;
						output.dataSource = linkedAccount.source;
						output.linkedAccount = linkedAccount;

						output.linkedAccount.source = new DataSource();
						output.linkedAccount.source.id = output.dataSource.id;
					}
				}
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getLinkedAccountItem");
		return output;
	}

	public GetNotificationsResponse getNotifications(GetNotificationsRequest input) {
		LOG.finer("Entering getNotifications");
		GetNotificationsResponse output = new GetNotificationsResponse();
		try {
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "created";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.pager = ValidationHelper.validatePager(input.pager, "input.pager");

			output.notifications = NotificationServiceProvider.provide().getUserNotifications(input.session.user,
					NotificationTypeType.NotificationTypeTypeInternal, input.pager);

			if (output.notifications != null) {
				for (Notification notification : output.notifications) {
					if ("beta@reflection.io".equals(notification.from)) {
						notification.from = "Beta (" + notification.from + ")";
					} else if ("hello@reflection.io".equals(notification.from)) {
						notification.from = "Hello (" + notification.from + ")";
					} else {
						notification.from = "Admin";
					}
				}
			}

			PagerHelper.updatePager(output.pager = input.pager, output.notifications, input.pager.totalCount == null ? NotificationServiceProvider.provide()
					.getUserNotificationsCount(input.session.user, NotificationTypeType.NotificationTypeTypeInternal, null, Boolean.FALSE)
					: input.pager.totalCount);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getNotifications");
		return output;
	}

	public DeleteNotificationsResponse deleteNotifications(DeleteNotificationsRequest input) {
		LOG.finer("Entering deleteNotifications");
		DeleteNotificationsResponse output = new DeleteNotificationsResponse();
		try {
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (input.notifications == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.notifications"));

			// all or nothing validation
			int i = 0;
			for (Notification notification : input.notifications) {
				ValidationHelper.validateExistingNotification(notification, "input.notifications[" + Integer.toString(i++) + "]");
			}

			// once validation/lookup is done... delete the items
			for (Notification notification : input.notifications) {
				NotificationServiceProvider.provide().deleteNotification(notification);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteNotifications");
		return output;
	}

	public UpdateNotificationsResponse updateNotifications(UpdateNotificationsRequest input) {
		LOG.finer("Entering updateNotifications");
		UpdateNotificationsResponse output = new UpdateNotificationsResponse();
		try {
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (input.notifications == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.notifications"));

			// all or nothing validation
			int i = 0;
			String path;
			Map<String, Notification> existing = new HashMap<String, Notification>();
			Notification lookup;
			for (Notification notification : input.notifications) {
				path = "input.notifications[" + Integer.toString(i++) + "]";

				lookup = ValidationHelper.validateExistingNotification(notification, path);
				existing.put(notification.id.toString(), lookup);

				// from is when sent to a client and we NEVER want to update it
				notification.from = lookup.from;

				// if the item exists, verify that the new parameters are valid update values
				ValidationHelper.validateNewNotification(notification, path);
			}

			// once validation/lookup is done... delete the items
			for (Notification notification : input.notifications) {
				NotificationServiceProvider.provide().updateNotification(existing.get(notification.id.toString()), notification);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting updateNotifications");
		return output;
	}

	public UpgradeAccountResponse upgradeAccount(UpgradeAccountRequest input) {
		LOG.finer("Entering upgradeAccount");
		UpgradeAccountResponse output = new UpgradeAccountResponse();
		try {
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");
			input.session.user = UserServiceProvider.provide().getUser(input.session.user.id); // Inflate user

			input.role = ValidationHelper.validateRole(input.role, "input.role");

			Role devRole = RoleServiceProvider.provide().getCodeRole(DataTypeHelper.ROLE_DEVELOPER_CODE);

			if (!UserServiceProvider.provide().hasRole(input.session.user, devRole).booleanValue())
				throw new InputValidationException(ApiError.RoleNotFound.getCode(), ApiError.RoleNotFound.getMessage("UpgradeAccountRequest: input"));

			UserServiceProvider.provide().revokeRole(input.session.user, devRole);

			// UserServiceProvider.provide().assignExpiringRole(input.session.user, input.role, 30);
			UserServiceProvider.provide().assignRole(input.session.user, input.role);

			// Notify the upgraded user
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("user", input.session.user);
			Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.UPGRADED_TO_PREMIUM_EVENT_CODE);
			if (event != null) {
				String body = NotificationHelper.inflate(values, event.longBody);
				Notification notification = (new Notification()).from("hello@reflection.io").user(input.session.user).event(event).body(body)
						.subject(event.subject);
				Notification added = NotificationServiceProvider.provide().addNotification(notification);
				if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					NotificationServiceProvider.provide().addNotification(notification);
				}
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting upgradeAccount");
		return output;
	}

	public RegisterInterestBusinessResponse registerInterestBusiness(RegisterInterestBusinessRequest input) {
		LOG.finer("Entering registerInterestBusiness");
		RegisterInterestBusinessResponse output = new RegisterInterestBusinessResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("RegisterInterestBusinessRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.user = ValidationHelper.validateUserWithoutPassword(input.user, "input.user");

			User user = UserServiceProvider.provide().getUsernameUser(input.user.username);

			if (user == null) { // Add a new user
				user = UserServiceProvider.provide().addUser(input.user);
				LOG.info(String.format("Added user with name [%s %s] and email [%s],", user.forename, user.surname, user.username));
			}
			if (user != null) {
				Role rbiRole = ValidationHelper.validateRole(DataTypeHelper.createRole(DataTypeHelper.ROLE_REGISTER_BUSINESS_INTEREST), "input.role");
				if (!UserServiceProvider.provide().hasRole(user, rbiRole, true)) { // User doesn't already have the RBI role
					// Add register interest business role - already expire so it won't conflict with the developer roles
					UserServiceProvider.provide().assignExpiringRole(user, rbiRole, 0);
					// Notify the user
					Map<String, Object> values = new HashMap<String, Object>();
					values.put("user", user);
					Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.REGISTER_BUSINESS_INTEREST_EVENT_CODE);
					if (event != null) {
						String body = NotificationHelper.inflate(values, event.longBody);
						Notification notification = (new Notification()).from("hello@reflection.io").user(user).event(event).body(body).subject(event.subject);
						Notification added = NotificationServiceProvider.provide().addNotification(notification);
						if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
							notification.type = NotificationTypeType.NotificationTypeTypeInternal;
							NotificationServiceProvider.provide().addNotification(notification);
						}
					}
				}
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting registerInterestBusiness");
		return output;
	}
}
