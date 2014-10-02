//  
//  RevokeRoleEventHandler.java
//  reflection.io
//
//  Created by William Shakour on September 25, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.RevokeRoleRequest;
import io.reflection.app.api.admin.shared.call.RevokeRoleResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface RevokeRoleEventHandler extends EventHandler {
	public static final GwtEvent.Type<RevokeRoleEventHandler> TYPE = new GwtEvent.Type<RevokeRoleEventHandler>();

	public void revokeRoleSuccess(final RevokeRoleRequest input, final RevokeRoleResponse output);

	public void revokeRoleFailure(final RevokeRoleRequest input, final Throwable caught);

	public class RevokeRoleSuccess extends GwtEvent<RevokeRoleEventHandler> {
		private RevokeRoleRequest input;
		private RevokeRoleResponse output;

		public RevokeRoleSuccess(final RevokeRoleRequest input, final RevokeRoleResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<RevokeRoleEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RevokeRoleEventHandler handler) {
			handler.revokeRoleSuccess(input, output);
		}
	}

	public class RevokeRoleFailure extends GwtEvent<RevokeRoleEventHandler> {
		private RevokeRoleRequest input;
		private Throwable caught;

		public RevokeRoleFailure(final RevokeRoleRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<RevokeRoleEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RevokeRoleEventHandler handler) {
			handler.revokeRoleFailure(input, caught);
		}
	}

}