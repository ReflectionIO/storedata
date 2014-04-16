//  
//  GetItemSalesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetItemSalesRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetItemSalesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetItemSalesEventHandler> TYPE = new GwtEvent.Type<GetItemSalesEventHandler>();

	public void getItemSalesSuccess(final GetItemSalesRequest input, final GetItemSalesResponse output);

	public void getItemSalesFailure(final GetItemSalesRequest input, final Throwable caught);

	public class GetItemSalesSuccess extends GwtEvent<GetItemSalesEventHandler> {
		private GetItemSalesRequest input;
		private GetItemSalesResponse output;

		public GetItemSalesSuccess(final GetItemSalesRequest input, final GetItemSalesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetItemSalesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemSalesEventHandler handler) {
			handler.getItemSalesSuccess(input, output);
		}
	}

	public class GetItemSalesFailure extends GwtEvent<GetItemSalesEventHandler> {
		private GetItemSalesRequest input;
		private Throwable caught;

		public GetItemSalesFailure(final GetItemSalesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetItemSalesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemSalesEventHandler handler) {
			handler.getItemSalesFailure(input, caught);
		}
	}

}