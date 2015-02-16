//
//  ValidationHelper.java
//  storedata
//
//  Created by William Shakour on 5 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api;

import io.reflection.app.api.exception.AuthorisationException;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventSubscription;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.eventsubscription.EventSubscriptionServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.service.session.ISessionService;
import io.reflection.app.service.session.SessionServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.spacehopperstudios.utility.StringUtils;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;

/**
 * @author billy1380
 * 
 */
public class ValidationHelper {

	/**
	 * 
	 * @param accessCode
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static String validateAccessCode(String accessCode, String parent) throws InputValidationException {
		if (accessCode == null) throw new InputValidationException(ApiError.AccessCodeNull.getCode(), ApiError.AccessCodeNull.getMessage(parent));

		if (!accessCode.matches("([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})"))
			throw new InputValidationException(ApiError.TokenNoMatch.getCode(), ApiError.TokenNoMatch.getMessage(parent));

		// if all is good look it up

		return accessCode;
	}

	public static User validateAlphaUser(User user, String parent) throws ServiceException {
		if (user == null) throw new InputValidationException(ApiError.UserNull.getCode(), ApiError.UserNull.getMessage(parent));

		if (user.forename == null || (user.forename = user.forename.trim()).length() == 0)
			throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".forename"));
		if (user.surname == null || (user.surname = user.surname.trim()).length() == 0)
			throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".surname"));
		if (user.company == null || (user.company = user.company.trim()).length() == 0)
			throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".company"));

		user.username = validateEmail(user.username, false, parent + ".username");

		return user;
	}

	/**
	 * 
	 * @param store
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Store validateStore(Store store, String parent) throws ServiceException {
		if (store == null) throw new InputValidationException(ApiError.StoreNull.getCode(), ApiError.StoreNull.getMessage(parent));

		boolean isIdLookup = false, isA3CodeLookup = false, isNameLookup = false;

		if (store.id != null) {
			isIdLookup = true;
		} else if (store.a3Code != null) {
			isA3CodeLookup = true;
		} else if (store.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isA3CodeLookup || isNameLookup))
			throw new InputValidationException(ApiError.StoreNoLookup.getCode(), ApiError.StoreNoLookup.getMessage(parent));

		Store lookupStore = null;
		if (isIdLookup) {
			lookupStore = StoreServiceProvider.provide().getStore(store.id);
		} else if (isA3CodeLookup) {
			lookupStore = StoreServiceProvider.provide().getA3CodeStore(store.a3Code);
		} else if (isNameLookup) {
			lookupStore = StoreServiceProvider.provide().getNamedStore(store.name);
		}

		if (lookupStore == null) throw new InputValidationException(ApiError.StoreNotFound.getCode(), ApiError.StoreNotFound.getMessage(parent));

		return lookupStore;
	}

	/**
	 * 
	 * @param query
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static String validateQuery(String query, String parent) throws InputValidationException {
		if (query == null) throw new InputValidationException(ApiError.SearchQueryNull.getCode(), ApiError.SearchQueryNull.getMessage(parent));

		return query;
	}

	/**
	 * @param pager
	 * @param string
	 * @return
	 * @throws InputValidationException
	 */
	public static Pager validatePager(Pager pager, String parent) throws InputValidationException {
		if (pager == null) {
			pager = new Pager();

			pager.start = Long.valueOf(0);
			pager.count = Long.valueOf(10);

			return pager;
		}

		if (pager.start == null) pager.start = Long.valueOf(0);

		if (pager.start < 0)
			throw new InputValidationException(ApiError.NegativeValueNotAllowed.getCode(), ApiError.NegativeValueNotAllowed.getMessage(StringUtils.join(
					Arrays.asList(parent, "pager", "start"), ".")));

		if (pager.count == null) pager.count = Long.valueOf(10);

		if (pager.count.intValue() <= 0)
			throw new InputValidationException(ApiError.NumericValueTooSmall.getCode(), ApiError.NumericValueTooSmall.getMessage(
					StringUtils.join(Arrays.asList(parent, "pager", "count"), "."), 0, 30));

		// TODO: for now this is disabled until we sort something out for it
		// if (pager.count.intValue() > 30)
		// throw new InputValidationException(ApiError.PagerCountTooLarge.getCode(), ApiError.PagerCountTooLarge.getMessage(parent));

		if (pager.start != null && pager.totalCount != null && pager.start.longValue() > pager.totalCount.longValue())
			throw new InputValidationException(ApiError.PagerStartLargerThanTotal.getCode(), ApiError.PagerStartLargerThanTotal.getMessage(parent));

		return pager;
	}

