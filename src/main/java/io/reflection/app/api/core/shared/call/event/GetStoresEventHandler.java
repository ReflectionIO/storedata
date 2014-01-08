//  
//  GetStoresEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetStoresEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetStoresEventHandler> TYPE = new GwtEvent.Type<GetStoresEventHandler>();

	public void getStoresSuccess(final GetStoresRequest input, final GetStoresResponse output);

	public void getStoresFailure(final GetStoresRequest input, final Throwable caught);

	public class GetStoresSuccess extends GwtEvent<GetStoresEventHandler> {
		private GetStoresRequest input;
		private GetStoresResponse output;

		public GetStoresSuccess(final GetStoresRequest input, final GetStoresResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetStoresEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetStoresEventHandler handler) {
			handler.getStoresSuccess(input, output);
		}
	}

	public class GetStoresFailure extends GwtEvent<GetStoresEventHandler> {
		private GetStoresRequest input;
		private Throwable caught;

		public GetStoresFailure(final GetStoresRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetStoresEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetStoresEventHandler handler) {
			handler.getStoresFailure(input, caught);
		}
	}

}