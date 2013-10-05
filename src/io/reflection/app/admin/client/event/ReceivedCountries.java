//
//  ReceivedCountries.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event;

import io.reflection.app.admin.client.event.handler.ReceivedCountriesEventHandler;
import io.reflection.app.shared.datatypes.Country;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public class ReceivedCountries extends GwtEvent<ReceivedCountriesEventHandler> {

	public static Type<ReceivedCountriesEventHandler> TYPE = new Type<ReceivedCountriesEventHandler>();

	private List<Country> mCountries;

	public ReceivedCountries(List<Country> countries) {
		mCountries = countries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ReceivedCountriesEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ReceivedCountriesEventHandler handler) {
		handler.receivedCountries(mCountries);

	}

}
