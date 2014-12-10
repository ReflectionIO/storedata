//  
//  DeleteNotificationsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.DeleteNotificationsRequest;
import io.reflection.app.api.core.shared.call.DeleteNotificationsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteNotificationsEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteNotificationsEventHandler> TYPE = new GwtEvent.Type<DeleteNotificationsEventHandler>();

	public void deleteNotificationsSuccess(final DeleteNotificationsRequest input, final DeleteNotificationsResponse output);

	public void deleteNotificationsFailure(final DeleteNotificationsRequest input, final Throwable caught);

	public class DeleteNotificationsSuccess extends GwtEvent<DeleteNotificationsEventHandler> {
		private DeleteNotificationsRequest input;
		private DeleteNotificationsResponse output;

		public DeleteNotificationsSuccess(final DeleteNotificationsRequest input, final DeleteNotificationsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteNotificationsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteNotificationsEventHandler handler) {
			handler.deleteNotificationsSuccess(input, output);
		}
	}

	public class DeleteNotificationsFailure extends GwtEvent<DeleteNotificationsEventHandler> {
		private DeleteNotificationsRequest input;
		private Throwable caught;

		public DeleteNotificationsFailure(final DeleteNotificationsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteNotificationsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteNotificationsEventHandler handler) {
			handler.deleteNotificationsFailure(input, caught);
		}
	}

}