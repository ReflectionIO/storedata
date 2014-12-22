//  
//  AddEventSubscriptionEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 22, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.AddEventSubscriptionRequest;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AddEventSubscriptionEventHandler extends EventHandler {
	public static final GwtEvent.Type<AddEventSubscriptionEventHandler> TYPE = new GwtEvent.Type<AddEventSubscriptionEventHandler>();

	public void addEventSubscriptionSuccess(final AddEventSubscriptionRequest input, final AddEventSubscriptionRequest output);

	public void addEventSubscriptionFailure(final AddEventSubscriptionRequest input, final Throwable caught);

	public class AddEventSubscriptionSuccess extends GwtEvent<AddEventSubscriptionEventHandler> {
		private AddEventSubscriptionRequest input;
		private AddEventSubscriptionRequest output;

		public AddEventSubscriptionSuccess(final AddEventSubscriptionRequest input, final AddEventSubscriptionRequest output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<AddEventSubscriptionEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AddEventSubscriptionEventHandler handler) {
			handler.addEventSubscriptionSuccess(input, output);
		}
	}

	public class AddEventSubscriptionFailure extends GwtEvent<AddEventSubscriptionEventHandler> {
		private AddEventSubscriptionRequest input;
		private Throwable caught;

		public AddEventSubscriptionFailure(final AddEventSubscriptionRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<AddEventSubscriptionEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AddEventSubscriptionEventHandler handler) {
			handler.addEventSubscriptionFailure(input, caught);
		}
	}

}