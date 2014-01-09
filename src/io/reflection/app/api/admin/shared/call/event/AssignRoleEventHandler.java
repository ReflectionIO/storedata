//  
//  AssignRoleEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AssignRoleEventHandler extends EventHandler {
	public static final GwtEvent.Type<AssignRoleEventHandler> TYPE = new GwtEvent.Type<AssignRoleEventHandler>();

	public void assignRoleSuccess(final AssignRoleRequest input, final AssignRoleResponse output);

	public void assignRoleFailure(final AssignRoleRequest input, final Throwable caught);

	public class AssignRoleSuccess extends GwtEvent<AssignRoleEventHandler> {
		private AssignRoleRequest input;
		private AssignRoleResponse output;

		public AssignRoleSuccess(final AssignRoleRequest input, final AssignRoleResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<AssignRoleEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AssignRoleEventHandler handler) {
			handler.assignRoleSuccess(input, output);
		}
	}

	public class AssignRoleFailure extends GwtEvent<AssignRoleEventHandler> {
		private AssignRoleRequest input;
		private Throwable caught;

		public AssignRoleFailure(final AssignRoleRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<AssignRoleEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AssignRoleEventHandler handler) {
			handler.assignRoleFailure(input, caught);
		}
	}

}