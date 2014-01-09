//  
//  GetLinkedAccountsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetLinkedAccountsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetLinkedAccountsEventHandler> TYPE = new GwtEvent.Type<GetLinkedAccountsEventHandler>();

	public void getLinkedAccountsSuccess(final GetLinkedAccountsRequest input, final GetLinkedAccountsResponse output);

	public void getLinkedAccountsFailure(final GetLinkedAccountsRequest input, final Throwable caught);

	public class GetLinkedAccountsSuccess extends GwtEvent<GetLinkedAccountsEventHandler> {
		private GetLinkedAccountsRequest input;
		private GetLinkedAccountsResponse output;

		public GetLinkedAccountsSuccess(final GetLinkedAccountsRequest input, final GetLinkedAccountsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetLinkedAccountsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetLinkedAccountsEventHandler handler) {
			handler.getLinkedAccountsSuccess(input, output);
		}
	}

	public class GetLinkedAccountsFailure extends GwtEvent<GetLinkedAccountsEventHandler> {
		private GetLinkedAccountsRequest input;
		private Throwable caught;

		public GetLinkedAccountsFailure(final GetLinkedAccountsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetLinkedAccountsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetLinkedAccountsEventHandler handler) {
			handler.getLinkedAccountsFailure(input, caught);
		}
	}

}