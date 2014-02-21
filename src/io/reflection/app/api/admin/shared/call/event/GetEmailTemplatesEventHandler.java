//  
//  GetEmailTemplatesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on February 20, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetEmailTemplatesRequest;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetEmailTemplatesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetEmailTemplatesEventHandler> TYPE = new GwtEvent.Type<GetEmailTemplatesEventHandler>();

	public void getEmailTemplatesSuccess(final GetEmailTemplatesRequest input, final GetEmailTemplatesResponse output);

	public void getEmailTemplatesFailure(final GetEmailTemplatesRequest input, final Throwable caught);

	public class GetEmailTemplatesSuccess extends GwtEvent<GetEmailTemplatesEventHandler> {
		private GetEmailTemplatesRequest input;
		private GetEmailTemplatesResponse output;

		public GetEmailTemplatesSuccess(final GetEmailTemplatesRequest input, final GetEmailTemplatesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetEmailTemplatesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetEmailTemplatesEventHandler handler) {
			handler.getEmailTemplatesSuccess(input, output);
		}
	}

	public class GetEmailTemplatesFailure extends GwtEvent<GetEmailTemplatesEventHandler> {
		private GetEmailTemplatesRequest input;
		private Throwable caught;

		public GetEmailTemplatesFailure(final GetEmailTemplatesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetEmailTemplatesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetEmailTemplatesEventHandler handler) {
			handler.getEmailTemplatesFailure(input, caught);
		}
	}

}