	/**
	 * 
	 * @param country
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Country validateCountry(Country country, String parent) throws ServiceException {
		if (country == null) throw new InputValidationException(ApiError.CountryNull.getCode(), ApiError.CountryNull.getMessage(parent));

		boolean isIdLookup = false, isA2CodeLookup = false, isNameLookup = false;

		if (country.id != null) {
			isIdLookup = true;
		} else if (country.a2Code != null) {
			isA2CodeLookup = true;
		} else if (country.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isA2CodeLookup || isNameLookup))
			throw new InputValidationException(ApiError.CountryNoLookup.getCode(), ApiError.CountryNoLookup.getMessage(parent));

		Country lookupCountry = null;
		if (isIdLookup) {
			lookupCountry = CountryServiceProvider.provide().getCountry(country.id);
		} else if (isA2CodeLookup) {
			lookupCountry = CountryServiceProvider.provide().getA2CodeCountry(country.a2Code);
		} else if (isNameLookup) {
			lookupCountry = CountryServiceProvider.provide().getNamedCountry(country.name);
		}

		if (lookupCountry == null) throw new InputValidationException(ApiError.CountryNotFound.getCode(), ApiError.CountryNotFound.getMessage(parent));

		return lookupCountry;
	}

	/**
	 * @param item
	 * @param string
	 * @return
	 * @throws InputValidationException
	 */
	public static Item validateItem(Item item, String parent) throws ServiceException {
		if (item == null) throw new InputValidationException(ApiError.ItemNull.getCode(), ApiError.ItemNull.getMessage(parent));

		Store itemStore = new Store();
		itemStore.a3Code = item.source;

		itemStore = validateStore(itemStore, parent + ".item");

		item.source = itemStore.a3Code;

		boolean isIdLookup = false, isIntIdLookup = false, isExtIdLookup = false;

		if (item.id != null) {
			isIdLookup = true;
		} else if (item.externalId != null) {
			isExtIdLookup = true;
		} else if (item.internalId != null) {
			isIntIdLookup = true;
		}

		if (!(isIdLookup || isIntIdLookup || isExtIdLookup))
			throw new InputValidationException(ApiError.ItemNoLookup.getCode(), ApiError.ItemNoLookup.getMessage(parent));

		Item lookupItem = null;
		if (isIdLookup) {
			lookupItem = ItemServiceProvider.provide().getItem(item.id);
		} else if (isExtIdLookup) {
			lookupItem = ItemServiceProvider.provide().getExternalIdItem(item.externalId);
		} else if (isIntIdLookup) {
			// first try to get the item from the item table
			lookupItem = ItemServiceProvider.provide().getInternalIdItem(item.internalId);

			// if that fails try to get it from the the sales table
			if (lookupItem == null) {
				lookupItem = SaleServiceProvider.provide().getItem(item.internalId);
			}
		}

		if (lookupItem == null) throw new InputValidationException(ApiError.ItemNotFound.getCode(), ApiError.ItemNotFound.getMessage(parent));

		return lookupItem;
	}

	/**
	 * @param listType
	 * @param store
	 * @return
	 */
	public static String validateListType(String listType, Store store) {
		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		List<String> types = collector.getTypes();

		return types.contains(listType) ? listType : null;
	}

	/**
	 * @param listTypes
	 * @param string
	 * @return
	 * @throws InputValidationException
	 */
	public static List<String> validateListTypes(List<String> listTypes, Store store, String parent) throws InputValidationException {
		if (listTypes == null)
			throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("List: " + parent));

		String validatedListType, listType;
		StringBuffer badListTypes = new StringBuffer();
		for (int i = 0; i < listTypes.size(); i++) {
			listType = listTypes.get(i);
			validatedListType = ValidationHelper.validateListType(listType, store);

			listTypes.remove(i);

			if (listType != null) {
				listTypes.add(i, validatedListType);
			} else {
				if (badListTypes.length() != 0) {
					badListTypes.append(",");
				}

				badListTypes.append(listType);
			}
		}

		if (listTypes.size() == 0)
			throw new InputValidationException(ApiError.ListTypeNotFound.getCode(), ApiError.ListTypeNotFound.getMessage("String: " + parent + ".listTypes"));

