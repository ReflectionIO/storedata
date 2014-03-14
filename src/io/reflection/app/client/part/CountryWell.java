//
//  CountryWell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler;
import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.datatypes.shared.Country;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class CountryWell extends Composite implements GetCountriesEventHandler {

	private static CountryWellUiBinder uiBinder = GWT.create(CountryWellUiBinder.class);

	interface CountryWellUiBinder extends UiBinder<Widget, CountryWell> {}

	@UiField Hyperlink mMore;
	@UiField HTMLPanel mMainCountries;
	@UiField HTMLPanel mMoreCountries;

	public CountryWell() {
		initWidget(uiBinder.createAndBindUi(this));

		EventController.get().addHandlerToSource(GetCountriesEventHandler.TYPE, CountryController.get(), this);

		CountryController.get().fetchAllCountries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler#getCountriesSuccess(io.reflection.app.api.core.shared.call.GetCountriesRequest,
	 * io.reflection.app.api.core.shared.call.GetCountriesResponse)
	 */
	@Override
	public void getCountriesSuccess(GetCountriesRequest input, GetCountriesResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.countries != null) {
			mMainCountries.clear();

			int i = 0;
			HTMLPanel row = null;
			for (Country country : output.countries) {
				if (i++ % 4 == 0) {
					row = new HTMLPanel("");
					row.setStyleName("row");
					mMainCountries.add(row);
				}

				row.add(new MiniCountry(country));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler#getCountriesFailure(io.reflection.app.api.core.shared.call.GetCountriesRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getCountriesFailure(GetCountriesRequest input, Throwable caught) {}

}
