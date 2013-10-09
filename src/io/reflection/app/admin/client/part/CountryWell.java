//
//  CountryWell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import io.reflection.app.admin.client.controller.CountryController;
import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.event.ReceivedCountries;
import io.reflection.app.admin.client.event.ReceivedCountries.Handler;
import io.reflection.app.shared.datatypes.Country;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class CountryWell extends Composite implements Handler {

	private static CountryWellUiBinder uiBinder = GWT.create(CountryWellUiBinder.class);

	interface CountryWellUiBinder extends UiBinder<Widget, CountryWell> {}

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public CountryWell() {
		initWidget(uiBinder.createAndBindUi(this));

		EventController.get().addHandlerToSource(ReceivedCountries.TYPE, CountryController.get(), this);

		CountryController.get().getAllCountries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.event.handler.ReceivedCountriesEventHandler#receivedCountries(java.util.List)
	 */
	@Override
	public void receivedCountries(List<Country> country) {

	}

}