		return listTypes;
	}

	/**
	 * 
	 * @param user
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static User validateExistingUser(User user, String parent) throws ServiceException {
		if (user == null) throw new InputValidationException(ApiError.UserNull.getCode(), ApiError.UserNull.getMessage(parent));

		boolean isIdLookup = false, isNameLookup = false;

		if (user.id != null) {
			isIdLookup = true;
		} else if (user.username != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isNameLookup)) { throw new InputValidationException(ApiError.UserNoLookup.getCode(), ApiError.UserNoLookup.getMessage(parent)); }

		User lookupUser = null;
		if (isIdLookup) {
			lookupUser = UserServiceProvider.provide().getUser(user.id);
		} else if (isNameLookup) {
			lookupUser = UserServiceProvider.provide().getUsernameUser(user.username);
		}

		if (lookupUser == null) throw new InputValidationException(ApiError.UserNotFound.getCode(), ApiError.UserNotFound.getMessage(parent));

		return lookupUser;
	}

	/**
	 * 
	 * @param user
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static User validateRegisteringUser(User user, String parent) throws InputValidationException {
		if (user == null) throw new InputValidationException(ApiError.UserNull.getCode(), ApiError.UserNull.getMessage(parent));

		user.username = validateEmail(user.username, false, parent + ".username");

		if (user.forename == null) throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".forename"));

		if (user.surname == null) throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".surname"));

		if (user.company == null) throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".company"));

		user.password = validatePassword(user.password, parent + ".password");

		return user;
	}

	/**
	 * 
	 * @param password
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static String validatePassword(String password, String parent) throws InputValidationException {
		if (password == null || password.length() == 0)
			throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent));

		password = validateStringLength(password, parent, 6, 100);

		return password;
	}

	/**
	 * 
	 * @param value
	 * @param parent
	 * @param minLenth
	 * @param maxLength
	 * @return
	 * @throws InputValidationException
	 */
	public static String validateStringLength(String value, String parent, int minLenth, int maxLength) throws InputValidationException {
		if (value.length() < minLenth)
			throw new InputValidationException(ApiError.InvalidStringTooShort.getCode(), ApiError.InvalidStringTooShort.getMessage(parent, minLenth, maxLength));

		if (value.length() > maxLength)
			throw new InputValidationException(ApiError.InvalidStringTooLong.getCode(), ApiError.InvalidStringTooLong.getMessage(parent, minLenth, maxLength));

		return value;
	}

	/**
	 * 
	 * @param role
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Role validateRole(Role role, String parent) throws ServiceException {
		if (role == null) throw new InputValidationException(ApiError.RoleNull.getCode(), ApiError.RoleNull.getMessage(parent));

		boolean isIdLookup = false, isCodeLookup = false, isNameLookup = false;

		if (role.id != null) {
			isIdLookup = true;
		} else if (role.name != null) {
			isNameLookup = true;
		} else if (role.code != null) {
			isCodeLookup = true;
		}

		if (!(isIdLookup || isNameLookup || isCodeLookup)) { throw new InputValidationException(ApiError.RoleNoLookup.getCode(),
				ApiError.RoleNoLookup.getMessage(parent)); }

		Role lookupRole = null;
		if (isIdLookup) {
			lookupRole = RoleServiceProvider.provide().getRole(role.id);
		} else if (isNameLookup) {
			lookupRole = RoleServiceProvider.provide().getNamedRole(role.name);
		} else if (isCodeLookup) {
			lookupRole = RoleServiceProvider.provide().getCodeRole(role.code);
		}

		if (lookupRole == null) throw new InputValidationException(ApiError.RoleNotFound.getCode(), ApiError.RoleNotFound.getMessage(parent));

		return lookupRole;
	}

	/**
	 * 
	 * @param permission
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Permission validatePermission(Permission permission, String parent) throws ServiceException {
		if (permission == null) throw new InputValidationException(ApiError.PermissionNull.getCode(), ApiError.PermissionNull.getMessage(parent));

		boolean isIdLookup = false, isCodeLookup = false, isNameLookup = false;

		if (permission.id != null) {
			isIdLookup = true;
		} else if (permission.name != null) {
			isNameLookup = true;
		} else if (permission.code != null) {
			isCodeLookup = true;
		}

		if (!(isIdLookup || isNameLookup || isCodeLookup))
			throw new InputValidationException(ApiError.PermissionNoLookup.getCode(), ApiError.PermissionNoLookup.getMessage(parent));

		Permission lookupPermission = null;
		if (isIdLookup) {
			lookupPermission = PermissionServiceProvider.provide().getPermission(permission.id);
		} else if (isNameLookup) {
			lookupPermission = PermissionServiceProvider.provide().getNamedPermission(permission.name);
		} else if (isCodeLookup) {
			lookupPermission = PermissionServiceProvider.provide().getCodePermission(permission.code);
		}

		if (lookupPermission == null)
			throw new InputValidationException(ApiError.PermissionNotFound.getCode(), ApiError.PermissionNotFound.getMessage(parent));

		return lookupPermission;
	}

	/**
	 * @param token
	 * @param string
	 * @return
	 */
	public static String validateToken(String token, String parent) throws InputValidationException {

		if (token == null) throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent));

		if (!token.matches("([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})"))
			throw new InputValidationException(ApiError.TokenNoMatch.getCode(), ApiError.TokenNoMatch.getMessage(parent));

		return token;
	}

	/**
	 * @param session
	 * @return
	 */
	public static Session validateAndExtendSession(Session session, String parent) throws ServiceException {

		if (session == null) throw new InputValidationException(ApiError.SessionNull.getCode(), ApiError.SessionNull.getMessage(parent));

		boolean isIdLookup = false, isTokenLookup = false;

		if (session.id != null) {
			isIdLookup = true;
		} else if (session.token != null) {
			isTokenLookup = true;
		}

		if (!(isIdLookup || isTokenLookup))
			throw new InputValidationException(ApiError.SessionNoLookup.getCode(), ApiError.SessionNoLookup.getMessage(parent));

		Session lookupSession = null;
		if (isIdLookup) {
			lookupSession = SessionServiceProvider.provide().getSession(session.id);

			if (lookupSession == null) throw new InputValidationException(ApiError.SessionNotFound.getCode(), ApiError.SessionNotFound.getMessage(parent));
		} else if (isTokenLookup) {
			session.token = validateToken(session.token, parent + ".token");

			lookupSession = SessionServiceProvider.provide().getTokenSession(session.token);

			if (lookupSession == null) throw new InputValidationException(ApiError.SessionNotFound.getCode(), ApiError.SessionNotFound.getMessage(parent));
		}

		if (lookupSession != null && lookupSession.expires != null
				&& (lookupSession.expires.getTime() - (new Date()).getTime()) < ISessionService.SESSION_SHORT_DURATION * 1000) {
			lookupSession = SessionServiceProvider.provide().extendSession(lookupSession, Long.valueOf(ISessionService.SESSION_SHORT_DURATION));
		}

		return lookupSession;
	}

	public static void validateAuthorised(User user, Role... roles) throws ServiceException {
		for (Role role : roles) {
			if (!UserServiceProvider.provide().hasRole(user, role)) throw new AuthorisationException(user, roles);
		}
	}

	public static void validateAuthorised(User user, Permission... permissions) throws ServiceException {
		for (Permission permission : permissions) {
			if (!UserServiceProvider.provide().hasPermission(user, permission)) throw new AuthorisationException(user, permissions);
		}
	}

	/**
	 * @param source
	 * @param string
	 * @return
	 * @throws InputValidationException
	 */
	public static DataSource validateDataSource(DataSource dataSource, String parent) throws ServiceException {
		if (dataSource == null) throw new InputValidationException(ApiError.DataSourceNull.getCode(), ApiError.DataSourceNull.getMessage(parent));

		boolean isIdLookup = false, isA3CodeLookup = false, isNameLookup = false;

		if (dataSource.id != null) {
			isIdLookup = true;
		} else if (dataSource.a3Code != null) {
			isA3CodeLookup = true;
		} else if (dataSource.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isA3CodeLookup || isNameLookup))
			throw new InputValidationException(ApiError.DataSourceNoLookup.getCode(), ApiError.DataSourceNoLookup.getMessage(parent));

		DataSource lookupDataSource = null;
		if (isIdLookup) {
			lookupDataSource = DataSourceServiceProvider.provide().getDataSource(dataSource.id);
		} else if (isA3CodeLookup) {
			lookupDataSource = DataSourceServiceProvider.provide().getA3CodeDataSource(dataSource.a3Code);
		} else if (isNameLookup) {
			lookupDataSource = DataSourceServiceProvider.provide().getNamedDataSource(dataSource.name);
		}

		if (lookupDataSource == null)
			throw new InputValidationException(ApiError.DataSourceNotFound.getCode(), ApiError.DataSourceNotFound.getMessage(parent));

		return lookupDataSource;
	}

	/**
	 * 
	 * @param dataAccount
	 * @param parent
	 * @return
	 * @throws ServiceException
	 */
	public static DataAccount validateDataAccount(DataAccount dataAccount, String parent) throws ServiceException {
		if (dataAccount == null) throw new InputValidationException(ApiError.DataAccountNull.getCode(), ApiError.DataAccountNull.getMessage(parent));

		if (dataAccount.id == null)
			throw new InputValidationException(ApiError.DataAccountNoLookup.getCode(), ApiError.DataAccountNoLookup.getMessage(parent));

		DataAccount lookupDataAccount = DataAccountServiceProvider.provide().getDataAccount(dataAccount.id);

		if (lookupDataAccount == null)
			throw new InputValidationException(ApiError.DataAccountNotFound.getCode(), ApiError.DataAccountNotFound.getMessage(parent));

		return lookupDataAccount;
	}

	public static Category validateCategory(Category category, String parent) throws ServiceException {
		if (category == null) throw new InputValidationException(ApiError.CategoryNull.getCode(), ApiError.CategoryNull.getMessage(parent));

		boolean isIdLookup = false, isStoreInternalIdLookup = false;

		Store store = null;

		if (category.id != null) {
			isIdLookup = true;
		} else if (category.internalId != null && category.store != null) {
			store = new Store();
			store.a3Code = category.store;

			store = validateStore(store, parent + ".store");

			isStoreInternalIdLookup = true;
		}

		if (!(isIdLookup || isStoreInternalIdLookup))
			throw new InputValidationException(ApiError.CategoryNoLookup.getCode(), ApiError.CategoryNoLookup.getMessage(parent));

		Category lookupCategory = null;
		if (isIdLookup) {
			lookupCategory = CategoryServiceProvider.provide().getCategory(category.id);
		} else if (isStoreInternalIdLookup) {
			lookupCategory = CategoryServiceProvider.provide().getInternalIdCategory(store, category.internalId);
		}

		if (lookupCategory == null) throw new InputValidationException(ApiError.CategoryNotFound.getCode(), ApiError.CategoryNotFound.getMessage(parent));

		return lookupCategory;
	}

	/**
	 * 
	 * @param feedFetch
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static FeedFetch validateFeedFetch(FeedFetch feedFetch, String parent) throws ServiceException {
		if (feedFetch == null) throw new InputValidationException(ApiError.FeedFetchNull.getCode(), ApiError.FeedFetchNull.getMessage(parent));

		FeedFetch lookupFeedFetch = null;
		if (feedFetch.id != null) {
			lookupFeedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetch.id);
		}

		if (lookupFeedFetch == null) throw new InputValidationException(ApiError.FeedFetchNotFound.getCode(), ApiError.FeedFetchNotFound.getMessage(parent));

		return lookupFeedFetch;
	}

	/**
	 * @param email
	 * @param isNullable
	 * @param parent
	 * @return
	 */
	public static String validateEmail(String email, boolean isNullable, String parent) throws InputValidationException {

		if (!isNullable) {
			if (email == null || (email = email.trim()).length() == 0)
				throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent));
		}

		if (!email.matches(FormHelper.EMAIL_PATTERN))
			throw new InputValidationException(ApiError.BadEmailFormat.getCode(), ApiError.BadEmailFormat.getMessage(parent));

		return email;
	}

	/**
	 * @param event
	 * @param string
	 * @return
	 */
	public static Event validateExistingEvent(Event event, String parent) throws ServiceException {
		if (event == null) throw new InputValidationException(ApiError.EventNull.getCode(), ApiError.EventNull.getMessage(parent));

		boolean isIdLookup = false, isCodeLookup = false;

		if (event.id != null) {
			isIdLookup = true;
		} else if (event.code != null) {
			isCodeLookup = true;
		}

		if (!(isIdLookup || isCodeLookup)) throw new InputValidationException(ApiError.EventNoLookup.getCode(), ApiError.EventNoLookup.getMessage(parent));

		Event lookupEvent = null;
		if (isIdLookup) {
			lookupEvent = EventServiceProvider.provide().getEvent(event.id);
		} else if (isCodeLookup) {
			lookupEvent = EventServiceProvider.provide().getCodeEvent(event.code);
		}

		if (lookupEvent == null) throw new InputValidationException(ApiError.EventNotFound.getCode(), ApiError.EventNotFound.getMessage(parent));

		// validateEvent(event, parent);

		return lookupEvent;
	}

	public static Event validateEvent(Event event, String parent) throws ServiceException {
		event.subject = ValidationHelper.validateStringLength(event.subject, parent + ".subject", 1, 2000);

		event.shortBody = ValidationHelper.validateStringLength(event.shortBody, parent + ".shortBody", 1, 50000);
		event.longBody = ValidationHelper.validateStringLength(event.longBody, parent + ".longBody", 1, 50000);

		event.name = ValidationHelper.validateStringLength(event.name, parent + ".name", 1, 50000);
		event.description = ValidationHelper.validateStringLength(event.description, parent + ".description", 1, 50000);
		event.code = ValidationHelper.validateStringLength(event.code, parent + ".code", 3, 3);

		return event;
	}

	public static Post validateExistingPost(Post post, String parent) throws ServiceException {
		// TODO: validate
		return post;
	}

	public static Post validateNewPost(Post post, String parent) throws ServiceException {
		// TODO: validate
		return post;
	}

	public static Forum validateForum(Forum forum, String parent) throws ServiceException {
		// TODO Auto-generated method stub
		return forum;
	}

	public static Topic validateExistingTopic(Topic topic, String parent) throws ServiceException {
		// TODO Auto-generated method stub
		return topic;
	}

	public static Topic validateNewTopic(Topic topic, String parent) throws ServiceException {
		// TODO Auto-generated method stub
		return topic;
	}

	public static Reply validateExistingReply(Reply reply, String parent) throws ServiceException {
		// TODO Auto-generated method stub
		return reply;
	}

	public static Reply validateNewReply(Reply reply, String parent) throws ServiceException {
		// TODO Auto-generated method stub
		return reply;
	}

	/**
	 * @param simpleModelRun
	 * @param string
	 * @return
	 * @throws ServiceException
	 */
	public static SimpleModelRun validateSimpleModelRun(SimpleModelRun simpleModelRun, String parent) throws ServiceException {
		if (simpleModelRun == null) throw new InputValidationException(ApiError.SimpleModelRunNull.getCode(), ApiError.SimpleModelRunNull.getMessage(parent));

		boolean isIdLookup = false, isFeedFetchIdLookup = false;

		if (simpleModelRun.id != null) {
			isIdLookup = true;
		} else if (simpleModelRun.feedFetch != null && simpleModelRun.feedFetch.id != null) {
			isFeedFetchIdLookup = true;
		}

		// TODO: if no lookup method is found put a better error to point api user in the correct direction

		SimpleModelRun lookupSimpleModelRun = null;
		if (isIdLookup) {
			lookupSimpleModelRun = SimpleModelRunServiceProvider.provide().getSimpleModelRun(simpleModelRun.id);
		} else if (isFeedFetchIdLookup) {
			lookupSimpleModelRun = SimpleModelRunServiceProvider.provide().getFeedFetchSimpleModelRun(simpleModelRun.feedFetch);
		}

		if (lookupSimpleModelRun == null)
			throw new InputValidationException(ApiError.SimpleModelRunNotFound.getCode(), ApiError.SimpleModelRunNotFound.getMessage(parent));

		return lookupSimpleModelRun;
	}

	/**
	 * Validate DataAccountFetch
	 * 
	 * @param dataAccountFetch
	 * @param parent
	 * @return
	 * @throws ServiceException
	 */
	public static DataAccountFetch validateDataAccountFetch(DataAccountFetch dataAccountFetch, String parent) throws ServiceException {
		if (dataAccountFetch == null)
			throw new InputValidationException(ApiError.DataAccountFetchNull.getCode(), ApiError.DataAccountFetchNull.getMessage(parent));

		if (dataAccountFetch.id == null)
			throw new InputValidationException(ApiError.DataAccountFetchNoLookup.getCode(), ApiError.DataAccountFetchNoLookup.getMessage(parent));

		DataAccountFetch lookupDataAccountFetch = DataAccountFetchServiceProvider.provide().getDataAccountFetch(dataAccountFetch.id);

		if (lookupDataAccountFetch == null)
			throw new InputValidationException(ApiError.DataAccountFetchNotFound.getCode(), ApiError.DataAccountFetchNotFound.getMessage(parent));

		return lookupDataAccountFetch;
	}

	/**
	 * @param notification
	 * @param string
	 * @return
	 * @throws ServiceException
	 */
	public static Notification validateNewNotification(Notification notification, String parent) throws ServiceException {

		if (notification == null) throw new InputValidationException(ApiError.NotificationNull.getCode(), ApiError.NotificationNull.getMessage(parent));

		if (notification.cause == null) {
			notification.user = ValidationHelper.validateExistingUser(notification.user, parent + ".user");
		}

		if (notification.cause == null && notification.event != null) {
			notification.event = ValidationHelper.validateExistingEvent(notification.event, parent + ".event");
		}

		if (notification.cause != null) {
			notification.cause = validateExistingEventSubscription(notification.cause, parent + ".cause");

			if (notification.user != null) {
				if (notification.cause.user.id.longValue() != notification.user.id.longValue())
					throw new ServiceException(0, "TODO: these should match in this very unlikely scenario");
			}

			if (notification.event != null) {
				if (notification.cause.event.id.longValue() != notification.event.id.longValue())
					throw new ServiceException(0, "TODO: these should match in this very unlikely scenario");
			}
		}

		notification.from = validateEmail(notification.from, false, parent + ".from");

		if (notification.subject == null)
			throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".subject"));
		notification.subject = ValidationHelper.validateStringLength(notification.subject, parent + ".subject", 1, 2000);

		if (notification.body == null) throw new InputValidationException(ApiError.StringNull.getCode(), ApiError.StringNull.getMessage(parent + ".body"));
		notification.body = ValidationHelper.validateStringLength(notification.body, parent + ".body", 1, 50000);

		return notification;
	}

	/**
	 * @param eventSubscription
	 * @param parent
	 * @return
	 * 
	 * @throws ServiceException
	 */
	private static EventSubscription validateExistingEventSubscription(EventSubscription eventSubscription, String parent) throws ServiceException {
		if (eventSubscription == null)
			throw new InputValidationException(ApiError.EventSubscriptionNull.getCode(), ApiError.EventSubscriptionNull.getMessage(parent));

		boolean isIdLookup = false, isUserAndEventLookup = false;

		if (eventSubscription.id != null) {
			isIdLookup = true;
		} else {
			boolean hasUser = false, hasEvent = false;

			try {
				eventSubscription.user = validateExistingUser(eventSubscription.user, parent + ".user");
				hasUser = true;
			} catch (ServiceException s) {}

			try {
				eventSubscription.event = validateEvent(eventSubscription.event, parent + ".event");
				hasEvent = true;
			} catch (ServiceException s) {}

			if (hasUser && hasEvent) {
				isUserAndEventLookup = true;
			}
		}

		if (!(isIdLookup || isUserAndEventLookup))
			throw new InputValidationException(ApiError.EventSubscriptionNoLookup.getCode(), ApiError.EventSubscriptionNoLookup.getMessage(parent));

		EventSubscription lookupEventSubscription = null;
		if (isIdLookup) {
			lookupEventSubscription = EventSubscriptionServiceProvider.provide().getEventSubscription(eventSubscription.id);
		} else if (isUserAndEventLookup) {
			lookupEventSubscription = EventSubscriptionServiceProvider.provide().getUserEventEventSubscription(eventSubscription.event, eventSubscription.user);
		}

		if (lookupEventSubscription == null)
			throw new InputValidationException(ApiError.EventSubscriptionNotFound.getCode(), ApiError.EventSubscriptionNotFound.getMessage(parent));

		return lookupEventSubscription;
	}

	public static Notification validateExistingNotification(Notification notification, String parent) throws ServiceException {
		if (notification == null) throw new InputValidationException(ApiError.NotificationNull.getCode(), ApiError.NotificationNull.getMessage(parent));

		if (notification.id == null)
			throw new InputValidationException(ApiError.NotificationNoLookup.getCode(), ApiError.NotificationNoLookup.getMessage(parent));

		Notification lookupNotification = NotificationServiceProvider.provide().getNotification(notification.id);

		if (lookupNotification == null)
			throw new InputValidationException(ApiError.NotificationNotFound.getCode(), ApiError.NotificationNotFound.getMessage(parent));

		return lookupNotification;
	}
}
