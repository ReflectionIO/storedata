//  
//  RevokePermissionEventHandler.java
//  reflection.io
//
//  Created by William Shakour on September 25, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.RevokePermissionRequest;
import io.reflection.app.api.admin.shared.call.RevokePermissionResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface RevokePermissionEventHandler extends EventHandler {
	public static final GwtEvent.Type<RevokePermissionEventHandler> TYPE = new GwtEvent.Type<RevokePermissionEventHandler>();

	public void revokePermissionSuccess(final RevokePermissionRequest input, final RevokePermissionResponse output);

	public void revokePermissionFailure(final RevokePermissionRequest input, final Throwable caught);

	public class RevokePermissionSuccess extends GwtEvent<RevokePermissionEventHandler> {
		private RevokePermissionRequest input;
		private RevokePermissionResponse output;

		public RevokePermissionSuccess(final RevokePermissionRequest input, final RevokePermissionResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<RevokePermissionEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RevokePermissionEventHandler handler) {
			handler.revokePermissionSuccess(input, output);
		}
	}

	public class RevokePermissionFailure extends GwtEvent<RevokePermissionEventHandler> {
		private RevokePermissionRequest input;
		private Throwable caught;

		public RevokePermissionFailure(final RevokePermissionRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<RevokePermissionEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RevokePermissionEventHandler handler) {
			handler.revokePermissionFailure(input, caught);
		}
	}

}