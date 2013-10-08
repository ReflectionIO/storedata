//
//  ValidationHelper.java
//  storedata
//
//  Created by William Shakour on 5 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.input;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Item;
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
			throw new InputValidationException(ValidationError.AccessCodeNoMatch.getCode(), ValidationError.AccessCodeNoMatch.getMessage(parent));

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

		if (pager.count.intValue() > 30)
			throw new InputValidationException(ValidationError.PagerCountTooLarge.getCode(), ValidationError.PagerCountTooLarge.getMessage(parent));

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
}
