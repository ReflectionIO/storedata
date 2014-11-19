//  
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

import static io.reflection.app.service.sale.ISaleService.FREE_OR_PAID_APP_IPAD_IOS;
import static io.reflection.app.service.sale.ISaleService.FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS;
import static io.reflection.app.service.sale.ISaleService.FREE_OR_PAID_APP_UNIVERSAL_IOS;
import static io.reflection.app.service.sale.ISaleService.INAPP_PURCHASE_PURCHASE_IOS;
import static io.reflection.app.service.sale.ISaleService.INAPP_PURCHASE_SUBSCRIPTION_IOS;
import static io.reflection.app.service.sale.ISaleService.UPDATE_IPAD_IOS;
import static io.reflection.app.service.sale.ISaleService.UPDATE_IPHONE_AND_IPOD_TOUCH_IOS;
import static io.reflection.app.service.sale.ISaleService.UPDATE_UNIVERSAL_IOS;
import static io.reflection.app.shared.util.PagerHelper.updatePager;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.CheckUsernameRequest;
import io.reflection.app.api.core.shared.call.CheckUsernameResponse;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
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
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;
import io.reflection.app.api.exception.AuthenticationException;
import io.reflection.app.api.exception.AuthorisationException;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.EmailFormatType;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ModelRun;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.EmailHelper;
import io.reflection.app.helpers.SliceHelper;
import io.reflection.app.itemrankarchivers.ItemRankArchiver;
import io.reflection.app.itemrankarchivers.ItemRankArchiverFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.modelrun.ModelRunServiceProvider;
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
import io.reflection.app.shared.util.FormattingHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.gson.json.service.shared.StatusType;

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
			} else throw new InputValidationException(ApiError.GetCountriesNeedsStoreOrQuery.getCode(),
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
			} else throw new InputValidationException(ApiError.GetStoresNeedsCountryOrQuery.getCode(),
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
			Date end = date.minusHours(12).toDate();
			Date start = date.minusDays(1).toDate();

			List<Rank> ranks = RankServiceProvider.provide().getRanks(input.country, input.store, input.category, input.listType, start, end, input.pager);

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
				updatePager(
						output.pager,
						output.ranks,
						input.pager.totalCount == null ? RankServiceProvider.provide().getRanksCount(input.country, input.store, input.category,
								input.listType, start, end) : input.pager.totalCount);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getTopItems");
		return output;
	}

	private final static String FULL_RANK_VIEW_CODE = "FRV";
	private final static int SESSIONLESS_MAX_ITEMS = 10;
	private final static int PERMISSIONLESS_MAX_ITEMS = 25;

	public GetAllTopItemsResponse getAllTopItems(GetAllTopItemsRequest input) {
		LOG.finer("Entering getAllTopItems");
		GetAllTopItemsResponse output = new GetAllTopItemsResponse();

		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetAllTopItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			boolean loggedIn = false;
			boolean canSeeFullList = false;

			if (input.session != null) {
				output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

				if (input.session != null) {
					loggedIn = true;

					List<Role> roles = UserServiceProvider.provide().getRoles(input.session.user);

					List<Permission> permissions;

					for (Role role : roles) {
						permissions = RoleServiceProvider.provide().getPermissions(role);

						for (Permission permission : permissions) {
							if (FULL_RANK_VIEW_CODE.equals(permission.code)) {
								canSeeFullList = true;
								break;
							}
						}

						if (canSeeFullList) {
							break;
						}
					}
				}
			}

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
			}

			boolean skip = false;
			int maxLimit = !loggedIn ? SESSIONLESS_MAX_ITEMS : (!canSeeFullList ? PERMISSIONLESS_MAX_ITEMS : -1);

			if (!loggedIn || !canSeeFullList) {
				if (input.pager.start.longValue() > maxLimit) {
					skip = true;
				} else if (input.pager.count.longValue() + input.pager.start.longValue() > maxLimit) {
					input.pager.count = Long.valueOf(maxLimit - input.pager.start.longValue());
				}
			}

			if (!skip) {
				input.country = ValidationHelper.validateCountry(input.country, "input");

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
						throw new InputValidationException(ApiError.CategoryStoreMismatch.getCode(),
								ApiError.CategoryStoreMismatch.getMessage("input.category"));
				}

				DateTime date = new DateTime(input.on.getTime(), DateTimeZone.UTC);
				Date end = date.minusHours(12).toDate();
				Date start = date.minusDays(1).toDate();

				Set<String> itemIds = new HashSet<String>();
				// final Map<String, Rank> lookup = new HashMap<String, Rank>();

				Long code = FeedFetchServiceProvider.provide().getGatherCode(input.country, input.store, start, end);

				List<String> listTypes = ApiHelper.getAllListTypes(input.store, input.listType);
				Collector collector = CollectorFactory.getCollectorForStore(input.store.a3Code);

				List<Rank> ranks;
				for (String listType : listTypes) {
					// get all the ranks for the list type (we are using an infinite pager with no sorting to allow us to generate a deletion key during
					// prediction)
					ranks = RankServiceProvider.provide().getGatherCodeRanks(input.country, input.store, input.category, listType, code,
							PagerHelper.createInfinitePager(), Boolean.TRUE);

					for (Rank rank : ranks) {
						itemIds.add(rank.itemId);
					}

					if (collector.isFree(listType)) {
						output.freeRanks = ranks;
					} else if (collector.isPaid(listType)) {
						output.paidRanks = ranks;
					} else if (collector.isGrossing(listType)) {
						output.grossingRanks = ranks;
					}
				}

				// List<Rank> ranks = RankServiceProvider.provide().getAllRanks(input.country, input.store, input.category,
				// getGrossingListName(input.store, input.listType), start, end);
				//
				// if (ranks != null && ranks.size() != 0) {
				// SparseArray<Rank> free = new SparseArray<Rank>();
				// SparseArray<Rank> paid = new SparseArray<Rank>();
				// SparseArray<Rank> grossing = new SparseArray<Rank>();
				//
				// for (Rank rank : ranks) {
				// if (!lookup.containsKey(rank.itemId)) {
				// itemIds.add(rank.itemId);
				// lookup.put(rank.itemId, rank);
				// }
				//
				// if (rank.price.floatValue() == 0 && rank.position.intValue() > 0) {
				// free.append(rank.position.intValue(), rank);
				// }
				//
				// if (rank.price.floatValue() != 0 && rank.position.intValue() > 0) {
				// paid.append(rank.position.intValue(), rank);
				// }
				//
				// if (rank.grossingPosition.intValue() != 0) {
				// grossing.append(rank.grossingPosition.intValue(), rank);
				// }
				// }
				//
				// output.freeRanks = free.toList();
				// output.paidRanks = paid.toList();
				// output.grossingRanks = grossing.toList();
				// }

				output.items = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);

				output.pager = input.pager;

				if (input.pager.totalCount == null && input.pager.boundless != Boolean.TRUE) {
					input.pager.totalCount = Long.valueOf(output.freeRanks.size());
				}

				updatePager(output.pager, output.freeRanks, input.pager.totalCount);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getAllTopItems");
		return output;
	}

	// private String latestCode(List<Rank> ranks) {
	// String code = null;
	//
	// if (ranks != null) {
	// Date latest = null;
	//
	// for (Rank rank : ranks) {
	// if (latest == null || (rank.date.getTime() > latest.getTime())) {
	// latest = rank.date;
	// code = rank.code;
	// }
	// }
	// }
	//
	// return code;
	// }

	public GetItemRanksResponse getItemRanks(GetItemRanksRequest input) {
		LOG.finer("Entering getItemRanks");
		GetItemRanksResponse output = new GetItemRanksResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetItemRanksRequest: input"));

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
			}

			if (input.start == null) {
				input.start = (new DateTime(input.end.getTime(), DateTimeZone.UTC)).minusDays(30).toDate();
			}

			input.listType = ValidationHelper.validateListType(input.listType, store);

			FormType form = ModellerFactory.getModellerForStore(store.a3Code).getForm(input.listType);

			long diff = input.end.getTime() - input.start.getTime();
			long diffDays = diff / ApiHelper.MILLIS_PER_DAY;

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ApiError.DateRangeOutOfBounds.getCode(),
						ApiError.DateRangeOutOfBounds.getMessage("0-60 days: input.end - input.start"));

			ItemRankArchiver archiver = ItemRankArchiverFactory.get();
			long[] slices = SliceHelper.offsets(input.start, input.end);

			String key;
			List<Rank> ranks;
			for (long slice : slices) {
				key = archiver.createKey(slice, input.item, form, store, input.country, input.category);

				ranks = archiver.getItemRanks(key);

				if (ranks != null) {
					if (output.ranks == null) {
						output.ranks = new ArrayList<Rank>();
					}

					output.ranks.addAll(ranks);
				}
			}

			if (output.ranks == null) {
				output.ranks = RankServiceProvider.provide()
						.getItemRanks(input.country, store, input.listType, input.item, input.start, input.end, input.pager);
			}

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

	public RegisterUserResponse registerUser(RegisterUserRequest input) {
		LOG.finer("Entering registerUser");
		RegisterUserResponse output = new RegisterUserResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("RegisterUserRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			boolean isAction = input.actionCode != null;

			if (input.accessCode.equalsIgnoreCase("b72b4e32-1062-4cc7-bc6b-52498ee10f09") || input.user.password == null || input.user.password.length() == 0) {
				// Alpha user or Request invite
				input.user = ValidationHelper.validateAlphaUser(input.user, "input.user");
			} else {
				if (isAction) {
					input.user.password = ValidationHelper.validatePassword(input.user.password, "input.user.password");
				} else {
					input.user = ValidationHelper.validateRegisteringUser(input.user, "input.user");
				}
			}

			User user = null;

			if (isAction) {
				user = UserServiceProvider.provide().getActionCodeUser(input.actionCode);

				if (user == null)
					throw new InputValidationException(ApiError.InvalidActionCode.getCode(), ApiError.InvalidActionCode.getMessage("input.actionCode"));

				UserServiceProvider.provide().updateUserPassword(user, input.user.password, Boolean.FALSE);

				LOG.info(String.format("Completed user registeration for user [%s %s] and email [%s] and action code [%s]", user.forename, user.surname,
						user.username, input.actionCode));
			} else {
				user = UserServiceProvider.provide().addUser(input.user);

				LOG.info(String.format("Added user with name [%s %s] and email [%s],", user.forename, user.surname, user.username));
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
					} else {
						throw new Exception("Unexpected blank session after creating user session.");
					}
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

			if (input.user.id.longValue() != input.session.user.id.longValue()) throw new ServiceException(-1, "User and session user do not match");

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

			String properties = input.linkedAccount.properties;
			String password = ValidationHelper.validateStringLength(input.linkedAccount.password, "input.linkedAccount.password", 2, 1000);

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			// DataAccountCollectorFactory.getCollectorForSource(input.linkedAccount.source.a3Code).validateProperties(input.linkedAccount.properties);

			input.linkedAccount.properties = properties;
			input.linkedAccount.password = password;
			if (password == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.linkedAccount.password"));

			DataAccount linkedAccount = DataAccountServiceProvider.provide().updateDataAccount(input.linkedAccount);

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
				User user = UserServiceProvider.provide().getDataAccountOwner(input.linkedAccount);

				if (user != null && user.id.longValue() == input.session.user.id.longValue()) {
					UserServiceProvider.provide().deleteAllUsersDataAccount(input.linkedAccount);

					if (!UserServiceProvider.provide().hasDataAccounts(user) && !isAdmin) {
						Permission hlaPermission = PermissionServiceProvider.provide().getCodePermission(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE);
						UserServiceProvider.provide().revokePermission(user, hlaPermission);
					}

					// Set linked account as inactive
					input.linkedAccount.active = DataTypeHelper.INACTIVE_VALUE;
					DataAccountServiceProvider.provide().updateDataAccount(input.linkedAccount);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.finer(String.format("Linked account with id [%d] deleted by owner [%d]", input.linkedAccount.id.longValue(),
								input.session.user.id.longValue()));
					}
				} else {
					UserServiceProvider.provide().deleteDataAccount(input.session.user, input.linkedAccount);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.finer(String.format("Linked account with id [%d] removed from user account [%d]", input.linkedAccount.id.longValue(),
								input.session.user.id.longValue()));
					}
				}

			} else throw new InputValidationException(ApiError.DataAccountUserMissmatch.getCode(), ApiError.DataAccountUserMissmatch.getMessage());

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
				output.roles = UserServiceProvider.provide().getRoles(input.session.user);

				if (output.roles != null) {
					if (input.idsOnly == Boolean.FALSE) {
						RoleServiceProvider.provide().inflateRoles(output.roles);
					}

					for (Role role : output.roles) {
						role.permissions = RoleServiceProvider.provide().getPermissions(role);

						if (role.permissions != null && input.idsOnly == Boolean.FALSE) {
							PermissionServiceProvider.provide().inflatePermissions(role.permissions);
						}
					}
				}
			}

			if (input.rolesOnly != Boolean.TRUE) {
				output.permissions = UserServiceProvider.provide().getPermissions(input.session.user);

				if (output.permissions != null && input.idsOnly == Boolean.FALSE) {
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

			List<String> freeOrPaidApps = new ArrayList<String>();
			freeOrPaidApps.add(FREE_OR_PAID_APP_UNIVERSAL_IOS);
			if (form == FormType.FormTypeOther) {
				freeOrPaidApps.add(FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS);
			} else if (form == FormType.FormTypeTablet) {
				freeOrPaidApps.add(FREE_OR_PAID_APP_IPAD_IOS);
			}

			output.items = SaleServiceProvider.provide().getDataAccountItems(input.linkedAccount, freeOrPaidApps, input.pager);

			output.pager = input.pager;
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

			DataAccountCollectorFactory.getCollectorForSource(input.source.a3Code).validateProperties(input.properties);

			// If not a test user, check if is a valid Apple linked account
			Role testRole = RoleServiceProvider.provide().getCodeRole(DataTypeHelper.ROLE_TEST_CODE);
			if (!UserServiceProvider.provide().hasRole(input.session.user, testRole)) {
				Date testDate = (DateTime.now(DateTimeZone.UTC)).minusDays(45).toDate();
				DataAccount dataAccountToTest = new DataAccount();
				dataAccountToTest.username = input.username;
				dataAccountToTest.password = input.password;
				dataAccountToTest.properties = input.properties;
				dataAccountToTest.source = input.source;
				dataAccountToTest.id = new Long(0);

				try {
					DataAccountServiceProvider.provide().verifyDataAccount(dataAccountToTest, testDate);
				} catch (ServiceException e) {
					throw new ServiceException(e.getCode(), e.getMessage());
				}
			}

			output.account = UserServiceProvider.provide().addDataAccount(input.session.user, input.source, input.username, input.password, input.properties);

			output.account.source = input.source;

			boolean isAdmin = UserServiceProvider.provide().hasRole(input.session.user, DataTypeHelper.adminRole());

			Permission hlaPermission = PermissionServiceProvider.provide().getCodePermission(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE);
			boolean hasPermission = UserServiceProvider.provide().hasPermission(input.session.user, hlaPermission);

			if (!hasPermission && !isAdmin) {
				UserServiceProvider.provide().assignPermission(input.session.user, hlaPermission);
			}

			if (output.account != null) {
				if (input.session.user == null || input.session.user.forename == null) {
					input.session.user = UserServiceProvider.provide().getUser(input.session.user.id);
				}

				EmailHelper
						.sendEmail(
								"hello@reflection.io",
								"chi@reflection.io",
								"Chi Dire",
								"A user's has linked thier account account",
								String.format(
										"Hi Chi,\n\nThis is to let you know that the user [%d - %s] has added the data account [%d] for the data source [%s] and the username.\n\nReflection",
										input.session.user.id.longValue(), FormattingHelper.getUserLongName(input.session.user), output.account.id.longValue(),
										input.source.name, output.account.username), EmailFormatType.EmailFormatTypePlainText);
			}

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

			List<Role> roles = UserServiceProvider.provide().getRoles(input.session.user);

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

	// private String getFreeListName(Store store, String type) {
	// String listName = null;
	//
	// if (DataTypeHelper.IOS_STORE_A3.equalsIgnoreCase(store.a3Code)) {
	// if ("ipad".equalsIgnoreCase(type)) {
	// listName = CollectorIOS.TOP_FREE_IPAD_APPS;
	// } else {
	// listName = CollectorIOS.TOP_FREE_APPS;
	// }
	// }
	//
	// return listName;
	// }
	//
	// private String getPaidListName(Store store, String type) {
	// String listName = null;
	//
	// if (DataTypeHelper.IOS_STORE_A3.equalsIgnoreCase(store.a3Code)) {
	// if ("ipad".equalsIgnoreCase(type)) {
	// listName = CollectorIOS.TOP_PAID_IPAD_APPS;
	// } else {
	// listName = CollectorIOS.TOP_PAID_APPS;
	// }
	// }
	//
	// return listName;
	//
	// }

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

			final Permission permissionMCA = PermissionServiceProvider.provide().getCodePermission(DataTypeHelper.PERMISSION_MANAGE_CATEGORIES_CODE);
			try {
				ValidationHelper.validateAuthorised(input.session.user, permissionMCA);
			} catch (AuthorisationException aEx) {

			}

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
				// TODO:
				// input.category = CategoryServiceProvider.provide().getAllCategory(stores);
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

			long diff = input.end.getTime() - input.start.getTime();
			long diffDays = diff / ApiHelper.MILLIS_PER_DAY;

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ApiError.DateRangeOutOfBounds.getCode(),
						ApiError.DateRangeOutOfBounds.getMessage("0-60 days: input.end - input.start"));

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

			List<Store> stores = StoreServiceProvider.provide().getDataSourceStores(input.linkedAccount.source);

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			// right now category
			if (input.category == null) {
				// TODO:
				// input.category = CategoryServiceProvider.provide().getAllCategory(stores);
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

			long diff = input.end.getTime() - input.start.getTime();
			long diffDays = diff / ApiHelper.MILLIS_PER_DAY;

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ApiError.DateRangeOutOfBounds.getCode(),
						ApiError.DateRangeOutOfBounds.getMessage("0-60 days: input.end - input.start"));

			// Get Items sales based on the filters
			List<Sale> sales = SaleServiceProvider.provide().getSales(input.country, input.category, input.linkedAccount, input.start, input.end,
					PagerHelper.createInfinitePager());

			if (sales.size() > 0) {
				// group sales by date
				Map<Date, List<Sale>> salesGroupByDate = new HashMap<Date, List<Sale>>();
				Date key;
				SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");

				Store defaultStore = stores.get(0);
				Modeller modeller = ModellerFactory.getModellerForStore(defaultStore.a3Code);
				FormType form = modeller.getForm(input.listType);

				Map<String, String> parentIdItemIdLookup = new HashMap<String, String>();
				for (Sale sale : sales) {
					// only add Sales that are consistent with the device type
					if (FREE_OR_PAID_APP_UNIVERSAL_IOS.equals(sale.typeIdentifier) // 1F
							|| UPDATE_UNIVERSAL_IOS.equals(sale.typeIdentifier) // 7F
							|| (form == FormType.FormTypeOther && (FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier))) // 1
							|| (form == FormType.FormTypeOther && (UPDATE_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier))) // 7
							|| (form == FormType.FormTypeTablet && (FREE_OR_PAID_APP_IPAD_IOS.equals(sale.typeIdentifier))) // 1T
							|| (form == FormType.FormTypeTablet && (UPDATE_IPAD_IOS.equals(sale.typeIdentifier))) // 7T
							|| INAPP_PURCHASE_PURCHASE_IOS.equals(sale.typeIdentifier) // IA1
							|| INAPP_PURCHASE_SUBSCRIPTION_IOS.equals(sale.typeIdentifier) // IA9
					) {

						// If type identifier != IA1 or IA9, add parent identifiers into the Map
						if (!sale.typeIdentifier.equals(INAPP_PURCHASE_PURCHASE_IOS) && !sale.typeIdentifier.equals(INAPP_PURCHASE_SUBSCRIPTION_IOS)) {
							parentIdItemIdLookup.put(sale.sku, sale.item.internalId);
						}

						key = keyFormat.parse(keyFormat.format(sale.begin));

						// Link list of item IDs with every day of the range
						if (salesGroupByDate.get(key) == null) {
							salesGroupByDate.put(key, new ArrayList<Sale>());
						}

						salesGroupByDate.get(key).add(sale);

					}
				}

				// get the model runs constants
				List<ModelRun> modelRuns = ModelRunServiceProvider.provide().getDateModelRunBatch(input.country, defaultStore, form, salesGroupByDate.keySet());

				Map<Date, ModelRun> modelRunLookup = new HashMap<Date, ModelRun>();

				for (ModelRun modelRun : modelRuns) {
					key = keyFormat.parse(keyFormat.format(modelRun.created));

					if (modelRunLookup.get(key) == null) {
						modelRunLookup.put(key, modelRun);
					}
				}

				// add the numbers up to create ranks and then predict the position and the grossing position
				Rank rank;
				ModelRun modelRun;

				// Create a dummy rank for every Item, every day of the date range
				output.ranks = new ArrayList<Rank>();

				// Keep track of the rank for a specific Item this day
				Map<String, Rank> itemIDsRankLookup;

				// Get range of dates
				Set<Date> dates = salesGroupByDate.keySet();

				// setup id only category to avoid bloating data with category
				Category category = null;
				if (input.category != null && input.category.id != null) {
					category = new Category();
					category.id = input.category.id;
				}

				for (Date salesGroupDate : dates) {

					modelRun = modelRunLookup.get(salesGroupDate);

					itemIDsRankLookup = new HashMap<String, Rank>();

					List<Sale> salesGroup = salesGroupByDate.get(salesGroupDate);

					String itemId;
					for (Sale sale : salesGroup) {
						// Assign item id of the parent to IAP and Subscriptions
						if (sale.typeIdentifier.equals(INAPP_PURCHASE_PURCHASE_IOS) || sale.typeIdentifier.equals(INAPP_PURCHASE_SUBSCRIPTION_IOS)) {
							itemId = parentIdItemIdLookup.get(sale.parentIdentifier);
						} else {
							itemId = sale.item.internalId;
						}

						if (itemIDsRankLookup.get(itemId) == null) {
							rank = new Rank();
							rank.downloads = 0;
							rank.revenue = (float) 0;

							// Add common values
							if (modelRun != null) {
								rank.code = modelRun.code;
							}

							rank.category = category;

							rank.country = input.country.a2Code;
							rank.currency = sale.customerCurrency;
							rank.date = salesGroupDate;
							rank.created = salesGroupDate;
							rank.source = defaultStore.a3Code;
							rank.type = input.listType;

							output.ranks.add(rank);
							itemIDsRankLookup.put(itemId, rank);
						} else {
							rank = itemIDsRankLookup.get(itemId);
						}

						rank.itemId = itemId;

						// If units and customer prices are negatives (refunds), subtract the value setting units positive
						rank.revenue += Math.abs(sale.units.floatValue()) * sale.customerPrice.floatValue();

						// Take into account price and downloads only from main Apps
						if (sale.typeIdentifier.equals(FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS)
								|| sale.typeIdentifier.equals(FREE_OR_PAID_APP_UNIVERSAL_IOS) || sale.typeIdentifier.equals(FREE_OR_PAID_APP_IPAD_IOS)) {
							rank.downloads += sale.units.intValue();
							// Ignore price if the Sale is a refund or a promotion
							if (rank.price == null && sale.units.intValue() > 0 && sale.promoCode.equals(" ")) {
								rank.price = sale.customerPrice;
							}
						}

						if (modelRun != null) {
							// TODO: use the mode to predict what rank that would be
							// rank.grossingPosition;
							// rank.position;
						}

					} // end 1 day sales loop

				} // end date range loop

			} // if sale.size > 0

			DataTypeHelper.sortRanksByDate(output.ranks);

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
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.item = ValidationHelper.validateItem(input.item, "input.item");

			DataAccount linkedAccount = SaleServiceProvider.provide().getDataAccount(input.item.internalId);

			// if we only have a partial data source get look it up - because it is required for getting the stores
			if (linkedAccount.source.stores == null) {
				linkedAccount.source = DataSourceServiceProvider.provide().getDataSource(linkedAccount.source.id);
			}

			List<Store> stores = StoreServiceProvider.provide().getDataSourceStores(linkedAccount.source);

			if (input.listType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("String: input.listType"));

			// right now category
			if (input.category == null) {
				// TODO:
				// input.category = CategoryServiceProvider.provide().getAllCategory(stores);
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

			long diff = input.end.getTime() - input.start.getTime();
			long diffDays = diff / ApiHelper.MILLIS_PER_DAY;

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ApiError.DateRangeOutOfBounds.getCode(),
						ApiError.DateRangeOutOfBounds.getMessage("0-60 days: input.end - input.start"));

			// Get Items sales based on the filters
			List<Sale> sales = SaleServiceProvider.provide().getItemSales(input.item, input.country, input.category, linkedAccount, input.start, input.end,
					PagerHelper.createInfinitePager());

			if (sales.size() > 0) {
				// group sales by date
				Map<Date, List<Sale>> salesGroupByDate = new HashMap<Date, List<Sale>>();
				Date key;
				SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");

				Store defaultStore = stores.get(0);
				Modeller modeller = ModellerFactory.getModellerForStore(defaultStore.a3Code);
				FormType form = modeller.getForm(input.listType);

				for (Sale sale : sales) {
					// only add Sales that are consistent with the device type
					if (FREE_OR_PAID_APP_UNIVERSAL_IOS.equals(sale.typeIdentifier) // 1F
							|| UPDATE_UNIVERSAL_IOS.equals(sale.typeIdentifier) // 7F
							|| (form == FormType.FormTypeOther && (FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier))) // 1
							|| (form == FormType.FormTypeOther && (UPDATE_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier))) // 7
							|| (form == FormType.FormTypeTablet && (FREE_OR_PAID_APP_IPAD_IOS.equals(sale.typeIdentifier))) // 1T
							|| (form == FormType.FormTypeTablet && (UPDATE_IPAD_IOS.equals(sale.typeIdentifier))) // 7T
							|| INAPP_PURCHASE_PURCHASE_IOS.equals(sale.typeIdentifier) // IA1
							|| INAPP_PURCHASE_SUBSCRIPTION_IOS.equals(sale.typeIdentifier) // IA9
					) {

						key = keyFormat.parse(keyFormat.format(sale.begin));

						// Link list of item IDs with every day of the range
						if (salesGroupByDate.get(key) == null) {
							salesGroupByDate.put(key, new ArrayList<Sale>());
						}

						salesGroupByDate.get(key).add(sale);

					}
				}

				// get the model runs constants
				List<ModelRun> modelRuns = ModelRunServiceProvider.provide().getDateModelRunBatch(input.country, defaultStore, form, salesGroupByDate.keySet());

				Map<Date, ModelRun> modelRunLookup = new HashMap<Date, ModelRun>();

				for (ModelRun modelRun : modelRuns) {
					key = keyFormat.parse(keyFormat.format(modelRun.created));

					if (modelRunLookup.get(key) == null) {
						modelRunLookup.put(key, modelRun);
					}
				}

				// add the numbers up to create ranks and then predict the position and the grossing position
				Rank rank = null;
				ModelRun modelRun;

				// Create a dummy rank for the Item, every day of the date range
				output.ranks = new ArrayList<Rank>();

				// Get range of dates
				Set<Date> dates = salesGroupByDate.keySet();

				// setup id only category to avoid bloating data with category
				Category category = null;
				if (input.category != null && input.category.id != null) {
					category = new Category();
					category.id = input.category.id;
				}

				for (Date salesGroupDate : dates) {

					boolean created = false;

					modelRun = modelRunLookup.get(salesGroupDate);

					List<Sale> salesGroup = salesGroupByDate.get(salesGroupDate);

					for (Sale sale : salesGroup) {

						if (!created) {
							rank = new Rank();
							rank.downloads = 0;
							rank.revenue = (float) 0;

							// Add common values
							if (modelRun != null) {
								rank.code = modelRun.code;
							}

							rank.category = category;
							rank.country = input.country.a2Code;
							rank.currency = sale.customerCurrency;
							rank.date = salesGroupDate;
							rank.created = salesGroupDate;
							rank.source = defaultStore.a3Code;
							rank.type = input.listType;
							rank.itemId = input.item.internalId;

							output.ranks.add(rank);

							created = true;
						}

						// If units and customer prices are negatives (refunds), subtract the value setting units positive
						rank.revenue += Math.abs(sale.units.floatValue()) * sale.customerPrice.floatValue();

						// Take into account price and downloads only from main Apps
						if (sale.typeIdentifier.equals(FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS)
								|| sale.typeIdentifier.equals(FREE_OR_PAID_APP_UNIVERSAL_IOS) || sale.typeIdentifier.equals(FREE_OR_PAID_APP_IPAD_IOS)) {
							rank.downloads += sale.units.intValue();
							// Ignore price if the Sale is a refund or a promotion
							if (rank.price == null && sale.units.intValue() > 0 && sale.promoCode.equals(" ")) {
								rank.price = sale.customerPrice;
							}
						}

						if (modelRun != null) {
							// TODO: use the mode to predict what rank that would be
							// rank.grossingPosition;
							// rank.position;
						}

					} // end 1 day sales loop

				} // end date range loop

			} // if sale.size > 0

			DataTypeHelper.sortRanksByDate(output.ranks);

			output.item = input.item;
			output.linkedAccount = linkedAccount;

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

			input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

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

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getLinkedAccountItem");
		return output;
	}
}
