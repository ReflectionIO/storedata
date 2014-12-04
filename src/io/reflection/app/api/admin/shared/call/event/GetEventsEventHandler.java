//  
//  GetEventsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetEventsRequest;
import io.reflection.app.api.admin.shared.call.GetEventsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetEventsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetEventsEventHandler> TYPE = new GwtEvent.Type<GetEventsEventHandler>();

	public void getEventsSuccess(final GetEventsRequest input, final GetEventsResponse output);

	public void getEventsFailure(final GetEventsRequest input, final Throwable caught);

	public class GetEventsSuccess extends GwtEvent<GetEventsEventHandler> {
		private GetEventsRequest input;
		private GetEventsResponse output;

		public GetEventsSuccess(final GetEventsRequest input, final GetEventsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetEventsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetEventsEventHandler handler) {
			handler.getEventsSuccess(input, output);
		}
	}

	public class GetEventsFailure extends GwtEvent<GetEventsEventHandler> {
		private GetEventsRequest input;
		private Throwable caught;

		public GetEventsFailure(final GetEventsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetEventsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetEventsEventHandler handler) {
			handler.getEventsFailure(input, caught);
		}
	}

}