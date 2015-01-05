//  
//  SendNotificationEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.SendNotificationRequest;
import io.reflection.app.api.admin.shared.call.SendNotificationResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SendNotificationEventHandler extends EventHandler {
	public static final GwtEvent.Type<SendNotificationEventHandler> TYPE = new GwtEvent.Type<SendNotificationEventHandler>();

	public void sendNotificationSuccess(final SendNotificationRequest input, final SendNotificationResponse output);

	public void sendNotificationFailure(final SendNotificationRequest input, final Throwable caught);

	public class SendNotificationSuccess extends GwtEvent<SendNotificationEventHandler> {
		private SendNotificationRequest input;
		private SendNotificationResponse output;

		public SendNotificationSuccess(final SendNotificationRequest input, final SendNotificationResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<SendNotificationEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SendNotificationEventHandler handler) {
			handler.sendNotificationSuccess(input, output);
		}
	}

	public class SendNotificationFailure extends GwtEvent<SendNotificationEventHandler> {
		private SendNotificationRequest input;
		private Throwable caught;

		public SendNotificationFailure(final SendNotificationRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<SendNotificationEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SendNotificationEventHandler handler) {
			handler.sendNotificationFailure(input, caught);
		}
	}

}