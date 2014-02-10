//  
//  Core.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

import static io.reflection.app.api.PagerHelper.updatePager;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.api.ApiError;
import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.CheckUsernameRequest;
import io.reflection.app.api.core.shared.call.CheckUsernameResponse;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.GetTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsResponse;
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
import io.reflection.app.api.exception.AuthenticationException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.service.session.ISessionService;
import io.reflection.app.service.session.SessionServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.service.user.IUserService;
import io.reflection.app.service.user.UserServiceProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

			Calendar cal = Calendar.getInstance();
			cal.setTime(input.on);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			Date end = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Date start = cal.getTime();

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
				output.items = ItemServiceProvider.provide().getExternalIdItemBatch(itemIds);

				output.pager = input.pager;
				updatePager(
						output.pager,
						output.ranks,
						input.pager.totalCount == null ? RankServiceProvider.provide().getRanksCount(input.country, input.store, input.category,
								input.listType, start, end) : null);
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
				input.session = ValidationHelper.validateSession(input.session, "input.session");

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

				Calendar cal = Calendar.getInstance();
				cal.setTime(input.on);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 1);
				Date end = cal.getTime();
				cal.add(Calendar.DAY_OF_YEAR, -1);
				Date start = cal.getTime();

				List<String> itemIds = new ArrayList<String>();
				final Map<String, Rank> lookup = new HashMap<String, Rank>();

				String freeListType;
				String code = null;

				List<Rank> ranks = RankServiceProvider.provide().getRanks(input.country, input.store, input.category,
						freeListType = getFreeListName(input.store, input.listType), start, end, input.pager);

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
					ranks = RankServiceProvider.provide().getRanks(input.country, input.store, input.category, getPaidListName(input.store, input.listType),
							start, end, input.pager);
				} else {
					ranks = RankServiceProvider.provide().getGatherCodeRanks(input.country, input.store, input.category,
							getPaidListName(input.store, input.listType), code, input.pager, false);
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
					ranks = RankServiceProvider.provide().getRanks(input.country, input.store, input.category,
							getGrossingListName(input.store, input.listType), start, end, input.pager);
				} else {
					ranks = RankServiceProvider.provide().getGatherCodeRanks(input.country, input.store, input.category,
							getGrossingListName(input.store, input.listType), code, input.pager, false);
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
						input.pager.totalCount = RankServiceProvider.provide().getRanksCount(input.country, input.store, input.category, freeListType, start,
								end);
					} else {
						input.pager.totalCount = RankServiceProvider.provide().getGatherCodeRanksCount(input.country, input.store, input.category,
								freeListType, code);
					}
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

			Calendar cal = Calendar.getInstance();

			if (input.end == null) input.end = cal.getTime();

			if (input.start == null) {
				cal.setTime(input.end);
				cal.add(Calendar.DAY_OF_YEAR, -30);
				input.start = cal.getTime();
			}

			input.listType = ValidationHelper.validateListType(input.listType, store);

			long diff = input.end.getTime() - input.start.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if (diffDays > 60 || diffDays < 0)
				throw new InputValidationException(ApiError.DateRangeOutOfBounds.getCode(),
						ApiError.DateRangeOutOfBounds.getMessage("0-60 days: input.end - input.start"));

			output.ranks = RankServiceProvider.provide().getItemRanks(input.country, store, input.listType, input.item, input.start, input.end, input.pager);

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

			if (input.accessCode.equalsIgnoreCase("b72b4e32-1062-4cc7-bc6b-52498ee10f09")) {
				// use the email servlet code to determine whether they are an alpha user
				input.user = ValidationHelper.validateAlphaUser(input.user, "input");
			} else {
				input.user = ValidationHelper.validateRegisteringUser(input.user, "input.user");
			}

			User user = UserServiceProvider.provide().addUser(input.user);

			LOG.info(String.format("Added user with name [%s %s] and email [%s],", user.forename, user.surname, user.username));

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
					output.session.user = user;
				}
			} else {
				input.session = ValidationHelper.validateSession(input.session, "input.session");

				output.session = input.session;
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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			SessionServiceProvider.provide().deleteSession(input.session);

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			input.password = ValidationHelper.validatePassword(input.password, "input.password");

			User sessionUser = UserServiceProvider.provide().getUser(input.session.user.id);

			User credentialUser = UserServiceProvider.provide().getLoginUser(sessionUser.username, input.password);

			if (credentialUser == null)
				throw new InputValidationException(ApiError.IncorrectPasswordForChange.getCode(),
						ApiError.IncorrectPasswordForChange.getMessage("input.password"));

			input.newPassword = ValidationHelper.validatePassword(input.newPassword, "input.newPassword");

			if (input.newPassword.equals(input.password))
				throw new InputValidationException(ApiError.InvalidPasswordSameAsCurrent.getCode(),
						ApiError.InvalidPasswordSameAsCurrent.getMessage("input.newPassword"));

			UserServiceProvider.provide().updateUserPassword(input.session.user, input.newPassword);

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			if (input.user.id.longValue() != input.session.user.id.longValue()) throw new ServiceException(-1, "User and session user do not match");

			input.user = ValidationHelper.validateRegisteringUser(input.user, "input.user");

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			if (input.permissionsOnly != Boolean.TRUE) {
				output.roles = UserServiceProvider.provide().getRoles(input.session.user);

				if (output.roles != null && input.idsOnly == Boolean.FALSE) {
					RoleServiceProvider.provide().inflateRoles(output.roles);
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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "created";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			output.linkedAccounts = UserServiceProvider.provide().getDataAccounts(input.session.user, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.linkedAccounts,
					input.pager.totalCount == null ? UserServiceProvider.provide().getDataAccountsCount(input.session.user) : null);

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			input.linkedAccount = ValidationHelper.validateDataAccount(input.linkedAccount, "input.linkedAccount");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "created";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			output.items = SaleServiceProvider.provide().getDataAccountItems(input.linkedAccount, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.items, input.pager.totalCount == null ? SaleServiceProvider.provide()
					.getDataAccountItemsCount(input.linkedAccount) : null);

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			input.source = ValidationHelper.validateDataSource(input.source, "input.source");

			if (input.username == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.username"));

			input.username = ValidationHelper.validateStringLength(input.username, "input.username", 2, 255);

			if (input.password == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.password"));

			input.password = ValidationHelper.validateStringLength(input.password, "input.password", 2, 1000);

			DataAccountCollectorFactory.getCollectorForSource(input.source.a3Code).validateProperties(input.properties);

			output.account = UserServiceProvider.provide().addDataAccount(input.session.user, input.source, input.username, input.password, input.properties);

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

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

	public SearchForItemResponse searchForItem(SearchForItemRequest input) {
		LOG.finer("Entering searchForItem");
		SearchForItemResponse output = new SearchForItemResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("SearchForItemResponse: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");
			input.session = ValidationHelper.validateSession(input.session, "input.session");
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

}