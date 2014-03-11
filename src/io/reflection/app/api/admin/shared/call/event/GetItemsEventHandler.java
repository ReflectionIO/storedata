//  
//  GetItemsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 11, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetItemsRequest;
import io.reflection.app.api.admin.shared.call.GetItemsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetItemsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetItemsEventHandler> TYPE = new GwtEvent.Type<GetItemsEventHandler>();

	public void getItemsSuccess(final GetItemsRequest input, final GetItemsResponse output);

	public void getItemsFailure(final GetItemsRequest input, final Throwable caught);

	public class GetItemsSuccess extends GwtEvent<GetItemsEventHandler> {
		private GetItemsRequest input;
		private GetItemsResponse output;

		public GetItemsSuccess(final GetItemsRequest input, final GetItemsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemsEventHandler handler) {
			handler.getItemsSuccess(input, output);
		}
	}

	public class GetItemsFailure extends GwtEvent<GetItemsEventHandler> {
		private GetItemsRequest input;
		private Throwable caught;

		public GetItemsFailure(final GetItemsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetItemsEventHandler handler) {
			handler.getItemsFailure(input, caught);
		}
	}

}