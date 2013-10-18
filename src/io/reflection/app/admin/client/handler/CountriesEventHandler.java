//
//  CountriesEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler;

import io.reflection.app.shared.datatypes.Country;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */

public interface CountriesEventHandler extends EventHandler {
	public static final GwtEvent.Type<CountriesEventHandler> TYPE = new GwtEvent.Type<CountriesEventHandler>();

	public void receivedCountries(List<Country> countries);

	public class ReceivedCountries extends GwtEvent<CountriesEventHandler> {

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
		public com.google.gwt.event.shared.GwtEvent.Type<CountriesEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(CountriesEventHandler handler) {
			handler.receivedCountries(mCountries);

		}

	}
}