//  
//  GetLinkedAccountItemsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetLinkedAccountItemsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetLinkedAccountItemsEventHandler> TYPE = new GwtEvent.Type<GetLinkedAccountItemsEventHandler>();

	public void getLinkedAccountItemsSuccess(final GetLinkedAccountItemsRequest input, final GetLinkedAccountItemsResponse output);

	public void getLinkedAccountItemsFailure(final GetLinkedAccountItemsRequest input, final Throwable caught);

	public class GetLinkedAccountItemsSuccess extends GwtEvent<GetLinkedAccountItemsEventHandler> {
		private GetLinkedAccountItemsRequest input;
		private GetLinkedAccountItemsResponse output;

		public GetLinkedAccountItemsSuccess(final GetLinkedAccountItemsRequest input, final GetLinkedAccountItemsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetLinkedAccountItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetLinkedAccountItemsEventHandler handler) {
			handler.getLinkedAccountItemsSuccess(input, output);
		}
	}

	public class GetLinkedAccountItemsFailure extends GwtEvent<GetLinkedAccountItemsEventHandler> {
		private GetLinkedAccountItemsRequest input;
		private Throwable caught;

		public GetLinkedAccountItemsFailure(final GetLinkedAccountItemsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetLinkedAccountItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetLinkedAccountItemsEventHandler handler) {
			handler.getLinkedAccountItemsFailure(input, caught);
		}
	}

}