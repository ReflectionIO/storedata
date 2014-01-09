//  
//  GetCountriesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetCountriesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetCountriesEventHandler> TYPE = new GwtEvent.Type<GetCountriesEventHandler>();

	public void getCountriesSuccess(final GetCountriesRequest input, final GetCountriesResponse output);

	public void getCountriesFailure(final GetCountriesRequest input, final Throwable caught);

	public class GetCountriesSuccess extends GwtEvent<GetCountriesEventHandler> {
		private GetCountriesRequest input;
		private GetCountriesResponse output;

		public GetCountriesSuccess(final GetCountriesRequest input, final GetCountriesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetCountriesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetCountriesEventHandler handler) {
			handler.getCountriesSuccess(input, output);
		}
	}

	public class GetCountriesFailure extends GwtEvent<GetCountriesEventHandler> {
		private GetCountriesRequest input;
		private Throwable caught;

		public GetCountriesFailure(final GetCountriesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetCountriesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetCountriesEventHandler handler) {
			handler.getCountriesFailure(input, caught);
		}
	}

}