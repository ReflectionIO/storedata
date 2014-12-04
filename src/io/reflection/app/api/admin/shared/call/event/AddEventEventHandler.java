//  
//  AddEventEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.AddEventRequest;
import io.reflection.app.api.admin.shared.call.AddEventResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AddEventEventHandler extends EventHandler {
	public static final GwtEvent.Type<AddEventEventHandler> TYPE = new GwtEvent.Type<AddEventEventHandler>();

	public void addEventSuccess(final AddEventRequest input, final AddEventResponse output);

	public void addEventFailure(final AddEventRequest input, final Throwable caught);

	public class AddEventSuccess extends GwtEvent<AddEventEventHandler> {
		private AddEventRequest input;
		private AddEventResponse output;

		public AddEventSuccess(final AddEventRequest input, final AddEventResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<AddEventEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AddEventEventHandler handler) {
			handler.addEventSuccess(input, output);
		}
	}

	public class AddEventFailure extends GwtEvent<AddEventEventHandler> {
		private AddEventRequest input;
		private Throwable caught;

		public AddEventFailure(final AddEventRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<AddEventEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AddEventEventHandler handler) {
			handler.addEventFailure(input, caught);
		}
	}

}