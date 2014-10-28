//
//  CountryController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler.GetCountriesFailure;
import io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler.GetCountriesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.res.flags.Styles;
import io.reflection.app.datatypes.shared.Country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class CountryController implements ServiceConstants {
	private static CountryController mOne = null;

	private Map<String, Country> mCountryLookup = null;
	private List<Country> countries = null;

	public static CountryController get() {
		if (mOne == null) {
			mOne = new CountryController();
		}

		return mOne;
	}

	// private void addCountry(String a2Code, String name) {
	// Country country = new Country();
	// country.a2Code = a2Code;
	// country.name = name;
	//
	// country.stores = new ArrayList<String>();
	// country.stores.add(DataTypeHelper.IOS_STORE_A3);
	//
	// mCountryLookup.put(country.a2Code, country);
	// }

	private void fetchAllCountries() {
		CoreService service = ServiceCreator.createCoreService();

		final GetCountriesRequest input = new GetCountriesRequest();
		input.accessCode = ACCESS_CODE;

		input.pager = new Pager();
		input.pager.count = STEP;

		input.query = "*";

		service.getCountries(input, new AsyncCallback<GetCountriesResponse>() {

			@Override
			public void onSuccess(GetCountriesResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.countries != null && output.countries.size() > 0) {

						if (mCountryLookup == null) {
							mCountryLookup = new HashMap<String, Country>();
						}

						for (Country country : output.countries) {
							mCountryLookup.put(country.a2Code, country);
						}
						
						countries = output.countries;
					}
				}

				EventController.get().fireEventFromSource(new GetCountriesSuccess(input, output), CountryController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetCountriesFailure(input, caught), CountryController.this);
			}
		});
	}

	/**
	 * @param mCountry
	 * @return
	 */
	public Country getCountry(String code) {
		Country country = null;
		if (code != null && (mCountryLookup == null || (country = mCountryLookup.get(code)) == null)) {
			country = new Country();
			country.a2Code = code;
		}

		return country;
	}

	/**
	 * @param a2Code
	 * @return
	 */
	public String getCountryFlag(String a2Code) {
		String styleName = null;

		if ("us".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().us();
		} else if ("gb".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().gb();
		} else if ("cn".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().cn();
		} else if ("de".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().de();
		} else if ("fr".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().fr();
		} else if ("jp".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().jp();
		} else if ("it".equals(a2Code)) {
			styleName = Styles.INSTANCE.flags().it();
		}

		return styleName;
	}

	/**
	 * @return
	 */
	public boolean prefetchCountries() {
		boolean attemptPrefetch = (countries == null || countries.size() == 0);
		if (attemptPrefetch) {
			fetchAllCountries();
		}

		return attemptPrefetch;
	}

	public List<Country> getCountries() {
		// only return first 7 items (for now)
		return countries.subList(0, 7);
	}
}
