//  
//  GetSalesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetSalesRequest;
import io.reflection.app.api.core.shared.call.GetSalesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetSalesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetSalesEventHandler> TYPE = new GwtEvent.Type<GetSalesEventHandler>();

	public void getSalesSuccess(final GetSalesRequest input, final GetSalesResponse output);

	public void getSalesFailure(final GetSalesRequest input, final Throwable caught);

	public class GetSalesSuccess extends GwtEvent<GetSalesEventHandler> {
		private GetSalesRequest input;
		private GetSalesResponse output;

		public GetSalesSuccess(final GetSalesRequest input, final GetSalesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetSalesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetSalesEventHandler handler) {
			handler.getSalesSuccess(input, output);
		}
	}

	public class GetSalesFailure extends GwtEvent<GetSalesEventHandler> {
		private GetSalesRequest input;
		private Throwable caught;

		public GetSalesFailure(final GetSalesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetSalesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetSalesEventHandler handler) {
			handler.getSalesFailure(input, caught);
		}
	}

}