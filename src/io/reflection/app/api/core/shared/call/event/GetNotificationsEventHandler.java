//  
//  GetNotificationsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetNotificationsRequest;
import io.reflection.app.api.core.shared.call.GetNotificationsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetNotificationsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetNotificationsEventHandler> TYPE = new GwtEvent.Type<GetNotificationsEventHandler>();

	public void getNotificationsSuccess(final GetNotificationsRequest input, final GetNotificationsResponse output);

	public void getNotificationsFailure(final GetNotificationsRequest input, final Throwable caught);

	public class GetNotificationsSuccess extends GwtEvent<GetNotificationsEventHandler> {
		private GetNotificationsRequest input;
		private GetNotificationsResponse output;

		public GetNotificationsSuccess(final GetNotificationsRequest input, final GetNotificationsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetNotificationsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetNotificationsEventHandler handler) {
			handler.getNotificationsSuccess(input, output);
		}
	}

	public class GetNotificationsFailure extends GwtEvent<GetNotificationsEventHandler> {
		private GetNotificationsRequest input;
		private Throwable caught;

		public GetNotificationsFailure(final GetNotificationsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetNotificationsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetNotificationsEventHandler handler) {
			handler.getNotificationsFailure(input, caught);
		}
	}

}