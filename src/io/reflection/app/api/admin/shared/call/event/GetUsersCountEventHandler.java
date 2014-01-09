//  
//  GetUsersCountEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetUsersCountEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetUsersCountEventHandler> TYPE = new GwtEvent.Type<GetUsersCountEventHandler>();

	public void getUsersCountSuccess(final GetUsersCountRequest input, final GetUsersCountResponse output);

	public void getUsersCountFailure(final GetUsersCountRequest input, final Throwable caught);

	public class GetUsersCountSuccess extends GwtEvent<GetUsersCountEventHandler> {
		private GetUsersCountRequest input;
		private GetUsersCountResponse output;

		public GetUsersCountSuccess(final GetUsersCountRequest input, final GetUsersCountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetUsersCountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetUsersCountEventHandler handler) {
			handler.getUsersCountSuccess(input, output);
		}
	}

	public class GetUsersCountFailure extends GwtEvent<GetUsersCountEventHandler> {
		private GetUsersCountRequest input;
		private Throwable caught;

		public GetUsersCountFailure(final GetUsersCountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetUsersCountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetUsersCountEventHandler handler) {
			handler.getUsersCountFailure(input, caught);
		}
	}

}