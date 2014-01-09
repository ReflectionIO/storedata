//  
//  GetItemRanksEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetItemRanksEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetItemRanksEventHandler> TYPE = new GwtEvent.Type<GetItemRanksEventHandler>();

	public void getItemRanksSuccess(final GetItemRanksRequest input, final GetItemRanksResponse output);

	public void getItemRanksFailure(final GetItemRanksRequest input, final Throwable caught);

	public class GetItemRanksSuccess extends GwtEvent<GetItemRanksEventHandler> {
		private GetItemRanksRequest input;
		private GetItemRanksResponse output;

		public GetItemRanksSuccess(final GetItemRanksRequest input, final GetItemRanksResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetItemRanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemRanksEventHandler handler) {
			handler.getItemRanksSuccess(input, output);
		}
	}

	public class GetItemRanksFailure extends GwtEvent<GetItemRanksEventHandler> {
		private GetItemRanksRequest input;
		private Throwable caught;

		public GetItemRanksFailure(final GetItemRanksRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetItemRanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemRanksEventHandler handler) {
			handler.getItemRanksFailure(input, caught);
		}
	}

}