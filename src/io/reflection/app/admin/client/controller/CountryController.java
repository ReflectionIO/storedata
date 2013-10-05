//
//  CountryController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.event.ReceivedCountries;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class CountryController {
	private static CountryController mOne = null;

	public static CountryController get() {
		if (mOne == null) {
			mOne = new CountryController();
		}

		return mOne;
	}

	public void getAllCountries() {
		CoreService service = new CoreService();
		service.setUrl(ControllerHelper.CORE_END_POINT);

		final GetCountriesRequest input = new GetCountriesRequest();
		input.accessCode = ControllerHelper.ACCESS_CODE;

		service.getCountries(input, new AsyncCallback<GetCountriesResponse>() {

			@Override
			public void onSuccess(GetCountriesResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.countries != null && result.countries.size() > 0) {
						EventController.get().fireEventFromSource(new ReceivedCountries(result.countries), CountryController.this);
					}
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");

			}
		});
	}
}
