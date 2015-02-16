//  
//  UpdateEventEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.UpdateEventRequest;
import io.reflection.app.api.admin.shared.call.UpdateEventResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdateEventEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdateEventEventHandler> TYPE = new GwtEvent.Type<UpdateEventEventHandler>();

	public void updateEventSuccess(final UpdateEventRequest input, final UpdateEventResponse output);

	public void updateEventFailure(final UpdateEventRequest input, final Throwable caught);

	public class UpdateEventSuccess extends GwtEvent<UpdateEventEventHandler> {
		private UpdateEventRequest input;
		private UpdateEventResponse output;

		public UpdateEventSuccess(final UpdateEventRequest input, final UpdateEventResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdateEventEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateEventEventHandler handler) {
			handler.updateEventSuccess(input, output);
		}
	}

	public class UpdateEventFailure extends GwtEvent<UpdateEventEventHandler> {
		private UpdateEventRequest input;
		private Throwable caught;

		public UpdateEventFailure(final UpdateEventRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdateEventEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateEventEventHandler handler) {
			handler.updateEventFailure(input, caught);
		}
	}

}