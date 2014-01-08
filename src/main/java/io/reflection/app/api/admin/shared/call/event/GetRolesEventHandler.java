//  
//  GetRolesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetRolesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetRolesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetRolesEventHandler> TYPE = new GwtEvent.Type<GetRolesEventHandler>();

	public void getRolesSuccess(final GetRolesRequest input, final GetRolesResponse output);

	public void getRolesFailure(final GetRolesRequest input, final Throwable caught);

	public class GetRolesSuccess extends GwtEvent<GetRolesEventHandler> {
		private GetRolesRequest input;
		private GetRolesResponse output;

		public GetRolesSuccess(final GetRolesRequest input, final GetRolesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetRolesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetRolesEventHandler handler) {
			handler.getRolesSuccess(input, output);
		}
	}

	public class GetRolesFailure extends GwtEvent<GetRolesEventHandler> {
		private GetRolesRequest input;
		private Throwable caught;

		public GetRolesFailure(final GetRolesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetRolesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetRolesEventHandler handler) {
			handler.getRolesFailure(input, caught);
		}
	}

}