//  
//  GetUserDetailsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 7, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetUserDetailsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetUserDetailsEventHandler> TYPE = new GwtEvent.Type<GetUserDetailsEventHandler>();

	public void getUserDetailsSuccess(final GetUserDetailsRequest input, final GetUserDetailsResponse output);

	public void getUserDetailsFailure(final GetUserDetailsRequest input, final Throwable caught);

	public class GetUserDetailsSuccess extends GwtEvent<GetUserDetailsEventHandler> {
		private GetUserDetailsRequest input;
		private GetUserDetailsResponse output;

		public GetUserDetailsSuccess(final GetUserDetailsRequest input, final GetUserDetailsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetUserDetailsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetUserDetailsEventHandler handler) {
			handler.getUserDetailsSuccess(input, output);
		}
	}

	public class GetUserDetailsFailure extends GwtEvent<GetUserDetailsEventHandler> {
		private GetUserDetailsRequest input;
		private Throwable caught;

		public GetUserDetailsFailure(final GetUserDetailsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetUserDetailsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetUserDetailsEventHandler handler) {
			handler.getUserDetailsFailure(input, caught);
		}
	}

}