//
//  ValidationHelper.java
//  storedata
//
//  Created by William Shakour on 5 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.input;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.session.SessionServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Permission;
import io.reflection.app.shared.datatypes.Role;
import io.reflection.app.shared.datatypes.Store;
import io.reflection.app.shared.datatypes.User;

import java.util.List;

import com.willshex.gson.json.service.server.InputValidationException;

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
		if (accessCode == null)
			throw new InputValidationException(ValidationError.AccessCodeNull.getCode(), ValidationError.AccessCodeNull.getMessage(parent));

		if (!accessCode.matches("([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})"))
			throw new InputValidationException(ValidationError.TokenNoMatch.getCode(), ValidationError.TokenNoMatch.getMessage(parent));

		// if all is good look it up

		return accessCode;
	}

	public static User validateAlphaUser(User user, String parent) throws InputValidationException {
		if (user == null) throw new InputValidationException(ValidationError.UserNull.getCode(), ValidationError.UserNull.getMessage(parent));

		if (user.forename == null || (user.forename = user.forename.trim()).length() == 0)
			throw new InputValidationException(ValidationError.StringNull.getCode(), ValidationError.StringNull.getMessage(parent + ".forename"));
		if (user.surname == null || (user.surname = user.surname.trim()).length() == 0)
			throw new InputValidationException(ValidationError.StringNull.getCode(), ValidationError.StringNull.getMessage(parent + ".surname"));
		if (user.company == null || (user.company = user.company.trim()).length() == 0)
			throw new InputValidationException(ValidationError.StringNull.getCode(), ValidationError.StringNull.getMessage(parent + ".company"));

		if (user.username == null || (user.username = user.username.trim()).length() == 0)
			throw new InputValidationException(ValidationError.StringNull.getCode(), ValidationError.StringNull.getMessage(parent + ".username"));

		if (!user.username.matches("^([\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4})?$"))
			throw new InputValidationException(ValidationError.BadEmailFormat.getCode(), ValidationError.BadEmailFormat.getMessage(parent + ".username"));

		return user;

	}

	/**
	 * 
	 * @param store
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Store validateStore(Store store, String parent) throws InputValidationException {
		if (store == null) throw new InputValidationException(ValidationError.StoreNull.getCode(), ValidationError.StoreNull.getMessage(parent));

		boolean isIdLookup = false, isA3CodeLookup = false, isNameLookup = false;

		if (store.id != null) {
			isIdLookup = true;
		} else if (store.a3Code != null) {
			isA3CodeLookup = true;
		} else if (store.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isA3CodeLookup || isNameLookup)) { throw new InputValidationException(ValidationError.StoreNoLookup.getCode(),
				ValidationError.StoreNoLookup.getMessage(parent)); }

		Store lookupStore = null;
		if (isIdLookup) {
			lookupStore = StoreServiceProvider.provide().getStore(store.id);
		} else if (isA3CodeLookup) {
			lookupStore = StoreServiceProvider.provide().getA3CodeStore(store.a3Code);
		} else if (isNameLookup) {
			lookupStore = StoreServiceProvider.provide().getNamedStore(store.name);
		}

		if (lookupStore == null) throw new InputValidationException(ValidationError.StoreNotFound.getCode(), ValidationError.StoreNotFound.getMessage(parent));

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
		if (query == null) throw new InputValidationException(ValidationError.SearchQueryNull.getCode(), ValidationError.SearchQueryNull.getMessage(parent));

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
			throw new InputValidationException(ValidationError.PagerStartNegative.getCode(), ValidationError.PagerStartNegative.getMessage(parent));

		if (pager.count == null) pager.count = Long.valueOf(0);

		if (pager.count.intValue() <= 0)
			throw new InputValidationException(ValidationError.PagerCountTooSmall.getCode(), ValidationError.PagerCountTooSmall.getMessage(parent));

		// TODO: for now this is disabled until we sort something out for it
		// if (pager.count.intValue() > 30)
		// throw new InputValidationException(ValidationError.PagerCountTooLarge.getCode(), ValidationError.PagerCountTooLarge.getMessage(parent));

		if (pager.start != null && pager.totalCount != null && pager.start.longValue() > pager.totalCount.longValue())
			throw new InputValidationException(ValidationError.PagerStartLargerThanTotal.getCode(),
					ValidationError.PagerStartLargerThanTotal.getMessage(parent));

		return pager;
	}

	/**
	 * 
	 * @param country
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Country validateCountry(Country country, String parent) throws InputValidationException {

		if (country == null) throw new InputValidationException(ValidationError.CountryNull.getCode(), ValidationError.CountryNull.getMessage(parent));

		boolean isIdLookup = false, isA2CodeLookup = false, isNameLookup = false;

		if (country.id != null) {
			isIdLookup = true;
		} else if (country.a2Code != null) {
			isA2CodeLookup = true;
		} else if (country.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isA2CodeLookup || isNameLookup))
			throw new InputValidationException(ValidationError.CountryNoLookup.getCode(), ValidationError.CountryNoLookup.getMessage(parent));

		Country lookupCountry = null;
		if (isIdLookup) {
			lookupCountry = CountryServiceProvider.provide().getCountry(country.id);
		} else if (isA2CodeLookup) {
			lookupCountry = CountryServiceProvider.provide().getA2CodeCountry(country.a2Code);
		} else if (isNameLookup) {
			lookupCountry = CountryServiceProvider.provide().getNamedCountry(country.name);
		}

		if (lookupCountry == null)
			throw new InputValidationException(ValidationError.CountryNotFound.getCode(), ValidationError.CountryNotFound.getMessage(parent));

		return lookupCountry;
	}

	/**
	 * @param item
	 * @param string
	 * @return
	 * @throws InputValidationException
	 */
	public static Item validateItem(Item item, String parent) throws InputValidationException {
		if (item == null) throw new InputValidationException(ValidationError.ItemNull.getCode(), ValidationError.ItemNull.getMessage(parent));

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
			throw new InputValidationException(ValidationError.ItemNoLookup.getCode(), ValidationError.ItemNoLookup.getMessage(parent));

		Item lookupItem = null;
		if (isIdLookup) {
			lookupItem = ItemServiceProvider.provide().getItem(item.id);
		} else if (isExtIdLookup) {
			lookupItem = ItemServiceProvider.provide().getExternalIdItem(item.externalId);
		} else if (isIntIdLookup) {
			lookupItem = ItemServiceProvider.provide().getInternalIdItem(item.internalId);
		}

		if (lookupItem == null) throw new InputValidationException(ValidationError.ItemNotFound.getCode(), ValidationError.ItemNotFound.getMessage(parent));

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
			throw new InputValidationException(ValidationError.InvalidValueNull.getCode(), ValidationError.InvalidValueNull.getMessage("List: " + parent
					+ ".listType"));

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
			throw new InputValidationException(ValidationError.ListTypeNotFound.getCode(), ValidationError.ListTypeNotFound.getMessage("String: " + parent
					+ ".listTypes"));

		return listTypes;
	}

	/**
	 * 
	 * @param user
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static User validateExistingUser(User user, String parent) throws InputValidationException {
		if (user == null) throw new InputValidationException(ValidationError.UserNull.getCode(), ValidationError.UserNull.getMessage(parent));

		boolean isIdLookup = false, isNameLookup = false;

		if (user.id != null) {
			isIdLookup = true;
		} else if (user.username != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isNameLookup)) { throw new InputValidationException(ValidationError.UserNoLookup.getCode(),
				ValidationError.UserNoLookup.getMessage(parent)); }

		User lookupUser = null;
		if (isIdLookup) {
			lookupUser = UserServiceProvider.provide().getUser(user.id);
		} else if (isNameLookup) {
			lookupUser = UserServiceProvider.provide().getUsernameUser(user.username);
		}

		if (lookupUser == null) throw new InputValidationException(ValidationError.UserNotFound.getCode(), ValidationError.UserNotFound.getMessage(parent));

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
		if (user == null) throw new InputValidationException(ValidationError.UserNull.getCode(), ValidationError.UserNull.getMessage(parent));

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
			throw new InputValidationException(ValidationError.StringNull.getCode(), ValidationError.StringNull.getMessage(parent));

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
			throw new InputValidationException(ValidationError.InvalidStringTooShort.getCode(), ValidationError.InvalidStringTooShort.getMessage(parent,
					minLenth, maxLength));

		if (value.length() > maxLength)
			throw new InputValidationException(ValidationError.InvalidStringTooLong.getCode(), ValidationError.InvalidStringTooLong.getMessage(parent,
					minLenth, maxLength));

		return value;
	}

	/**
	 * 
	 * @param role
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Role validateRole(Role role, String parent) throws InputValidationException {
		if (role == null) throw new InputValidationException(ValidationError.RoleNull.getCode(), ValidationError.RoleNull.getMessage(parent));

		boolean isIdLookup = false, isNameLookup = false;

		if (role.id != null) {
			isIdLookup = true;
		} else if (role.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isNameLookup)) { throw new InputValidationException(ValidationError.RoleNoLookup.getCode(),
				ValidationError.RoleNoLookup.getMessage(parent)); }

		Role lookupRole = null;
		if (isIdLookup) {
			lookupRole = RoleServiceProvider.provide().getRole(role.id);
		} else if (isNameLookup) {
			lookupRole = RoleServiceProvider.provide().getNamedRole(role.name);
		}

		if (lookupRole == null) throw new InputValidationException(ValidationError.RoleNotFound.getCode(), ValidationError.RoleNotFound.getMessage(parent));

		return lookupRole;
	}

	/**
	 * 
	 * @param permission
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Permission validatePermission(Permission permission, String parent) throws InputValidationException {
		if (permission == null)
			throw new InputValidationException(ValidationError.PermissionNull.getCode(), ValidationError.PermissionNull.getMessage(parent));

		boolean isIdLookup = false, isNameLookup = false;

		if (permission.id != null) {
			isIdLookup = true;
		} else if (permission.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isNameLookup))
			throw new InputValidationException(ValidationError.PermissionNoLookup.getCode(), ValidationError.PermissionNoLookup.getMessage(parent));

		Permission lookupPermission = null;
		if (isIdLookup) {
			lookupPermission = PermissionServiceProvider.provide().getPermission(permission.id);
		} else if (isNameLookup) {
			lookupPermission = PermissionServiceProvider.provide().getNamedPermission(permission.name);
		}

		if (lookupPermission == null)
			throw new InputValidationException(ValidationError.PermissionNotFound.getCode(), ValidationError.PermissionNotFound.getMessage(parent));

		return lookupPermission;
	}

	/**
	 * @param token
	 * @param string
	 * @return
	 */
	public static String validateToken(String token, String parent) throws InputValidationException {

		if (token == null) throw new InputValidationException(ValidationError.StringNull.getCode(), ValidationError.StringNull.getMessage(parent));

		if (!token.matches("([0-9a-f]{8})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{4})-([0-9a-f]{12})"))
			throw new InputValidationException(ValidationError.TokenNoMatch.getCode(), ValidationError.TokenNoMatch.getMessage(parent));

		return token;
	}

	/**
	 * @param session
	 * @return
	 */
	public static Session validateSession(Session session, String parent) throws InputValidationException {

		if (session == null) throw new InputValidationException(ValidationError.SessionNull.getCode(), ValidationError.SessionNull.getMessage(parent));

		boolean isIdLookup = false, isTokenLookup = false;

		if (session.id != null) {
			isIdLookup = true;
		} else if (session.token != null) {
			isTokenLookup = true;
		}

		if (!(isIdLookup || isTokenLookup))
			throw new InputValidationException(ValidationError.SessionNoLookup.getCode(), ValidationError.SessionNoLookup.getMessage(parent));

		Session lookupSession = null;
		if (isIdLookup) {
			lookupSession = SessionServiceProvider.provide().getSession(session.id);

			if (lookupSession == null)
				throw new InputValidationException(ValidationError.SessionNotFound.getCode(), ValidationError.SessionNotFound.getMessage(parent));
		} else if (isTokenLookup) {
			session.token = validateToken(session.token, parent + ".token");

			lookupSession = SessionServiceProvider.provide().getTokenSession(session.token);

			if (lookupSession == null)
				throw new InputValidationException(ValidationError.SessionNotFound.getCode(), ValidationError.SessionNotFound.getMessage(parent));
		}

		return lookupSession;
	}
}
