//  
//  GetItemSalesRanksEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 20, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetItemSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetItemSalesRanksEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetItemSalesRanksEventHandler> TYPE = new GwtEvent.Type<GetItemSalesRanksEventHandler>();

	public void getItemSalesRanksSuccess(final GetItemSalesRanksRequest input, final GetItemSalesRanksResponse output);

	public void getItemSalesRanksFailure(final GetItemSalesRanksRequest input, final Throwable caught);

	public class GetItemSalesRanksSuccess extends GwtEvent<GetItemSalesRanksEventHandler> {
		private GetItemSalesRanksRequest input;
		private GetItemSalesRanksResponse output;

		public GetItemSalesRanksSuccess(final GetItemSalesRanksRequest input, final GetItemSalesRanksResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetItemSalesRanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemSalesRanksEventHandler handler) {
			handler.getItemSalesRanksSuccess(input, output);
		}
	}

	public class GetItemSalesRanksFailure extends GwtEvent<GetItemSalesRanksEventHandler> {
		private GetItemSalesRanksRequest input;
		private Throwable caught;

		public GetItemSalesRanksFailure(final GetItemSalesRanksRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetItemSalesRanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemSalesRanksEventHandler handler) {
			handler.getItemSalesRanksFailure(input, caught);
		}
	}

}