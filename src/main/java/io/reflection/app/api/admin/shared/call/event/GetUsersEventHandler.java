//  
//  GetUsersEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetUsersEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetUsersEventHandler> TYPE = new GwtEvent.Type<GetUsersEventHandler>();

	public void getUsersSuccess(final GetUsersRequest input, final GetUsersResponse output);

	public void getUsersFailure(final GetUsersRequest input, final Throwable caught);

	public class GetUsersSuccess extends GwtEvent<GetUsersEventHandler> {
		private GetUsersRequest input;
		private GetUsersResponse output;

		public GetUsersSuccess(final GetUsersRequest input, final GetUsersResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetUsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetUsersEventHandler handler) {
			handler.getUsersSuccess(input, output);
		}
	}

	public class GetUsersFailure extends GwtEvent<GetUsersEventHandler> {
		private GetUsersRequest input;
		private Throwable caught;

		public GetUsersFailure(final GetUsersRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetUsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetUsersEventHandler handler) {
			handler.getUsersFailure(input, caught);
		}
	}

}