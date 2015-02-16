//  
//  UpdateNotificationsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.UpdateNotificationsRequest;
import io.reflection.app.api.core.shared.call.UpdateNotificationsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdateNotificationsEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdateNotificationsEventHandler> TYPE = new GwtEvent.Type<UpdateNotificationsEventHandler>();

	public void updateNotificationsSuccess(final UpdateNotificationsRequest input, final UpdateNotificationsResponse output);

	public void updateNotificationsFailure(final UpdateNotificationsRequest input, final Throwable caught);

	public class UpdateNotificationsSuccess extends GwtEvent<UpdateNotificationsEventHandler> {
		private UpdateNotificationsRequest input;
		private UpdateNotificationsResponse output;

		public UpdateNotificationsSuccess(final UpdateNotificationsRequest input, final UpdateNotificationsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdateNotificationsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateNotificationsEventHandler handler) {
			handler.updateNotificationsSuccess(input, output);
		}
	}

	public class UpdateNotificationsFailure extends GwtEvent<UpdateNotificationsEventHandler> {
		private UpdateNotificationsRequest input;
		private Throwable caught;

		public UpdateNotificationsFailure(final UpdateNotificationsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdateNotificationsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateNotificationsEventHandler handler) {
			handler.updateNotificationsFailure(input, caught);
		}
	}

}