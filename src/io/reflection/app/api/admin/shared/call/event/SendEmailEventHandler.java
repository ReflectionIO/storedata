//  
//  SendEmailEventHandler.java
//  reflection.io
//
//  Created by William Shakour on February 20, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.SendEmailRequest;
import io.reflection.app.api.admin.shared.call.SendEmailResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SendEmailEventHandler extends EventHandler {
	public static final GwtEvent.Type<SendEmailEventHandler> TYPE = new GwtEvent.Type<SendEmailEventHandler>();

	public void sendEmailSuccess(final SendEmailRequest input, final SendEmailResponse output);

	public void sendEmailFailure(final SendEmailRequest input, final Throwable caught);

	public class SendEmailSuccess extends GwtEvent<SendEmailEventHandler> {
		private SendEmailRequest input;
		private SendEmailResponse output;

		public SendEmailSuccess(final SendEmailRequest input, final SendEmailResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<SendEmailEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SendEmailEventHandler handler) {
			handler.sendEmailSuccess(input, output);
		}
	}

	public class SendEmailFailure extends GwtEvent<SendEmailEventHandler> {
		private SendEmailRequest input;
		private Throwable caught;

		public SendEmailFailure(final SendEmailRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<SendEmailEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SendEmailEventHandler handler) {
			handler.sendEmailFailure(input, caught);
		}
	}

}