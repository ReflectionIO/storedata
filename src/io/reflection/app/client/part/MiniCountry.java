//
//  MiniCountry.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.datatypes.shared.Country;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class MiniCountry extends Composite {

	private static MiniCountryUiBinder uiBinder = GWT.create(MiniCountryUiBinder.class);

	interface MiniCountryUiBinder extends UiBinder<Widget, MiniCountry> {}

	private Country mCountry;

	@UiField InlineHyperlink mName;

	public MiniCountry() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * @param store
	 */
	public MiniCountry(Country country) {
		this();

		mCountry = country;

		mName.setText(mCountry.name);
		// FIXME: this is not going to work... modally or not
		mName.setTargetHistoryToken(NavigationController.get().getCurrentPage().asTargetHistoryToken(mCountry.a2Code));
	}

}
