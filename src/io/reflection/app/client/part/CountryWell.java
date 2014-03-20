//
//  CountryWell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.CountryController;
import io.reflection.app.datatypes.shared.Country;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class CountryWell extends Composite {

	private static CountryWellUiBinder uiBinder = GWT.create(CountryWellUiBinder.class);

	interface CountryWellUiBinder extends UiBinder<Widget, CountryWell> {}

	@UiField Hyperlink mMore;

	@UiField HTMLPanel mMainCountries;
	@UiField HTMLPanel mMoreCountries;

	public CountryWell() {
		initWidget(uiBinder.createAndBindUi(this));

		List<Country> countries = CountryController.get().getCountries();

		if (countries != null && countries.size() > 0) {
			mMainCountries.clear();

			int i = 0;
			HTMLPanel row = null;
			for (Country country : countries) {
				if (i++ % 4 == 0) {
					row = new HTMLPanel("");
					row.setStyleName("row");
					mMainCountries.add(row);
				}

				row.add(new MiniCountry(country));
			}
		}
	}

	@UiHandler("mMore")
	public void onMoreClicked(ClickEvent event) {
		// NOTE: we do not have enough countries for this yet
	}
}
