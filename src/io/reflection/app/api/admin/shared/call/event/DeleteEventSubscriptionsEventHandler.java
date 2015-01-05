//  
//  DeleteEventSubscriptionsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 22, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.DeleteEventSubscriptionsRequest;
import io.reflection.app.api.admin.shared.call.DeleteEventSubscriptionsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteEventSubscriptionsEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteEventSubscriptionsEventHandler> TYPE = new GwtEvent.Type<DeleteEventSubscriptionsEventHandler>();

	public void deleteEventSubscriptionsSuccess(final DeleteEventSubscriptionsRequest input, final DeleteEventSubscriptionsResponse output);

	public void deleteEventSubscriptionsFailure(final DeleteEventSubscriptionsRequest input, final Throwable caught);

	public class DeleteEventSubscriptionsSuccess extends GwtEvent<DeleteEventSubscriptionsEventHandler> {
		private DeleteEventSubscriptionsRequest input;
		private DeleteEventSubscriptionsResponse output;

		public DeleteEventSubscriptionsSuccess(final DeleteEventSubscriptionsRequest input, final DeleteEventSubscriptionsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteEventSubscriptionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteEventSubscriptionsEventHandler handler) {
			handler.deleteEventSubscriptionsSuccess(input, output);
		}
	}

	public class DeleteEventSubscriptionsFailure extends GwtEvent<DeleteEventSubscriptionsEventHandler> {
		private DeleteEventSubscriptionsRequest input;
		private Throwable caught;

		public DeleteEventSubscriptionsFailure(final DeleteEventSubscriptionsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteEventSubscriptionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteEventSubscriptionsEventHandler handler) {
			handler.deleteEventSubscriptionsFailure(input, caught);
		}
	}

}