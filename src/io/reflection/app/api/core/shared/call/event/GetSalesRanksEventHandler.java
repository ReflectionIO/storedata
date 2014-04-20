//  
//  GetSalesRanksEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 20, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetSalesRanksEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetSalesRanksEventHandler> TYPE = new GwtEvent.Type<GetSalesRanksEventHandler>();

	public void getSalesRanksSuccess(final GetSalesRanksRequest input, final GetSalesRanksResponse output);

	public void getSalesRanksFailure(final GetSalesRanksRequest input, final Throwable caught);

	public class GetSalesRanksSuccess extends GwtEvent<GetSalesRanksEventHandler> {
		private GetSalesRanksRequest input;
		private GetSalesRanksResponse output;

		public GetSalesRanksSuccess(final GetSalesRanksRequest input, final GetSalesRanksResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetSalesRanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetSalesRanksEventHandler handler) {
			handler.getSalesRanksSuccess(input, output);
		}
	}

	public class GetSalesRanksFailure extends GwtEvent<GetSalesRanksEventHandler> {
		private GetSalesRanksRequest input;
		private Throwable caught;

		public GetSalesRanksFailure(final GetSalesRanksRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetSalesRanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetSalesRanksEventHandler handler) {
			handler.getSalesRanksFailure(input, caught);
		}
	}

}