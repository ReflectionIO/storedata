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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.handler.CountriesEventHandler;
import io.reflection.app.datatypes.shared.Country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class CountryController implements ServiceController {
	private static CountryController mOne = null;

	private Map<String, Country> mCountryLookup = null;

	public static CountryController get() {
		if (mOne == null) {
			mOne = new CountryController();
		}

		return mOne;
	}

	
	public CountryController() {
		mCountryLookup = new HashMap<String, Country>();
		
		addCountry("us", "United States");
		addCountry("gb", "United Kingdom");
		addCountry("cn", "China");
		addCountry("de", "Germany");
		addCountry("fr", "France");
	}
	
	private void addCountry(String a2Code, String name) {
		Country country = new Country();
		country.a2Code = a2Code;
		country.name = name;
		
		country.stores = new ArrayList<String>();
		country.stores.add("ios");
		
		mCountryLookup.put(country.a2Code, country);
	}
	
	public void fetchAllCountries() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetCountriesRequest input = new GetCountriesRequest();
		input.accessCode = ACCESS_CODE;

		input.pager = new Pager();
		input.pager.count = STEP;

		input.query = "*";

		service.getCountries(input, new AsyncCallback<GetCountriesResponse>() {

			@Override
			public void onSuccess(GetCountriesResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.countries != null && result.countries.size() > 0) {

						if (mCountryLookup == null) {
							mCountryLookup = new HashMap<String, Country>();
						}

						for (Country country : result.countries) {
							mCountryLookup.put(country.a2Code, country);
						}

						EventController.get().fireEventFromSource(new CountriesEventHandler.ReceivedCountries(result.countries), CountryController.this);
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");

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
	public Image getCountryFlat(String a2Code) {
		return new Image();
	}
}
