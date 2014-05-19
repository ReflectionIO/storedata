//  
//  GetLinkedAccountItemEventHandler.java
//  reflection.io
//
//  Created by William Shakour on May 19, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetLinkedAccountItemRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetLinkedAccountItemEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetLinkedAccountItemEventHandler> TYPE = new GwtEvent.Type<GetLinkedAccountItemEventHandler>();

	public void getLinkedAccountItemSuccess(final GetLinkedAccountItemRequest input, final GetLinkedAccountItemResponse output);

	public void getLinkedAccountItemFailure(final GetLinkedAccountItemRequest input, final Throwable caught);

	public class GetLinkedAccountItemSuccess extends GwtEvent<GetLinkedAccountItemEventHandler> {
		private GetLinkedAccountItemRequest input;
		private GetLinkedAccountItemResponse output;

		public GetLinkedAccountItemSuccess(final GetLinkedAccountItemRequest input, final GetLinkedAccountItemResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetLinkedAccountItemEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetLinkedAccountItemEventHandler handler) {
			handler.getLinkedAccountItemSuccess(input, output);
		}
	}

	public class GetLinkedAccountItemFailure extends GwtEvent<GetLinkedAccountItemEventHandler> {
		private GetLinkedAccountItemRequest input;
		private Throwable caught;

		public GetLinkedAccountItemFailure(final GetLinkedAccountItemRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetLinkedAccountItemEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetLinkedAccountItemEventHandler handler) {
			handler.getLinkedAccountItemFailure(input, caught);
		}
	}

}