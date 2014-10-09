//  
//  GetDataAccountsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 7, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetDataAccountsRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetDataAccountsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetDataAccountsEventHandler> TYPE = new GwtEvent.Type<GetDataAccountsEventHandler>();

	public void getDataAccountsSuccess(final GetDataAccountsRequest input, final GetDataAccountsResponse output);

	public void getDataAccountsFailure(final GetDataAccountsRequest input, final Throwable caught);

	public class GetDataAccountsSuccess extends GwtEvent<GetDataAccountsEventHandler> {
		private GetDataAccountsRequest input;
		private GetDataAccountsResponse output;

		public GetDataAccountsSuccess(final GetDataAccountsRequest input, final GetDataAccountsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetDataAccountsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetDataAccountsEventHandler handler) {
			handler.getDataAccountsSuccess(input, output);
		}
	}

	public class GetDataAccountsFailure extends GwtEvent<GetDataAccountsEventHandler> {
		private GetDataAccountsRequest input;
		private Throwable caught;

		public GetDataAccountsFailure(final GetDataAccountsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetDataAccountsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetDataAccountsEventHandler handler) {
			handler.getDataAccountsFailure(input, caught);
		}
	}

}