//  
//  GetEventSubscriptionsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 22, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetEventSubscriptionsRequest;
import io.reflection.app.api.admin.shared.call.GetEventSubscriptionsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetEventSubscriptionsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetEventSubscriptionsEventHandler> TYPE = new GwtEvent.Type<GetEventSubscriptionsEventHandler>();

	public void getEventSubscriptionsSuccess(final GetEventSubscriptionsRequest input, final GetEventSubscriptionsResponse output);

	public void getEventSubscriptionsFailure(final GetEventSubscriptionsRequest input, final Throwable caught);

	public class GetEventSubscriptionsSuccess extends GwtEvent<GetEventSubscriptionsEventHandler> {
		private GetEventSubscriptionsRequest input;
		private GetEventSubscriptionsResponse output;

		public GetEventSubscriptionsSuccess(final GetEventSubscriptionsRequest input, final GetEventSubscriptionsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetEventSubscriptionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetEventSubscriptionsEventHandler handler) {
			handler.getEventSubscriptionsSuccess(input, output);
		}
	}

	public class GetEventSubscriptionsFailure extends GwtEvent<GetEventSubscriptionsEventHandler> {
		private GetEventSubscriptionsRequest input;
		private Throwable caught;

		public GetEventSubscriptionsFailure(final GetEventSubscriptionsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetEventSubscriptionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetEventSubscriptionsEventHandler handler) {
			handler.getEventSubscriptionsFailure(input, caught);
		}
	}

}