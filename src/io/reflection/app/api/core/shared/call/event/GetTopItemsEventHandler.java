//  
//  GetTopItemsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetTopItemsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetTopItemsEventHandler> TYPE = new GwtEvent.Type<GetTopItemsEventHandler>();

	public void getTopItemsSuccess(final GetTopItemsRequest input, final GetTopItemsResponse output);

	public void getTopItemsFailure(final GetTopItemsRequest input, final Throwable caught);

	public class GetTopItemsSuccess extends GwtEvent<GetTopItemsEventHandler> {
		private GetTopItemsRequest input;
		private GetTopItemsResponse output;

		public GetTopItemsSuccess(final GetTopItemsRequest input, final GetTopItemsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetTopItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetTopItemsEventHandler handler) {
			handler.getTopItemsSuccess(input, output);
		}
	}

	public class GetTopItemsFailure extends GwtEvent<GetTopItemsEventHandler> {
		private GetTopItemsRequest input;
		private Throwable caught;

		public GetTopItemsFailure(final GetTopItemsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetTopItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetTopItemsEventHandler handler) {
			handler.getTopItemsFailure(input, caught);
		}
	}

}