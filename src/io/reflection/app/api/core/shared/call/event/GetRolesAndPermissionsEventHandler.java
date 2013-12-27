//  
//  GetRolesAndPermissionsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetRolesAndPermissionsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetRolesAndPermissionsEventHandler> TYPE = new GwtEvent.Type<GetRolesAndPermissionsEventHandler>();

	public void getRolesAndPermissionsSuccess(final GetRolesAndPermissionsRequest input, final GetRolesAndPermissionsResponse output);

	public void getRolesAndPermissionsFailure(final GetRolesAndPermissionsRequest input, final Throwable caught);

	public class GetRolesAndPermissionsSuccess extends GwtEvent<GetRolesAndPermissionsEventHandler> {
		private GetRolesAndPermissionsRequest input;
		private GetRolesAndPermissionsResponse output;

		public GetRolesAndPermissionsSuccess(final GetRolesAndPermissionsRequest input, final GetRolesAndPermissionsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetRolesAndPermissionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetRolesAndPermissionsEventHandler handler) {
			handler.getRolesAndPermissionsSuccess(input, output);
		}
	}

	public class GetRolesAndPermissionsFailure extends GwtEvent<GetRolesAndPermissionsEventHandler> {
		private GetRolesAndPermissionsRequest input;
		private Throwable caught;

		public GetRolesAndPermissionsFailure(final GetRolesAndPermissionsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetRolesAndPermissionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetRolesAndPermissionsEventHandler handler) {
			handler.getRolesAndPermissionsFailure(input, caught);
		}
	}

}