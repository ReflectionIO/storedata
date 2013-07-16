//
//  ValidationHelper.java
//  storedata
//
//  Created by William Shakour on 5 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.input;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.api.datatypes.Pager;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Item;
import io.reflection.app.datatypes.Store;

import java.util.List;

import com.willshex.gson.json.service.InputValidationException;

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

	/**
	 * 
	 * @param store
	 * @param parent
	 * @return
	 * @throws InputValidationException
	 */
	public static Store validateStore(Store store, String parent) throws InputValidationException {
		if (store == null)
			throw new InputValidationException(ValidationError.StoreNull.getCode(), ValidationError.StoreNull.getMessage(parent));

		boolean isIdLookup = false, isA3CodeLookup = false, isNameLookup = false;

		if (store.id != null) {
			isIdLookup = true;
		} else if (store.a3Code != null) {
			isA3CodeLookup = true;
		} else if (store.name != null) {
			isNameLookup = true;
		}

		if (!(isIdLookup || isA3CodeLookup || isNameLookup)) {
			throw new InputValidationException(ValidationError.StoreNoLookup.getCode(), ValidationError.StoreNoLookup.getMessage(parent));
		}

		Store lookupStore = null;
		if (isIdLookup) {
			lookupStore = ofy().load().type(Store.class).id(store.id).now();
		} else if (isA3CodeLookup) {
			List<Store> found = ofy().load().type(Store.class).filter("a3Code", store.a3Code).limit(1).list();

			if (found != null && found.size() > 0) {
				lookupStore = found.get(0);
			}
		} else if (isNameLookup) {
			List<Store> found = ofy().load().type(Store.class).filter("name", store.name).limit(1).list();

			if (found != null && found.size() > 0) {
				lookupStore = found.get(0);
			}
		}

		if (lookupStore == null)
			throw new InputValidationException(ValidationError.StoreNotFound.getCode(), ValidationError.StoreNotFound.getMessage(parent));

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
		if (query == null)
			throw new InputValidationException(ValidationError.SearchQueryNull.getCode(), ValidationError.SearchQueryNull.getMessage(parent));

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

		if (pager.start == null)
			pager.start = Long.valueOf(0);

		if (pager.start < 0)
			throw new InputValidationException(ValidationError.PagerStartNegative.getCode(), ValidationError.PagerStartNegative.getMessage(parent));

		if (pager.count == null)
			pager.count = Long.valueOf(0);

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

		if (country == null)
			throw new InputValidationException(ValidationError.CountryNull.getCode(), ValidationError.CountryNull.getMessage(parent));

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
			lookupCountry = ofy().load().type(Country.class).id(country.id).now();
		} else if (isA2CodeLookup) {
			List<Country> found = ofy().load().type(Country.class).filter("a2Code", country.a2Code).limit(1).list();

			if (found != null && found.size() > 0) {
				lookupCountry = found.get(0);
			}
		} else if (isNameLookup) {
			List<Country> found = ofy().load().type(Country.class).filter("name", country.name).limit(1).list();

			if (found != null && found.size() > 0) {
				lookupCountry = found.get(0);
			}
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
		if (item == null)
			throw new InputValidationException(ValidationError.ItemNull.getCode(), ValidationError.ItemNull.getMessage(parent));

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
			lookupItem = ofy().load().type(Item.class).id(item.id).now();
		} else if (isExtIdLookup) {
			List<Item> found = ofy().load().type(Item.class).filter("externalId", item.externalId).limit(1).list();

			if (found != null && found.size() > 0) {
				lookupItem = found.get(0);
			}
		} else if (isIntIdLookup) {
			List<Item> found = ofy().load().type(Item.class).filter("internalId", item.internalId).limit(1).list();

			if (found != null && found.size() > 0) {
				lookupItem = found.get(0);
			}
		}

		if (lookupItem == null)
			throw new InputValidationException(ValidationError.ItemNotFound.getCode(), ValidationError.ItemNotFound.getMessage(parent));

		return lookupItem;
	}
}
