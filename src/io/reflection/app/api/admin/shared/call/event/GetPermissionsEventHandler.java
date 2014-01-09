//  
//  GetPermissionsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetPermissionsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetPermissionsEventHandler> TYPE = new GwtEvent.Type<GetPermissionsEventHandler>();

	public void getPermissionsSuccess(final GetPermissionsRequest input, final GetPermissionsResponse output);

	public void getPermissionsFailure(final GetPermissionsRequest input, final Throwable caught);

	public class GetPermissionsSuccess extends GwtEvent<GetPermissionsEventHandler> {
		private GetPermissionsRequest input;
		private GetPermissionsResponse output;

		public GetPermissionsSuccess(final GetPermissionsRequest input, final GetPermissionsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetPermissionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetPermissionsEventHandler handler) {
			handler.getPermissionsSuccess(input, output);
		}
	}

	public class GetPermissionsFailure extends GwtEvent<GetPermissionsEventHandler> {
		private GetPermissionsRequest input;
		private Throwable caught;

		public GetPermissionsFailure(final GetPermissionsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetPermissionsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetPermissionsEventHandler handler) {
			handler.getPermissionsFailure(input, caught);
		}
	}

}